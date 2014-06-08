<%--
    Document   : ketju
    Created on : 8.6.2014, 21:40:28
    Author     : John Lång <jllang@cs.helsinki.fi>
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"
        trimDirectiveWhitespaces="true" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<t:kehys otsikko="${otsikko}" fooruminNimi="Esimerkkifoorumi">
    <h2>${aihe}</h2>
    <p style="max-width: 800px">
        <em>
            ${alueet.get(0).annaNimi()}
            <c:forEach items="${alueet}" var="alue" begin="1">
                , alue.annaNimi()
            </c:forEach>
        </em>
    </p>
    <c:forEach items="${viestit}" var="viesti" varStatus="tila">
        <t:viesti numero="tila.getCount()"
                  kirjoittaja="viesti.annaKirjoittaja()"
                  listausnimi="&lt;nimi&gt;"
                  kirjoitettu="viesti.annaKirjoitettu()"
                  muokattu="viesti.annaMuokattu()"
                  moderoitu="viesti.annaModeroitu()">
            ${viesti.annaSisalto()}
        </t:viesti>
    </c:forEach>
</t:kehys>