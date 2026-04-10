package com.example.hmassignment.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.rules.TestWatcher
import org.junit.runner.Description

// ---------------------------------------------------------------------------
// JUnit 5 extension – use with @RegisterExtension in Jupiter test classes
// ---------------------------------------------------------------------------

/**
 * A JUnit 5 [Extension] that installs [testDispatcher] as [Dispatchers.Main]
 * before every test and resets it afterwards.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainCoroutineExtension(
    val testDispatcher: TestDispatcher = StandardTestDispatcher(),
) : BeforeEachCallback, AfterEachCallback {

    private val testScope = TestScope(testDispatcher)

    override fun beforeEach(context: ExtensionContext) = Dispatchers.setMain(testDispatcher)
    override fun afterEach(context: ExtensionContext) = Dispatchers.resetMain()

    fun runTest(block: suspend TestScope.() -> Unit) = testScope.runTest(testBody = block)
}

// ---------------------------------------------------------------------------
// JUnit 4 rule – use with @get:Rule in Robolectric / Vintage test classes
// ---------------------------------------------------------------------------

/**
 * A JUnit 4 [TestWatcher] that installs [testDispatcher] as [Dispatchers.Main]
 * before every test and resets it afterwards.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainCoroutineRule(
    val testDispatcher: TestDispatcher = StandardTestDispatcher(),
) : TestWatcher() {

    private val testScope = TestScope(testDispatcher)

    override fun starting(description: Description) = Dispatchers.setMain(testDispatcher)
    override fun finished(description: Description) = Dispatchers.resetMain()

    fun runTest(block: suspend TestScope.() -> Unit) = testScope.runTest(testBody = block)
}
