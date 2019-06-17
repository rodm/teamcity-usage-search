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
import org.hamcrest.Matchers.*
import org.junit.Test

class ParameterMatcherTest {

    @Test
    fun `property value without parameter reference does not match`() {
        val value = "parameter"

        val names = ParameterMatcher("parameter").getMatchingNames(value)
        assertThat(names, hasSize(0))
    }

    @Test
    fun `property value with parameter reference does match`() {
        val value = "%parameter%"

        val names = ParameterMatcher("parameter").getMatchingNames(value)

        assertThat(names, hasSize(1))
        assertThat(names, contains(equalTo("parameter")))
    }

    @Test
    fun `property value with parameter reference and leading text matches`() {
        val value = "other value %parameter%"

        val names = ParameterMatcher("parameter").getMatchingNames(value)

        assertThat(names, contains(equalTo("parameter")))
    }

    @Test
    fun `property value with parameter reference and trailing text matches`() {
        val value = "%parameter% other value"

        val names = ParameterMatcher("parameter").getMatchingNames(value)

        assertThat(names, contains(equalTo("parameter")))
    }

    @Test
    fun `partial name search matches start of parameter name`() {
        val value = "%parameter%"

        val names = ParameterMatcher("parameter").getMatchingNames(value)

        assertThat(names, contains(equalTo("parameter")))
    }

    @Test
    fun `partial name search matches end of parameter name`() {
        val value = "%parameter%"

        val names = ParameterMatcher("parameter").getMatchingNames(value)

        assertThat(names, contains(equalTo("parameter")))
    }

    @Test
    fun `partial name search returns full name of reference parameter`() {
        val value = "-Dopt=%parameter%"

        val names = ParameterMatcher("param").getMatchingNames(value)

        assertThat(names, contains(equalTo("parameter")))
    }

    @Test
    fun `partial name search returns a list of matching parameter names`() {
        val value = "-Dopt1=%my.parameter% -Dopt2=%my.para% -Dopt3=%my.param%test"
        
        val names = ParameterMatcher("param").getMatchingNames(value)

        assertThat(names, hasSize(2))
        assertThat(names, containsInAnyOrder(equalTo("my.parameter"), equalTo("my.param")))
    }
}
