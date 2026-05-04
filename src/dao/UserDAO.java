package dao;

import dto.User;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public User findByUsername(String username) throws Exception {
        String sql = "SELECT * FROM users WHERE username = ?";
        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);

        ps.setString(1, username);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getInt("role"),
                    (Integer) rs.getObject("emp_id")
            );
        }
        return null;
    }
    //add user

    public void createUser(User user) throws Exception {
        String sql = "INSERT INTO users(username, password, role, emp_id) VALUES (?, ?, ?, ?)";

        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);

        ps.setString(1, user.getUsername());
        ps.setString(2, user.getPassword());
        ps.setInt(3, user.getRole());
        if (user.getEmpId() == null) ps.setNull(4, Types.INTEGER);
        else ps.setInt(4, user.getEmpId());

        ps.executeUpdate();
    }

    public List<User> findAll() throws Exception {
        List<User> list = new ArrayList<>();
        String sql = "SELECT u.*, e.emp_name AS emp_name " +
                "FROM users u " +
                "LEFT JOIN employees e ON u.emp_id = e.id";

        Connection conn = DBConnection.getConnection();
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            User u = new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getInt("role"),
                    (Integer) rs.getObject("emp_id")
            );

// 🔥 THÊM DÒNG NÀY
            u.setEmpName(rs.getString("emp_name"));

            list.add(u);
        }
        return list;
    }

    public void deleteUser(int id) throws Exception {
        String sql = "DELETE FROM users WHERE id = ?";

        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);

        ps.setInt(1, id);
        ps.executeUpdate();
    }
}
