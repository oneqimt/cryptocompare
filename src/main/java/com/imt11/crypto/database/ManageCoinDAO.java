package com.imt11.crypto.database;

import com.imt11.crypto.model.Coin;
import com.imt11.crypto.util.ManageCoinUtil;
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

import static com.imt11.crypto.util.ManageCoinUtil.checkUpdateCounts;

/**
 * @author Dennis Miller
 */
public class ManageCoinDAO {

    public String getLatestFromCoinMarketCap(int flag) throws URISyntaxException, IOException {
        String responseContent;
        String testApiKey = SecurityUtil.getInstance().getCoinMarketCapTestApiKey();
        String prodApiKey = SecurityUtil.getInstance().getCoinMarketCapProdApiKey();

        int prodOrTest = 0;
        String apiKey = "";

        List<NameValuePair> parameters = new ArrayList<>();
        parameters.add(new BasicNameValuePair("start", "1"));// start at 250 to populate database
        parameters.add(new BasicNameValuePair("limit", "199"));
        parameters.add(new BasicNameValuePair("convert", "USD"));

        switch (flag) {
            case 1:
                prodOrTest = 1;
                apiKey = prodApiKey;
                break;

            case 2:
                prodOrTest = 2;
                apiKey = testApiKey;
                break;

        }

        String uri = ManageCoinUtil.getCoinMarketCapEndpoint(prodOrTest);
        URIBuilder query = new URIBuilder(uri);
        query.addParameters(parameters);

        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet request = new HttpGet(query.build());

        request.setHeader(HttpHeaders.ACCEPT, "application/json");
        request.addHeader("X-CMC_PRO_API_KEY", apiKey);

        try (CloseableHttpResponse response = client.execute(request)) {
            System.out.println("LATEST FROM COINMARKETCAP RESPONSE IS: " + " " + response.getStatusLine());
            HttpEntity entity = response.getEntity();
            responseContent = EntityUtils.toString(entity);
            EntityUtils.consume(entity);
        }

        return responseContent;
    }

    public String getCoinFromCoinMarketCap(int flag, String slug) throws URISyntaxException, IOException {
        String responseContent;
        String testApiKey = SecurityUtil.getInstance().getCoinMarketCapTestApiKey();
        String prodApiKey = SecurityUtil.getInstance().getCoinMarketCapProdApiKey();
        int prodOrTest = 0;
        String apiKey = "";

        List<NameValuePair> parameters = new ArrayList<>();
        parameters.add(new BasicNameValuePair("slug", slug));

        switch (flag) {
            case 1:
                prodOrTest = 1;
                apiKey = prodApiKey;
                break;

            case 2:
                prodOrTest = 2;
                apiKey = testApiKey;
                break;

        }

        String uri = ManageCoinUtil.getSingleCoinCoinMarketCapEndpoint(prodOrTest);
        URIBuilder query = new URIBuilder(uri);
        query.addParameters(parameters);

        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet request = new HttpGet(query.build());

        request.setHeader(HttpHeaders.ACCEPT, "application/json");
        request.addHeader("X-CMC_PRO_API_KEY", apiKey);

        try (CloseableHttpResponse response = client.execute(request)) {
            System.out.println("SINGLE COIN FROM COINMARKETCAP RESPONSE IS: " + " " + response.getStatusLine());
            HttpEntity entity = response.getEntity();
            responseContent = EntityUtils.toString(entity);
            EntityUtils.consume(entity);
        }

        return responseContent;
    }

