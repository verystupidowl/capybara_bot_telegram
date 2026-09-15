package ru.tggc.botapp.migration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import java.nio.file.Files;
import java.nio.file.Path;

@EnabledIfSystemProperty(named = "schema.export", matches = "true")
class SchemaExportTest {
    @Test
    void exportReferenceSchemaWithoutConnectingToDatabase() throws Exception {
        var cfg = SchemaModel.configuration();
        cfg.setProperty("hibernate.boot.allow_jdbc_metadata_access", "false");
        cfg.setProperty("jakarta.persistence.schema-generation.database.action", "none");
        cfg.setProperty("jakarta.persistence.schema-generation.scripts.action", "create");
        cfg.setProperty("hibernate.hbm2ddl.delimiter", ";");
        cfg.setProperty("hibernate.format_sql", "true");
        Files.createDirectories(Path.of("target"));
        try (var writer = Files.newBufferedWriter(Path.of("target/schema-reference.sql"))) {
            cfg.getProperties().put("jakarta.persistence.schema-generation.scripts.create-target", writer);
            try (var factory = cfg.buildSessionFactory()) {
            }
        }
    }
}
