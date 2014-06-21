<%--
    Document   : hakulomake
    Created on : 21.6.2014, 12:22:34
    Author     : John Lång <jllang@cs.helsinki.fi>
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"
        trimDirectiveWhitespaces="true" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:kehys otsikko="${otsikko}" fooruminNimi="Esimerkkifoorumi">
    <t:listaus listanNimi="Haku" lista="${tuloslista}" otsikot="${tulosotsikot}"
               lisakenttia="1" hakupalkki="true" sivu="1" />
</t:kehys>