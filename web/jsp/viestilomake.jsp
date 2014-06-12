<%--
    Document   : uusiviesti
    Created on : 8.6.2014, 12:50:59
    Author     : John Lång <jllang@cs.helsinki.fi>
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"
        trimDirectiveWhitespaces="true" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:kehys otsikko="${otsikko}" fooruminNimi="Esimerkkifoorumi">
            <h2>Uusi ketju</h2>
            <form action="viesti?${pageContext.request.queryString}"
                  method="POST" accept-charset="UTF-8">
                <input type="hidden" name="lahetetty" value="tosi">
                <table class="sisalto pariton" style="width: auto">
                    <tr>
                        <td class="virhe" colspan="4">
                            <c:if test="${virhekoodi == 1}">
                            <t:virhe>Aihetta, alueita tai viestiä ei annettu.</t:virhe>
                            </c:if>
                            <c:if test="${virhekoodi == 2}">
                            <t:virhe>Palvelimella tapahtui virhe. Ketjua ei luotu.</t:virhe>
                            </c:if>
                        </td>
                    </tr>
                    <tr>
                        <td class="peruskentta">Aihe</td>
                        <td class="peruskentta">
                            <input title="Aihe" type="text" name="aihe"
                                   placeholder="Ketjun aihe" value="${aihe}"
                                   autofocus="autofocus" ${muokattavuus}>
                        </td>
                        <td class="peruskentta">Alueet</td>
                        <td class="peruskentta">
                            <select multiple="multiple" name="alueet" ${muokattavuus}>
                                <c:forEach items="${aluelista}" var="alue">
                                    <option>${alue}</option>
                                </c:forEach>
                            </select>
                        </td>
                    </tr>
                    <tr>
                        <td class="peruskentta">Viesti</td>
                        <td colspan="3" class="peruskentta">
                            <textarea rows="10" cols="80" name="sisalto"
                                      placeholder="Viestin sisältö"></textarea>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="4"><input title="Lähetä" value="Lähetä"
                                               type="submit"></td>
                    </tr>
                </table>
            </form>
</t:kehys>