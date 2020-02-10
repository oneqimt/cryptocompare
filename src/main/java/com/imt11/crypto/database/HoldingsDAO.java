package com.imt11.crypto.database;

import com.imt11.crypto.model.Holdings;
import com.imt11.crypto.util.HoldingsUtil;
import com.imt11.crypto.util.SecurityUtil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dennis Miller
 */
public class HoldingsDAO {

    // get coin slugs from coin market cap API

    public String getLatestFromCoinMarketCap() throws URISyntaxException, IOException {
        String responseContent = "";
        String testApiKey = SecurityUtil.getInstance().getCoinMarketCapTestApiKey();
        String prodApiKey = SecurityUtil.getInstance().getCoinMarketCapProdApiKey();

        List<NameValuePair> parameters = new ArrayList<>();
        parameters.add(new BasicNameValuePair("start", "1"));
        parameters.add(new BasicNameValuePair("limit", "200"));
        parameters.add(new BasicNameValuePair("convert", "USD"));

        // 1 = prod 2 = test
        String uri = HoldingsUtil.getCoinMarketCapEndpoint(2);
        URIBuilder query = new URIBuilder(uri);
        query.addParameters(parameters);

        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet request = new HttpGet(query.build());

        request.setHeader(HttpHeaders.ACCEPT, "application/json");
        request.addHeader("X-CMC_PRO_API_KEY", testApiKey);

        try (CloseableHttpResponse response = client.execute(request)) {
            System.out.println("LATEST FROM COINMARKETCAP RESPONSE IS: " + " " + response.getStatusLine());
            HttpEntity entity = response.getEntity();
            responseContent = EntityUtils.toString(entity);
            EntityUtils.consume(entity);
        }

        return responseContent;
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
            String sql = "UPDATE holdings SET holding_id=?, quantity=?, cost=? WHERE coin_id=? AND person_id=?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, holding.getHolding_id());
            ps.setDouble(2, holding.getQuantity());
            ps.setDouble(3, holding.getCost());
            ps.setInt(4, holding.getCoin_id());
            ps.setInt(5, holding.getPerson_id());

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

}
