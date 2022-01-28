package com.orrish.automation.database;

import com.orrish.automation.entrypoint.SetUp;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseService {

    private static BasicDataSource basicDataSource = new BasicDataSource();
    private static DatabaseService databaseService;

    static {
        basicDataSource.setDriverClassName(SetUp.databaseDriverClassName);
        basicDataSource.setUrl(SetUp.databaseConnectionString);
        basicDataSource.setUsername(SetUp.databaseUserName);
        basicDataSource.setPassword(SetUp.databasePassword);
        basicDataSource.setMinIdle(5);
        basicDataSource.setMaxIdle(10);
        basicDataSource.setMaxOpenPreparedStatements(100);
    }

    private DatabaseService() {
    }

    public static synchronized DatabaseService getInstance() {
        if (databaseService == null) {
            databaseService = new DatabaseService();
        }
        return databaseService;
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
        basicDataSource.setDriverClassName(SetUp.databaseDriverClassName);
        basicDataSource.setUrl(SetUp.databaseConnectionString);
        basicDataSource.setUsername(SetUp.databaseUserName);
        basicDataSource.setPassword(SetUp.databasePassword);
        basicDataSource.setMinIdle(5);
        basicDataSource.setMaxIdle(10);
        basicDataSource.setMaxOpenPreparedStatements(100);
        return true;
    }

    public List runQuery(String query) throws Exception {
        try (Connection connection = basicDataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
                int columns = resultSetMetaData.getColumnCount();
                if (columns == 1) {
                    List<String> valueToReturn = new ArrayList<>();
                    while (resultSet.next()) {
                        valueToReturn.add(resultSet.getString(1));
                    }
                    return valueToReturn;
                } else {
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
