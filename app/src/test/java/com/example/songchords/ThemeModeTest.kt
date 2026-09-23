package com.example.songchords

import com.example.songchords.ui.theme.ThemeMode
import org.junit.Assert.assertEquals
import org.junit.Test

class ThemeModeTest {

    @Test
    fun testThemeModeEnumValues() {
        val values = ThemeMode.entries
        assertEquals(3, values.size)
        assertEquals(ThemeMode.LIGHT, ThemeMode.valueOf("LIGHT"))
        assertEquals(ThemeMode.DARK, ThemeMode.valueOf("DARK"))
        assertEquals(ThemeMode.SYSTEM, ThemeMode.valueOf("SYSTEM"))
    }

    @Test
    fun testToggleThemeModeLogic() {
        fun toggle(current: ThemeMode, isSystemDark: Boolean): ThemeMode {
            val isDark = when (current) {
                ThemeMode.DARK -> true
                ThemeMode.LIGHT -> false
                ThemeMode.SYSTEM -> isSystemDark
            }
            return if (isDark) ThemeMode.LIGHT else ThemeMode.DARK
        }

        assertEquals(ThemeMode.LIGHT, toggle(ThemeMode.DARK, isSystemDark = true))
        assertEquals(ThemeMode.DARK, toggle(ThemeMode.LIGHT, isSystemDark = false))
        assertEquals(ThemeMode.LIGHT, toggle(ThemeMode.SYSTEM, isSystemDark = true))
        assertEquals(ThemeMode.DARK, toggle(ThemeMode.SYSTEM, isSystemDark = false))
    }
}
