package com.imt11.crypto.database;

import com.imt11.crypto.model.Auth;
import com.imt11.crypto.model.Coin;
import com.imt11.crypto.model.Person;
import com.imt11.crypto.model.State;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Dennis Miller
 */
public class DBManager {

	private Connection connection = null;

	//private final static String createTable = "CREATE TABLE `example` (id INT, data VARCHAR(100))";


	public DBManager(){}

	public Connection createConnection() throws IOException, ClassNotFoundException, SQLException {
		//String host="jdbc:mysql://node12626-env-4194466.fr-1.paas.massivegrid.net/crypto";
		String host="jdbc:mysql://185.44.64.238:3306/crypto";
		String username="denny1";
		String password="2zq46qlnGIWY1Rqq";
		String driver="com.mysql.jdbc.Driver";

		Class.forName(driver);
		System.out.println("--------------------------");
		System.out.println("DRIVER: " + driver);

		connection = DriverManager.getConnection(host, username, password);
		System.out.println("CONNECTION: " + connection);

		return connection;
	}

	public List<Coin> getAllCoins(){
		List<Coin> coins = new ArrayList<>();
		try{
			Connection connection = createConnection();

			String query = "SELECT * from coins";
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery(query);

			while(rs.next()){
				Coin coin = new Coin();
				coin.setCoin_id(rs.getInt("coin_id"));
				coin.setCoin_symbol(rs.getString("coin_symbol"));
				coin.setCoin_name(rs.getString("coin_name"));
				coins.add(coin);
			}

			statement.close();


		}catch (IOException ex) {
			Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		return coins;
	}

	public Auth getCredentials(String uname, String pass) throws IOException, ClassNotFoundException, SQLException{
			Auth auth = new Auth();
			Connection connection = createConnection();
			String query = "SELECT * FROM crypto.auth WHERE username=? AND password=?";
			//System.out.println("SQL STATEMENT is: "+ query);
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, uname);
			preparedStatement.setString(2, pass);

			System.out.println("Prepared Statement after bind variables set" +" "+preparedStatement.toString());

			ResultSet rs = preparedStatement.executeQuery();

			while(rs.next()){
				auth.setAuth_id(rs.getInt("auth_id"));
				auth.setPassword(rs.getString("password"));
				auth.setUsername(rs.getString("username"));
				auth.setPerson_id(rs.getInt("person_id"));

			}

			preparedStatement.close();

		return auth;
	}

	public Person getPerson(int person_id) throws IOException, ClassNotFoundException, SQLException{
		Connection connection = createConnection();
		String query = "SELECT auth.*, person.* FROM person\n" +
				"JOIN auth ON auth.person_id = person.person_id\n" +
				"WHERE person.person_id =" + person_id;
		Statement st = connection.createStatement();
		//  result set
		ResultSet rs = st.executeQuery(query);
		Person person=null;
		while(rs.next()){
			person = new Person();
			person.setPerson_id(rs.getInt("person_id"));
			person.setFirst_name(rs.getString("first_name"));
			person.setLast_name(rs.getString("last_name"));
		}

		return person;

	}

	public List<Person> getPersonCoins(int personId){
		List<Person> persons = new ArrayList<>();
		try{
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

			while(rs.next()){

				Person person = new Person();
				person.setPerson_id(rs.getInt("person_id"));
				person.setFirst_name(rs.getString("first_name"));
				person.setLast_name(rs.getString("last_name"));
				person.setQuantity(rs.getDouble("quantity"));
				person.setCost(rs.getDouble("cost"));

				State state = new State();
				state.setAbbreviation(rs.getString("abbreviation"));
				state.setId(rs.getInt("id"));
				person.setState(state);

				Coin coin = new Coin();
				coin.setCoin_id(rs.getInt("coin_id"));
				coin.setCoin_name(rs.getString("coin_name"));
				coin.setCoin_symbol(rs.getString("coin_symbol"));
				person.setCoin(coin);

				persons.add(person);
			}

			st.close();

		}catch (IOException ex) {
			Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		return persons;
	}

	public void runSqlStatement(){

		try {
			Connection connection = createConnection();
			//Statement statement = connection.createStatement();
			//boolean rs = statement.execute(createTable);
			// the mysql insert statement
			/*String query = " insert into example (id, data)"
					               + " values (?, ?)";*/

			// create the mysql insert preparedstatement
			/*PreparedStatement preparedStmt = connection.prepareStatement(query);
			preparedStmt.setInt (1, 3);
			preparedStmt.setString(2, "mycoolrecord");*/

			// execute the preparedstatement
			//preparedStmt.execute();

			//boolean test = statement.execute(insertTest);

		} catch (IOException ex) {
			Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}


	}

}
