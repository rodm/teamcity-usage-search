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

import jetbrains.buildServer.controllers.XmlResponseUtil
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.jdom.Element
import org.junit.jupiter.api.Test

class SearchResultsTest {

    @Test
    fun `captures matching names by configuration section`() {
        val result = SearchResult("id", "name")

        result.namesFor("General Settings", listOf("param1", "param2"))
        
        assertThat(result.namesBySection.keys, hasSize(1))
        assertThat(result.namesBySection["General Settings"], containsInAnyOrder(equalTo("param1"), equalTo("param2")))
    }

    @Test
    fun `captures matching names by configuration in section order`() {
        val result = SearchResult("id", "name")

        result.namesFor("General Settings", listOf("param1"))
        result.namesFor("Build Features", listOf("param1"))

        val sections = result.namesBySection.keys.toList()
        assertThat(sections, hasSize(2))
        assertThat(sections[0], equalTo("General Settings"))
        assertThat(sections[1], equalTo("Build Features"))
    }

    @Test
    fun `captured matching names are not duplicated in a section`() {
        val result = SearchResult("id", "name")

        result.namesFor("Build Steps", listOf("param1"))
        result.namesFor("Build Steps", listOf("param1"))

        val buildSteps = result.namesBySection["Build Steps"]
        assertThat(buildSteps, hasSize(1))
    }

    @Test
    fun `empty matching names are not captured`() {
        val result = SearchResult("id", "name")

        result.namesFor("Build Steps", listOf())

        assertThat(result.namesBySection.keys, hasSize(0))
    }

    @Test
    fun `search result with no matches`() {
        val result = SearchResult("id", "name")

        assertThat(result.hasMatches(), `is`(false))
    }

    @Test
    fun `search result with matches`() {
        val result = SearchResult("id", "name")
        result.namesFor("General Settings", listOf("param1"))

        assertThat(result.hasMatches(), `is`(true))
    }

    @Test
    fun `serialize empty search results returns no result elements`() {
        val xmlResponse = XmlResponseUtil.newXmlResponse()
        val results = SearchResults(listOf())

        results.serialize(xmlResponse)

        val nodes = xmlResponse.getChildren("result")
        assertThat(nodes, hasSize(0))
    }

    @Test
    fun `serialize search results with matching build type returns result element`() {
        val xmlResponse = XmlResponseUtil.newXmlResponse()
        val results = SearchResults(listOf(SearchResult("extId", "build name")))

        results.serialize(xmlResponse)

        val nodes = xmlResponse.getChildren("result")
        assertThat(nodes, hasSize(1))
        val result = nodes[0] as Element
        assertThat(result.getAttributeValue("id") as String, equalTo("extId"))
        assertThat(result.getAttributeValue("name") as String, equalTo("build name"))
    }

    @Test
    fun `serialize search results returns result element for each build type`() {
        val xmlResponse = XmlResponseUtil.newXmlResponse()
        val results = SearchResults(
            listOf(
                SearchResult("extId1", "build name 1"),
                SearchResult("extId2", "build name 2")
            ))

        results.serialize(xmlResponse)

        val nodes = xmlResponse.getChildren("result")
        assertThat(nodes, hasSize(2))
        val result = nodes[1] as Element
        assertThat(result.getAttributeValue("id") as String, equalTo("extId2"))
        assertThat(result.getAttributeValue("name") as String, equalTo("build name 2"))
    }

    @Test
    fun `serialize search result outputs sections for matching parameters`() {
        val xmlResponse = XmlResponseUtil.newXmlResponse()
        val searchResult = SearchResult("extId", "build name")
        searchResult.namesFor("General Settings", listOf("param1", "param2"))
        searchResult.namesFor("Build Steps", listOf("param1", "param3"))
        val results = SearchResults(listOf(searchResult))

        results.serialize(xmlResponse)

        val nodes = xmlResponse.getChildren("result")
        val result = nodes[0] as Element
        val sections = result.getChildren("section").map { (it as Element).getAttributeValue("name") }
        assertThat(sections, contains(equalTo("General Settings"), equalTo("Build Steps")))
    }

    private fun namesFrom(element: Element): List<String> {
        return element.getChildren("name").map { (it as Element).getAttributeValue("value") }.toList()
    }

    @Test
    fun `serialize search result outputs names for each section for matching parameters`() {
        val xmlResponse = XmlResponseUtil.newXmlResponse()
        val searchResult = SearchResult("extId", "build name")
        searchResult.namesFor("General Settings", listOf("param1", "param2"))
        searchResult.namesFor("Build Steps", listOf("param1", "param3"))
        val results = SearchResults(listOf(searchResult))

        results.serialize(xmlResponse)

        val nodes = xmlResponse.getChildren("result")
        val result = nodes[0] as Element
        val sections = result.getChildren("section")
        val names1 = namesFrom(sections[0] as Element)
        assertThat(names1, contains(equalTo("param1"), equalTo("param2")))
        val names2 = namesFrom(sections[1] as Element)
        assertThat(names2, contains(equalTo("param1"), equalTo("param3")))
    }
}
