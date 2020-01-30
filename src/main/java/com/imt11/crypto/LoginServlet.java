package com.imt11.crypto;

import com.google.gson.Gson;
import com.imt11.crypto.database.AuthDAO;
import com.imt11.crypto.database.PersonDAO;
import com.imt11.crypto.model.Auth;
import com.imt11.crypto.model.Person;
import com.imt11.crypto.util.CryptoUtil;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

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
@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    private Gson gson = new Gson();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("In Login Servlet");
        // LOAD BASELINE DATA first
        try {
            CryptoUtil.loadBaselineData();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        response.setContentType("text/html");

        String un = request.getParameter("uname");
        String pwd = request.getParameter("pass");
        String cleanusername = un.replaceAll("\\s", "");
        String cleanpassword = pwd.replaceAll("\\s", "");

        Auth auth = null;
        Person person = null;
        PersonDAO personDAO = new PersonDAO();
        AuthDAO authDAO = new AuthDAO();
        try {
            auth = authDAO.getCredentials(cleanusername, cleanpassword);
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

                String personjson = this.gson.toJson(person);
                System.out.println("PERSON is : " + " " + personjson);
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

            // NOTE : could return a little better response to client
            System.out.println("BAIL NO DICE");
            RequestDispatcher rd = request.getRequestDispatcher("index.html");
            System.out.println("<font color=red>Either user name or password is wrong.</font>");
            rd.include(request, response);
        }

    }


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
