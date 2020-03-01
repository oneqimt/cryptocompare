package com.imt11.crypto;

import com.google.gson.Gson;
import com.imt11.crypto.database.DBManager;
import com.imt11.crypto.database.HoldingsDAO;
import com.imt11.crypto.database.ManageCoinDAO;
import com.imt11.crypto.model.Coin;
import com.imt11.crypto.model.CoinHolding;
import com.imt11.crypto.model.CryptoError;
import com.imt11.crypto.model.CryptoValue;
import com.imt11.crypto.model.Holdings;
import com.imt11.crypto.model.Person;
import com.imt11.crypto.model.TotalValues;
import com.imt11.crypto.util.CryptoUtil;
import com.imt11.crypto.util.ParserUtil;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Dennis Miller
 */
@WebServlet(name = "CryptoCompareServlet", urlPatterns = {"/holdings"},
        initParams = {@WebInitParam(name = "person_id", value = "0")})

public class CryptoCompareServlet extends HttpServlet {
    private Gson gson = new Gson();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        int status;

        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Gson gson = new Gson();
        HoldingsDAO holdingsDAO = new HoldingsDAO();
        ManageCoinDAO manageCoinDAO = new ManageCoinDAO();
        BufferedReader reader = request.getReader();

        CoinHolding coinHolding = gson.fromJson(reader, CoinHolding.class);
        Coin coin = coinHolding.getCoin();
        Holdings holdings = coinHolding.getHoldings();
        System.out.println("In CryptoCompareServlet and coinholding sent is: " + " " + coinHolding.toString());

        String action = request.getParameter("action");

        if (action.equalsIgnoreCase(CryptoUtil.ADD_HOLDING)) {

            // Find out if coin exists in our database first
            System.out.println("WE are in ADD_HOLDING");
            boolean doesCoinExist = manageCoinDAO.checkIfCoinExists(coinHolding.getCoin().getCmc_id());
            if (!doesCoinExist) {
                System.out.println("doescoinexist = false");
                // if it does not exist add it to our database
                manageCoinDAO.insertCoin(coinHolding.getCoin());
                // get last inserted coin_id and set that on the Coin object
                int lastInsertedId = manageCoinDAO.getCoinLastInsertedId();
                coin.setCoin_id(lastInsertedId);
                holdings.setCoin_id(lastInsertedId);
            } else {
                System.out.println("doescoinexist = true");
                // get the coin from database and set the coin_id on the Coin object
                Coin mycoin = manageCoinDAO.getCoinByCmcId(coinHolding.getCoin().getCmc_id());
                coin.setCoin_id(mycoin.getCoin_id());
                holdings.setCoin_id(mycoin.getCoin_id());
                // update market_cap and cmc_rank
                int updatestatus = manageCoinDAO.updateCoin(coin);
                if (updatestatus > 0){
                    System.out.println("COIN updated success");
                }

            }
            // Now check if holdings exists
            Holdings exists = holdingsDAO.checkIfHoldingExists(holdings);
            if (exists == null) {
                // add the holding
                status = holdingsDAO.addHolding(holdings);
                int new_holdings_id = holdingsDAO.getHoldingLastInsertedId();
                holdings.setHolding_id(new_holdings_id);
                System.out.println("new_holdings_id is: " + " " + new_holdings_id);
                if (status > 0) {
                    System.out.println("ADDED HOLDING");
                    String str = gson.toJson(holdings);
                    out.print(str);
                }
            } else {
                System.out.println("HOLDING already exists");
                CryptoError cryptoError = new CryptoError();
                cryptoError.setErrorId(404);
                cryptoError.setErrorName("Holding already exists.");
                cryptoError.setErrorDescription("Please update instead of add this holding.");
                String errorstr = gson.toJson(cryptoError);
                out.print(errorstr);
            }


        } else if (action.equalsIgnoreCase(CryptoUtil.DELETE_HOLDING)) {

            //call delete
            status = holdingsDAO.deleteHolding(coinHolding.getHoldings());
            if (status > 0) {
                System.out.println("HOLDING DELETED");
                String str = gson.toJson(coinHolding.getHoldings());
                out.print(str);
            }

        } else if (action.equalsIgnoreCase(CryptoUtil.UPDATE_HOLDING)) {

            System.out.println("WE are in UPDATE HOLDING  and coinHolding obj is: "+" "+coinHolding.toString());
            Coin mycoin = manageCoinDAO.getCoinByCmcId(coinHolding.getCoin().getCmc_id());
            coin.setCoin_id(mycoin.getCoin_id());
            holdings.setCoin_id(mycoin.getCoin_id());

            // get the quantity and cost from the Holdings object passed in
            double quantity = coinHolding.getHoldings().getQuantity();
            double cost = coinHolding.getHoldings().getCost();

            // get the holding from the database - coin_id and person_id
            Holdings dbHolding = holdingsDAO.getExistingHolding(holdings);
            double dbquantity = dbHolding.getQuantity();
            double dbcost = dbHolding.getCost();

            Holdings holdingToUpdate = new Holdings();
            holdingToUpdate.setHolding_id(dbHolding.getHolding_id());
            holdingToUpdate.setCoin_id(dbHolding.getCoin_id());
            holdingToUpdate.setPerson_id(dbHolding.getPerson_id());

            double newquantity = quantity + dbquantity;
            double newcost = cost + dbcost;

            BigDecimal bdquantity = new BigDecimal(newquantity);
            bdquantity = bdquantity.setScale(2, RoundingMode.HALF_UP);

            BigDecimal bdcost = new BigDecimal(newcost);
            // round to 2 decimal places
            bdcost = bdcost.setScale(2, RoundingMode.HALF_UP);
            // maybe set these as BigDecimal on the Holdings object
            holdingToUpdate.setQuantity(bdquantity.doubleValue());
            holdingToUpdate.setCost(bdcost.doubleValue());

            System.out.println("HOLDING TO UPDATE is: "+" "+holdingToUpdate.toString());

            status = holdingsDAO.updateHolding(holdingToUpdate);
            if (status > 0) {
                System.out.println("HOLDING UPDATED");
                String str = gson.toJson(holdingToUpdate);
                out.print(str);
            }else{
                response.sendError(400, "There was a problem updating that holding");
            }

        } else {
            response.sendError(400, "There was no action type sent in the query string");

        }

