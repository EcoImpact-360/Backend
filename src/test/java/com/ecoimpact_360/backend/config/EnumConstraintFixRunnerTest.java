package com.ecoimpact_360.backend.config;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

class EnumConstraintFixRunnerTest {
    @Test
    void run_DropsAlertTypeCheckConstraint_WhenPostgres() throws Exception {
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        DataSource dataSource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        DatabaseMetaData metaData = mock(DatabaseMetaData.class);
        when(jdbcTemplate.getDataSource()).thenReturn(dataSource);
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.getMetaData()).thenReturn(metaData);
        when(metaData.getDatabaseProductName()).thenReturn("PostgreSQL");
        EnumConstraintFixRunner runner = new EnumConstraintFixRunner(jdbcTemplate);
        runner.run(null);
        verify(jdbcTemplate).execute(contains("alerts"));
        verify(jdbcTemplate).execute(contains("alerts_alert_type_check"));
    }
    @Test
    void run_DoesNothing_WhenNotPostgres() throws Exception {
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        DataSource dataSource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        DatabaseMetaData metaData = mock(DatabaseMetaData.class);
        when(jdbcTemplate.getDataSource()).thenReturn(dataSource);
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.getMetaData()).thenReturn(metaData);
        when(metaData.getDatabaseProductName()).thenReturn("H2");
        EnumConstraintFixRunner runner = new EnumConstraintFixRunner(jdbcTemplate);
        runner.run(null);
        verify(jdbcTemplate, never()).execute(anyString());
    }
}
