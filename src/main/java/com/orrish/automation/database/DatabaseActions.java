package com.orrish.automation.database;

import com.orrish.automation.entrypoint.SetUp;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseActions {

    private static BasicDataSource basicDataSource = new BasicDataSource();
    private static DatabaseActions databaseActions;

    static {
        setBasicDataSource();
    }

    private static String getDriverClassName() {
        if (SetUp.databaseConnectionString.contains("mysql")) {
            return "com.mysql.cj.jdbc.Driver";
        } else if (SetUp.databaseConnectionString.contains("oracle")) {
            return "oracle.jdbc.driver.OracleDriver";
        } else if (SetUp.databaseConnectionString.contains("postgresql")) {
            return "org.postgresql.Driver";
        } else if (SetUp.databaseConnectionString.contains("snowflake")) {
            return "com.snowflake.client.jdbc.SnowflakeDriver";
        }
        return "";
    }

    private DatabaseActions() {
    }

    public static DatabaseActions getInstance() {
        if (databaseActions == null) {
            synchronized (DatabaseActions.class) {
                if (databaseActions == null) {
                    databaseActions = new DatabaseActions();
                }
            }
        }
        return databaseActions;
    }

    public String getConnectionString() {
        return basicDataSource.getUrl();
    }

    public boolean reassignDataSource() {
        try {
            basicDataSource.close();
        } catch (Exception ex) {
        } finally {
            basicDataSource = new BasicDataSource();
        }
        setBasicDataSource();
        return true;
    }

    private static void setBasicDataSource() {
        basicDataSource.setDriverClassName(getDriverClassName());
        basicDataSource.setUrl(SetUp.databaseConnectionString);
        basicDataSource.setUsername(SetUp.databaseUserName);
        basicDataSource.setPassword(SetUp.databasePassword);
        basicDataSource.setMinIdle(5);
        basicDataSource.setMaxIdle(10);
        basicDataSource.setMaxOpenPreparedStatements(100);
    }

    public List runQuery(String query) throws Exception {
        try (Connection connection = basicDataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
                int columns = resultSetMetaData.getColumnCount();
                List<Map<String, Object>> resultRows = new ArrayList<>();
                while (resultSet.next()) {
                    Map<String, Object> eachRow = new HashMap<>(columns);
                    for (int i = 1; i <= columns; ++i) {
                        eachRow.put(resultSetMetaData.getColumnName(i), resultSet.getObject(i));
                    }
                    resultRows.add(eachRow);
                }
                return resultRows;
            }
        }
    }

    public int runCommand(String command) throws SQLException {
        try (Connection connection = basicDataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(command)) {
            int rowsImpacted = preparedStatement.executeUpdate();
            try {
                connection.commit();
            } catch (Exception ex) {
            }
            return rowsImpacted;
        }
    }
}
