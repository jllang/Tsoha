<%--
    Document   : listaus
    Created on : 6.6.2014, 16:57:05
    Author     : John Lång <jllang@cs.helsinki.fi>
--%>

<%@tag description="Listaus dynaamisella sisällöllä." pageEncoding="UTF-8"
       trimDirectiveWhitespaces="true" %>
<%@tag import="kontrollerit.tyypit.ListaAlkio"%>
<%@tag import="java.util.List"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags/" %>
<%@attribute name="listanNimi" type="String" %>
<%@attribute name="otsikot" type="String[]" %>
<%@attribute name="lista" type="List<ListaAlkio>" %>
<%@attribute name="lisakenttia" type="Integer"%>
            <div class="sisalto">
                <h2>${ListanNimi}</h2>
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
