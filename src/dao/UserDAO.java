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
        String sql = "SELECT * FROM users";

        Connection conn = DBConnection.getConnection();
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            list.add(new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getInt("role"),
                    (Integer) rs.getObject("emp_id")
            ));
        }
        return list;
    }
}
