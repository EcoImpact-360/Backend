package com.ecoimpact_360.backend.config;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

class SequenceSyncRunnerTest {
    @Test
    void run_ExecutesSetvalForEachTable_WhenPostgres() throws Exception {
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        DataSource dataSource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        DatabaseMetaData metaData = mock(DatabaseMetaData.class);
        when(jdbcTemplate.getDataSource()).thenReturn(dataSource);
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.getMetaData()).thenReturn(metaData);
        when(metaData.getDatabaseProductName()).thenReturn("PostgreSQL");
        SequenceSyncRunner runner = new SequenceSyncRunner(jdbcTemplate);
        runner.run(null);
        verify(jdbcTemplate).execute(contains("'schools'"));
        verify(jdbcTemplate).execute(contains("'classrooms'"));
        verify(jdbcTemplate).execute(contains("'waste_types'"));
        verify(jdbcTemplate).execute(contains("'waste_entries'"));
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
        SequenceSyncRunner runner = new SequenceSyncRunner(jdbcTemplate);
        runner.run(null);
        verify(jdbcTemplate, never()).execute(anyString());
    }
}
