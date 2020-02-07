package com.imt11.crypto;

import com.google.gson.Gson;
import com.imt11.crypto.database.PersonDAO;
import com.imt11.crypto.model.Person;
import com.imt11.crypto.util.CryptoUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Dennis Miller
 * pass in ?action=save etc...
 */
@WebServlet(name = "PersonServlet", urlPatterns = {"/person"})
public class PersonServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        int status = 0;
        String action = request.getParameter("action");

        BufferedReader reader = request.getReader();
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Gson gson = new Gson();
        Person person = gson.fromJson(reader, Person.class);
        System.out.println("In PERSON SERVLET and person sent is: "+" "+person.toString());

        PersonDAO dao = new PersonDAO();

        if (action.equalsIgnoreCase(CryptoUtil.UPDATE)){

            // call update
            status = dao.updatePerson(person);
            if (status > 0){
                System.out.println("UPDATE PERSON");
                out.print(person);
            }

        }else if (action.equalsIgnoreCase(CryptoUtil.DELETE)){
            //call delete
            status = dao.deletePerson(person);
            if (status >0 ){
                System.out.println("DELETE PERSON");
                out.print(person);
            }

        }else if(action.equalsIgnoreCase(CryptoUtil.SAVE)){
            // check to see if they are not already in DB
            Person exists = dao.checkIfPersonExists(person.getEmail());
            if (exists == null){
                // call save
                status = dao.savePerson(person);
                int new_person_id = dao.getPersonLastInsertedId();
                person.setPerson_id(new_person_id);
                System.out.println("new_person_id is: "+" "+new_person_id);
                if (status > 0){
                    System.out.println("SAVE PERSON");
                    out.print(person);
                }
            }else {
                System.out.println("PERSON already exists");
                out.print("PERSON ALREADY EXISTS");
            }

        }else{
            // it is a get
            Person newperson = dao.getPersonById(person.getPerson_id());
            System.out.println("GET PERSON");
            out.print(newperson);
        }

        out.flush();
        out.close();

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
