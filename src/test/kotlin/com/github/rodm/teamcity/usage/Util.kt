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

import jetbrains.buildServer.requirements.Requirement
import jetbrains.buildServer.requirements.RequirementType.EQUALS
import jetbrains.buildServer.serverSide.*
import jetbrains.buildServer.serverSide.artifacts.SArtifactDependency
import jetbrains.buildServer.serverSide.dependency.Dependency
import jetbrains.buildServer.util.Option
import jetbrains.buildServer.util.StringOption
import org.mockito.Mockito

fun searchResult(buildType: BuildTypeIdentity): SearchResult {
    val type = when (buildType) {
        is BuildTypeTemplate -> Type.TEMPLATE
        else -> Type.BUILD
    }
    return SearchResult(buildType.externalId, buildType.fullName, type)
}

fun searchResult(project: SProject): SearchResult = SearchResult(project.externalId, project.fullName, Type.PROJECT)

fun project(): FakeProject {
    return FakeProject()
}

fun buildType(): FakeBuildType {
    return FakeBuildType()
}

fun buildTemplate(): FakeBuildTemplate = FakeBuildTemplate()

fun failureCondition(): FakeBuildFeature = FakeBuildFeature(FailureConditionBuildFeature())

fun buildFeature(): FakeBuildFeature = FakeBuildFeature()

fun requirement(name: String, value: String): Requirement {
    return Requirement(name, value, EQUALS)
}

fun dependency(): FakeDependency = FakeDependency()

fun artifactDependency(): FakeArtifactDependency = FakeArtifactDependency()

class FakeProject: SProject by Mockito.mock(SProject::class.java) {

    private val subProjects = mutableListOf<SProject>()
    private val buildTypes: MutableList<SBuildType> = mutableListOf()
    private val ownBuildTemplates = mutableListOf<BuildTypeTemplate>()
    private val ownParams: MutableMap<String, String> = mutableMapOf()

    override fun getExternalId(): String = "projectId"
    override fun getFullName(): String = "project name"
    override fun getOwnProjects(): MutableList<SProject> = subProjects
    override fun getBuildTypes(): MutableList<SBuildType> = buildTypes
    override fun getOwnBuildTypeTemplates(): MutableList<BuildTypeTemplate> = ownBuildTemplates
    override fun getOwnParameters(): MutableMap<String, String> = ownParams

    fun withSubProject(project: SProject): FakeProject {
        subProjects.add(project)
        return this
    }

    fun withBuildType(buildType: SBuildType): FakeProject {
        buildTypes.add(buildType)
        return this
    }

    fun withBuildTemplate(template: BuildTypeTemplate): FakeProject {
        ownBuildTemplates.add(template)
        return this
    }

    fun withOwnParameters(parameters: Map<String, String>): FakeProject {
        ownParams.clear()
        ownParams.putAll(parameters)
        return this
    }
}

class FakeBuildType: SBuildType by Mockito.mock(SBuildType::class.java) {

    private var id: String = ""
    private var name: String = ""
    private val ownParams: MutableMap<String, String> = mutableMapOf()
    private val params: MutableMap<String, String> = mutableMapOf()
    private val runners = mutableListOf<SBuildRunnerDescriptor>()
    private val features = mutableListOf<SBuildFeatureDescriptor>()
    private val requirements = mutableListOf<Requirement>()
    private val ownOptions = mutableMapOf<String, String>()
    private val ownDependencies = mutableListOf<Dependency>()
    private val artifactDependencies = mutableListOf<SArtifactDependency>()

    override fun getExternalId(): String = id
    override fun getFullName(): String = name

    override fun addBuildRunner(name: String, runnerType: String, parameters: MutableMap<String, String>): SBuildRunnerDescriptor {
        val runner = FakeBuildRunner()
        runner.setParameters(parameters)
        runners.add(runner)
        return runner
    }

    override fun getBuildRunners(): MutableList<SBuildRunnerDescriptor> {
        return runners
    }

    override fun getBuildFeatures(): MutableCollection<SBuildFeatureDescriptor> {
        return features
    }

    override fun getRequirements(): MutableList<Requirement> = requirements

    override fun getOwnParameters(): MutableMap<String, String> {
        return ownParams
    }

    override fun getParameters(): MutableMap<String, String> {
        val result = mutableMapOf<String, String>()
        result.putAll(params)
        result.putAll(ownParams)
        return result
    }

    override fun getOwnOptions(): MutableCollection<Option<Any>> {
        return ownOptions.keys.map { key -> StringOption(key, "") }.toMutableList() as MutableCollection<Option<Any>>
    }

    override fun <T : Any?> getOption(option: Option<T>): T {
        return (ownOptions[option.key] ?: option.defaultValue) as T
    }

    override fun getOwnDependencies(): MutableList<Dependency> {
        return ownDependencies
    }

    override fun getArtifactDependencies(): MutableList<SArtifactDependency> = artifactDependencies
    
    fun setParameters(parameters: Map<String, String>) {
        params.clear()
        params.putAll(parameters)
    }

    fun withOwnParameters(parameters: Map<String, String>): SBuildType {
        ownParams.clear()
        ownParams.putAll(parameters)
        return this
    }

    fun withFailureCondition(condition: SBuildFeatureDescriptor): FakeBuildType = withBuildFeature(condition)

    fun withBuildFeature(feature: SBuildFeatureDescriptor): FakeBuildType {
        features.add(feature)
        return this
    }

    fun withRequirement(requirement: Requirement): FakeBuildType {
        requirements.add(requirement)
        return this
    }

