package com.example.server.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

@ConfigurationProperties(prefix = "app.database")
public class DatabaseAuthenticationProperties {

    public enum AuthenticationMode {
        SQL,
        WINDOWS
    }

    private AuthenticationMode authenticationMode = AuthenticationMode.SQL;

    /**
     * Authentication scheme passed to the SQL Server JDBC driver when integrated security is enabled.
     * Defaults to {@code NativeAuthentication} which lets the driver pick the appropriate mechanism.
     */
    private String authenticationScheme = "NativeAuthentication";

    /**
     * Optional path to the folder that contains the {@code sqljdbc_auth.dll} library required for
     * Windows integrated authentication. When specified the path is prepended to the
     * {@code java.library.path} system property.
     */
    private String nativeLibraryPath;

    public AuthenticationMode getAuthenticationMode() {
        return authenticationMode;
    }

    public void setAuthenticationMode(AuthenticationMode authenticationMode) {
        this.authenticationMode = authenticationMode == null ? AuthenticationMode.SQL : authenticationMode;
    }

    public String getAuthenticationScheme() {
        return authenticationScheme;
    }

    public void setAuthenticationScheme(String authenticationScheme) {
        this.authenticationScheme = StringUtils.hasText(authenticationScheme)
                ? authenticationScheme
                : "NativeAuthentication";
    }

    public String getNativeLibraryPath() {
        return nativeLibraryPath;
    }

    public void setNativeLibraryPath(String nativeLibraryPath) {
        this.nativeLibraryPath = StringUtils.hasText(nativeLibraryPath) ? nativeLibraryPath : null;
    }

    public boolean isWindowsAuthentication() {
        return authenticationMode == AuthenticationMode.WINDOWS;
    }
}
