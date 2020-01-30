package com.imt11.crypto.database;

import com.imt11.crypto.model.Coin;
import com.imt11.crypto.model.Holdings;
import com.imt11.crypto.model.Person;
import com.imt11.crypto.model.State;
import com.imt11.crypto.model.TotalValues;
import com.imt11.crypto.util.SecurityUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dennis Miller
 */
public class DBManager {

    public DBManager() {
    }

    public Connection createConnection() throws ClassNotFoundException, SQLException {

        Connection connection;

        String host = SecurityUtil.getInstance().getHost();
        String username = SecurityUtil.getInstance().getUsername();
        String password = SecurityUtil.getInstance().getPassword();
        String driver = SecurityUtil.getInstance().getDriver();

        Class.forName(driver);
        System.out.println("--------------------------");
        System.out.println("DRIVER: " + driver);

        connection = DriverManager.getConnection(host, username, password);
        System.out.println("CONNECTION: " + connection);

        return connection;
    }

    public void updateGrandTotals(int person_id, TotalValues totalValues) {

        try {
            Connection connection = createConnection();
            PreparedStatement ps = connection.prepareStatement("UPDATE grand_totals SET total_cost = ?, " +
                    "total_value = ?, total_change = ?, increase_decrease = ? WHERE person_id = ?");

            ps.setString(1, totalValues.getTotalCost());
            ps.setString(2, totalValues.getTotalValue());
            ps.setString(3, totalValues.getTotalPercentageIncreaseDecrease());
            ps.setString(4, totalValues.getIncreaseDecrease());
            ps.setInt(5, person_id);

            ps.executeUpdate();
            ps.close();
            connection.close();


        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }

    }

    public TotalValues getTotalValues(int personId) {
        TotalValues totalValues = new TotalValues();
        try {
            Connection connection = createConnection();

            String query = "SELECT * from grand_totals WHERE person_id=" + personId;
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()) {
                totalValues.setTotalCost(rs.getString("total_cost"));
                totalValues.setTotalValue(rs.getString("total_value"));
                totalValues.setTotalPercentageIncreaseDecrease(rs.getString("total_change"));
                totalValues.setIncreaseDecrease(rs.getString("increase_decrease"));
                totalValues.setPersonId(rs.getInt("person_id"));
            }

            statement.close();
            rs.close();
            connection.close();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        return totalValues;
    }



    public List<Person> getPersonCoins(int personId) {
        List<Person> persons = new ArrayList<>();
        try {
            Connection connection = createConnection();
            // query
            String query = "SELECT person.first_name, person.last_name, person.person_id, holdings.quantity,\n" +
                    "  holdings.cost, coins.*, state.id, state.abbreviation\n" +
                    "FROM person\n" +
                    "JOIN holdings ON holdings.person_id = person.person_id\n" +
                    "JOIN coins ON holdings.coin_id = coins.coin_id\n" +
                    "JOIN state ON state.id = person.state_id\n" +
                    "WHERE person.person_id =" + personId;
            // create java statement
            Statement st = connection.createStatement();
            //  result set
            ResultSet rs = st.executeQuery(query);

            while (rs.next()) {

                Person person = new Person();
                person.setPerson_id(rs.getInt("person_id"));
                person.setFirst_name(rs.getString("first_name"));
                person.setLast_name(rs.getString("last_name"));

                Holdings holdings = new Holdings();
                holdings.setQuantity(rs.getDouble("quantity"));
                holdings.setCost(rs.getDouble("cost"));
                person.setHoldings(holdings);
                /*person.setQuantity(rs.getDouble("quantity"));
                person.setCost(rs.getDouble("cost"));
*/
                State state = new State();
                state.setAbbreviation(rs.getString("abbreviation"));
                state.setId(rs.getInt("id"));
                person.setState(state);

                Coin coin = new Coin();
                coin.setCoin_id(rs.getInt("coin_id"));
                coin.setCoin_name(rs.getString("coin_name"));
                coin.setCoin_symbol(rs.getString("coin_symbol"));
                coin.setName_id(rs.getString("name_id"));
                person.setCoin(coin);

                //TODO add holdings...

                persons.add(person);
            }

            st.close();
            rs.close();
            connection.close();

        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }

        return persons;
    }

}
