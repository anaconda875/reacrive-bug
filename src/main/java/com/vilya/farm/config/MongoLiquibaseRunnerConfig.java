package com.vilya.farm.config;

import com.mongodb.ConnectionString;
import liquibase.*;
import liquibase.configuration.ConfiguredValue;
import liquibase.database.DatabaseFactory;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.ext.mongodb.database.MongoLiquibaseDatabase;
import liquibase.integration.commandline.LiquibaseCommandLineConfiguration;
import liquibase.integration.spring.SpringLiquibase;
import liquibase.integration.spring.SpringResourceAccessor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.util.Map;

@Configuration
@EnableConfigurationProperties(LiquibaseProperties.class)
public class MongoLiquibaseRunnerConfig {

  @Bean
  public MongoLiquibaseDatabase database(MongoProperties properties) throws Exception {
    String uri;
    String username = null;
    String password = null;
    if (properties.getUri() != null) {
      uri = new ConnectionString(properties.getUri()).getConnectionString();
    } else {
      String host = properties.getHost();
      int port = properties.getPort();
      String database = properties.getDatabase();
      username = properties.getUsername();
      password = properties.getPassword() == null ? null : new String(properties.getPassword());
      //      String credentials = buildCredentials(username, password);
      uri =
          String.format(
              "mongodb://%s:%d/%s?socketTimeoutMS=1000&connectTimeoutMS=1000&serverSelectionTimeoutMS=1000",
              host, port, database);
    }

    return (MongoLiquibaseDatabase)
        DatabaseFactory.getInstance().openDatabase(uri, username, password, null, null);
  }

  @Bean
  public SpringLiquibase liquibase(
      LiquibaseProperties properties, MongoLiquibaseDatabase database) {
    SpringLiquibase liquibase = new MongoSpringLiquibase(database);
    liquibase.setChangeLog(properties.getChangeLog());
    liquibase.setClearCheckSums(properties.isClearChecksums());
    liquibase.setContexts(properties.getContexts());
    liquibase.setDefaultSchema(properties.getDefaultSchema());
    liquibase.setLiquibaseSchema(properties.getLiquibaseSchema());
    liquibase.setLiquibaseTablespace(properties.getLiquibaseTablespace());
    liquibase.setDatabaseChangeLogTable(properties.getDatabaseChangeLogTable());
    liquibase.setDatabaseChangeLogLockTable(properties.getDatabaseChangeLogLockTable());
    liquibase.setDropFirst(properties.isDropFirst());
    liquibase.setShouldRun(properties.isEnabled());
    liquibase.setLabels(properties.getLabels());
    liquibase.setChangeLogParameters(properties.getParameters());
    liquibase.setRollbackFile(properties.getRollbackFile());
    liquibase.setTestRollbackOnUpdate(properties.isTestRollbackOnUpdate());
    liquibase.setTag(properties.getTag());
    return liquibase;
  }

  private static String buildCredentials(String username, String password) {
    if (username == null) {
      return "";
    }

    return String.format("%s:%s@", username, password == null ? "" : password);
  }

  @RequiredArgsConstructor
  static class MongoSpringLiquibase extends SpringLiquibase {

    final MongoLiquibaseDatabase database;

    @Override
    public void afterPropertiesSet() throws LiquibaseException {
      ConfiguredValue<Boolean> shouldRunProperty =
          LiquibaseCommandLineConfiguration.SHOULD_RUN.getCurrentConfiguredValue();

      if (!(Boolean) shouldRunProperty.getValue()) {
        Scope.getCurrentScope()
            .getLog(getClass())
            .info(
                "Liquibase did not run because "
                    + shouldRunProperty.getProvidedValue().describe()
                    + " was set to false");
        return;
      }
      if (!shouldRun) {
        Scope.getCurrentScope()
            .getLog(getClass())
            .info(
                "Liquibase did not run because 'shouldRun' "
                    + "property was set "
                    + "to false on "
                    + getBeanName()
                    + " Liquibase Spring bean.");
        return;
      }

      try (Liquibase liquibase = createLiquibase(database)) {
        generateRollbackFile(liquibase);
        performUpdate(liquibase);
      } catch (Exception e) {
        throw new DatabaseException(e);
      }
    }

    private void generateRollbackFile(Liquibase liquibase) throws LiquibaseException {
      if (rollbackFile != null) {

        try (final OutputStream outputStream = Files.newOutputStream(rollbackFile.toPath());
            Writer output =
                new OutputStreamWriter(
                    outputStream, GlobalConfiguration.OUTPUT_FILE_ENCODING.getCurrentValue())) {

          if (tag != null) {
            liquibase.futureRollbackSQL(
                tag, new Contexts(getContexts()), new LabelExpression(getLabelFilter()), output);
          } else {
            liquibase.futureRollbackSQL(
                new Contexts(getContexts()), new LabelExpression(getLabelFilter()), output);
          }
        } catch (IOException e) {
          throw new LiquibaseException("Unable to generate rollback file.", e);
        }
      }
    }

    protected Liquibase createLiquibase(MongoLiquibaseDatabase database) throws LiquibaseException {
      SpringResourceAccessor resourceAccessor = createResourceOpener();
      Liquibase liquibase = new Liquibase(getChangeLog(), resourceAccessor, database);
      if (parameters != null) {
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
          liquibase.setChangeLogParameter(entry.getKey(), entry.getValue());
        }
      }

      if (isDropFirst()) {
        liquibase.dropAll();
      }

      return liquibase;
    }
  }
}
