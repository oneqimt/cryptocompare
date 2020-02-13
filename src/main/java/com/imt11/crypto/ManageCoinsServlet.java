package com.imt11.crypto;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.imt11.crypto.database.ManageCoinDAO;
import com.imt11.crypto.model.Coin;
import com.imt11.crypto.model.CoinMarketCapCoin;
import com.imt11.crypto.model.CoinMarketCapLatest;
import com.imt11.crypto.util.CryptoUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Dennis Miller
 */
@WebServlet(name = "ManageCoinsServlet", urlPatterns = {"/managecoins"})
public class ManageCoinsServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Gson gson = new Gson();
        ManageCoinDAO manageCoinDAO = new ManageCoinDAO();

        BufferedReader reader = request.getReader();
        Coin coin = gson.fromJson(reader, Coin.class);
        System.out.println("In ManageCoinsServlet and coin sent is: " + " " + coin.toString());

        String action = request.getParameter("action");
        //addcoin
        if (action.equalsIgnoreCase(CryptoUtil.ADD_COIN)){
            Boolean doesExist = manageCoinDAO.checkIfCoinExists(coin);
            if (!doesExist){
                // add it
                int status = manageCoinDAO.insertCoin(coin);
                if (status > 0) {
                    System.out.println("ADDED COIN");
                    String str = gson.toJson(coin);
                    out.print(str);
                }
            }else{
                response.sendError(403);
            }

        }
        out.flush();
        out.close();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String slug = request.getParameter("slug");
        String responseStr = "";
        ManageCoinDAO manageCoinDAO = new ManageCoinDAO();
        Gson gson = new Gson();

        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        if (slug != null){
            // GETS a SINGLE COIN FROM COINMARKETCAP
            System.out.println("SLUG IS NOT NULL");
            try {
                responseStr = manageCoinDAO.getCoinFromCoinMarketCap(2, slug);
            } catch (URISyntaxException | IOException e) {
                e.printStackTrace();
            }
            // we can parse this ourselves
            JsonParser parser = new JsonParser();
            JsonElement jsonTree = parser.parse(responseStr);
            Coin coin = new Coin();
            if(jsonTree.isJsonObject()){
                JsonObject jsonObject = jsonTree.getAsJsonObject();
                JsonElement dataObj = jsonObject.get("data");
                if (dataObj != null && dataObj.isJsonObject()){
                    JsonObject mydata = dataObj.getAsJsonObject();
                    Set<Map.Entry<String, JsonElement>> entrySet = mydata.entrySet();
                    for(Map.Entry entry : entrySet){
                        JsonObject valueobj = (JsonObject) entry.getValue();
                        JsonElement name = valueobj.get("name");
                        JsonElement id = valueobj.get("id");
                        JsonElement symbol = valueobj.get("symbol");
                        JsonElement myslug = valueobj.get("slug");

                        coin.setCoin_id(0);
                        coin.setCoin_name(name.getAsString());
                        coin.setCoin_symbol(symbol.getAsString());
                        coin.setCmc_id(Integer.parseInt(id.toString()));
                        coin.setSlug(myslug.getAsString());
                    }

                }

            }

            String mycoin = gson.toJson(coin);
            out.print(mycoin);


        }else{
            // GET the LATEST COINS FROM COINMARKETCAP
            try {
                responseStr = manageCoinDAO.getLatestFromCoinMarketCap(2);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            CoinMarketCapLatest coinMarketCapLatest = gson.fromJson(responseStr, CoinMarketCapLatest.class);
            List<CoinMarketCapCoin> data = coinMarketCapLatest.getData();
            List<Coin> coins = new ArrayList<>();
            for (CoinMarketCapCoin marketCapCoin : data) {
                Coin coin = new Coin();
                coin.setCoin_id(0);
                coin.setCmc_id(marketCapCoin.getId());
                coin.setSlug(marketCapCoin.getSlug());
                coin.setCoin_symbol(marketCapCoin.getSymbol().trim());
                coin.setCoin_name(marketCapCoin.getName());
                coins.add(coin);

            }

            String lastestcoins = gson.toJson(coins);
            out.print(lastestcoins);
        }
        out.flush();
        out.close();

    }
}
