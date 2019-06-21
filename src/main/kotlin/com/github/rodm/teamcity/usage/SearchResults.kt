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

import org.jdom.Element

class SearchResults(elements: Collection<SearchResult>): ArrayList<SearchResult>(elements) {

    fun serialize(root: Element) {
        forEach { result ->
            val resultElement = Element("result")
            resultElement.setAttribute("id", result.externalId)
            resultElement.setAttribute("name", result.fullName)
            result.namesBySection.forEach { section ->
                val sectionElement = Element("section")
                sectionElement.setAttribute("name", section.key)
                section.value.forEach { name ->
                    val nameElement = Element("name")
                    nameElement.setAttribute("value", name)
                    sectionElement.addContent(nameElement)
                }
                resultElement.addContent(sectionElement)
            }
            root.addContent(resultElement)
        }
    }
}

data class SearchResult(val externalId: String, val fullName: String) {
    val namesBySection = LinkedHashMap<String, LinkedHashSet<String>>()

    fun namesFor(section: String, names: List<String>) {
        if (names.isNotEmpty()) {
            if (namesBySection.containsKey(section)) {
                namesBySection[section]?.addAll(names)
            } else {
                namesBySection[section] = LinkedHashSet(names)
            }
        }
    }

    fun hasMatches(): Boolean {
        return namesBySection.isNotEmpty()
    }
}
