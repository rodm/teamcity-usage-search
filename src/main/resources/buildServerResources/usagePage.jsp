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

<%@ include file="/include-internal.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="admin" tagdir="/WEB-INF/tags/admin" %>

<jsp:useBean id="currentProject" type="jetbrains.buildServer.serverSide.SProject" scope="request"/>

<div class="section noMargin">
  <h2 class="noBorder">Parameter Search</h2>
  <bs:smallNote>
      Search for parameters referenced by build configurations, build templates and project parameters.
  </bs:smallNote>

  <div id="search-container">
    <div class="actionBar">
      <span class="nowrap">
        <label class="firstLabel" for="paramName" id="paramNameLabel">Parameter: </label>
        <forms:textField className="actionInput" name="paramName" noAutoComplete="true" />
      </span>

      <input id="searchButton" class="btn btn_mini" type="button" value="Search"
             onclick="return BS.UsageSearch.search('${currentProject.externalId}');"/>

      <div id="paramNameError" class="error" style="display: none;">
        A parameter name must be specified and be a least 2 characters long
      </div>
    </div>
  </div>

  <script type="text/javascript">
    BS.UsageSearch.initForm();
  </script>

  <div id="errors"></div>

  <div>
      <table class="usageResultsTable" style="width: 100%">
          <tbody id="usageResults">
          </tbody>
      </table>
  </div>
</div>

<div id="usageResultsTemplate" style="display:none">
    <table>
        <tbody>
            <tr id="usageResultBuildTypeTemplate">
                <td><admin:editBuildTypeLink buildTypeId="##EXTERNAL_ID##">NAME</admin:editBuildTypeLink></td>
            </tr>
            <tr id="usageResultBuildTemplateTemplate">
                <td><admin:editTemplateLink templateId="##EXTERNAL_ID##">NAME</admin:editTemplateLink></td>
            </tr>
            <tr id="usageResultProjectTemplate">
                <td><admin:editProjectLink projectId="##EXTERNAL_ID##">NAME</admin:editProjectLink></td>
            </tr>
        </tbody>
    </table>
</div>
