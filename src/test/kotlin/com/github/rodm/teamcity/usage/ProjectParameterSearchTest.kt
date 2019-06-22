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
import org.junit.jupiter.api.Test

class ProjectParameterSearchTest {

    @Test
    fun `search for parameter referenced by in a project`() {
        val project = project().withOwnParameters(mapOf("param1" to "%parameter%"))

        val searchFor = "parameter"
        val searcher = ParameterSearch(searchFor, project)
        val matches = searcher.findMatches()

        assertThat(matches, hasSize(1))
        assertThat(matches[0], equalTo(searchResult(project)))
    }

    @Test
    fun `search for parameter not referenced in a project returns no matches`() {
        val project = project().withOwnParameters(mapOf("param1" to "value1"))

        val searchFor = "parameter"
        val searcher = ParameterSearch(searchFor, project)
        val matches = searcher.findMatches()

        assertThat(matches, hasSize(0))
    }
}
