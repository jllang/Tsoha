<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@page import="mallit.tyypit.Kayttajataso"%>
<%@page contentType="text/html" pageEncoding="UTF-8"
        trimDirectiveWhitespaces="true" %>
<t:kehys otsikko="Esimerkkifoorumi: Kirjautuminen"
         fooruminNimi="Esimerkkifoorumi">
            <h2>Sisäänkirjautuminen</h2>
            <form accept-charset="ASCII">
                <table class="sisalto parillinen" style="width: auto">
                    <tr>
                        <td class="virhe" colspan="2">
                            <c:if test="${epaonnistui}">
                                <t:virhe>Sisäänkirjautuminen epäonnistui.</t:virhe>
                            </c:if>
                        </td>
                    </tr>
                    <tr>
                        <td>Käyttäjätunnus</td>
                        <td><input title="Käyttäjätunnus" type="text"
                                   value="${kayttajatunnus}"
                                   autofocus="autofocus"></td>
                    </tr>
                    <tr>
                        <td>Salasana</td>
                        <td><input title="Salasana" type="password"
                                   value="${salasana}"></td>
                    </tr>
                    <tr>
                        <td colspan="2">
                            <input type="submit" title="Kirjaudu sisään"
                                   value="Kirjaudu">
                        </td>
                    </tr>
                </table>
            </form>
</t:kehys>