package com.imt11.crypto;

import com.google.gson.Gson;
import com.imt11.crypto.database.AuthDAO;
import com.imt11.crypto.database.PersonDAO;
import com.imt11.crypto.model.Person;
import com.imt11.crypto.model.SignUp;
import com.imt11.crypto.util.CryptoUtil;

import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Dennis Miller
 */
@WebServlet(name = "SignUpServlet", urlPatterns = {"/signup"})
public class SignUpServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        System.out.println("In SignUpServlet");
        // NEED TO LOAD BASELINE DATA because user did not go thru login
        try {
            CryptoUtil.loadBaselineData();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        PersonDAO personDAO = new PersonDAO();
        AuthDAO authDao = new AuthDAO();
        BufferedReader reader = request.getReader();
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Gson gson = new Gson();

        SignUp signUp = gson.fromJson(reader, SignUp.class);
        System.out.println("In SIGN UP SERVLET and signup sent is: " + " " + signUp.toString());

        // INSERT INTO PERSON
        Person exists = personDAO.getPersonByEmail(signUp.getPerson().getEmail());
        if (exists == null) {
            personDAO.savePerson(signUp.getPerson());
            // GET PERSON LAST INSERTED ID
            int new_person_id = personDAO.getPersonLastInsertedId();
            signUp.getPerson().setPerson_id(new_person_id);
            // INSERT INTO AUTH
            // IMPORTANT - set person_id on auth FIRST
            signUp.getAuth().setPerson_id(new_person_id);
            authDao.saveAuth(signUp.getAuth());
            // GET LAST INSERTED AUTH ID
            int new_auth_id = authDao.getAuthLastInsertedId();
            signUp.getAuth().setAuth_id(new_auth_id);

            String jsonsignup = gson.toJson(signUp);
            out.print(jsonsignup);
            out.flush();
            out.close();


        } else {
            System.out.println("PERSON already exists");
            //403 : attempting a prohibited action (e.g. creating a duplicate record where only one is allowed).
            response.sendError(403);
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) {

    }
}
