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

import jetbrains.buildServer.serverSide.SBuildType
import jetbrains.buildServer.serverSide.SProject
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.sameInstance
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock

class ParameterSearchTest {

    class FakeProject: SProject by mock(SProject::class.java) {

        private val buildTypes: MutableList<SBuildType> = mutableListOf()

        override fun getBuildTypes(): MutableList<SBuildType> {
            return buildTypes
        }
    }

    class FakeBuildType: SBuildType by mock(SBuildType::class.java) {

        private val ownParams: MutableMap<String, String> = mutableMapOf()
        private val params: MutableMap<String, String> = mutableMapOf()

        override fun getOwnParameters(): MutableMap<String, String> {
            return ownParams
        }

        fun setOwnParameters(parameters: Map<String, String>) {
            ownParams.clear()
            ownParams.putAll(parameters)
        }

        override fun getParameters(): MutableMap<String, String> {
            val result = mutableMapOf<String, String>()
            result.putAll(params)
            result.putAll(ownParams)
            return result
        }

        fun setParameters(parameters: Map<String, String>) {
            params.clear()
            params.putAll(parameters)
        }
    }

    @Test
    fun `search for parameter not referenced by a build configuration returns no matches`() {
        val buildType = FakeBuildType()
        buildType.setOwnParameters(mapOf("param1" to "value1"))
        val project = FakeProject()
        project.buildTypes.add(buildType)

        val searchFor = "parameter"
        val searcher = ParameterSearch(searchFor, project)
        val matches = searcher.findMatchingBuildTypes()

        assertThat(matches, hasSize(0))
    }

    @Test
    fun `search for parameter referenced by a build configuration returns matched build type`() {
        val buildType = FakeBuildType()
        buildType.setOwnParameters(mapOf("param1" to "%parameter%"))
        val project = FakeProject()
        project.buildTypes.add(buildType)

        val searchFor = "parameter"
        val searcher = ParameterSearch(searchFor, project)
        val matches = searcher.findMatchingBuildTypes()

        assertThat(matches, hasSize(1))
        assertThat(matches[0], sameInstance<SBuildType>(buildType))
    }

    @Test
    fun `search for parameter referenced by a build configuration thru inheritance returns no matches`() {
        val buildType = FakeBuildType()
        buildType.setParameters(mapOf("param1" to "%parameter%"))
        val project = FakeProject()
        project.buildTypes.add(buildType)

        val searchFor = "parameter"
        val searcher = ParameterSearch(searchFor, project)
        val matches = searcher.findMatchingBuildTypes()

        assertThat(matches, hasSize(0))
    }
}
