<%@page import="mallit.tyypit.Kayttajataso"%>
<%@page contentType="text/html" pageEncoding="UTF-8"
        trimDirectiveWhitespaces="true" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:kehys otsikko="Esimerkkifoorumi: Etusivu" fooruminNimi="Esimerkkifoorumi">
    <h2>Etusivu</h2>
    <jsp:include page="/listaus" />
</t:kehys>