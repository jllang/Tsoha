<%--
    Document   : viesti
    Created on : 8.6.2014, 21:42:41
    Author     : John Lång (jllang@cs.helsinki.fi)
--%>

<%@tag import="mallit.java.Kayttajataso"%>
<%@tag import="mallit.java.Viesti"%>
<%@tag import="mallit.java.Jasen"%>
<%@tag import="java.sql.Timestamp"%>
<%@tag import="java.util.Date" %>
<%@tag import="java.text.DateFormat" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@tag description="Viestipohja dynaamisella sisällöllä." pageEncoding="UTF-8"
       trimDirectiveWhitespaces="true" %>
<%@attribute name="listausnumero" type="Integer" %>
<%@attribute name="kirjoittaja" type="Jasen" %>
<%@attribute name="katselija" type="Jasen" %>
<%@attribute name="omaViesti" type="Boolean" %>
<%@attribute name="viesti" type="Viesti" %>
                <table
                    class="${listausnumero % 2 == 0 ? "parillinen" : "pariton"}">
                    <tr>
                        <th><a href="/profiili?tunnus=${
                        kirjoittaja.annaKayttajanumero()}"
                               class="${kirjoittaja.annaTaso().toString()
                                        .toLowerCase()}">
                                ${kirjoittaja.listausnimi()}</a></th>
                        <th>
                            <a href="muokkaus?ketju=${viesti.annaKetjunTunnus()}">
                                Kirjoita vastine
                            </a>
                            <c:if test="${omaViesti}">
                            | <a href="muokkaus?ketju=${viesti.annaKetjunTunnus()}&viesti=${viesti.annaNumero()}">
                                Muokkaa
                            </a>
                            </c:if>
                            <c:if test="<%= Kayttajataso.syk(katselija
                                    .annaTaso(), Kayttajataso.MODERAATTORI)
                                    && !omaViesti %>">
                            | <a href="muokkaus?ketju=${viesti.annaKetjunTunnus()}&viesti=${viesti.annaNumero()}">
                                Moderoi
                            </a>
                            | <a href="poisto?ketju=${viesti.annaKetjunTunnus()}&viesti=${viesti.annaNumero()}">
                                Poista
                            </a>
                            </c:if>
                            <c:if test="${omaViesti}">
                            | <a href="poisto?ketju=${viesti.annaKetjunTunnus()}&viesti=${viesti.annaNumero()}">
                                Poista
                            </a>
                            </c:if>
                        </th>
                    </tr>
                    <tr>
                        <td class="avatar">
                            <img src="data/paikanpitaja.png" alt="Käyttäjän
                                 <c:out value="${kirjoittaja.listausnimi()}"
                                        escapeXml="true" />
                                 avatar">
                        </td>
                        <td class="peruskentta"><c:out value="${viesti
                                                                .annaSisalto()}"
                               escapeXml="true" /></td>
                    </tr>
                    <tr>
                        <td><%= DateFormat.getInstance().format(
                                new Date(viesti.annaKirjoitettu().getTime())) %>
                        </td>
                        <td>
                            <%  // Luullakseni DateFormat vaatii scriptletin.
                                final Timestamp muokattu = viesti.annaMuokattu(),
                                        moderoitu = viesti.annaModeroitu();
                                if (muokattu != null) {
                                    out.println("Muokattu: " +
                                            DateFormat.getInstance().format(
                                            new Date(viesti.annaMuokattu()
                                                    .getTime())) + " ");
                                }
                                if (moderoitu != null) {
                                    out.println("Moderoitu: " +
                                            DateFormat.getInstance().format(
                                            new Date(viesti.annaModeroitu()
                                                    .getTime())));
                                }
                                if (muokattu != null || moderoitu != null) {
                                    // Sisennykset kuntoon:
                                    out.println("                        ");
                                }
                            %>
                        </td>
                    </tr>
                </table>