    fun withOption(name: String, value: String): FakeBuildType {
        ownOptions[name] = value
        return this
    }

    fun withDependency(dependency: Dependency): FakeBuildType {
        ownDependencies.add(dependency)
        return this
    }

    fun withArtifactDependency(dependency: SArtifactDependency): FakeBuildType {
        artifactDependencies.add(dependency)
        return this
    }
}

class FakeBuildTemplate: BuildTypeTemplate by Mockito.mock(BuildTypeTemplate::class.java) {

    private val ownParams = mutableMapOf<String, String>()
    private val params = mutableMapOf<String, String>()
    private val runners = mutableListOf<SBuildRunnerDescriptor>()
    private val features = mutableListOf<SBuildFeatureDescriptor>()
    private val requirements = mutableListOf<Requirement>()
    private val ownOptions = mutableMapOf<String, String>()
    private val dependencies = mutableListOf<Dependency>()
    private val artifactDependencies = mutableListOf<SArtifactDependency>()

    override fun getExternalId(): String = "templateId"
    override fun getFullName(): String = "template name"

    override fun getOwnOptions(): MutableCollection<Option<Any>> {
        return ownOptions.keys.map { key -> StringOption(key, "") }.toMutableList() as MutableCollection<Option<Any>>
    }

    override fun <T : Any?> getOption(option: Option<T>): T {
        return (ownOptions[option.key] ?: option.defaultValue) as T
    }

    override fun addBuildRunner(name: String, runnerType: String, parameters: MutableMap<String, String>): SBuildRunnerDescriptor {
        val runner = FakeBuildRunner()
        runner.setParameters(parameters)
        runners.add(runner)
        return runner
    }

    override fun getBuildRunners(): MutableList<SBuildRunnerDescriptor> = runners

    override fun getOwnParameters(): MutableMap<String, String> = ownParams

    override fun getBuildFeatures(): MutableCollection<SBuildFeatureDescriptor> = features

    override fun getDependencies(): MutableList<Dependency> = dependencies

    override fun getArtifactDependencies(): MutableList<SArtifactDependency> = artifactDependencies

    override fun getRequirements(): MutableList<Requirement> = requirements

    fun setParameters(parameters: Map<String, String>) {
        params.clear()
        params.putAll(parameters)
    }

    fun withOwnParameters(parameters: Map<String, String>): FakeBuildTemplate {
        ownParams.clear()
        ownParams.putAll(parameters)
        return this
    }

    fun withFailureCondition(condition: SBuildFeatureDescriptor): FakeBuildTemplate = withBuildFeature(condition)

    fun withBuildFeature(feature: SBuildFeatureDescriptor): FakeBuildTemplate {
        features.add(feature)
        return this
    }

    fun withRequirement(requirement: Requirement): FakeBuildTemplate {
        requirements.add(requirement)
        return this
    }

    fun withOption(name: String, value: String): FakeBuildTemplate {
        ownOptions[name] = value
        return this
    }

    fun withDependency(dependency: Dependency): FakeBuildTemplate {
        dependencies.add(dependency)
        return this
    }

    fun withArtifactDependency(dependency: SArtifactDependency): FakeBuildTemplate {
        artifactDependencies.add(dependency)
        return this
    }
}

class FakeBuildRunner : SBuildRunnerDescriptor by Mockito.mock(SBuildRunnerDescriptor::class.java) {

    private val params: MutableMap<String, String> = mutableMapOf()

    override fun getParameters(): MutableMap<String, String> {
        return params
    }

    fun setParameters(parameters: Map<String, String>) {
        params.clear()
        params.putAll(parameters)
    }
}

class FakeBuildFeature(val type: BuildFeature = GeneralBuildFeature())
    : SBuildFeatureDescriptor by Mockito.mock(SBuildFeatureDescriptor::class.java)
{
    private val params: MutableMap<String, String> = mutableMapOf()

    override fun getBuildFeature(): BuildFeature = type
    override fun getParameters(): MutableMap<String, String> = params

    fun withParameters(parameters: Map<String, String>): FakeBuildFeature {
        params.clear()
        params.putAll(parameters)
        return this
    }
}

class GeneralBuildFeature: BuildFeature()  {
    override fun getType(): String = "General"
    override fun getDisplayName(): String = "General display name"
    override fun getEditParametersUrl(): String? = "not used"
}

class FailureConditionBuildFeature: BuildFeature()  {
    override fun getType(): String = "Failure"
    override fun getDisplayName(): String = "Failure condition display name"
    override fun getEditParametersUrl(): String? = "not used"
    override fun getPlaceToShow(): PlaceToShow = PlaceToShow.FAILURE_REASON
}

class FakeDependency : Dependency by Mockito.mock(Dependency::class.java) {

    private val ownOptions = mutableMapOf<String, String>()

    override fun getOwnOptions(): MutableCollection<Option<Any>> {
        return ownOptions.keys.map { key -> StringOption(key, "") }.toMutableList() as MutableCollection<Option<Any>>
    }

    override fun <T : Any?> getOption(option: Option<T>): T {
        return (ownOptions[option.key] ?: option.defaultValue) as T
    }

    fun withOption(name: String, value: String): FakeDependency {
        ownOptions[name] = value
        return this
    }
}

class FakeArtifactDependency : SArtifactDependency by Mockito.mock(SArtifactDependency::class.java) {

    private var sourcePaths: String = ""

    override fun getSourcePaths(): String = sourcePaths

    fun withSourcePaths(paths: String) = apply { sourcePaths = paths }
}
