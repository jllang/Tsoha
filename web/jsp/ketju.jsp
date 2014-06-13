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
                    <a href="alueet?kohde=${alueet.get(0).annaTunnus()}"
                       >${alueet.get(0).listausnimi()}</a>
                    <c:forEach items="${alueet}" var="alue" begin="1">
                        , <a href="alueet?kohde=${alue.annaTunnus()}">
                            ${alue.listausnimi()}</a>
                    </c:forEach>
                </em>
            </p>
            <c:forEach items="${viestit}" var="viesti" varStatus="tila">
            <div class="sisalto">
                <c:if test="${viesti.annaPoistettu() == null}">
                <t:viesti ketjunTunnus="${ketjunTunnus}"
                          viestinumero="${viesti.annaNumero()}"
                          listausnumero="${tila.getCount()}"
                          kirjoittaja="${kirjoittajat.get(tila.getCount() - 1)}"
                          kirjoitettu="${viesti.annaKirjoitettu()}"
                          muokattu="${viesti.annaMuokattu()}"
                          moderoitu="${viesti.annaModeroitu()}"
                          sisalto="${viesti.annaSisalto()}" />
                </c:if>
            </div>
            &nbsp;
            </c:forEach>
            <p>Sivu: 1</p>
</t:kehys>