package com.imt11.crypto;

import com.google.gson.Gson;
import com.imt11.crypto.database.AuthDAO;
import com.imt11.crypto.database.PersonDAO;
import com.imt11.crypto.model.Auth;
import com.imt11.crypto.model.Person;
import com.imt11.crypto.model.SignUp;
import com.imt11.crypto.util.CryptoUtil;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author Dennis Miller
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    private Gson gson = new Gson();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("In Login Servlet!!! ");
        // LOAD BASELINE DATA first
        try {
            CryptoUtil.loadBaselineData();
        } catch (ParseException e) {
            e.printStackTrace();
            System.out.println("In Login Servlet and ParseException is " + e.getLocalizedMessage());
        } catch(IOException e){
            e.printStackTrace();
            System.out.println("In Login Servlet and IOException is " + e.getLocalizedMessage());
        }

        String un = request.getParameter("uname");
        String pwd = request.getParameter("pass");
        System.out.println("In Login Servlet and uname is " + un.toString());
        System.out.println("In Login Servlet and pass is " + pwd.toString());
        String cleanusername = un.replaceAll("\\s", "");
        String cleanpassword = pwd.replaceAll("\\s", "");

        Auth auth = null;
        Person person = null;
        SignUp signUp;
        PersonDAO personDAO = new PersonDAO();
        AuthDAO authDAO = new AuthDAO();
        try {
            auth = authDAO.getCredentials(cleanusername, pwd);
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }

        if (auth != null && auth.getPassword() != null && !auth.getPassword().equals("")) {

            person = personDAO.getPersonById(auth.getPerson_id());
        }

        if (auth != null && auth.getUsername() != null && auth.getPassword() != null) {
            if (auth.getUsername().equals(cleanusername) && auth.getPassword().equals(cleanpassword)) {
                System.out.println("WE HAVE A MATCH PROCEED");
                // reuse existing session if it exists or create new one
                HttpSession session = request.getSession(true);
                session.setAttribute("user", cleanusername);
                if (person != null) {
                    session.setAttribute("firstname", person.getFirst_name());
                    session.setAttribute("lastname", person.getLast_name());
                }
                session.setMaxInactiveInterval(30 * 60); // 30 minutes

                signUp = new SignUp();
                signUp.setPerson(person);
                signUp.setAuth(auth);

                String personjson = this.gson.toJson(signUp);
                PrintWriter out = response.getWriter();
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                out.print(personjson);
                out.flush();
                out.close();

                // WEB
                //response.sendRedirect(request.getContextPath()+"/holdings?person_id="+auth.getPerson_id());
            }
        } else {
            //401 :specifically for use when authentication is required and has failed or has not yet been provided.
            response.sendError(401);
        }

    }


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
