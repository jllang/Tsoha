<%--
    Document   : rekisterointi
    Created on : 1.6.2014, 20:13:04
    Author     : John Lång (jllang@cs.helsinki.fi)
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"
        trimDirectiveWhitespaces="true" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:kehys otsikko="${otsikko}" fooruminNimi="Esimerkkifoorumi">
            <h2>Rekisteröityminen</h2>
            <div class="pariton sisalto" style="width: auto">
                <div>
                    <c:choose>
                        <%-- Näissä virheilmoituksissa olisi ehkä hieman
                            tarkentamisen varaa... --%>
                        <c:when test="${virhekoodi eq 1}">
                            <t:virhe>Kaikkia pakollisia kenttiä ei täytetty.</t:virhe>
                        </c:when>
                        <c:when test="${virhekoodi eq 2}">
                            <t:virhe>Johonkin kenttään annettiin liian pitkä syöte.</t:virhe>
                        </c:when>
                        <c:when test="${virhekoodi eq 3}">
                            <t:virhe>Käyttäjätunnusta ei voitu rekisteröidä virheellisen syötteen vuoksi.</t:virhe>
                        </c:when>
                        <c:when test="${virhekoodi eq 4}">
                            <t:virhe>Valittu käyttäjätunnus on jo olemassa.</t:virhe>
                        </c:when>
                        <c:when test="${virhekoodi eq 5}">
                            <t:virhe>Annetut salasanat eivät täsmänneet.</t:virhe>
                        </c:when>
                        <c:when test="${virhekoodi eq 6}">
                            <t:virhe>Palvelimella tapahtui sisäinen virhe.</t:virhe>
                        </c:when>
                        <%--<c:otherwise>
                            &nbsp;
                        </c:otherwise>--%>
                    </c:choose>
                </div>
                <form action="rekisterointi" method="POST">
                    <input type="hidden" name="lahetetty" value="tosi">
                    <fieldset>
                        <legend>Pakolliset kentät</legend>
                        <table style="width: auto">
                            <tr>
                                <td class="peruskentta">Käyttäjätunnus</td>
                                <td><input title="Luotava käyttäjätunnus. Enintään 32 merkkiä."
                                           type="text" name="kayttajatunnus"
                                           value="${kayttajatunnus}"
                                           autofocus="autofocus"
                                           maxlength="32" required="required"></td>
                            </tr>
                            <tr>
                                <td class="peruskentta">Salasana</td>
                                <td><input title="Salasana" type="password"
                                           name="salasana1" value="${salasana1}"
                                           required="required"></td>
                            </tr>
                            <tr>
                                <td class="peruskentta">Salasana uudelleen</td>
                                <td><input title="Salasana varmuuden vuoksi uudelleen"
                                           type="password" name="salasana2"
                                           value="${salasana2}"
                                           required="required"></td>
                            </tr>
                            <tr>
                                <td class="peruskentta">Sähköpostiosoite</td>
                                <td><input title="Sähköpostiosoite, joka näkyy vain ylläpidolle. Enintään 64 merkkiä."
                                           type="text" name="sahkoposti"
                                           value="${sahkoposti}" maxlength="64"
                                           required="required"></td>
                            </tr>
                        </table>
                    </fieldset>
                    <fieldset>
                        <legend>Valinnaiset kentät</legend>
                        <table style="width: auto">
                            <tr>
                                <td  class="peruskentta">Nimimerkki</td>
                                <td><input title="Nimimerkki, joka näytetään foorumin jäsenille käyttäjätunnuksen sijaan. Enintään 32 merkkiä."
                                           type="text" name="nimimerkki"
                                           value="${nimimerkki}" maxlength="32"></td>
                            </tr>
                            <tr>
                                <td colspan="2">
                                    (Avatarin lähettämistä ei ole toteutettu.)
                                </td>
                            </tr>
                            <tr>
                                <td colspan="2">
                                    <textarea rows="10" cols="80" name="kuvaus"
                                              placeholder="Kuvaus itsestänne. Enintään 512 merkkiä."
                                              maxlength="512">${kuvaus}</textarea>
                                </td>
                            </tr>
                        </table>
                    </fieldset>
                    <input type="submit" title="Lähetä" value="Lähetä">
                </form>
            </div>
</t:kehys>