<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@page contentType="text/html" pageEncoding="UTF-8"
        trimDirectiveWhitespaces="true" %>
<t:kehys otsikko="${otsikko}"
         fooruminNimi="Esimerkkifoorumi">
            <h2>Sisäänkirjautuminen</h2>
            <form action="istunto?${pageContext.request.queryString}" method="POST">
                <input type="hidden" name="lahetetty" value="tosi">
                <table class="sisalto pariton" style="width: auto">
                    <tr>
                        <td class="virhe" colspan="2">
                            <c:if test="${virhekoodi == 1}">
                                <t:virhe>Käyttäjätunnusta tai salasanaa ei annettu.</t:virhe>
                            </c:if>
                            <c:if test="${virhekoodi == 2}">
                                <t:virhe>Sisäänkirjautuminen epäonnistui.</t:virhe>
                            </c:if>
                        </td>
                    </tr>
                    <tr>
                        <td>Käyttäjätunnus</td>
                        <td><input title="Käyttäjätunnus" type="text"
                                   name="kayttajatunnus"
                                   value="${kayttajatunnus}"
                                   autofocus="autofocus"></td>
                    </tr>
                    <tr>
                        <td>Salasana</td>
                        <td><input title="Salasana" type="password"
                                   name="salasana" value="${salasana}"></td>
                    </tr>
                    <tr>
                        <td colspan="2">
                            <input type="submit" title="Kirjaudu sisään"
                                   value="Kirjaudu">
                        </td>
                    </tr>
                </table>
                <p>
                    <a href="rekisterointi.jsp" >Rekisteröityminen</a>
                </p>
            </form>
</t:kehys>