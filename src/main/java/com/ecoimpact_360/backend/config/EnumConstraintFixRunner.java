package com.ecoimpact_360.backend.config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Con spring.jpa.hibernate.ddl-auto=update, Hibernate genera un CHECK constraint
 * en Postgres para las columnas @Enumerated(EnumType.STRING) en el momento en que
 * CREA la tabla, listando los valores del enum de entonces. Si mas tarde se anade
 * un valor nuevo al enum (p.ej. AlertType.CUSTOM), "update" no toca constraints ya
 * existentes en columnas ya existentes, y Postgres sigue rechazando el valor nuevo
 * con un DataIntegrityViolationException.
 *
 * Se elimina ese constraint al arrancar: la validez de los valores ya la garantiza
 * la capa Java (el propio enum + el parseo defensivo en AlertService), por lo que
 * el CHECK en base de datos es redundante y, peor, queda desincronizado tras cada
 * cambio en el enum.
 */
@Component
public class EnumConstraintFixRunner implements ApplicationRunner {
    private static final Logger logger = LoggerFactory.getLogger(EnumConstraintFixRunner.class);
    private static final String[][] ENUM_CHECK_CONSTRAINTS = {
            {"alerts", "alerts_alert_type_check"},
    };

    private final JdbcTemplate jdbcTemplate;

    public EnumConstraintFixRunner(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) throws SQLException {
        if (!isPostgres()) {
            return;
        }
        for (String[] tableAndConstraint : ENUM_CHECK_CONSTRAINTS) {
            String table = tableAndConstraint[0];
            String constraint = tableAndConstraint[1];
            jdbcTemplate.execute("ALTER TABLE " + table + " DROP CONSTRAINT IF EXISTS " + constraint);
        }
        logger.info("Constraints de enum obsoletos eliminados (la validacion la hace la capa Java)");
    }

    private boolean isPostgres() throws SQLException {
        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            return "PostgreSQL".equalsIgnoreCase(connection.getMetaData().getDatabaseProductName());
        }
    }
}
