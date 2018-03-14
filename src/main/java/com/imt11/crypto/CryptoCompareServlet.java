package com.imt11.crypto;

import com.imt11.crypto.database.DBManager;
import com.imt11.crypto.model.CryptoValue;
import com.imt11.crypto.model.Person;
import com.imt11.crypto.model.TotalValues;
import com.imt11.crypto.util.CryptoUtil;
import com.imt11.crypto.util.ParserUtil;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
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
        initParams = {@WebInitParam(name="person_id", value="0")})

public class CryptoCompareServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //TO USE INIT DEFAULT VALUE
        //getServletConfig().getInitParameter("person_id")
        // passed in from index.html
        int person_id = Integer.parseInt(request.getParameter("person_id"));
        System.out.println("PERSON_ID is :"+" "+person_id);

        // get database info first
        DBManager dbManager = new DBManager();
        List<Person> persons = dbManager.getPersonCoins(person_id);

        /*if(persons != null){
            for(Person person : persons){
                System.out.println("DENNIS in Servlet :"+person.toString());
            }
        }*/

        // MAIN
        String mainjson = getCryptoStringJson(CryptoUtil.getMainCryptoEndpoint(persons));
        System.out.println("DENNIS and mainjson is: "+" "+mainjson);

        JSONParser mainparser = new JSONParser();
        List<CryptoValue> maincryptos = new ArrayList<>();
        try{

            JSONObject mainjsonObject = (JSONObject)mainparser.parse(mainjson);
            maincryptos = ParserUtil.parseMainCryptos(mainjsonObject, persons);

        }catch(ParseException e){
            e.printStackTrace();
        }

        // ALTS
        String altjson = getCryptoStringJson(CryptoUtil.getAltCryptoEndpoint(persons));
        System.out.println("DENNIS ALT JSON is: "+" "+altjson);
        JSONParser altparser = new JSONParser();
        List<CryptoValue> altcryptos = new ArrayList<>();
        try{
            JSONObject altjsonObject = (JSONObject)altparser.parse(altjson);
            altcryptos = ParserUtil.parseAltCryptos(altjsonObject, persons);
        }catch(ParseException e){
            e.printStackTrace();
        }

        List<CryptoValue> combinedList = new ArrayList<>();
        combinedList.addAll(maincryptos);
        combinedList.addAll(altcryptos);

        TotalValues grandTotals = CryptoUtil.getGrandTotals(combinedList);

        if(combinedList != null){
            request.setAttribute("cryptos", combinedList);
            request.setAttribute("grandtotals", grandTotals);
            request.getRequestDispatcher("cryptoresponse.jsp").forward(request, response);
        }else{
            response.getWriter().print("Cryptos not available!");
        }

    }

    private String getCryptoStringJson(String endpoint){

        StringBuilder sb = new StringBuilder();

        try{

            URL url = new URL(endpoint);
            URLConnection mainurlConnection = url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(mainurlConnection.getInputStream()));

            String mainLine;

            while ((mainLine = bufferedReader.readLine()) != null){
                sb.append(mainLine + "\n");
            }

            bufferedReader.close();

        }catch(MalformedURLException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }

        return sb.toString();

    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        if (name == null) name = "World";
        request.setAttribute("user", name);
        request.getRequestDispatcher("response.jsp").forward(request, response);
    }


}
