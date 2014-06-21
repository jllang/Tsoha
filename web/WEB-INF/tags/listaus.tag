<%--
    Document   : listaus
    Created on : 6.6.2014, 16:57:05
    Author     : John Lång (jllang@cs.helsinki.fi)
--%>

<%@tag description="Listaus dynaamisella sisällöllä." pageEncoding="UTF-8"
       trimDirectiveWhitespaces="true" %>
<%@tag import="kontrollerit.tyypit.ListaAlkio"%>
<%@tag import="java.util.List"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags/" %>
<%@attribute name="listanNimi" type="String" required="true"%>
<%@attribute name="listanKuvaus" type="String" required="false"%>
<%@attribute name="otsikot" type="String[]" required="true"%>
<%@attribute name="lista" type="List<ListaAlkio>" required="true"%>
<%@attribute name="lisakenttia" type="Integer" required="true"%>
<%@attribute name="sivu" type="Integer" required="false"%>
<%@attribute name="sivuja" type="Integer" required="false"%>
<%@attribute name="hakupalkki" type="Boolean" required="true"%>
            <h2>${listanNimi}</h2>
            <c:if test="${listanKuvaus ne null and not empty listanKuvaus}">
                <p><em>${listanKuvaus}</em></p>
            </c:if>
            <c:if test="${hakupalkki}">
                <t:hakupalkki hakusana="${hakusana}" />
            </c:if>
            <div class="sisalto">
                <table>
                    <tr>
                        <c:forEach items="${otsikot}" var="otsikko">
                        <th>${otsikko}</th>
                        </c:forEach>
                    </tr>
                    <c:forEach items="${lista}" var="alkio">
                        <t:rivi alkio="${alkio}" lisakenttia="${lisakenttia}" />
                    </c:forEach>
                </table>
            </div>
            <c:if test="${sivu ne null}"><p>Sivu: ${sivu}</p></c:if>
            <%=""%>
