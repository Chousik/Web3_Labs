package ru.chousik.web3_tomee;

import ru.chousik.web3_tomee.models.Point;

import java.sql.*;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        try {
            Class.forName("oracle.jdbc.OracleDriver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(CREATE_TABLE)) {
            statement.execute();

        } catch (SQLException e) {
            System.out.println("dsdsd");
            System.err.println(e);
        }
    }
    public void addPoint(Point point) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(ADD_POINT)) {

            stmt.setDouble(1, point.getX());
            stmt.setDouble(2, point.getY());
            stmt.setDouble(3, point.getR());
            stmt.setInt(4, point.isInFlag() ? 1 : 0);
            stmt.setString(5, point.getTime());
            stmt.setLong(6, point.getExecutionTime());
            stmt.setString(7, point.getSessionId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public ArrayList<Point> getPoints(String sessionId) {
        ArrayList<Point> points = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(GET_POINTS)) {
            statement.setString(1, sessionId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Point point = new Point();
                    point.setId(resultSet.getInt("id"));
                    point.setX(resultSet.getDouble("x"));
                    point.setY(resultSet.getDouble("y"));
                    point.setR(resultSet.getDouble("r"));
                    point.setInFlag(resultSet.getBoolean("in_flag"));
                    point.setTime(resultSet.getString("time"));
                    point.setExecutionTime(resultSet.getLong("execution_time"));
                    point.setSessionId(resultSet.getString("session_id"));
                    points.add(point);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return points;
    }
    private static final String CREATE_TABLE = """
        
            CREATE TABLE points (
                                    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, -- Corrected the identity syntax
                                    x NUMBER(10, 2) NOT NULL,
                                    y NUMBER(10, 2) NOT NULL,
                                    r NUMBER(10, 2) NOT NULL,
                                    in_flag NUMBER(1) NOT NULL, -- Representing boolean as 1 (true) or 0 (false)
                                    time VARCHAR2(255) NOT NULL,
                                    execution_time NUMBER NOT NULL,
                                    session_id VARCHAR2(255)
                                );
        """;

    private static final String ADD_POINT = """
        INSERT INTO points (x, y, r, in_flag, time, execution_time, session_id)\s
        VALUES (?, ?, ?, ?, ?, ?, ?);
    """;
    private static final String GET_POINTS = """
                SELECT * FROM points WHERE session_id = ?;
            """;
    private static final String USER = "SYSTEM";
    private static final String PASSWORD = "Oracle_123";
    private static final String DB_URL = "jdbc:oracle:thin:@//localhost:1521/FREE";
}
