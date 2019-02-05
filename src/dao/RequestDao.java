package dao;

import entity.Request;
import entity.RequestStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RequestDao implements IDao<Request> {

    private static String connectUrl = MyConfig.setConfig("url");
    private static String user = MyConfig.setConfig("user");
    private static String password = MyConfig.setConfig("password");
    private Connection connection;
    private static RequestDao instance = null;

    public static RequestDao getInstance() {
        if (instance == null)
            instance = new RequestDao();
        return instance;
    }

    private RequestDao() {
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
        String sql = "CREATE TABLE IF NOT EXISTS Request (" +
                "id INTEGER PRIMARY KEY AUTO_INCREMENT NOT NULL," +
                "clientId INTEGER NOT NULL," +
                "creationTime TIMESTAMP NOT NULL," +
                "leadTime INTEGER NOT NULL," +
                "employee VARCHAR(20)," +
                "status VARCHAR(20) NOT NULL," +
                "endTime TIMESTAMP);";
        try (Statement statement = this.connection.createStatement()) {
            int row = statement.executeUpdate(sql);
            System.out.println(row);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void add(Request request) {
        String sql = "INSERT INTO Request (clientId, creationTime, leadTime, status)" + "VALUES (?, ?, ?, ?);";
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.setInt(1, request.getClientId());
            statement.setTimestamp(2, Timestamp.valueOf(request.getCreationTime()));
            statement.setInt(3, request.getLeadTime());
            statement.setString(4, request.getRequestStatus().name());
            int row = statement.executeUpdate();
            System.out.println(row);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Request> getAll() {
        String sql = "SELECT * FROM Request;";
        try (Statement statement = this.connection.createStatement()) {
            List<Request> requestList = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                Request request = new Request();
                request.setId(resultSet.getInt("id"));
                request.setId(resultSet.getInt("clientId"));
                request.setCreationTime(resultSet.getTimestamp("creationTime").toLocalDateTime());
                request.setLeadTime(resultSet.getInt("leadTime"));
                request.setEmployee(resultSet.getString("employee"));
                request.setRequestStatus(RequestStatus.valueOf(resultSet.getString("status")));
//                request.setEndTime(resultSet.getTimestamp("endTime").toLocalDateTime());
                requestList.add(request);
            }
            return requestList;

        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public Request getById(int id) {
        String sql = "SELECT * FROM Request WHERE id=? LIMIT 1;";
        Request request = new Request();
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            request.setId(id);
            request.setClientId(resultSet.getInt("clientId"));
            request.setCreationTime(resultSet.getTimestamp("creationTime").toLocalDateTime());
            request.setLeadTime(resultSet.getInt("leadTime"));
            request.setEmployee(resultSet.getString("employee"));
            request.setRequestStatus(RequestStatus.valueOf(resultSet.getString("status")));
//            request.setEndTime(resultSet.getTimestamp("endTime").toLocalDateTime());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return request;
    }

    public List<Request> getByClientId(int clientId) {
        String sql = "SELECT * FROM Request WHERE clientId=?;";
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.setInt(1, clientId);
            List<Request> requestList = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Request request = new Request();
                request.setId(resultSet.getInt("id"));
                request.setClientId(clientId);
                request.setCreationTime(resultSet.getTimestamp("creationTime").toLocalDateTime());
                request.setLeadTime(resultSet.getInt("leadTime"));
                request.setEmployee(resultSet.getString("employee"));
                request.setRequestStatus(RequestStatus.valueOf(resultSet.getString("status")));
//                request.setEndTime(resultSet.getTimestamp("endTime").toLocalDateTime());
                requestList.add(request);
            }
            return requestList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public Request getByStatus(RequestStatus status) {
        String sql = "SELECT * FROM Request WHERE status=? LIMIT 0,1;";
        Request request = new Request();
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.setString(1, status.toString());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                request.setId(resultSet.getInt("id"));
                request.setClientId(resultSet.getInt("clientId"));
                request.setCreationTime(resultSet.getTimestamp("creationTime").toLocalDateTime());
                request.setLeadTime(resultSet.getInt("leadTime"));
                request.setEmployee(resultSet.getString("employee"));
                request.setRequestStatus(RequestStatus.valueOf(resultSet.getString("status")));
//                request.setEndTime(resultSet.getTimestamp("endTime").toLocalDateTime());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return request;
    }

    @Override
    public <RequestStatus> void update(int id, RequestStatus status) {
        String sql = "UPDATE Request SET status=? WHERE id=?";
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.setString(1, status.toString());
            statement.setInt(2, id);
            int row = statement.executeUpdate();
            System.out.println(row);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM Request WHERE id=?;";
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Request> getAllByStatus(RequestStatus status) {
        String sql = "SELECT * FROM Request WHERE status=?;";
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.setString(1, status.toString());
            List<Request> requestList = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Request request = new Request();
                request.setId(resultSet.getInt("id"));
                request.setClientId(resultSet.getInt("clientId"));
                request.setCreationTime(resultSet.getTimestamp("creationTime").toLocalDateTime());
                request.setLeadTime(resultSet.getInt("leadTime"));
                request.setEmployee(resultSet.getString("employee"));
                request.setRequestStatus(RequestStatus.valueOf(resultSet.getString("status")));
//                request.setEndTime(resultSet.getTimestamp("endTime").toLocalDateTime());
                requestList.add(request);
            }
            return requestList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public  void setEmployee(int id, String employeeRole, int employeeId) {
        String sql = "UPDATE Request SET employee=? WHERE id=?";
        String employee = employeeRole + "_" + String.valueOf(employeeId);
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.setString(1, employeeRole + " " + employeeId);
            statement.setInt(2, id);
            int row = statement.executeUpdate();
            System.out.println(row);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
