package com.imt11.crypto;

import com.google.gson.Gson;
import com.imt11.crypto.database.DBManager;
import com.imt11.crypto.database.ManageCoinDAO;
import com.imt11.crypto.model.Coin;
import com.imt11.crypto.model.CoinMarketCapCoin;
import com.imt11.crypto.model.CoinMarketCapLatest;
import com.imt11.crypto.model.CryptoValue;
import com.imt11.crypto.model.Holdings;
import com.imt11.crypto.model.PercentageDTO;
import com.imt11.crypto.model.Person;
import com.imt11.crypto.model.TotalValues;
import com.imt11.crypto.util.CryptoUtil;
import com.imt11.crypto.util.ManageCoinUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URISyntaxException;
import java.text.NumberFormat;
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
            Boolean doesExist = manageCoinDAO.checkIfCoinExists(coin.getCmc_id());
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
                responseStr = manageCoinDAO.getCoinFromCoinMarketCap(slug);
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
                responseStr = manageCoinDAO.getLatestFromCoinMarketCap();
            } catch (URISyntaxException | IOException e) {
                e.printStackTrace();
            }

            CoinMarketCapLatest coinMarketCapLatest = gson.fromJson(responseStr, CoinMarketCapLatest.class);
            List<CoinMarketCapCoin> coinMarketCapCoins = coinMarketCapLatest.getData();
            List<Coin> ourcoins = new ArrayList<>();
            for (CoinMarketCapCoin coinMarketCapCoin : coinMarketCapCoins) {
                Coin coin = new Coin();
                coin.setCoin_id(0);
                coin.setMarket_cap(coinMarketCapCoin.getQuote().getUSD().getMarket_cap());
                coin.setSlug(coinMarketCapCoin.getSlug());
                coin.setCmc_id(coinMarketCapCoin.getId());
                coin.setCoin_symbol(coinMarketCapCoin.getSymbol());
                coin.setCoin_name(coinMarketCapCoin.getName());
                coin.setCmc_rank(coinMarketCapCoin.getCmc_rank());
                coin.setCurrentPrice((coinMarketCapCoin.getQuote().getUSD().getPrice()));

                ourcoins.add(coin);
            }

            String latestcoins = gson.toJson(ourcoins);
            out.print(latestcoins);


        } else if (action.equalsIgnoreCase(CryptoUtil.PERSON_CMC_COINS)) {

            System.out.println("PERSON CMC COINS");
            int person_id = 0;
            if (request.getParameter("person_id") != null){
                person_id = Integer.parseInt(request.getParameter("person_id"));
            }

            // Get Person coins from database
            DBManager dbManager = new DBManager();
            List<Person> persons = dbManager.getPersonCoins(person_id);
            // don't call this, call single coin
            if (persons != null && persons.size() > 1){
                // Create a list of coinSymbols to send to CoinMarketCap
                List<String> personCoins = new ArrayList<>();
                for (Person person : persons){
                    String coinSymbol = person.getCoin().getCoin_symbol();
                    personCoins.add(coinSymbol);
                }

                try {
                    responseStr = manageCoinDAO.getPersonCoinListFromCoinMarketCap(personCoins);
                } catch (URISyntaxException | IOException e) {
                    e.printStackTrace();
                }

                List<Coin> coins = ManageCoinUtil.parsePersonCoinList(responseStr);
                List<CryptoValue> cryptos = new ArrayList<>();
                NumberFormat currencyFormat = CryptoUtil.getCurrencyFormat();
                for (Coin coin : coins){
                    CryptoValue cryptoValue = new CryptoValue();
                    cryptoValue.setCoin(coin);
                    cryptoValue.setUSD(currencyFormat.format(coin.getCurrentPrice()));
                    cryptos.add(cryptoValue);
                }
                for (Person person : persons){
                    Holdings holdings = person.getHoldings();
                    for (CryptoValue cryptoValue : cryptos){

                        if (person.getCoin().getCmc_id() == cryptoValue.getCoin().getCmc_id()){
                            cryptoValue.setQuantity(holdings.getQuantity());
                            BigDecimal bd = new BigDecimal(String.valueOf(cryptoValue.getCoin().getCurrentPrice()));
                            bd = bd.setScale(2, RoundingMode.HALF_UP);
                            double roundedDbl = bd.doubleValue();
                            cryptoValue.setHoldingValue(String.valueOf(CryptoUtil.formatDoubleValue(roundedDbl, holdings.getQuantity())));
                            cryptoValue.setCost(CryptoUtil.formatDoubleValue(holdings.getQuantity(), holdings.getCost()));

                            PercentageDTO dto = CryptoUtil.getPercentage(holdings.getQuantity(), holdings.getCost(), roundedDbl);
                            cryptoValue.setPercentage(dto.getValueString());
                            if (dto.getValueDouble() >= 0.0) {
                                cryptoValue.setIncreaseDecrease(CryptoUtil.INCREASE);
                            } else {
                                cryptoValue.setIncreaseDecrease(CryptoUtil.DECREASE);
                            }
                        }
                    }

                }

                // Store grandTotals to Database
                TotalValues grandTotals = CryptoUtil.getNewGrandTotals(cryptos);
                System.out.println("GRAND TOTALS is: "+" "+grandTotals.toString());
                dbManager.updateGrandTotals(person_id, grandTotals);

                String cryptosJsonString = gson.toJson(cryptos);
                out.print(cryptosJsonString);
                // FOR WEB just in case...
                /*request.setAttribute("cryptos", cryptos);
                request.setAttribute("grandtotals", grandTotals);
                request.getRequestDispatcher("cryptoresponse.jsp").forward(request, response);*/
            }else {

                if (persons != null && persons.size() == 1){

                    String myslug = persons.get(0).getCoin().getSlug();
                    System.out.println("PERSON HAS 1 coin only SLUG is: "+" "+myslug);

                    try {
                        responseStr = manageCoinDAO.getCoinFromCoinMarketCap(myslug);
                    } catch (URISyntaxException | IOException e) {
                        e.printStackTrace();
                    }
                    // we can parse this ourselves
                    // the reason for this is that the "data" object has the id as the node
                    Coin coin = ManageCoinUtil.parseSingleCoin(responseStr);
                    String coinstr = gson.toJson(coin);
                    out.print(coinstr);
                }else{
                    System.out.println("PERSON has NO coins yet");
                    response.sendError(400, "Person does not have ony coins yet.");
                }

            }


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
