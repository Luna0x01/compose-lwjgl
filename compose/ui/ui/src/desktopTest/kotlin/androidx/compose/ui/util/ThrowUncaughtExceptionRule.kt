/*
 * Copyright 2024 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.compose.ui.util

import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

/**
 * A rule that throw an exception in the end of test if there were any uncaught exceptions.
 *
 * It is needed in cases, where exceptions are thrown outside testing thread.
 *
 * For example, AWT Event Thread can fire event independently,
 * and its handler can throw an exception without failing the test.
 *
 * Usage:
 *     @get:Rule
 *     val throwUncaughtExceptionRule = ThrowUncaughtExceptionRule()
 */
class ThrowUncaughtExceptionRule : TestRule {
    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            @Throws(Throwable::class)
            override fun evaluate() {
                val oldHandler = Thread.getDefaultUncaughtExceptionHandler()
                var exception: Throwable? = null

                Thread.setDefaultUncaughtExceptionHandler { t, e ->
                    if (exception != null) {
                        exception!!.addSuppressed(e)
                    } else {
                        exception = e
                    }
                }

                try {
                    base.evaluate()
                } finally {
                    Thread.setDefaultUncaughtExceptionHandler(oldHandler)
                }

                exception?.let { throw it }
            }
        }
    }
}