package qa.autogate.db;

import qa.autogate.config.QaConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DatabaseManager {

    private DatabaseManager() {}

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(QaConfig.getJdbcUrl());
    }

    public static int getOrderCount() {
        String sql = "SELECT COUNT(*) FROM orders";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error getting order count: " + e.getMessage(), e);
        }
        return 0;
    }

    public static Map<String, Object> getOrderByNumber(String orderNumber) {
        String sql = "SELECT * FROM orders WHERE order_number = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, orderNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rowToMap(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error getting order by number: " + e.getMessage(), e);
        }
        return null;
    }

    public static Map<String, Object> getOrderById(long id) {
        String sql = "SELECT * FROM orders WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rowToMap(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error getting order by id: " + e.getMessage(), e);
        }
        return null;
    }

    public static List<Map<String, Object>> getOrderItems(long orderId) {
        String sql = "SELECT * FROM order_items WHERE order_id = ? ORDER BY id ASC";
        List<Map<String, Object>> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(rowToMap(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error getting order items: " + e.getMessage(), e);
        }
        return list;
    }

    public static Map<String, Object> getProductById(int id) {
        String sql = "SELECT * FROM products WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rowToMap(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error getting product by id: " + e.getMessage(), e);
        }
        return null;
    }

    private static Map<String, Object> rowToMap(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int colCount = meta.getColumnCount();
        Map<String, Object> map = new HashMap<>();
        for (int i = 1; i <= colCount; i++) {
            map.put(meta.getColumnName(i), rs.getObject(i));
        }
        return map;
    }
}
