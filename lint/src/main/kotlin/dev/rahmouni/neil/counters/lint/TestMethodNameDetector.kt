/*
 * Counters
 * Copyright (C) 2024 Rahmouni Neïl
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package dev.rahmouni.neil.counters.lint

import com.android.tools.lint.detector.api.AnnotationInfo
import com.android.tools.lint.detector.api.AnnotationUsageInfo
import com.android.tools.lint.detector.api.Category.Companion.TESTING
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.LintFix
import com.android.tools.lint.detector.api.Scope.JAVA_FILE
import com.android.tools.lint.detector.api.Scope.TEST_SOURCES
import com.android.tools.lint.detector.api.Severity.WARNING
import com.android.tools.lint.detector.api.SourceCodeScanner
import com.android.tools.lint.detector.api.TextFormat.RAW
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UElement
import java.util.EnumSet
import kotlin.io.path.Path

/**
 * A detector that checks for common patterns in naming the test methods:
 * - [detectPrefix] removes unnecessary "test" prefix in all unit test.
 * - [detectFormat] Checks the `given_when_then` format of Android instrumented tests (backticks are not supported).
 */
class TestMethodNameDetector : Detector(), SourceCodeScanner {

    override fun applicableAnnotations() = listOf("org.junit.Test")

    override fun visitAnnotationUsage(
        context: JavaContext,
        element: UElement,
        annotationInfo: AnnotationInfo,
        usageInfo: AnnotationUsageInfo,
    ) {
        val method = usageInfo.referenced as? PsiMethod ?: return

        method.detectPrefix(context, usageInfo)
        method.detectFormat(context, usageInfo)
    }

    private fun JavaContext.isAndroidTest() = Path("androidTest") in file.toPath()

    private fun PsiMethod.detectPrefix(
        context: JavaContext,
        usageInfo: AnnotationUsageInfo,
    ) {
        if (!name.startsWith("test")) return
        context.report(
            issue = PREFIX,
            scope = usageInfo.usage,
            location = context.getNameLocation(this),
            message = PREFIX.getBriefDescription(RAW),
            quickfixData = LintFix.create()
                .name("Remove prefix")
                .replace().pattern("""test[\s_]*""")
                .with("")
                .autoFix()
                .build(),
        )
    }

    private fun PsiMethod.detectFormat(
        context: JavaContext,
        usageInfo: AnnotationUsageInfo,
    ) {
        if (!context.isAndroidTest()) return
        if ("""[^\W_]+(_[^\W_]+){1,2}""".toRegex().matches(name)) return
        context.report(
            issue = FORMAT,
            scope = usageInfo.usage,
            location = context.getNameLocation(this),
            message = FORMAT.getBriefDescription(RAW),
        )
    }

    companion object {

        private fun issue(
            id: String,
            briefDescription: String,
            explanation: String,
        ): Issue = Issue.create(
            id = id,
            briefDescription = briefDescription,
            explanation = explanation,
            category = TESTING,
            priority = 5,
            severity = WARNING,
            implementation = Implementation(
                TestMethodNameDetector::class.java,
                EnumSet.of(JAVA_FILE, TEST_SOURCES),
            ),
        )

        @JvmField
        val PREFIX: Issue = issue(
            id = "TestMethodPrefix",
            briefDescription = "Test method starts with `test`",
            explanation = "Test method should not start with `test`.",
        )

        @JvmField
        val FORMAT: Issue = issue(
            id = "TestMethodFormat",
            briefDescription = "Test method does not follow the `given_when_then` or `when_then` format",
            explanation = "Test method should follow the `given_when_then` or `when_then` format.",
        )
    }
}