        out.flush();
        out.close();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // arg passed in from client
        int person_id = Integer.parseInt(request.getParameter("person_id"));

        // get database info first
        DBManager dbManager = new DBManager();
        // GET PERSON COINS
        List<Person> persons = dbManager.getPersonCoins(person_id);

        // MAIN
        String mainjson = CryptoUtil.getStringJson(CryptoUtil.getMainCryptoEndpoint(persons));

        JSONParser mainparser = new JSONParser();
        List<CryptoValue> maincryptos = new ArrayList<>();
        try {

            JSONObject mainjsonObject = (JSONObject) mainparser.parse(mainjson);
            maincryptos = ParserUtil.parseMainCryptos(mainjsonObject, persons);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        // ALTS
        String altjson = CryptoUtil.getStringJson(CryptoUtil.getAltCryptoEndpoint(persons));
        JSONParser altparser = new JSONParser();
        List<CryptoValue> altcryptos = new ArrayList<>();
        try {
            JSONObject altjsonObject = (JSONObject) altparser.parse(altjson);
            altcryptos = ParserUtil.parseAltCryptos(altjsonObject, persons);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        List<CryptoValue> combinedList = new ArrayList<>();
        combinedList.addAll(maincryptos);
        combinedList.addAll(altcryptos);

        // Store grandTotals to Database
        TotalValues grandTotals = CryptoUtil.getGrandTotals(combinedList);
        dbManager.updateGrandTotals(person_id, grandTotals);


        String testjson = this.gson.toJson(combinedList);
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        out.print(testjson);
        out.flush();
        out.close();


        // FOR WEB
            /*request.setAttribute("cryptos", combinedList);
            request.setAttribute("grandtotals", grandTotals);
            request.getRequestDispatcher("cryptoresponse.jsp").forward(request, response);*/

    }

}
