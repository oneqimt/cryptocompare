package com.imt11.crypto;

import com.google.gson.Gson;
import com.imt11.crypto.database.DBManager;
import com.imt11.crypto.database.HoldingsDAO;
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

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        int status = 0;

        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Gson gson = new Gson();
        HoldingsDAO holdingsDAO = new HoldingsDAO();

        BufferedReader reader = request.getReader();
        Holdings holding = gson.fromJson(reader, Holdings.class);
        System.out.println("In CryptoCompareServlet and holding sent is: " + " " + holding.toString());

        String action = request.getParameter("action");
        if (action.equalsIgnoreCase(CryptoUtil.ADD_HOLDING)) {
            // /holdings?action=addholding
            Holdings exists = holdingsDAO.checkIfHoldingExists(holding);
            if (exists == null) {
                // call save
                status = holdingsDAO.addHolding(holding);
                int new_holdings_id = holdingsDAO.getHoldingLastInsertedId();
                holding.setHolding_id(new_holdings_id);
                System.out.println("new_holdings_id is: " + " " + new_holdings_id);
                if (status > 0) {
                    System.out.println("ADDED HOLDING");
                    String str = gson.toJson(holding);
                    out.print(str);
                }
            } else {
                System.out.println("HOLDING already exists");
                out.print("HOLDING ALREADY EXISTS");
            }

        } else if (action.equalsIgnoreCase(CryptoUtil.DELETE_HOLDING)) {

            //call delete
            status = holdingsDAO.deleteHolding(holding);
            if (status > 0) {
                System.out.println("HOLDING DELETED");
                String str = gson.toJson(holding);
                out.print(str);
            }

        } else if (action.equalsIgnoreCase(CryptoUtil.UPDATE_HOLDING)) {

            // call update
            status = holdingsDAO.updateHolding(holding);
            if (status > 0) {
                System.out.println("HOLDING UPDATED");
                String str = gson.toJson(holding);
                out.print(str);
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
        // TODO refactor this to return less maybe
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
