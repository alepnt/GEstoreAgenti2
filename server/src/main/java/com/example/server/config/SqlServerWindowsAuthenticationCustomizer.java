package com.example.server.config;

import com.zaxxer.hikari.HikariDataSource;
import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Configures the primary {@link javax.sql.DataSource} to use Windows integrated authentication when
 * requested through {@link DatabaseAuthenticationProperties}.
 */
@Component
@EnableConfigurationProperties(DatabaseAuthenticationProperties.class)
public class SqlServerWindowsAuthenticationCustomizer implements BeanPostProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SqlServerWindowsAuthenticationCustomizer.class);

    private final DatabaseAuthenticationProperties authenticationProperties;
    private final AtomicBoolean libraryPathUpdated = new AtomicBoolean(false);

    public SqlServerWindowsAuthenticationCustomizer(DatabaseAuthenticationProperties authenticationProperties) {
        this.authenticationProperties = authenticationProperties;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof HikariDataSource dataSource) {
            configureForWindowsAuthenticationIfRequired(dataSource);
        }
        return bean;
    }

    private void configureForWindowsAuthenticationIfRequired(HikariDataSource dataSource) {
        if (!authenticationProperties.isWindowsAuthentication()) {
            return;
        }

        String jdbcUrl = dataSource.getJdbcUrl();
        if (!isSqlServer(jdbcUrl)) {
            return;
        }

        ensureIntegratedSecurityEnabled(dataSource, jdbcUrl);
        dataSource.setUsername(null);
        dataSource.setPassword(null);
        dataSource.addDataSourceProperty("integratedSecurity", "true");

        if (StringUtils.hasText(authenticationProperties.getAuthenticationScheme())) {
            dataSource.addDataSourceProperty("authenticationScheme", authenticationProperties.getAuthenticationScheme());
        }

        updateNativeLibraryPathIfNecessary();
        LOGGER.info("Configured SQL Server datasource '{}' to use Windows integrated authentication.", dataSource.getPoolName());
    }

    private boolean isSqlServer(String jdbcUrl) {
        return jdbcUrl != null && jdbcUrl.toLowerCase(Locale.ROOT).startsWith("jdbc:sqlserver:");
    }

    private void ensureIntegratedSecurityEnabled(HikariDataSource dataSource, String jdbcUrl) {
        if (jdbcUrl == null) {
            return;
        }

        String lowerUrl = jdbcUrl.toLowerCase(Locale.ROOT);
        if (!lowerUrl.contains("integratedsecurity=true")) {
            StringBuilder builder = new StringBuilder(jdbcUrl);
            if (!jdbcUrl.endsWith(";")) {
                builder.append(';');
            }
            builder.append("integratedSecurity=true");
            dataSource.setJdbcUrl(builder.toString());
        }
    }

    private void updateNativeLibraryPathIfNecessary() {
        String nativeLibraryPath = authenticationProperties.getNativeLibraryPath();
        if (!StringUtils.hasText(nativeLibraryPath)) {
            return;
        }

        if (!libraryPathUpdated.compareAndSet(false, true)) {
            return;
        }

        Path dllDirectory = Paths.get(nativeLibraryPath).toAbsolutePath();
        if (!Files.exists(dllDirectory)) {
            LOGGER.warn("The configured sqljdbc_auth.dll directory '{}' does not exist. Windows authentication may fail.",
                    dllDirectory);
            return;
        }

        String currentLibraryPath = System.getProperty("java.library.path", "");
        String newLibraryPath = dllDirectory + File.pathSeparator + currentLibraryPath;
        System.setProperty("java.library.path", newLibraryPath);

        try {
            Field sysPathsField = ClassLoader.class.getDeclaredField("sys_paths");
            sysPathsField.setAccessible(true);
            sysPathsField.set(null, null);
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            LOGGER.warn("Unable to refresh java.library.path for SQL Server integrated authentication."
                    + " The sqljdbc_auth.dll must be reachable through the system path.", ex);
        }
    }
}
