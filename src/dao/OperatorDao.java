package dao;

import entity.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OperatorDao implements IDao<Operator> {

    private static String connectUrl = "jdbc:mysql://localhost:3306/courses?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
    private static String user = "newuser";
    private static String password = "password";
    private Connection connection;
    private static OperatorDao instance = null;

    public static OperatorDao getInstance() {
        if (instance == null)
            instance = new OperatorDao();
        return instance;
    }

    private OperatorDao() {
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
        String sql = "CREATE TABLE IF NOT EXISTS Operator (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL" +
                "status VARCHAR NOT NULL);";
        try (Statement statement = this.connection.createStatement()) {
            int row = statement.executeUpdate(sql);
            System.out.println(row);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void add(Operator operator) {
        String sql = "INSERT INTO Operator (id, status)" + "VALUES (?, ?);";
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.setInt(1, operator.getId());
            statement.setString(2, operator.getStatus().name());
            int row = statement.executeUpdate();
            System.out.println(row);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Operator> getAll() {
        String sql = "SELECT * FROM Operator;";
        try (Statement statement = this.connection.createStatement()) {
            List<Operator> operatorList = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                Operator operator = new Operator();
                operator.setId(resultSet.getInt("id"));
                operator.setStatus(EmployeeStatus.valueOf(resultSet.getString("status")));
                operatorList.add(operator);
            }
            return operatorList;

        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public Operator getById(int id) {
        String sql = "SELECT * FROM Operator WHERE id=?;";
        Operator operator = new Operator();
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            operator.setId(id);
            operator.setStatus(EmployeeStatus.valueOf(resultSet.getString("status")));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return operator;
    }

    public List<Operator> getByStatus(EmployeeStatus status) {
        String sql = "SELECT * FROM Operator WHERE status=?;";
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.setString(1, status.name());
            List<Operator> operatorList = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Operator operator = new Operator();
                operator.setId(resultSet.getInt("id"));
                operator.setStatus(status);
                operatorList.add(operator);
            }
            return operatorList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    @Override
    public <EmployeeStatus> void update(int id, EmployeeStatus status) {
        String sql = "UPDATE Operator SET id=?, status=? WHERE id=?";
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.setString(2, status.toString());
            statement.setInt(3, id);
            int row = statement.executeUpdate();
            System.out.println(row);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM Operator WHERE id=?;";
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
