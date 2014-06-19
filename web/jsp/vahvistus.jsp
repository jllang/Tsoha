<%--
    Document   : vahvistus
    Created on : 19.6.2014, 11:47:22
    Author     : John Lång <jllang@cs.helsinki.fi>
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"
        trimDirectiveWhitespaces="true" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:kehys otsikko="${otsikko}" fooruminNimi="Esimerkkifoorumi">
    <h2>Toimenpiteen vahvistus</h2>
    <div class="pariton sisalto">
        <p>Haluatteko varmasti ${toiminnonKuvaus}?</p>
        <p>
        <form action="${param.toiminto}?ketju=${param.ketju}&viesti=${param.viesti}"
              method="POST">
            <c:if test="${autentikoitava}">
                <%-- Toimenpide (esim. alueen poisto) vaatii salasanan syöttämisen.
                Tähän kohtaan olisi tarkoitus tulla tekstikenttä salasanalle. --%>
            </c:if>
            <input type="hidden" name="vahvistettu" value="tosi">
            <input type="submit" title="Vahvista toimenpide" value="Vahvista">
            </form>
        </p>
    </div>
</t:kehys>