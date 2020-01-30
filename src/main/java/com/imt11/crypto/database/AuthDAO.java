package com.imt11.crypto.database;

import com.imt11.crypto.model.Auth;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Dennis Miller
 */
public class AuthDAO {

    public Auth getCredentials(String uname, String pass) throws ClassNotFoundException, SQLException {
        Auth auth = new Auth();
        DBManager db = new DBManager();
        Connection connection = db.createConnection();
        String query = "SELECT * FROM crypto.auth WHERE username=? AND password=?";

        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, uname);
        preparedStatement.setString(2, pass);

        ResultSet rs = preparedStatement.executeQuery();

        while (rs.next()) {
            auth.setAuth_id(rs.getInt("auth_id"));
            auth.setPassword(rs.getString("password"));
            auth.setUsername(rs.getString("username"));
            auth.setPerson_id(rs.getInt("person_id"));

        }
        preparedStatement.close();
        rs.close();
        connection.close();

        return auth;
    }

    public int saveAuth(Auth auth) {
        int status = 0;

        try {
            DBManager db = new DBManager();
            Connection connection = db.createConnection();
            String sql = "INSERT INTO auth values(?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, 0);
            ps.setString(2, auth.getUsername());
            ps.setString(3, auth.getPassword());
            ps.setInt(4, auth.getPerson_id());
            ps.setString(5, auth.getRole());
            ps.setInt(6, auth.getEnabled());

            status = ps.executeUpdate();

            ps.close();
            connection.close();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return status;
    }

    public int updateAuth(Auth auth) {
        int status = 0;

        try {
            DBManager db = new DBManager();
            Connection connection = db.createConnection();
            String sql = "UPDATE auth SET username=?, password=? WHERE person_id=?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, auth.getUsername());
            ps.setString(2, auth.getPassword());
            ps.setInt(3, auth.getPerson_id());

            status = ps.executeUpdate();

            ps.close();
            connection.close();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();

        }

        return status;
    }

    public int deleteAuth(Auth auth) {
        int status = 0;

        try {
            DBManager db = new DBManager();
            Connection connection = db.createConnection();
            String sql = "DELETE FROM auth WHERE person_id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, auth.getPerson_id());

            status = ps.executeUpdate();

            ps.close();
            connection.close();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        return status;
    }

    public Auth getAuthById(int personId) {
        Auth auth = new Auth();
        try {
            DBManager db = new DBManager();
            Connection connection = db.createConnection();
            String sql = "SELECT * FROM auth WHERE person_id=?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, personId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                auth.setAuth_id(rs.getInt(1));
                auth.setUsername(rs.getString(2));
                auth.setPassword(rs.getString(3));
                auth.setPerson_id(rs.getInt(4));
                auth.setRole(rs.getString(5));
                auth.setEnabled(rs.getInt(6));
            }
            ps.close();
            rs.close();
            connection.close();


        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();

        }

        return auth;
    }

    public int getAuthLastInsertedId() {
        int maxId = 0;
        try {
            DBManager db = new DBManager();
            Connection connection = db.createConnection();
            String sql = "SELECT MAX(auth_id) FROM auth";
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            if (rs.next()) {
                maxId = rs.getInt(1);
            }
            statement.close();
            rs.close();
            connection.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }


        return maxId;
    }

}
