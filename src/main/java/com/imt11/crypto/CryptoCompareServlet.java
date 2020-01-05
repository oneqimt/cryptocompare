package com.imt11.crypto;

import com.google.gson.Gson;
import com.imt11.crypto.database.DBManager;
import com.imt11.crypto.model.CryptoValue;
import com.imt11.crypto.model.Person;
import com.imt11.crypto.model.TotalValues;
import com.imt11.crypto.util.CryptoUtil;
import com.imt11.crypto.util.ParserUtil;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
@WebServlet(name = "CryptoCompareServlet", urlPatterns = {"/holdings"}, loadOnStartup = 2,
        initParams = {@WebInitParam(name = "person_id", value = "0")})

public class CryptoCompareServlet extends HttpServlet {
    private Gson gson = new Gson();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // arg passed in from client
        int person_id = Integer.parseInt(request.getParameter("person_id"));

        // get database info first
        DBManager dbManager = new DBManager();
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


        if (combinedList != null) {

            String testjson = this.gson.toJson(combinedList);
            PrintWriter out = response.getWriter();
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            out.print(testjson);
            out.flush();
            // FOR WEB
            /*request.setAttribute("cryptos", combinedList);
            request.setAttribute("grandtotals", grandTotals);
            request.getRequestDispatcher("cryptoresponse.jsp").forward(request, response);*/
        } else {
            response.getWriter().print("Cryptos not available!");
        }

    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        if (name == null) name = "Test";
        request.setAttribute("user", name);
        request.getRequestDispatcher("response.jsp").forward(request, response);
    }


}
