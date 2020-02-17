package com.imt11.crypto;

import com.google.gson.Gson;
import com.imt11.crypto.database.ManageCoinDAO;
import com.imt11.crypto.model.Coin;
import com.imt11.crypto.model.CoinMarketCapCoin;
import com.imt11.crypto.model.CoinMarketCapLatest;
import com.imt11.crypto.util.CryptoUtil;
import com.imt11.crypto.util.ManageCoinUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

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
        if (action.equalsIgnoreCase(CryptoUtil.ADD_COIN)) {
            Boolean doesExist = manageCoinDAO.checkIfCoinExists(coin);
            if (!doesExist) {
                // add it
                int status = manageCoinDAO.insertCoin(coin);
                if (status > 0) {
                    System.out.println("ADDED COIN");
                    String str = gson.toJson(coin);
                    out.print(str);
                }
            } else {
                response.sendError(403);
            }

        }
        out.flush();
        out.close();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String action = request.getParameter("action");
        String slug = request.getParameter("slug");
        String coinid = request.getParameter("coinid");

        String responseStr = "";
        ManageCoinDAO manageCoinDAO = new ManageCoinDAO();
        Gson gson = new Gson();

        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        if (action.equalsIgnoreCase(CryptoUtil.SINGLE_CMC_COIN)) {
            // GETS a SINGLE COIN FROM COINMARKETCAP
            System.out.println("GET SINGLE CMC COIN and slug is: " + " " + slug);
            try {
                responseStr = manageCoinDAO.getCoinFromCoinMarketCap(2, slug);
            } catch (URISyntaxException | IOException e) {
                e.printStackTrace();
            }
            // we can parse this ourselves
            // the reason for this is that the "data" object has the id as the node
            Coin coin = ManageCoinUtil.parseSingleCoin(responseStr);
            String coinstr = gson.toJson(coin);
            out.print(coinstr);

        } else if (action.equalsIgnoreCase(CryptoUtil.CMC_COINS)) {
            // GET the LATEST COINS FROM COINMARKETCAP
            try {
                responseStr = manageCoinDAO.getLatestFromCoinMarketCap(2);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

            CoinMarketCapLatest coinMarketCapLatest = gson.fromJson(responseStr, CoinMarketCapLatest.class);
            List<CoinMarketCapCoin> coinMarketCapCoins = coinMarketCapLatest.getData();
            List<Coin> ourcoins = new ArrayList<>();
            for (CoinMarketCapCoin coinMarketCapCoin : coinMarketCapCoins){
                Coin coin = new Coin();
                coin.setCoin_id(0);
                coin.setMarket_cap(coinMarketCapCoin.getQuote().getUSD().getMarket_cap());
                coin.setSlug(coinMarketCapCoin.getSlug());
                coin.setCmc_id(coinMarketCapCoin.getId());
                coin.setCoin_symbol(coinMarketCapCoin.getSymbol());
                coin.setCoin_name(coinMarketCapCoin.getName());

                ourcoins.add(coin);
            }


            String latestcoins = gson.toJson(ourcoins);
            out.print(latestcoins);


        } else if (action.equalsIgnoreCase(CryptoUtil.DB_COINS)) {

            System.out.println("ALL DB COINS");
            List<Coin> coinsFromDB;
            // GET ALL COINS in DATABASE
            try {
                coinsFromDB = manageCoinDAO.getCurrentCoins();
                String allCoinsFromDB = gson.toJson(coinsFromDB);
                out.print(allCoinsFromDB);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (action.equalsIgnoreCase(CryptoUtil.SINGLE_DB_COIN)) {

            System.out.println("SINGLE DB COIN and coinid is " + " " + coinid);
            Coin singleCoin;
            int numbercoinId = Integer.parseInt(request.getParameter("coinid"));
            // GET SINGLE COIN FROM DATABASE
            try {
                singleCoin = manageCoinDAO.getCoinFromDatabase(numbercoinId);
                String coinstr = gson.toJson(singleCoin);
                out.print(coinstr);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        out.flush();
        out.close();

    }
}
