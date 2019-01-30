package dao;

import entity.Manager;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ManagerDao implements IDao<Manager> {

    private static String connectUrl = MyConfig.setConfig("url");
    private static String user = MyConfig.setConfig("user");
    private static String password = MyConfig.setConfig("password");
    private Connection connection;
    private static ManagerDao instance = null;

    public static ManagerDao getInstance() {
        if (instance == null)
            instance = new ManagerDao();
        return instance;
    }

    private ManagerDao() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(connectUrl, user, password);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        try {
            this.connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS Manager (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL" +
                "requestId INTEGER NOT NULL);";
        try (Statement statement = this.connection.createStatement()) {
            int row = statement.executeUpdate(sql);
            System.out.println(row);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void add(Manager manager) {
        String sql = "INSERT INTO Manager (id, requestId)" + "VALUES (?, ?);";
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.setInt(1, manager.getId());
            statement.setInt(2, manager.getRequestId());
            int row = statement.executeUpdate();
            System.out.println(row);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Manager> getAll() {
        String sql = "SELECT * FROM Manager;";
        try (Statement statement = this.connection.createStatement()) {
            List<Manager> managerList = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                Manager manager = new Manager();
                manager.setId(resultSet.getInt("id"));
                manager.setRequestId(resultSet.getInt("requestId"));
                managerList.add(manager);
            }
            return managerList;

        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public Manager getById(int id) {
        String sql = "SELECT * FROM Manager WHERE id=?;";
        Manager manager = new Manager();
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            manager.setId(id);
            manager.setRequestId(resultSet.getInt("requestId"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return manager;
    }

    public List<Manager> getNotBusy() {
        String sql = "SELECT * FROM Manager WHERE requestId=NULL;";
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            List<Manager> managerList = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Manager manager = new Manager();
                manager.setId(resultSet.getInt("id"));
                managerList.add(manager);
            }
            return managerList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    @Override
    public <Integer> void update(int id, Integer requestId) {
        String sql = "UPDATE Manager SET requestId=? WHERE id=?";
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.setInt(1, (int) requestId);
            statement.setInt(2, id);
            int row = statement.executeUpdate();
            System.out.println(row);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM Manager WHERE id=?;";
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}