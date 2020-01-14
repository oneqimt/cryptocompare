package com.imt11.crypto;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author Dennis Miller
 */
@WebServlet(name = "LogoutServlet", urlPatterns = {"/logout"}, loadOnStartup = 3)
public class LogoutServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws  IOException {

        System.out.println("in LOGOUT");

        // CLEAR session
        HttpSession session = request.getSession(false);

        if (session != null) {

            session.removeAttribute("user");
            session.removeAttribute("firstname");
            session.removeAttribute("lastname");
            session.setMaxInactiveInterval(1);
            session.invalidate();
        }

        // SEND response to client
        Boolean success = true;
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        response.setCharacterEncoding("UTF-8");
        out.println(success);
        out.flush();


        System.out.println("SESSION ENDED response is: " + " " + response.getHeaderNames().toString());

        //response.sendRedirect("index.html");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