    // SINGLE INSERT
    public int insertCoin(Coin coin){
        int status = 0;
        try{
            DBManager db = new DBManager();
            Connection connection = db.createConnection();
            String sql = "INSERT INTO coins values(?, ?, ?, ?, ?,?,?)";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, coin.getCoin_id());
            ps.setString(2, coin.getCoin_name());
            ps.setString(3, coin.getCoin_symbol());
            ps.setInt(4, coin.getCmc_id());
            ps.setString(5, coin.getSlug());
            ps.setBigDecimal(6, coin.getMarket_cap());
            ps.setInt(7, coin.getCmc_rank());

            status = ps.executeUpdate();
            ps.close();
            connection.close();

        }catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        return status;
    }

    public int getCoinLastInsertedId() {
        int maxId = 0;
        try {
            DBManager db = new DBManager();
            Connection connection = db.createConnection();
            String sql = "SELECT MAX(coin_id) FROM coins";
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


    // BATCH INSERT
    public int[] insertCoins(List<Coin> coins) {
        int[] insertCounts = new int[0];

        try {
            DBManager db = new DBManager();
            Connection connection = db.createConnection();
            connection.setAutoCommit(false);
            String sql = "INSERT INTO coins values(?, ?, ?, ?, ?,?,?)";
            PreparedStatement ps = connection.prepareStatement(sql);

            for (Coin coin : coins) {
                ps.setInt(1, coin.getCoin_id());// zero, will be auto increment
                ps.setString(2, coin.getCoin_name());
                ps.setString(3, coin.getCoin_symbol());
                ps.setInt(4, coin.getCmc_id());
                ps.setString(5, coin.getSlug());
                ps.setBigDecimal(6, coin.getMarket_cap());
                ps.setInt(7, coin.getCmc_rank());
                ps.addBatch();
            }

            insertCounts = ps.executeBatch();

            Boolean noErrors = ManageCoinUtil.checkUpdateCounts(insertCounts);
            if (noErrors) {
                connection.commit();
            }

            ps.close();
            connection.close();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        return insertCounts;
    }

    // Cuurent Coins in database
    public List<Coin> getCurrentCoins() {
        List<Coin> currentCoins = new ArrayList<>();

        try {
            DBManager db = new DBManager();
            Connection connection = db.createConnection();
            String sql = "SELECT * FROM coins";
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            while (rs.next()) {
                Coin coin = new Coin();
                coin.setCoin_id(rs.getInt("coin_id"));
                coin.setSlug(rs.getString("slug"));
                coin.setCmc_id(rs.getInt("cmc_id"));
                coin.setCoin_name(rs.getString("coin_name"));
                coin.setCoin_symbol(rs.getString("coin_symbol"));
                coin.setMarket_cap(rs.getBigDecimal("market_cap"));
                coin.setCmc_rank(rs.getInt("cmc_rank"));

                currentCoins.add(coin);

            }

            rs.close();
            statement.close();
            connection.close();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        return currentCoins;
    }

    public Coin getCoinByCmcId(int cmcId){
        Coin coin = new Coin();
        try{
            DBManager db = new DBManager();
            Connection connection = db.createConnection();
            String sql = "SELECT * FROM coins WHERE cmc_id=?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, cmcId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                coin.setCoin_id(rs.getInt("coin_id"));
                coin.setCoin_name(rs.getString("coin_name"));
                coin.setCoin_symbol(rs.getString("coin_symbol"));
                coin.setCmc_id(rs.getInt("cmc_id"));
                coin.setSlug(rs.getString("slug"));
                coin.setMarket_cap(rs.getBigDecimal("market_cap"));
                coin.setCmc_rank(rs.getInt("cmc_rank"));
            }

            rs.close();
            ps.close();
            connection.close();

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return coin;
    }

    public Coin getCoinFromDatabase(int coinId){
        Coin coin = new Coin();
        try{
            DBManager db = new DBManager();
            Connection connection = db.createConnection();
            String sql = "SELECT * FROM coins WHERE coin_id=?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, coinId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                coin.setCoin_id(rs.getInt("coin_id"));
                coin.setCoin_name(rs.getString("coin_name"));
                coin.setCoin_symbol(rs.getString("coin_symbol"));
                coin.setCmc_id(rs.getInt("cmc_id"));
                coin.setSlug(rs.getString("slug"));
                coin.setMarket_cap(rs.getBigDecimal("market_cap"));
                coin.setCmc_rank(rs.getInt(rs.getInt("cmc_rank")));
            }

            rs.close();
            ps.close();
            connection.close();

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return coin;
    }

    public Boolean checkIfCoinExists(int cmc_id) {
        boolean doesExist = false;

        try {
            DBManager db = new DBManager();
            Connection connection = db.createConnection();
            String sql = "SELECT * FROM coins WHERE cmc_id =?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, cmc_id);

            ResultSet rs = ps.executeQuery();
            if (!rs.isBeforeFirst()) {
                System.out.println("NO DATA NO COIN in DATABASE");
                doesExist = false;
            } else {
                doesExist = true;
            }
            ps.close();
            rs.close();
            connection.close();

        } catch (ClassNotFoundException | SQLException e) {
            e.getLocalizedMessage();
        }
        return doesExist;

    }

    public int updateCoin(Coin coin) {
        int status = 0;

        try {
            DBManager db = new DBManager();
            Connection connection = db.createConnection();
            String sql = "UPDATE coins SET market_cap=?, cmc_rank=? WHERE cmc_id=?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setBigDecimal(1, coin.getMarket_cap());
            ps.setInt(2, coin.getCmc_rank());
            ps.setInt(3, coin.getCmc_id());
            status = ps.executeUpdate();
            ps.close();
            connection.close();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        return status;
    }

    // NOTE this is TEMPORARY
    public int[] updateCurrentCoins(List<Coin> coins) {
        int[] updateCounts = new int[0];

        DBManager db = new DBManager();
        String sql = "UPDATE coins SET cmc_id = ? , slug = ? WHERE coin_symbol=?";
        try {
            Connection connection = db.createConnection();
            connection.setAutoCommit(false);
            PreparedStatement ps = connection.prepareStatement(sql);
            for (Coin coin : coins) {
                ps.setInt(1, coin.getCmc_id());
                ps.setString(2, coin.getSlug());
                ps.setString(3, coin.getCoin_symbol());
                ps.addBatch();
            }

            updateCounts = ps.executeBatch();

            Boolean noErrors = checkUpdateCounts(updateCounts);

            if (noErrors) {
                //java.sql.SQLException: Can't call commit when autocommit=true
                connection.commit();
            }
            ps.close();
            connection.close();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        return updateCounts;
    }

}
