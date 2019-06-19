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

import jetbrains.buildServer.serverSide.SBuildFeatureDescriptor
import jetbrains.buildServer.serverSide.SBuildRunnerDescriptor
import jetbrains.buildServer.serverSide.SBuildType
import jetbrains.buildServer.serverSide.SProject
import org.mockito.Mockito

fun project(): FakeProject {
    return FakeProject()
}

fun buildType(): FakeBuildType {
    return FakeBuildType()
}

fun buildFeature(): FakeBuildFeature {
    return FakeBuildFeature()
}

class FakeProject: SProject by Mockito.mock(SProject::class.java) {

    private val buildTypes: MutableList<SBuildType> = mutableListOf()

    override fun getBuildTypes(): MutableList<SBuildType> {
        return buildTypes
    }

    fun withBuildType(buildType: SBuildType): FakeProject {
        buildTypes.add(buildType)
        return this
    }
}

class FakeBuildType: SBuildType by Mockito.mock(SBuildType::class.java) {

    private val ownParams: MutableMap<String, String> = mutableMapOf()
    private val params: MutableMap<String, String> = mutableMapOf()
    private val runners = mutableListOf<SBuildRunnerDescriptor>()
    private val features = mutableListOf<SBuildFeatureDescriptor>()

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

    override fun getOwnParameters(): MutableMap<String, String> {
        return ownParams
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

    fun withOwnParameters(parameters: Map<String, String>): SBuildType {
        ownParams.clear()
        ownParams.putAll(parameters)
        return this
    }

    fun withBuildFeature(feature: SBuildFeatureDescriptor): FakeBuildType {
        features.add(feature)
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

class FakeBuildFeature : SBuildFeatureDescriptor by Mockito.mock(SBuildFeatureDescriptor::class.java) {

    private val params: MutableMap<String, String> = mutableMapOf()

    override fun getParameters(): MutableMap<String, String> {
        return params
    }

    fun withParameters(parameters: Map<String, String>): FakeBuildFeature {
        params.clear()
        params.putAll(parameters)
        return this
    }
}
