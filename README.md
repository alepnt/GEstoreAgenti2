# Gestore Agenti

Questo repository contiene un progetto multi-modulo per l'applicazione Gestore Agenti. Il modulo **server** espone le API REST basate su Spring Boot e utilizza Microsoft SQL Server come database principale.

## Prerequisiti

- JDK 21+
- Maven 3.9+
- Docker (necessario per i test di integrazione basati su Testcontainers)
- Un'istanza Microsoft SQL Server raggiungibile dalla JVM dell'applicazione

## Configurazione del database

Le credenziali del database e i parametri del connection pool sono gestiti tramite variabili d'ambiente. È possibile valorizzarle direttamente prima dell'avvio oppure aggiungerle a un file `.env` utilizzato dagli strumenti di deploy.

| Variabile            | Default                                                                  | Descrizione                                   |
|----------------------|--------------------------------------------------------------------------|-----------------------------------------------|
| `DB_URL`             | `jdbc:sqlserver://localhost:1433;databaseName=gestoreagenti;encrypt=true;trustServerCertificate=true` | URL JDBC Microsoft SQL Server                |
| `DB_USERNAME`        | `sa`                                                                     | Utente SQL Server                              |
| `DB_PASSWORD`        | `ChangeMe!`                                                              | Password SQL Server                            |
| `DB_MAX_POOL_SIZE`   | `10`                                                                     | Numero massimo di connessioni nel pool         |
| `DB_MIN_IDLE`        | `5`                                                                      | Connessioni minime inattive mantenute nel pool |

## Migrazioni database

Flyway è abilitato di default e applica automaticamente le migrazioni allo startup. Le migrazioni creano lo schema applicativo e inseriscono i dati seed (ruoli e team) necessari all'inizializzazione.

Per applicare le migrazioni manualmente:

```bash
mvn -pl server flyway:migrate
```

## Build e packaging

Per compilare l'intero progetto e lanciare tutti i test (unitari e di integrazione):

```bash
mvn clean verify
```

Il modulo server può essere impacchettato come JAR eseguibile con:

```bash
mvn -pl server -am clean package
```

Il file risultante sarà disponibile in `server/target/gestore-agenti-server-0.0.1-SNAPSHOT.jar`.

## Test di integrazione

I test di integrazione utilizzano Testcontainers per avviare automaticamente un container Microsoft SQL Server. Assicurarsi che Docker sia in esecuzione prima di lanciare i test.

```bash
mvn -pl server -am test
```

## Deploy

1. Assicurarsi che il database SQL Server di destinazione sia raggiungibile e che l'utente configurato disponga dei privilegi necessari (creazione tabelle, lettura, scrittura).
2. Configurare le variabili `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `DB_MAX_POOL_SIZE` e `DB_MIN_IDLE` nell'ambiente della macchina/servizio in cui verrà eseguita l'applicazione.
3. Copiare il JAR prodotto (`server/target/gestore-agenti-server-0.0.1-SNAPSHOT.jar`) sulla macchina di destinazione.
4. Avviare l'applicazione:

   ```bash
   java -jar server/target/gestore-agenti-server-0.0.1-SNAPSHOT.jar
   ```

Al primo avvio Flyway applicherà automaticamente lo schema e i dati seed.
