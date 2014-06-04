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
    <t:virhe>${tyyppi} ${kohde} ei löydy!</t:virhe>
</t:kehys>