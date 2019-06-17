<%--
  * Copyright 2019 Rod MacKenzie.
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  *     http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  --%>

<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="admin" tagdir="/WEB-INF/tags/admin" %>
<%@ include file="/include-internal.jsp" %>

<%--@elvariable id="results" type="kotlin.collections.List<com.github.rodm.teamcity.usage.MatchedBuildType>"--%>
<c:choose>
    <c:when test="${empty results}">
        <div><c:out value="No build configurations found"/></div>
    </c:when>

    <c:otherwise>
        <table class="parametersTable" style="width: 100%">
            <c:forEach var="buildType" items="${results}">
                <tr>
                    <td>
                        <admin:editBuildTypeLink buildTypeId="${buildType.externalId}">
                            <c:out value="${buildType.fullName}"/>
                        </admin:editBuildTypeLink>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </c:otherwise>
</c:choose>
