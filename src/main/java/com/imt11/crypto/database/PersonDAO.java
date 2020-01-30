package com.imt11.crypto.database;

import com.imt11.crypto.model.Person;
import com.imt11.crypto.model.State;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Dennis Miller
 */
public class PersonDAO {

    public int savePerson(Person person) {
        int status = 0;

        try {
            DBManager db = new DBManager();
            Connection connection = db.createConnection();

            String sql = "INSERT INTO person values(?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, 0);
            ps.setString(2, person.getFirst_name());
            ps.setString(3, person.getLast_name());
            ps.setString(4, person.getEmail());
            ps.setString(5, person.getPhone());
            ps.setString(6, person.getAddress());
            ps.setString(7, person.getCity());
            ps.setInt(8, person.getState().getId());
            ps.setString(9, person.getZip());

            status = ps.executeUpdate();
            ps.close();
            connection.close();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        return status;
    }

    public int deletePerson(Person person) {
        int status = 0;

        try {
            DBManager db = new DBManager();
            Connection connection = db.createConnection();
            String sql = "DELETE FROM person WHERE person_id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, person.getPerson_id());

            status = ps.executeUpdate();
            ps.close();
            connection.close();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        return status;
    }

    public int updatePerson(Person person) {
        int status = 0;

        try {
            DBManager db = new DBManager();
            Connection connection = db.createConnection();
            String sql = "UPDATE person SET first_name=?, last_name=?, email=?, phone=?, address=?, city=?, state_id=?, zip=? WHERE person_id=?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, person.getFirst_name());
            ps.setString(2, person.getLast_name());
            ps.setString(3, person.getEmail());
            ps.setString(4, person.getPhone());
            ps.setString(5, person.getAddress());
            ps.setString(6, person.getCity());
            ps.setInt(7, person.getState().getId());
            ps.setString(8, person.getZip());
            ps.setInt(9, person.getPerson_id());

            status = ps.executeUpdate();
            ps.close();
            connection.close();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();

        }

        return status;
    }

    public Person getPersonByEmail(String email) {
        Person person = null;

        try {
            DBManager db = new DBManager();
            Connection connection = db.createConnection();
            String sql = "SELECT * FROM person WHERE email =?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, email);

            ResultSet rs = ps.executeQuery();
            if (!rs.isBeforeFirst()) {
                System.out.println("OK PERSON DOES NOT EXIST in DATABASE, ADD THEM");
            } else {
                person = new Person();
                person.setPerson_id(rs.getInt(1));
                person.setFirst_name(rs.getString(2));
                person.setLast_name(rs.getString(3));
                person.setEmail(rs.getString(4));
                person.setPhone(rs.getString(5));
                person.setAddress(rs.getString(6));
                person.setCity(rs.getString(7));
                State state = new State();
                state.setId(rs.getInt(8));
                person.setState(state);
                person.setZip(rs.getString(9));
            }

            ps.close();
            rs.close();
            connection.close();

        } catch (ClassNotFoundException | SQLException e) {
            e.getLocalizedMessage();
        }

        return person;
    }

    public Person getPersonById(int personId) {
        Person person = new Person();
        try {
            DBManager db = new DBManager();
            Connection connection = db.createConnection();


            String sql = new StringBuilder().append("SELECT person.person_id,\n").append("       person.first_name,\n").append("       person.last_name,\n").append("       person.email,\n").append("       person.phone,\n").append("       person.address,\n").append("       person.city,\n").append("       person.zip,\n").append("       state.id,\n").append("       state.name,\n").append("       state.country,\n").append("       state.abbreviation\n").append("FROM person\n").append("         JOIN state ON state.id = person.state_id\n").append("WHERE person.person_id =?").toString();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, personId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                person.setPerson_id(rs.getInt(1));
                person.setFirst_name(rs.getString(2));
                person.setLast_name(rs.getString(3));
                person.setEmail(rs.getString(4));
                person.setPhone(rs.getString(5));
                person.setAddress(rs.getString(6));
                person.setCity(rs.getString(7));
                person.setZip(rs.getString(8));
                State state = new State();
                state.setId(rs.getInt(9));
                state.setName(rs.getString(10));
                state.setCountry(rs.getString(11));
                state.setAbbreviation(rs.getString(12));
                person.setState(state);


            }
            ps.close();
            rs.close();
            connection.close();


        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();

        }

        return person;
    }

    public int getPersonLastInsertedId() {
        int maxId = 0;
        try {
            DBManager db = new DBManager();
            Connection connection = db.createConnection();
            String sql = "SELECT MAX(person_id) FROM person";
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
