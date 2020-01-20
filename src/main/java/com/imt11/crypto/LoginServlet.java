package com.imt11.crypto;

import com.google.gson.Gson;
import com.imt11.crypto.database.DBManager;
import com.imt11.crypto.model.Auth;
import com.imt11.crypto.model.Person;
import com.imt11.crypto.util.SecurityUtil;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author Dennis Miller
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/login"}, loadOnStartup = 1)
public class LoginServlet extends HttpServlet {

    private Gson gson = new Gson();
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // LOAD BASELINE DATA first
        System.out.println("In Login Servlet");
        InputStream resourceAsStream = LoginServlet.class.getResourceAsStream("/baseline.json"); // works!
        if(resourceAsStream != null){
            InputStreamReader streamReader = new InputStreamReader(resourceAsStream, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(streamReader);
            StringBuilder sb = new StringBuilder();
            for (String line; (line = reader.readLine()) != null;) {
                sb.append(line);
            }
            JSONParser parser = new JSONParser();
            try{
                JSONObject baseline = (JSONObject)parser.parse(sb.toString());
                String host = (String)baseline.get("host");
                String username = (String)baseline.get("username");
                String password = (String)baseline.get("password");
                String driver = (String)baseline.get("driver");
                String cryptoControlApiKey = (String)baseline.get("crypto_control_api_key");
                String cryptoCompareApiKey = (String)baseline.get("crypto_compare_api_key");
                SecurityUtil.getInstance().setDriver(driver);
                SecurityUtil.getInstance().setHost(host);
                SecurityUtil.getInstance().setPassword(password);
                SecurityUtil.getInstance().setUsername(username);
                SecurityUtil.getInstance().setCryptoControlApi(cryptoControlApiKey);
                SecurityUtil.getInstance().setCryptoCompareApi(cryptoCompareApiKey);
            }catch(ParseException e){
                e.printStackTrace();
            }

            response.setContentType("text/html");
            String un = request.getParameter("uname");
            String pwd = request.getParameter("pass");
            String cleanusername = un.replaceAll("\\s", "");
            String cleanpassword = pwd.replaceAll("\\s", "");

            DBManager dbManager = new DBManager();
            Auth auth = null;
            Person person = null;
            try{
                auth = dbManager.getCredentials(cleanusername, cleanpassword);
            }catch (IOException ex) {
                Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            if(auth.getPassword() != null && !auth.getPassword().equals("")) {
                try {
                    person = dbManager.getPerson(auth.getPerson_id());
                } catch (IOException ex) {
                    Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }

            if(auth.getUsername() != null && auth.getPassword() != null){
                if(auth.getUsername().equals(cleanusername) && auth.getPassword().equals(cleanpassword)){
                    System.out.println("WE HAVE A MATCH PROCEED");
                    // reuse existing session if it exists or create new one
                    HttpSession session = request.getSession(true);
                    session.setAttribute("user", cleanusername);
                    if(person != null){
                        session.setAttribute("firstname", person.getFirst_name());
                        session.setAttribute("lastname", person.getLast_name());
                    }
                    session.setMaxInactiveInterval(30 * 60); // 30 minutes

                    String personjson = this.gson.toJson(person);
                    System.out.println("PERSON is : "+" "+personjson);
                    PrintWriter out = response.getWriter();
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    out.print(personjson);
                    out.flush();
                    //response.sendRedirect(request.getContextPath()+"/holdings?person_id="+auth.getPerson_id());
                }
            }else{
                System.out.println("BAIL NO DICE");
                RequestDispatcher rd = request.getRequestDispatcher("index.html");
                System.out.println("<font color=red>Either user name or password is wrong.</font>");
                rd.include(request, response);
            }

        }else{
            // TODO add error handling
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
