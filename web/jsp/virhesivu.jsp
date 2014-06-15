<%--
    Document   : virhesivu
    Created on : 04-Jun-2014, 15:57:03
    Author     : jllang
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"
          trimDirectiveWhitespaces="true"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<!DOCTYPE html>
<t:kehys fooruminNimi="Esimerkkifoorumi" otsikko="Virhe">
            <h2 class="virhe">Virhe</h2>
            <%-- Ei pidä paljastaa liikoja jottei script kiddiet innostu. --%>
            <t:virhe>
                Pyydettyä kohdetta ei voida näyttää.
                <hr style="color: red; max-width: 800px" />
            </t:virhe>
                <h3>Mahdollisia syitä virheeseen:</h3>
                <ul class="selitys">
                    <li>Pyydettyä sivua tai kohdetta ei ole olemassa;</li>
                    <li>käyttäjällä ei ole tarvittavia pääsyoikeuksia; tai</li>
                    <li>palvelimella tapahtui jokin virhe.</li>
                </ul>
</t:kehys>