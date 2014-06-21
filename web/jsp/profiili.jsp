<%--
    Document   : profiili
    Created on : 04-Jun-2014, 15:42:53
    Author     : jllang
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"
        trimDirectiveWhitespaces="true"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:kehys otsikko="${otsikko}" fooruminNimi="Esimerkkifoorumi">
            <h2>${profiiliotsikko}</h2>
            <table class="pariton sisalto">
                <tr>
                    <td class="peruskentta"><em>Nimimerkki</em></td>
                    <td class="peruskentta">${nimimerkki}</td>
                    <td class="avatar" rowspan="4">
                        <%-- Missäköhän vaiheessa kuvat hajosivat... --%>
                        <img src="data/staattinen/oletusavatar.png" alt="Avatar">
                    </td>
                </tr>
                <tr>
                    <td class="peruskentta"><em>Käyttäjätaso</em></td>
                    <td class="peruskentta">${taso}</td>
                </tr>
                <tr>
                    <td class="peruskentta"><em>Rekisteröity</em></td>
                    <td class="peruskentta">${rekisteroity}</td>
                </tr>
                <tr>
                    <td class="peruskentta"><em>Viestejä</em></td>
                    <td class="peruskentta">${viesteja}</td>
                </tr>
                <tr>
                    <!--<td class="viesti">Kuvaus</td>-->
                    <td class="peruskentta" colspan="3"><em>${kuvaus}</em></td>
                </tr>
            </table>
</t:kehys>