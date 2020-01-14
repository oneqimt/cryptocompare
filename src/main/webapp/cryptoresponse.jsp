<%--
  Created by IntelliJ IDEA.
  User: dmiller
  Date: 2/19/18
  Time: 9:24 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@page import="java.util.ArrayList" %>
<%@page import="java.util.List" %>
<%@page import="com.imt11.crypto.model.CryptoValue" %>
<%@page import="com.imt11.crypto.model.TotalValues" %>
<%@page import="com.imt11.crypto.util.CryptoUtil" %>
<%@page import="com.imt11.crypto.model.Person" %>
<%--<%@taglib uri="http://java.sun.com/jstl/core" prefix="c"%>--%>

<html>

    <head>
        <title>Crypto Holdings</title>
        <link rel="stylesheet" href="crypto.css">
    </head>

    <body>


        <div>
            <% String firstname=null;%>
            <% String lastname=null;%>
            <%
                if (session != null) {
                    if (session.getAttribute("user") != null) {
                        String user = (String) session.getAttribute("user");
                        firstname = (String) session.getAttribute("firstname");
                        lastname = (String) session.getAttribute("lastname");
                        System.out.print("Hello, " + firstname + lastname +"  Welcome to ur Profile");
                    } else {
                        response.sendRedirect(request.getContextPath()+"/index.html");
                    }
                }
            %>
            <h3><%=firstname%> <%=lastname%> Crypto Holdings Dashboard (Alpha Version 0.1)  </h3>
            <form action="logout" method="post">
                <input type="submit" value="Logout">
            </form>
            <%--<form action="totals" method="get">
            <input type="submit" value="Totals">--%>
        </form></div>
        <% List<CryptoValue> cryptos = (ArrayList<CryptoValue>)request.getAttribute("cryptos"); %>
        <% TotalValues grandtotals = (TotalValues)request.getAttribute("grandtotals"); %>

            <%
                for(CryptoValue cryptoValue : cryptos){
            %>
                	<div class="holdings">
                        <p><%=cryptoValue.getCoin().getCoin_name()%> ( <%=cryptoValue.getCoin().getCoin_symbol()%> ) : </p>
                        <p>CURRENT PRICE: <%=cryptoValue.getUSD()%></p>
                        <p>QUANTITY HELD: <%=cryptoValue.getQuantity()%></p>
                        <p>HOLDING VALUE: <%=cryptoValue.getHoldingValue()%></p>
                        <p>COST: <%=cryptoValue.getCost()%></p>
                        <p>PERCENTAGE: <%=cryptoValue.getIncreaseDecrease()%> /  <%=cryptoValue.getPercentage()%></p>

                    </div>

            <%
                }
            %>

            <div class="holdings">
                <p>GRAND TOTAL VALUE: <%=grandtotals.getTotalValue()%></p>
                <p>GRAND TOTAL COST: <%=grandtotals.getTotalCost()%></p>
                <p>GRAND TOTAL PERCENTAGE: <%=grandtotals.getIncreaseDecrease()%> / <%=grandtotals.getTotalPercentageIncreaseDecrease()%></p>
            </div>

    </body>

</html>
