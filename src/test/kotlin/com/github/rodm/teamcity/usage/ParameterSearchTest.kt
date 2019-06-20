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
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.sameInstance
import org.junit.jupiter.api.Test

class ParameterSearchTest {

    @Test
    fun `search for parameter not referenced by a build configuration returns no matches`() {
        val buildType = buildType().withOwnParameters(mapOf("param1" to "value1"))
        val project = project().withBuildType(buildType)

        val searchFor = "parameter"
        val searcher = ParameterSearch(searchFor, project)
        val matches = searcher.findMatchingBuildTypes()

        assertThat(matches, hasSize(0))
    }

    @Test
    fun `search for parameter referenced by a build configuration returns matched build type`() {
        val buildType = buildType().withOwnParameters(mapOf("param1" to "%parameter%"))
        val project = project().withBuildType(buildType)

        val searchFor = "parameter"
        val searcher = ParameterSearch(searchFor, project)
        val matches = searcher.findMatchingBuildTypes()

        assertThat(matches, hasSize(1))
        assertThat(matches[0], sameInstance<SBuildType>(buildType))
    }

    @Test
    fun `search for parameter referenced by a build configuration thru inheritance returns no matches`() {
        val buildType = buildType()
        buildType.setParameters(mapOf("param1" to "%parameter%"))
        val project = project().withBuildType(buildType)

        val searchFor = "parameter"
        val searcher = ParameterSearch(searchFor, project)
        val matches = searcher.findMatchingBuildTypes()

        assertThat(matches, hasSize(0))
    }

    @Test
    fun `search for parameter returns build configuration when found in build steps`() {
        val buildType = buildType()
        buildType.addBuildRunner("name", "type", mutableMapOf("param1" to "%parameter%"))
        val project = project().withBuildType(buildType)

        val searchFor = "parameter"
        val searcher = ParameterSearch(searchFor, project)
        val matches = searcher.findMatchingBuildTypes()

        assertThat(matches, hasSize(1))
        assertThat(matches[0], sameInstance<SBuildType>(buildType))
    }

    @Test
    fun `search for parameter returns no matches when not found in build steps`() {
        val buildType = buildType()
        buildType.addBuildRunner("name", "type", mutableMapOf("param1" to "value1"))
        val project = project().withBuildType(buildType)

        val searchFor = "parameter"
        val searcher = ParameterSearch(searchFor, project)
        val matches = searcher.findMatchingBuildTypes()

        assertThat(matches, hasSize(0))
    }

    @Test
    fun `search for parameter returns build configuration when found in build feature`() {
        val feature = buildFeature().withParameters(mapOf("param1" to "%parameter%"))
        val buildType = buildType().withBuildFeature(feature)
        val project = project().withBuildType(buildType)

        val searchFor = "parameter"
        val searcher = ParameterSearch(searchFor, project)
        val matches = searcher.findMatchingBuildTypes()

        assertThat(matches, hasSize(1))
        assertThat(matches[0], sameInstance<SBuildType>(buildType))
    }

    @Test
    fun `search for parameter returns build configuration when found in agent requirements`() {
        val requirement = requirement("param", "%parameter%")
        val buildType = buildType().withRequirement(requirement)
        val project = project().withBuildType(buildType)

        val searchFor = "parameter"
        val searcher = ParameterSearch(searchFor, project)
        val matches = searcher.findMatchingBuildTypes()

        assertThat(matches, hasSize(1))
        assertThat(matches[0], sameInstance<SBuildType>(buildType))
    }

    @Test
    fun `search for parameter returns build configuration when found in general settings`() {
        val buildType = buildType().withOption("name", "%parameter%")
        val project = project().withBuildType(buildType)

        val searchFor = "parameter"
        val searcher = ParameterSearch(searchFor, project)
        val matches = searcher.findMatchingBuildTypes()

        assertThat(matches, hasSize(1))
        assertThat(matches[0], sameInstance<SBuildType>(buildType))
    }

    @Test
    fun `search for parameter returns build configuration when found in a dependency`() {
        val dependency = dependency().withOption("name", "%parameter%")
        val buildType = buildType().withDependency(dependency)
        val project = project().withBuildType(buildType)

        val searchFor = "parameter"
        val searcher = ParameterSearch(searchFor, project)
        val matches = searcher.findMatchingBuildTypes()

        assertThat(matches, hasSize(1))
        assertThat(matches[0], sameInstance<SBuildType>(buildType))
    }

    @Test
    fun `search for parameter returns build configuration when found in an artifact dependency`() {
        val dependency = artifactDependency().withSourcePaths("%paths.parameter%")
        val buildType = buildType().withArtifactDependency(dependency)
        val project = project().withBuildType(buildType)

        val searchFor = "parameter"
        val searcher = ParameterSearch(searchFor, project)
        val matches = searcher.findMatchingBuildTypes()

        assertThat(matches, hasSize(1))
        assertThat(matches[0], sameInstance<SBuildType>(buildType))
    }
}
