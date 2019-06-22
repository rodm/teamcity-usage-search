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

class ParameterSearch(parameter: String, private val project: SProject) {

    private val matcher = ParameterMatcher(parameter)

    fun findMatches(): SearchResults {
        val results = mutableListOf<SearchResult>()
        findMatches(project, results)
        return SearchResults(results)
    }

    private fun findMatches(project: SProject, results: MutableList<SearchResult>) {
        val projectResult = SearchResult(project.externalId, project.fullName, Type.PROJECT)
        project.ownParameters.forEach { parameter ->
            val names = matcher.getMatchingNames(parameter.value)
            projectResult.namesFor("Parameters", names)
        }
        if (projectResult.hasMatches()) {
            results.add(projectResult)
        }

        project.ownBuildTypeTemplates.forEach { template ->
            val result = SearchResult(template.externalId, template.fullName, Type.TEMPLATE)

            template.ownOptions.forEach { option ->
                val optionValue = template.getOption(option).toString()
                val names = matcher.getMatchingNames(optionValue)
                result.namesFor("General Settings", names)
            }
            template.ownParameters.forEach { parameter ->
                val names = matcher.getMatchingNames(parameter.value)
                result.namesFor("Parameters", names)
            }

            template.buildRunners.forEach { runner ->
                runner.parameters.forEach { parameter ->
                    val names = matcher.getMatchingNames(parameter.value)
                    result.namesFor("Build Steps", names)
                }
            }
            template.buildFeatures.forEach { feature ->
                feature.parameters.forEach { parameter ->
                    val names = matcher.getMatchingNames(parameter.value)
                    result.namesFor("Build Features", names)
                }
            }
            template.dependencies.forEach { dependency ->
                dependency.ownOptions.forEach { option ->
                    val optionValue = dependency.getOption(option).toString()
                    val names = matcher.getMatchingNames(optionValue)
                    result.namesFor("Dependencies", names)
                }
            }
            template.artifactDependencies.forEach { dependency ->
                val names = matcher.getMatchingNames(dependency.sourcePaths)
                result.namesFor("Dependencies", names)
            }
            template.requirements.forEach { requirement ->
                val names = matcher.getMatchingNames(requirement.propertyValue ?: "")
                result.namesFor("Agent Requirements", names)
            }
            if (result.hasMatches()) {
                results.add(result)
            }
        }
        project.buildTypes.forEach { buildType ->
            val result = SearchResult(buildType.externalId, buildType.fullName)
            buildType.ownOptions.forEach { option ->
                val optionValue = buildType.getOption(option).toString()
                val names = matcher.getMatchingNames(optionValue)
                result.namesFor("General Settings", names)
            }
            buildType.ownParameters.forEach { parameter ->
                val names = matcher.getMatchingNames(parameter.value)
                result.namesFor("Parameters", names)
            }
            buildType.buildRunners.forEach { runner ->
                runner.parameters.forEach { parameter ->
                    val names = matcher.getMatchingNames(parameter.value)
                    result.namesFor("Build Steps", names)
                }
            }
            buildType.buildFeatures.forEach { feature ->
                feature.parameters.forEach { parameter ->
                    val names = matcher.getMatchingNames(parameter.value)
                    result.namesFor("Build Features", names)
                }
            }
            buildType.ownDependencies.forEach { dependency ->
                dependency.ownOptions.forEach { option ->
                    val optionValue = dependency.getOption(option).toString()
                    val names = matcher.getMatchingNames(optionValue)
                    result.namesFor("Dependencies", names)
                }
            }
            buildType.artifactDependencies.forEach { dependency ->
                val names = matcher.getMatchingNames(dependency.sourcePaths)
                result.namesFor("Dependencies", names)
            }
            buildType.requirements.forEach { requirement ->
                val names = matcher.getMatchingNames(requirement.propertyValue ?: "")
                result.namesFor("Agent Requirements", names)
            }
            if (result.hasMatches()) {
                results.add(result)
            }
        }

        project.ownProjects.forEach { subProject ->
            findMatches(subProject, results)
        }
    }
}
