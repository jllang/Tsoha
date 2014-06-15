<%--
    Document   : ketju
    Created on : 8.6.2014, 21:40:28
    Author     : John Lång (jllang@cs.helsinki.fi)
--%>

<%@page import="java.sql.Timestamp"%>
<%@page import="java.util.List"%>
<%@page import="mallit.java.Jasen"%>
<%@page import="mallit.java.Viesti"%>
<%@page contentType="text/html" pageEncoding="UTF-8"
        trimDirectiveWhitespaces="true" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<t:kehys otsikko="${otsikko}" fooruminNimi="Esimerkkifoorumi">
            <h2><c:out value="${aihe}" escapeXml="true" /></h2>
            <p>
                <em>
                    <a href="alueet?kohde=${alueet[0].annaTunnus()}"
                       >${alueet[0].listausnimi()}</a>
                    <c:forEach items="${alueet}" var="alue" begin="1">
                        , <a href="alueet?kohde=${alue.annaTunnus()}">
                            ${alue.listausnimi()}</a>
                    </c:forEach>
                </em>
            </p>
            <c:forEach items="${viestit}" var="viesti" varStatus="tila">
            <div class="sisalto">
                <c:if test="${viesti.annaPoistettu() == null}">
                    <t:viesti listausnumero="${tila.getIndex()}"
                              kirjoittaja="${kirjoittajat[tila.getIndex()]}"
                              katselija="${katselija}" viesti="${viesti}"
                              omaViesti="${katselija.annaKayttajanumero() eq
                                           kirjoittajat[tila.getIndex()]
                                           .annaKayttajanumero()}"/>
                </c:if>
            </div>
            &nbsp;
            </c:forEach>
            <p>Sivu: 1</p>
</t:kehys>