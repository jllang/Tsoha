<%--
    Document   : hakupalkki
    Created on : 6.6.2014, 20:18:57
    Author     : John Lång (jllang@cs.helsinki.fi)
--%>

<%@tag description="Hakutoiminnossa käytettävä lomake."
       pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@attribute name="hakusana" type="String" required="false" %>
                <div class="pariton sisalto">
                    <form action="haku" method="GET">
                        <%--<input type="hidden" name="lahetetty" value="tosi">--%>
                        <table>
                            <tr>
                                <td>Hakusana</td>
                                <td><input name="hakusana" type="text"
                                           title="Haettava teksti. Korkeintaan 128 merkkiä."
                                           value="${hakusana}" maxlength="128"
                                           required="required"></td>
                                <td>Asiayhteys</td>
                                <td><select>
                                        <option>Keskustelualueet</option>
                                        <option>Viestiketjut</option>
                                        <option disabled="disabled">Käyttäjät</option>
                                        <option disabled="disabled">Porttikiellot</option>
                                    </select></td>
                                <td><input type="submit" title="Lähetä kysely"
                                           value="Hae" disabled="disabled"></td>
                            </tr>
                        </table>
                    </form>
                </div>&nbsp;