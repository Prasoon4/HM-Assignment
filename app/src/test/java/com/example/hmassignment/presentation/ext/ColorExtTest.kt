package com.example.hmassignment.presentation.ext

import androidx.compose.ui.graphics.toArgb
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
internal class ColorExtTest {

    @Test
    fun `toSafeColor returns correct Color for valid 6-digit uppercase hex`() {
        val result = "FF0000".toSafeColor()
        assertNotNull(result)
        assertEquals(0xFFFF0000.toInt(), result!!.toArgb())
    }

    @Test
    fun `toSafeColor returns correct Color for valid 6-digit lowercase hex`() {
        val result = "ff0000".toSafeColor()
        assertNotNull(result)
        assertEquals(0xFFFF0000.toInt(), result!!.toArgb())
    }

    @Test
    fun `toSafeColor returns correct Color for mixed-case hex`() {
        val result = "fF0000".toSafeColor()
        assertNotNull(result)
        assertEquals(0xFFFF0000.toInt(), result!!.toArgb())
    }

    @Test
    fun `toSafeColor returns correct Color for 8-digit hex with partial alpha`() {
        val result = "80FF0000".toSafeColor()
        assertNotNull(result)
        assertEquals(0x80FF0000.toInt(), result!!.toArgb())
    }

    @Test
    fun `toSafeColor returns correct Color when caller already includes a hash prefix`() {
        val result = "#40FF0000".toSafeColor()
        assertNotNull(result)
        assertEquals(0x40FF0000, result!!.toArgb())
    }

    @Test
    fun `toSafeColor returns null for empty string`() {
        assertNull("".toSafeColor())
    }

    @Test
    fun `toSafeColor returns null for non-hex characters`() {
        assertNull("ZZZZZZ".toSafeColor())
    }

    @Test
    fun `toSafeColor returns null for too short hex string`() {
        assertNull("F00".toSafeColor())
    }

    @Test
    fun `toSafeColor returns null for too long hex string`() {
        assertNull("FFFF000000".toSafeColor())
    }

    @Test
    fun `toSafeColor returns null for whitespace string`() {
        assertNull("      ".toSafeColor())
    }

    @Test
    fun `toSafeColor returns null for string with embedded spaces`() {
        assertNull("FF 000".toSafeColor())
    }
}
