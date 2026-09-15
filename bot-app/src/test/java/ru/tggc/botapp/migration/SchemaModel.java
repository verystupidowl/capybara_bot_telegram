package ru.tggc.botapp.migration;
import jakarta.persistence.Entity;
import org.hibernate.cfg.Configuration;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
final class SchemaModel {
    static Configuration configuration() throws ClassNotFoundException {
        var cfg = new Configuration();
        cfg.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        cfg.setProperty("hibernate.physical_naming_strategy", "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy");
        cfg.setProperty("hibernate.implicit_naming_strategy", "org.springframework.boot.hibernate.SpringImplicitNamingStrategy");
        var scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Entity.class));
        for (var bean : scanner.findCandidateComponents("ru.tggc.botapp.domain.model")) cfg.addAnnotatedClass(Class.forName(bean.getBeanClassName()));
        return cfg;
    }
}
