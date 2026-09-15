package ru.tggc.botapp.migration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

@EnabledIfSystemProperty(named = "schema.validate.existing", matches = "true")
class ExistingSchemaValidationTest {
    @Test
    void validateOnlyWithoutStartingBotOrLiquibase() throws Exception {
        var cfg = SchemaModel.configuration();
        cfg.setProperty("hibernate.connection.url", required("DB_URL"));
        cfg.setProperty("hibernate.connection.username", required("DB_USER"));
        cfg.setProperty("hibernate.connection.password", required("DB_PASSWORD"));
        cfg.setProperty("hibernate.connection.readOnly", "true");
        cfg.setProperty("hibernate.connection.readOnlyMode", "always");
        cfg.setProperty("hibernate.hbm2ddl.auto", "validate");
        try (var factory = cfg.buildSessionFactory()) {
        }
    }

    private static String required(String key) {
        String value = System.getenv(key);
        if (value == null || value.isBlank()) throw new IllegalStateException("Missing environment variable: " + key);
        return value;
    }
}
