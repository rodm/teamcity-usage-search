/*
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
 */

package com.github.rodm.teamcity.usage

import jetbrains.buildServer.controllers.BaseAjaxActionController
import jetbrains.buildServer.serverSide.ProjectManager
import jetbrains.buildServer.serverSide.SBuildServer
import jetbrains.buildServer.serverSide.SProject
import jetbrains.buildServer.web.openapi.ControllerAction
import jetbrains.buildServer.web.openapi.WebControllerManager
import org.jdom.Element

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class ParameterSearchController(buildServer: SBuildServer, controllerManager: WebControllerManager)
    : BaseAjaxActionController(controllerManager)
{
    init {
        controllerManager.registerController("/admin/usage.html", this)
        controllerManager.registerAction(this, ParameterSearchAction(buildServer.projectManager))
    }

    inner class ParameterSearchAction(private val projectManager: ProjectManager) : ControllerAction {

        override fun canProcess(request: HttpServletRequest): Boolean {
            return "search" == request.getParameter("action")
        }

        override fun process(request: HttpServletRequest, response: HttpServletResponse, ajaxResponse: Element?) {
            val searchFor = getParamName(request)
            val project = getProject(request)

            if (project != null) {
                val searcher = ParameterSearch(searchFor, project)
                val results = searcher.findMatches()
                ajaxResponse?.apply { results.serialize(ajaxResponse) }
            }
        }

        private fun getParamName(request: HttpServletRequest): String {
            return request.getParameter("paramName")
        }

        private fun getProject(request: HttpServletRequest): SProject? {
            return projectManager.findProjectByExternalId(request.getParameter("projectId"))
        }
    }
}
