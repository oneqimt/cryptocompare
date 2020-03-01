package com.imt11.crypto.database;

import com.imt11.crypto.model.Holdings;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Dennis Miller
 */
public class HoldingsDAO {

    public HoldingsDAO(){

    }

    public int addHolding(Holdings holding) {

        int status = 0;
        try {
            DBManager db = new DBManager();
            Connection connection = db.createConnection();

            String sql = "INSERT INTO holdings values(?, ?, ?, ?, ?)";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, 0);
            ps.setInt(2, holding.getCoin_id());
            ps.setDouble(3, holding.getQuantity());
            ps.setDouble(4, holding.getCost());
            ps.setInt(5, holding.getPerson_id());

            status = ps.executeUpdate();
            ps.close();
            connection.close();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        return status;
    }


    public int getHoldingLastInsertedId() {
        int maxId = 0;
        try {
            DBManager db = new DBManager();
            Connection connection = db.createConnection();
            String sql = "SELECT MAX(holding_id) FROM holdings";
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

    public int deleteHolding(Holdings holding) {
        int status = 0;

        try {
            DBManager db = new DBManager();
            Connection connection = db.createConnection();
            String sql = "DELETE FROM holdings WHERE coin_id=? and person_id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, holding.getCoin_id());
            ps.setInt(2, holding.getPerson_id());

            status = ps.executeUpdate();
            ps.close();
            connection.close();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        return status;
    }

    public int updateHolding(Holdings holding) {
        int status = 0;

        try {
            DBManager db = new DBManager();
            Connection connection = db.createConnection();
            String sql = "UPDATE holdings SET quantity=?, cost=? WHERE coin_id=? AND person_id=?";
            PreparedStatement ps = connection.prepareStatement(sql);
            BigDecimal quantityBigDecimal = BigDecimal.valueOf(holding.getQuantity());
            BigDecimal costBigDecimal = BigDecimal.valueOf(holding.getCost());
            ps.setBigDecimal(1, quantityBigDecimal);
            ps.setBigDecimal(2, costBigDecimal);
            ps.setInt(3, holding.getCoin_id());
            ps.setInt(4, holding.getPerson_id());

            status = ps.executeUpdate();
            ps.close();
            connection.close();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        return status;
    }


    public Holdings checkIfHoldingExists(Holdings holding) {
        Holdings returnHolding = null;

        try {
            DBManager db = new DBManager();
            Connection connection = db.createConnection();
            String sql = "SELECT * FROM holdings WHERE coin_id =? AND person_id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, holding.getCoin_id());
            ps.setInt(2, holding.getPerson_id());

            ResultSet rs = ps.executeQuery();
            if (!rs.isBeforeFirst()) {
                System.out.println("OK HOLDING DOES NOT EXIST in DATABASE, ADD IT");
            } else {
                returnHolding = new Holdings();
                returnHolding.setHolding_id(rs.getInt("holding_id"));
                returnHolding.setCoin_id(rs.getInt("coin_id"));
                returnHolding.setQuantity(rs.getDouble("quantity"));
                returnHolding.setCost(rs.getDouble("cost"));
                returnHolding.setPerson_id(rs.getInt("person_id"));
            }

            ps.close();
            rs.close();
            connection.close();

        } catch (ClassNotFoundException | SQLException e) {
            e.getLocalizedMessage();
        }

        return returnHolding;
    }

    public Holdings getExistingHolding(Holdings holdings){
        Holdings myholding = new Holdings();
        try{
            DBManager db = new DBManager();
            Connection connection = db.createConnection();
            String sql = "SELECT * FROM holdings WHERE coin_id =? AND person_id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, holdings.getCoin_id());
            ps.setInt(2, holdings.getPerson_id());
            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                myholding.setHolding_id(rs.getInt("holding_id"));
                myholding.setCoin_id(rs.getInt("coin_id"));
                myholding.setQuantity(rs.getDouble("quantity"));
                myholding.setCost(rs.getDouble("cost"));
                myholding.setPerson_id(rs.getInt("person_id"));

            }

            rs.close();
            ps.close();
            connection.close();

        }catch (ClassNotFoundException | SQLException e) {
            e.getLocalizedMessage();
        }

        return myholding;
    }



}
