package com.imt11.crypto;

import com.google.gson.Gson;
import com.imt11.crypto.database.AuthDAO;
import com.imt11.crypto.database.PersonDAO;
import com.imt11.crypto.model.Auth;
import com.imt11.crypto.model.Person;
import com.imt11.crypto.model.ReturnDTO;
import com.imt11.crypto.util.CryptoUtil;
import com.imt11.crypto.util.EmailUtil;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.PrintWriter;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Dennis Miller
 */
@WebServlet(name = "ResetPasswordServlet", urlPatterns = {"/resetpassword"})
public class ResetPasswordServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("In Reset Password Servlet");
        // LOAD BASELINE DATA first
        try {
            CryptoUtil.loadBaselineData();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String email = request.getParameter("email");
        System.out.println("RESET and email passed in is: "+" "+email);

        PersonDAO personDAO = new PersonDAO();
        Person person = personDAO.getPersonByEmail(email);
        if (person != null){
            System.out.println("RESET PERSON is : "+" "+person.toString());
            String randomPassword = EmailUtil.getRandomPassword();
            AuthDAO authDAO = new AuthDAO();
            Auth auth = authDAO.getAuthById(person.getPerson_id());
            if (auth != null){
                System.out.println("RESET AUTH is : "+" "+auth.toString());
                auth.setUsername(auth.getUsername());
                auth.setPassword(randomPassword);
                authDAO.updateAuth(auth);
                try {
                    String content = "Hi, this is your new password: " + randomPassword;
                    content += "\nNote: for security reasons, "
                            + "you should change your password after logging in.";
                    EmailUtil.sendEmail(content, email);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }

                ReturnDTO dto = new ReturnDTO();
                dto.setId(200);
                dto.setTitle("Success");
                dto.setDescription("User forgot password email sent.");

                Gson gson = new Gson();

                String returnjson =  gson.toJson(dto);
                System.out.println("ResetPasswordServlet and randomPassword is : " + " " + randomPassword);
                PrintWriter out = response.getWriter();
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                out.print(returnjson);
                out.flush();
                out.close();
            }else{
                response.sendError(401);
            }

        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
