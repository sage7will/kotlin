/*
 * Copyright 2010-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.idea.spring.tests

import com.intellij.facet.impl.FacetUtil
import com.intellij.openapi.module.Module
import com.intellij.spring.facet.SpringFacet
import org.jetbrains.kotlin.idea.test.ConfigLibraryUtil
import org.jetbrains.kotlin.idea.test.PluginTestCaseBase
import org.jetbrains.kotlin.idea.test.TestFixtureExtension
import java.util.*

@Suppress("unused")
class SpringTestFixtureExtension() : TestFixtureExtension {
    private var module: Module? = null

    enum class SpringFramework(val version: String, vararg val artifactIds: String) {
        FRAMEWORK_4_2_0(
                "4.2.0.RELEASE",
                "core", "beans", "context"
        )
    }

    val SPRING_LIBRARY_ROOT = "${PluginTestCaseBase.getTestDataPathBase()}/spring/_lib"

    override fun setUp(module: Module) {
        this.module = module
        val library = SpringFramework.FRAMEWORK_4_2_0
        val libraryPath = "$SPRING_LIBRARY_ROOT/spring/${library.version}/"
        val jarNames = HashSet<String>(library.artifactIds.size)
        for (id in library.artifactIds) {
            jarNames.add("spring-$id-${library.version}.jar")
        }
        ConfigLibraryUtil.addLibrary(module, "spring" + library.version, libraryPath, jarNames.toTypedArray())

        FacetUtil.addFacet(module, SpringFacet.getSpringFacetType())
    }

    override fun tearDown() {
        try {
            // clear existing SpringFacet configuration before running next test
            module?.let { SpringFacet.getInstance(it) }?.let {
                it.removeFileSets()
                FacetUtil.deleteFacet(it)
            }
        }
        finally {
            module = null
        }
    }
}