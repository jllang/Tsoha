<%--
    Document   : kaikille
    Created on : 19.6.2014, 13:11:56
    Author     : John Lång <jllang@cs.helsinki.fi>
--%>

<%@tag description="Toistorakenne taulukon läpikäyntiin." pageEncoding="UTF-8"
       trimDirectiveWhitespaces="true" %>
<%@attribute name="taulukko" type="String[]" %>
<%@attribute name="merkkijono" type="String" %>

<%
    for (String alkio : taulukko) {
        merkkijono = alkio;
%>
    <jsp:doBody />
<%
    }
%>