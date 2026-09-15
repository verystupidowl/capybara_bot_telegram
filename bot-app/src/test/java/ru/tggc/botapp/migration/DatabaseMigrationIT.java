package ru.tggc.botapp.migration;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Testcontainers
class DatabaseMigrationIT {
    @Container
    static final PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:13.3");
    static final String MASTER = "db/changelog/db.changelog-master.yaml";
    static final String BASELINE = "db/changelog/001-baseline.yaml";

    private String schema() throws Exception {
        String name = "migration_" + UUID.randomUUID().toString().replace("-", "");
        try (var c = postgres.createConnection(""); var s = c.createStatement()) {
            s.execute("CREATE SCHEMA " + name);
        }
        return name;
    }

    private Connection connect(String schema) throws Exception {
        String separator = postgres.getJdbcUrl().contains("?") ? "&" : "?";
        return DriverManager.getConnection(postgres.getJdbcUrl() + separator + "currentSchema=" + schema, postgres.getUsername(), postgres.getPassword());
    }

    private Liquibase liquibase(String schema, String changelog) throws Exception {
        return new Liquibase(changelog, new ClassLoaderResourceAccessor(), new JdbcConnection(connect(schema)));
    }

    private void validateHibernate(String schema) throws Exception {
        var cfg = SchemaModel.configuration();
        cfg.setProperty("hibernate.connection.url", postgres.getJdbcUrl());
        cfg.setProperty("hibernate.connection.username", postgres.getUsername());
        cfg.setProperty("hibernate.connection.password", postgres.getPassword());
        cfg.setProperty("hibernate.default_schema", schema);
        cfg.setProperty("hibernate.hbm2ddl.auto", "validate");
        try (var factory = cfg.buildSessionFactory()) {
        }
    }

    private long count(String schema, String sql) throws Exception {
        try (var c = connect(schema); var s = c.createStatement(); var r = s.executeQuery(sql)) {
            r.next();
            return r.getLong(1);
        }
    }

    @Test
    void freshDatabaseMatchesHibernateAndSecondRunPreservesData() throws Exception {
        String schema = schema();
        try (var lb = liquibase(schema, MASTER)) {
            lb.validate();
            lb.update(new Contexts(), new LabelExpression());
        }
        validateHibernate(schema);
        try (var c = connect(schema); var s = c.createStatement()) {
            s.execute("INSERT INTO usr(id,username,blocked) VALUES (123,'sentinel',false)");
        }
        try (var lb = liquibase(schema, MASTER)) {
            lb.update(new Contexts(), new LabelExpression());
        }
        assertEquals(1, count(schema, "SELECT count(*) FROM databasechangelog"));
        assertEquals(1, count(schema, "SELECT count(*) FROM usr WHERE id=123"));
    }

    @Test
    void existingUnmanagedDatabaseIsNotSilentlyMarkedAsMigrated() throws Exception {
        String schema = schema();
        try (var c = connect(schema); var s = c.createStatement()) {
            s.execute("CREATE TABLE existing_data(id bigint primary key); INSERT INTO existing_data VALUES (7)");
        }
        try (var lb = liquibase(schema, MASTER)) {
            assertThrows(Exception.class, () -> lb.update(new Contexts(), new LabelExpression()));
        }
        assertEquals(1, count(schema, "SELECT count(*) FROM existing_data"));
        assertEquals(0, count(schema, "SELECT count(*) FROM databasechangelog"));
    }

    @Test
    void explicitBaselineAdoptionKeepsExistingRecords() throws Exception {
        String schema = schema();
        try (var stream = getClass().getClassLoader().getResourceAsStream("db/changelog/sql/001-initial-schema.sql")) {
            assertNotNull(stream);
            try (var c = connect(schema); var s = c.createStatement()) {
                s.execute(new String(stream.readAllBytes(), StandardCharsets.UTF_8));
                s.execute("INSERT INTO usr(id,username,blocked) VALUES (456,'legacy',false)");
            }
        }
        validateHibernate(schema);
        try (var lb = liquibase(schema, BASELINE)) {
            lb.changeLogSync(new Contexts(), new LabelExpression());
        }
        try (var lb = liquibase(schema, MASTER)) {
            lb.update(new Contexts(), new LabelExpression());
        }
        assertEquals(1, count(schema, "SELECT count(*) FROM usr WHERE id=456"));
        assertEquals(1, count(schema, "SELECT count(*) FROM databasechangelog"));
    }
}
