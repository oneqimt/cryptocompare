package com.imt11.crypto;

import com.google.gson.Gson;
import com.imt11.crypto.database.DBManager;
import com.imt11.crypto.model.TotalValues;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Dennis Miller
 */
@WebServlet(name = "TotalServlet", urlPatterns = {"/totals"},
        initParams = {@WebInitParam(name = "person_id", value = "0")})
public class TotalServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // arg passed in from client
        int person_id = Integer.parseInt(request.getParameter("person_id"));

        DBManager dbManager = new DBManager();
        TotalValues totalValues = dbManager.getTotalValues(person_id);

        Gson gson = new Gson();

        String totalsjson = gson.toJson(totalValues);
        System.out.println("TOTALS are : " + " " + totalsjson);
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        out.print(totalsjson);
        out.flush();


    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
