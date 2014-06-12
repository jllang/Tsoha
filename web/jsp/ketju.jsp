<%--
    Document   : ketju
    Created on : 8.6.2014, 21:40:28
    Author     : John Lång <jllang@cs.helsinki.fi>
--%>

<%@page import="java.util.List"%>
<%@page import="mallit.java.Viesti"%>
<%@page contentType="text/html" pageEncoding="UTF-8"
        trimDirectiveWhitespaces="true" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<t:kehys otsikko="${otsikko}" fooruminNimi="Esimerkkifoorumi">
            <h2>${aihe}</h2>
            <p>
                <em>
                    <a href="alueet?kohde=${alueet.get(0)}">${alueet.get(0)}</a>
                    <c:forEach items="${alueet}" var="alue" begin="1">
                        , <a href="alueet?kohde=${alue}">${alue}</a>
                    </c:forEach>
                </em>
            </p>
            <div class="sisalto">
            <c:forEach items="${viestit}" var="viesti" varStatus="tila">
                <t:viesti ketjunTunnus="${ketjunTunnus}"
                          listausnumero="${tila.getCount()}"
                          kirjoittaja="${viesti.annaKirjoittaja()}"
                          listausnimi="&lt;nimi&gt;"
                          kirjoitettu="${viesti.annaKirjoitettu()}"
                          muokattu="${viesti.annaMuokattu()}"
                          moderoitu="${viesti.annaModeroitu()}">
                    ${viesti.annaSisalto()}
                </t:viesti>
            </c:forEach>
            </div>
</t:kehys>