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

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.contains
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.jupiter.api.Test

class BuildTemplateParameterSearchTest {

    @Test
    fun `search for parameter not referenced by a build template returns no matches`() {
        val buildTemplate = buildTemplate().withOwnParameters(mapOf("param1" to "value1"))
        val project = project().withBuildTemplate(buildTemplate)

        val searchFor = "parameter"
        val searcher = ParameterSearch(searchFor, project)
        val matches = searcher.findMatchingBuildTypes()

        assertThat(matches, hasSize(0))
    }

    @Test
    fun `search for parameter referenced by a build template returns matched build type`() {
        val buildTemplate = buildTemplate().withOwnParameters(mapOf("param1" to "%parameter%"))
        val project = project().withBuildTemplate(buildTemplate)

        val searchFor = "parameter"
        val searcher = ParameterSearch(searchFor, project)
        val matches = searcher.findMatchingBuildTypes()

        assertThat(matches, hasSize(1))
        assertThat(matches[0], equalTo(searchResult(buildTemplate)))
    }

    @Test
    fun `search for parameter referenced by a build template thru inheritance returns no matches`() {
        val buildTemplate = buildTemplate()
        buildTemplate.setParameters(mapOf("param1" to "%parameter%"))
        val project = project().withBuildTemplate(buildTemplate)

        val searchFor = "parameter"
        val searcher = ParameterSearch(searchFor, project)
        val matches = searcher.findMatchingBuildTypes()

        assertThat(matches, hasSize(0))
    }

    @Test
    fun `search for parameter returns result with matching names`() {
        val buildTemplate = buildTemplate().withOwnParameters(mapOf("param1" to "%parameter1%", "param2" to "%prop2% %param3%"))
        val project = project().withBuildTemplate(buildTemplate)

        val searchFor = "param"
        val searcher = ParameterSearch(searchFor, project)
        val matches = searcher.findMatchingBuildTypes()

        assertThat(matches[0], equalTo(searchResult(buildTemplate)))
        assertThat(matches[0].namesBySection["Parameters"], containsInAnyOrder(equalTo("parameter1"), equalTo("param3")))
    }
    
    @Test
    fun `search for parameter returns build template when found in build steps`() {
        val buildTemplate = buildTemplate()
        buildTemplate.addBuildRunner("name", "type", mutableMapOf("param1" to "%parameter%"))
        val project = project().withBuildTemplate(buildTemplate)

        val searchFor = "parameter"
        val searcher = ParameterSearch(searchFor, project)
        val matches = searcher.findMatchingBuildTypes()

        assertThat(matches, hasSize(1))
        assertThat(matches[0], equalTo(searchResult(buildTemplate)))
        assertThat(matches[0].namesBySection["Build Steps"], contains(equalTo("parameter")))
    }

    @Test
    fun `search for parameter returns no matches when not found in build steps`() {
        val buildTemplate = buildTemplate()
        buildTemplate.addBuildRunner("name", "type", mutableMapOf("param1" to "value1"))
        val project = project().withBuildTemplate(buildTemplate)

        val searchFor = "parameter"
        val searcher = ParameterSearch(searchFor, project)
        val matches = searcher.findMatchingBuildTypes()

        assertThat(matches, hasSize(0))
    }

    @Test
    fun `search for parameter returns build template when found in build feature`() {
        val feature = buildFeature().withParameters(mapOf("param1" to "%parameter%"))
        val buildTemplate = buildTemplate().withBuildFeature(feature)
        val project = project().withBuildTemplate(buildTemplate)

        val searchFor = "parameter"
        val searcher = ParameterSearch(searchFor, project)
        val matches = searcher.findMatchingBuildTypes()

        assertThat(matches, hasSize(1))
        assertThat(matches[0], equalTo(searchResult(buildTemplate)))
        assertThat(matches[0].namesBySection["Build Features"], contains(equalTo("parameter")))
    }

    @Test
    fun `search for parameter returns build template when found in agent requirements`() {
        val requirement = requirement("param", "%parameter%")
        val buildTemplate = buildTemplate().withRequirement(requirement)
        val project = project().withBuildTemplate(buildTemplate)

        val searchFor = "parameter"
        val searcher = ParameterSearch(searchFor, project)
        val matches = searcher.findMatchingBuildTypes()

        assertThat(matches, hasSize(1))
        assertThat(matches[0], equalTo(searchResult(buildTemplate)))
        assertThat(matches[0].namesBySection["Agent Requirements"], contains(equalTo("parameter")))
    }

    @Test
    fun `search for parameter returns build template when found in general settings`() {
        val buildTemplate = buildTemplate().withOption("name", "%parameter%")
        val project = project().withBuildTemplate(buildTemplate)

        val searchFor = "parameter"
        val searcher = ParameterSearch(searchFor, project)
        val matches = searcher.findMatchingBuildTypes()

        assertThat(matches, hasSize(1))
        assertThat(matches[0], equalTo(searchResult(buildTemplate)))
        assertThat(matches[0].namesBySection["General Settings"], contains(equalTo("parameter")))
    }

    @Test
    fun `search for parameter returns build template when found in a dependency`() {
        val dependency = dependency().withOption("name", "%parameter%")
        val buildTemplate = buildTemplate().withDependency(dependency)
        val project = project().withBuildTemplate(buildTemplate)

        val searchFor = "parameter"
        val searcher = ParameterSearch(searchFor, project)
        val matches = searcher.findMatchingBuildTypes()

        assertThat(matches, hasSize(1))
        assertThat(matches[0], equalTo(searchResult(buildTemplate)))
        assertThat(matches[0].namesBySection["Dependencies"], contains(equalTo("parameter")))
    }

    @Test
    fun `search for parameter returns build template when found in an artifact dependency`() {
        val dependency = artifactDependency().withSourcePaths("%paths.parameter%")
        val buildTemplate = buildTemplate().withArtifactDependency(dependency)
        val project = project().withBuildTemplate(buildTemplate)

        val searchFor = "parameter"
        val searcher = ParameterSearch(searchFor, project)
        val matches = searcher.findMatchingBuildTypes()

        assertThat(matches, hasSize(1))
        assertThat(matches[0], equalTo(searchResult(buildTemplate)))
        assertThat(matches[0].namesBySection["Dependencies"], contains(equalTo("paths.parameter")))
    }
}
