<%--
    Document   : viesti
    Created on : 8.6.2014, 21:42:41
    Author     : John Lång <jllang@cs.helsinki.fi>
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@tag description="Viestipohja dynaamisella sisällöllä." pageEncoding="UTF-8"
       trimDirectiveWhitespaces="true" %>
<%@attribute name="numero" type="int" %>
<%@attribute name="kirjoittaja" type="int" %>
<%@attribute name="listausnimi" type="String" %>
<%@attribute name="kirjoitettu" type="String" %>
<%@attribute name="muokattu" type="String" %>
<%@attribute name="moderoitu" type="String" %>
                <table
                    class="<%=(numero % 2 == 0 ? "parillinen" : "pariton")%>">
                    <tr>
                        <th><a href="/profiili?tunnus=${kirjoittaja}">
                                ${listausnimi}</a></th>
                        <th>&nbsp;</th>
                    </tr>
                    <tr>
                        <td class="avatar">
                            <img src="/data/paikanpitaja.png" alt="Käyttäjän
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