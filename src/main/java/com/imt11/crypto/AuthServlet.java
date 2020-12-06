package com.imt11.crypto;

import com.google.gson.Gson;
import com.imt11.crypto.database.AuthDAO;
import com.imt11.crypto.model.Auth;
import com.imt11.crypto.util.CryptoUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Dennis Miller
 *
 * pass in ?action=save etc...
 */
@WebServlet(name = "AuthServlet", urlPatterns = {"/auth"})
public class AuthServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        int status;
        String action = request.getParameter("action");

        BufferedReader reader = request.getReader();
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Gson gson = new Gson();
        Auth auth = gson.fromJson(reader, Auth.class);
        auth.setRole(CryptoUtil.ROLE_USER);
        auth.setEnabled(CryptoUtil.ENABLED);

        System.out.println("In AuthServlet and auth sent in is: " + " " + auth.toString());

        AuthDAO dao = new AuthDAO();

        if (action.equalsIgnoreCase(CryptoUtil.UPDATE)) {
            // call update
            status = dao.updateAuth(auth);
            if (status > 0){
                System.out.println("UPDATE AUTH");
                out.print(auth);
            }

        } else if (action.equalsIgnoreCase(CryptoUtil.DELETE)) {
            // call delete
            status = dao.deleteAuth(auth);
            if (status > 0){
                System.out.println("DELETE AUTH");
                out.print(auth);
            }

        } else if(action.equalsIgnoreCase(CryptoUtil.SAVE)){
            //it is a save
            status = dao.saveAuth(auth);
            int new_auth_id = dao.getAuthLastInsertedId();
            auth.setAuth_id(new_auth_id);
            System.out.println("new_auth_id is: "+" "+new_auth_id);
            if (status > 0){
                System.out.println("SAVE AUTH");
                out.print(auth);
            }

        }else{
            // it is a get
            Auth newAuth = dao.getAuthById(auth.getPerson_id());
            System.out.println("GET AUTH");
            out.print(newAuth);
        }

        out.flush();
        out.close();

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) {

    }
}
