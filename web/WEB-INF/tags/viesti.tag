<%--
    Document   : viesti
    Created on : 8.6.2014, 21:42:41
    Author     : John Lång <jllang@cs.helsinki.fi>
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@tag description="Viestipohja dynaamisella sisällöllä." pageEncoding="UTF-8"
       trimDirectiveWhitespaces="true" %>
<%@tag import="java.sql.Timestamp"%>
<%@attribute name="ketjunTunnus" type="Integer" %>
<%--@attribute name="viestinumero" type="Integer" --%>
<%@attribute name="listausnumero" type="Integer" %>
<%@attribute name="kirjoittaja" type="Integer" %>
<%@attribute name="listausnimi" type="String" %>
<%@attribute name="kirjoitettu" type="Timestamp" %>
<%@attribute name="muokattu" type="Timestamp" %>
<%@attribute name="moderoitu" type="Timestamp" %>
                <table
                    class="<%=(listausnumero % 2 == 0 ? "parillinen" : "pariton")%>">
                    <tr>
                        <th><a href="/profiili?tunnus=${kirjoittaja}">
                                ${listausnimi}</a></th>
                        <th>
                            <a href="viesti?ketju=${ketjunTunnus}">
                                Kirjoita vastine
                            </a>
                        </th>
                    </tr>
                    <tr>
                        <td class="avatar">
                            <img src="data/paikanpitaja.png" alt="Käyttäjän
                                 ${listausnimi} avatar">
                        </td>
                        <td class="peruskentta"><jsp:doBody /></td>
                    </tr>
                    <tr>
                        <td>${kirjoitettu}</td>
                        <td>
                            <c:if test="${not empty muokattu}">
                                <em>Viimeksi muokattu ${muokattu}. </em>
                            </c:if>
                            <c:if test="${not empty moderoitu}">
                                <em>Viimeksi moderoitu ${moderoitu}.</em>
                            </c:if>
                        </td>
                    </tr>

                </table>