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
@WebServlet(name = "LogoutServlet", urlPatterns = {"/logout"}, loadOnStartup =3)
public class LogoutServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("thank you!!, Your session was destroyed successfully!!");
        String contextPath = request.getContextPath();
        String requestURI = request.getRequestURI();
        StringBuffer requestURL = request.getRequestURL();
        String pathInfo = request.getPathInfo();
        String pathtranslated = request.getPathTranslated();
        String servletpath = request.getServletPath();
        System.out.println("LOGOUT CONTEXT PATH is: "+" "+contextPath);
        System.out.println("LOGOUT REQUEST URI is: "+" "+requestURI);
        System.out.println("LOGOUT REQUEST URL is: "+" "+requestURL);
        System.out.println("LOGOUT PATH INFO is: "+" "+pathInfo);
        System.out.println("LOGOUT PATH TRANSLATED is: "+" "+pathtranslated);
        System.out.println("LOGOUT SERVLET PATH is: "+" "+ servletpath);
        HttpSession session = request.getSession(false);
        if(session != null){

            session.removeAttribute("user");
            session.removeAttribute("firstname");
            session.removeAttribute("lastname");
            session.setMaxInactiveInterval(1);
            session.invalidate();
        }

        response.sendRedirect("index.html");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
