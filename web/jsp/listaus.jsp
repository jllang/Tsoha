<%--
    Document   : listaus
    Created on : 4.6.2014, 10:35:51
    Author     : John Lång <jllang@cs.helsinki.fi>
--%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@page contentType="text/html" pageEncoding="UTF-8"
        trimDirectiveWhitespaces="true"%>
<t:kehys otsikko="Esimerkkifoorumi: Listaus" fooruminNimi="Esimerkkifoorumi">
            <div class="sisalto">
                <table>
                    <tr><th>${listanNimi}</th></tr>
                    <c:forEach items="${lista}" var="alkio">
                    <tr><td class="${alkio.parillisuus}">
                    <c:if test="${not empty alkio.osoite}">
                            <a href="${alkio.osoite}" target="_blank">
                                ${alkio.nimi}</a></td></tr>
                    </c:if>
                    </c:forEach>
                </table>
            </div>
</t:kehys>