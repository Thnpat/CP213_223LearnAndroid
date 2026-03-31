package com.example.a223lablearnandroid.utils

import android.content.Context
import android.content.SharedPreferences
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals
import java.lang.reflect.Field

class SharedPreferencesUtilTest {

    private val context: Context = mockk()
    private val sharedPreferences: SharedPreferences = mockk()
    private val editor: SharedPreferences.Editor = mockk()

    @Before
    fun setup() {
        // ใช้ Reflection เพื่อรีเซ็ตค่า Singleton ก่อนเริ่มแต่ละเทสต์
        val field: Field = SharedPreferencesUtil::class.java.getDeclaredField("sharedPreferences")
        field.isAccessible = true
        field.set(null, null)

        // กำหนดพฤติกรรมพื้นฐานให้ Mock
        every { context.getSharedPreferences(any(), any()) } returns sharedPreferences
        every { sharedPreferences.edit() } returns editor
        
        // Mocking chain calls สำหรับ Editor
        every { editor.putString(any(), any()) } returns editor
        every { editor.putInt(any(), any()) } returns editor
        every { editor.putBoolean(any(), any()) } returns editor
        every { editor.remove(any()) } returns editor
        every { editor.clear() } returns editor
        every { editor.apply() } returns Unit

        SharedPreferencesUtil.init(context)
    }

    @Test
    fun saveString_callsSharedPreferences() {
        SharedPreferencesUtil.saveString("testKey", "testValue")
        verify { editor.putString("testKey", "testValue") }
        verify { editor.apply() }
    }

    @Test
    fun getString_returnsValue() {
        every { sharedPreferences.getString("testKey", "") } returns "testValue"
        val result = SharedPreferencesUtil.getString("testKey")
        assertEquals("testValue", result)
    }

    @Test
    fun saveInt_callsSharedPreferences() {
        SharedPreferencesUtil.saveInt("testKey", 123)
        verify { editor.putInt("testKey", 123) }
        verify { editor.apply() }
    }

    @Test
    fun getInt_returnsValue() {
        every { sharedPreferences.getInt("testKey", 0) } returns 123
        val result = SharedPreferencesUtil.getInt("testKey")
        assertEquals(123, result)
    }

    @Test
    fun saveBoolean_callsSharedPreferences() {
        SharedPreferencesUtil.saveBoolean("testKey", true)
        verify { editor.putBoolean("testKey", true) }
        verify { editor.apply() }
    }

    @Test
    fun getBoolean_returnsValue() {
        every { sharedPreferences.getBoolean("testKey", false) } returns true
        val result = SharedPreferencesUtil.getBoolean("testKey")
        assertEquals(true, result)
    }

    @Test
    fun remove_callsSharedPreferences() {
        SharedPreferencesUtil.remove("testKey")
        verify { editor.remove("testKey") }
        verify { editor.apply() }
    }

    @Test
    fun clearAll_callsSharedPreferences() {
        SharedPreferencesUtil.clearAll()
        verify { editor.clear() }
        verify { editor.apply() }
    }
}