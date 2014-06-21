<%--
    Document   : alue
    Created on : 19.6.2014, 19:21:08
    Author     : John Lång <jllang@cs.helsinki.fi>
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"
        trimDirectiveWhitespaces="true" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:kehys otsikko="${otsikko}" fooruminNimi="Esimerkkifoorumi">
    <t:listaus listanNimi="${listanNimi}" listanKuvaus="${listanKuvaus}"
               lista="${ketjulista}" lisakenttia="2"
               otsikot="${ketjulistanOtsikot}" sivu="${sivu}" hakupalkki="false"
               />
</t:kehys>