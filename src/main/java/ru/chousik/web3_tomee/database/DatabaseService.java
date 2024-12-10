package ru.chousik.web3_tomee.database;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import ru.chousik.web3_tomee.models.Point;

import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;

@Named
@ApplicationScoped
public class DatabaseService implements Serializable {

    public void addPoint(Point point) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {

            String query = String.format(
                    "INSERT INTO points (x, y, r, in_flag, time, execution_time, session_id) " +
                            "VALUES (%f, %f, %f, %d, '%s', %d, '%s')",
                    point.getX(), point.getY(), point.getR(),
                    point.isInFlag() ? 1 : 0,
                    point.getTime(), point.getExecutionTime(), point.getSessionId()
            );

            stmt.executeUpdate(query);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Point> getPoints(String sessionId) {
        ArrayList<Point> points = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {

            String query = String.format("SELECT * FROM points WHERE session_id = '%s'", sessionId);
            try (ResultSet resultSet = statement.executeQuery(query)) {
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
        id NUMBER PRIMARY KEY,
        x NUMBER NOT NULL,
        y NUMBER NOT NULL,
        r NUMBER NOT NULL,
        in_flag NUMBER(1) NOT NULL,
        time VARCHAR2(255) NOT NULL,
        execution_time NUMBER NOT NULL,
        session_id VARCHAR2(255)
    );
    """;

    private static final String USER = "SYSTEM";
    private static final String PASSWORD = "Oracle_123";
    private static final String DB_URL = "jdbc:oracle:thin:@//db:1521/FREE";

    static {
        try {
            Class.forName("oracle.jdbc.OracleDriver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {
            statement.execute(CREATE_TABLE);

        } catch (SQLException e) {
            System.err.println(e);
        }
    }
}
