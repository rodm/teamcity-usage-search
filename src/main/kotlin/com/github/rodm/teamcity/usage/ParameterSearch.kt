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

import jetbrains.buildServer.serverSide.SProject

class ParameterSearch(private val parameter: String, private val project: SProject) {

    fun findMatchingBuildTypes(): SearchResults {
        val results = linkedMapOf<String, SearchResult>()
        val matcher = ParameterMatcher(parameter)

        project.buildTypes.forEach { buildType ->
            val buildTypeResult = SearchResult(buildType.externalId, buildType.fullName)
            buildType.ownOptions.forEach { option ->
                val optionValue = buildType.getOption(option).toString()
                val names = matcher.getMatchingNames(optionValue)
                if (names.isNotEmpty()) {
                    buildTypeResult.names.addAll(names)
                    results.putIfAbsent(buildType.externalId, buildTypeResult)
                }
            }
            buildType.ownParameters.forEach { parameter ->
                val names = matcher.getMatchingNames(parameter.value)
                if (names.isNotEmpty()) {
                    buildTypeResult.names.addAll(names)
                    results.putIfAbsent(buildType.externalId, buildTypeResult)
                }
            }
            buildType.buildRunners.forEach { runner ->
                runner.parameters.forEach { parameter ->
                    val names = matcher.getMatchingNames(parameter.value)
                    if (names.isNotEmpty()) {
                        buildTypeResult.names.addAll(names)
                        results.putIfAbsent(buildType.externalId, buildTypeResult)
                    }
                }
            }
            buildType.buildFeatures.forEach { feature ->
                feature.parameters.forEach { parameter ->
                    val names = matcher.getMatchingNames(parameter.value)
                    if (names.isNotEmpty()) {
                        buildTypeResult.names.addAll(names)
                        results.putIfAbsent(buildType.externalId, buildTypeResult)
                    }
                }
            }
            buildType.ownDependencies.forEach { dependency ->
                dependency.ownOptions.forEach { option ->
                    val optionValue = dependency.getOption(option).toString()
                    val names = matcher.getMatchingNames(optionValue)
                    if (names.isNotEmpty()) {
                        buildTypeResult.names.addAll(names)
                        results.putIfAbsent(buildType.externalId, buildTypeResult)
                    }
                }
            }
            buildType.artifactDependencies.forEach { dependency ->
                val names = matcher.getMatchingNames(dependency.sourcePaths)
                if (names.isNotEmpty()) {
                    buildTypeResult.names.addAll(names)
                    results.putIfAbsent(buildType.externalId, buildTypeResult)
                }
            }
            buildType.requirements.forEach { requirement ->
                val names = matcher.getMatchingNames(requirement.propertyValue ?: "")
                if (names.isNotEmpty()) {
                    buildTypeResult.names.addAll(names)
                    results.putIfAbsent(buildType.externalId, buildTypeResult)
                }
            }
        }
        return SearchResults(results.values)
    }
}
