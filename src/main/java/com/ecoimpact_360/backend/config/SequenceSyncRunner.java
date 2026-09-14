package com.ecoimpact_360.backend.config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class SequenceSyncRunner implements ApplicationRunner {
    private static final Logger logger = LoggerFactory.getLogger(SequenceSyncRunner.class);
    private static final String[] IDENTITY_TABLES = {"schools", "classrooms", "waste_types", "waste_entries"};

    private final JdbcTemplate jdbcTemplate;

    public SequenceSyncRunner(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) throws SQLException {
        if (!isPostgres()) {
            return;
        }
        for (String table : IDENTITY_TABLES) {
            jdbcTemplate.execute(
                    "SELECT setval(pg_get_serial_sequence('" + table + "', 'id'), "
                            + "(SELECT COALESCE(MAX(id), 1) FROM " + table + "))"
            );
        }
        logger.info("Secuencias de identidad resincronizadas con el MAX(id) real de cada tabla");
    }

    private boolean isPostgres() throws SQLException {
        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            return "PostgreSQL".equalsIgnoreCase(connection.getMetaData().getDatabaseProductName());
        }
    }
}
