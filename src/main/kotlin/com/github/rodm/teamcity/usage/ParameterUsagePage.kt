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

import jetbrains.buildServer.controllers.admin.projects.EditProjectTab
import jetbrains.buildServer.web.openapi.PagePlaces
import jetbrains.buildServer.web.openapi.PluginDescriptor

class ParameterUsagePage(pagePlaces: PagePlaces,
                         descriptor: PluginDescriptor) :
        EditProjectTab(pagePlaces, descriptor.pluginName, descriptor.getPluginResourcesPath("usagePage.jsp"), "Parameter Usage")
{
    init {
        addJsFile(descriptor.getPluginResourcesPath("usage.js"))
    }
}
