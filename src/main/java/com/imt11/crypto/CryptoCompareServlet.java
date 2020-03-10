package com.imt11.crypto;

import com.google.gson.Gson;
import com.imt11.crypto.database.HoldingsDAO;
import com.imt11.crypto.database.ManageCoinDAO;
import com.imt11.crypto.model.Coin;
import com.imt11.crypto.model.CoinHolding;
import com.imt11.crypto.model.CryptoError;
import com.imt11.crypto.model.Holdings;
import com.imt11.crypto.util.CryptoUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;

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
                if (updatestatus > 0) {
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

            int coinId = coin.getCoin_id();
            int personId = coinHolding.getHoldings().getPerson_id();
            status = holdingsDAO.deleteHolding(coinId, personId);
            if (status > 0) {
                System.out.println("HOLDING DELETED");
                String str = gson.toJson(coinHolding.getHoldings());
                out.print(str);
            }

        } else if (action.equalsIgnoreCase(CryptoUtil.UPDATE_HOLDING)) {

            //System.out.println("WE are in UPDATE HOLDING  and coinHolding obj is: "+" "+coinHolding.toString());
            Coin mycoin = manageCoinDAO.getCoinByCmcId(coinHolding.getCoin().getCmc_id());
            coin.setCoin_id(mycoin.getCoin_id());
            holdings.setCoin_id(mycoin.getCoin_id());

            // get the quantity and cost from the Holdings object passed in
            double quantity = coinHolding.getHoldings().getQuantity();
            double cost = coinHolding.getHoldings().getCost();
            System.out.println("QUANTITY passed in: " + " " + quantity);
            System.out.println("COST passed in: " + " " + cost);

            // get the holding from the database - coin_id and person_id
            Holdings dbHolding = holdingsDAO.getExistingHolding(holdings);
            double dbquantity = dbHolding.getQuantity();
            double dbcost = dbHolding.getCost();
            System.out.println("DATABASE QUANTITY result: " + " " + dbquantity);
            System.out.println("DATABASE COST result: " + " " + dbcost);

            Holdings holdingToUpdate = new Holdings();
            holdingToUpdate.setHolding_id(dbHolding.getHolding_id());
            holdingToUpdate.setCoin_id(dbHolding.getCoin_id());
            holdingToUpdate.setPerson_id(dbHolding.getPerson_id());

            //Find average - NOTE cost is per coin
            double quantityRawTotal = (quantity + dbquantity);
            double costRawTotal = (cost + dbcost);
            double newcost = costRawTotal / 2;
            System.out.println("quantityRawTotal: " + " " + quantityRawTotal);
            System.out.println("costRawTotal: " + " " + costRawTotal);
            System.out.println("newcost: " + " " + newcost);

            // ROUNDING to 2 decimal places
            BigDecimal bdquantity = new BigDecimal(quantityRawTotal);
            bdquantity = bdquantity.setScale(2, RoundingMode.HALF_UP);
            System.out.println("bdquantity is : " + " " + bdquantity);

            BigDecimal bdcost = new BigDecimal(newcost);
            bdcost = bdcost.setScale(2, RoundingMode.HALF_UP);
            System.out.println("bdcost is: " + " " + bdcost);

            // maybe set these as BigDecimal type on the Holdings object
            holdingToUpdate.setQuantity(bdquantity.doubleValue());
            holdingToUpdate.setCost(bdcost.doubleValue());

            System.out.println("HOLDING TO UPDATE is: " + " " + holdingToUpdate.toString());

            status = holdingsDAO.updateHolding(holdingToUpdate);
            if (status > 0) {
                System.out.println("HOLDING UPDATED");
                String str = gson.toJson(holdingToUpdate);
                out.print(str);
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "There was a problem updating that holding");
            }

        } else {
            response.sendError(400, "There was no action type sent in the query string");

        }

        out.flush();
        out.close();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

}
