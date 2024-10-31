package com.kaleyra.video_common_ui.theme.factory

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import com.kaleyra.material_color_utilities.dynamiccolor.DynamicColor
import com.kaleyra.material_color_utilities.hct.Hct
import com.kaleyra.material_color_utilities.scheme.SchemeFidelity
import com.kaleyra.material_color_utilities.scheme.SchemeMonochrome
import org.junit.Assert.assertEquals
import org.junit.Test

class ColorSchemeFactoryTest {

    @Test
    fun testLightMonochromeScheme() {
        val monochromeScheme = SchemeMonochrome(Hct.fromInt(Color.Cyan.toArgb()), false, .0)
        val result = ColorSchemeFactory.lightSchemeMonochrome
        assertEquals(monochromeScheme.primary, result.primary)
        assertEquals(monochromeScheme.onPrimary, result.onPrimary)
        assertEquals(monochromeScheme.primaryContainer, result.primaryContainer)
        assertEquals(monochromeScheme.onPrimaryContainer, result.onPrimaryContainer)
        assertEquals(monochromeScheme.inversePrimary, result.inversePrimary)
        assertEquals(monochromeScheme.secondary, result.secondary)
        assertEquals(monochromeScheme.onSecondary, result.onSecondary)
        assertEquals(monochromeScheme.secondaryContainer, result.secondaryContainer)
        assertEquals(monochromeScheme.onSecondaryContainer, result.onSecondaryContainer)
        assertEquals(monochromeScheme.tertiary, result.tertiary)
        assertEquals(monochromeScheme.onTertiary, result.onTertiary)
        assertEquals(monochromeScheme.tertiaryContainer, result.tertiaryContainer)
        assertEquals(monochromeScheme.onTertiaryContainer, result.onTertiaryContainer)
        assertEquals(monochromeScheme.background, result.background)
        assertEquals(monochromeScheme.onBackground, result.onBackground)
        assertEquals(monochromeScheme.onBackground, result.onBackground)
        assertEquals(monochromeScheme.surface, result.surface)
        assertEquals(monochromeScheme.onSurface, result.onSurface)
        assertEquals(monochromeScheme.surfaceVariant, result.surfaceVariant)
        assertEquals(monochromeScheme.onSurfaceVariant, result.onSurfaceVariant)
        assertEquals(monochromeScheme.surfaceTint, result.surfaceTint)
        assertEquals(monochromeScheme.inverseSurface, result.inverseSurface)
        assertEquals(monochromeScheme.inverseOnSurface, result.inverseOnSurface)
        assertEquals(monochromeScheme.error, result.error)
        assertEquals(monochromeScheme.onError, result.onError)
        assertEquals(monochromeScheme.errorContainer, result.errorContainer)
        assertEquals(monochromeScheme.onErrorContainer, result.onErrorContainer)
        assertEquals(monochromeScheme.outline, result.outline)
        assertEquals(monochromeScheme.outlineVariant, result.outlineVariant)
        assertEquals(monochromeScheme.scrim, result.scrim)
        assertEquals(monochromeScheme.surfaceBright, result.surfaceBright)
        assertEquals(monochromeScheme.surfaceContainer, result.surfaceContainer)
        assertEquals(monochromeScheme.surfaceContainerHigh, result.surfaceContainerHigh)
        assertEquals(monochromeScheme.surfaceContainerHighest, result.surfaceContainerHighest)
        assertEquals(monochromeScheme.surfaceContainerLow, result.surfaceContainerLow)
        assertEquals(monochromeScheme.surfaceContainerLowest, result.surfaceContainerLowest)
        assertEquals(monochromeScheme.surfaceDim, result.surfaceDim)
    }

    @Test
    fun testDarkMonochromeScheme() {
        val monochromeScheme = SchemeMonochrome(Hct.fromInt(Color.Cyan.toArgb()), true, .0)
        val result = ColorSchemeFactory.darkSchemeMonochrome
        assertEquals(monochromeScheme.primary, result.primary)
        assertEquals(monochromeScheme.onPrimary, result.onPrimary)
        assertEquals(monochromeScheme.primaryContainer, result.primaryContainer)
        assertEquals(monochromeScheme.onPrimaryContainer, result.onPrimaryContainer)
        assertEquals(monochromeScheme.inversePrimary, result.inversePrimary)
        assertEquals(monochromeScheme.secondary, result.secondary)
        assertEquals(monochromeScheme.onSecondary, result.onSecondary)
        assertEquals(monochromeScheme.secondaryContainer, result.secondaryContainer)
        assertEquals(monochromeScheme.onSecondaryContainer, result.onSecondaryContainer)
        assertEquals(monochromeScheme.tertiary, result.tertiary)
        assertEquals(monochromeScheme.onTertiary, result.onTertiary)
        assertEquals(monochromeScheme.tertiaryContainer, result.tertiaryContainer)
        assertEquals(monochromeScheme.onTertiaryContainer, result.onTertiaryContainer)
        assertEquals(monochromeScheme.background, result.background)
        assertEquals(monochromeScheme.onBackground, result.onBackground)
        assertEquals(monochromeScheme.onBackground, result.onBackground)
        assertEquals(monochromeScheme.surface, result.surface)
        assertEquals(monochromeScheme.onSurface, result.onSurface)
        assertEquals(monochromeScheme.surfaceVariant, result.surfaceVariant)
        assertEquals(monochromeScheme.onSurfaceVariant, result.onSurfaceVariant)
        assertEquals(monochromeScheme.surfaceTint, result.surfaceTint)
        assertEquals(monochromeScheme.inverseSurface, result.inverseSurface)
        assertEquals(monochromeScheme.inverseOnSurface, result.inverseOnSurface)
        assertEquals(monochromeScheme.error, result.error)
        assertEquals(monochromeScheme.onError, result.onError)
        assertEquals(monochromeScheme.errorContainer, result.errorContainer)
        assertEquals(monochromeScheme.onErrorContainer, result.onErrorContainer)
        assertEquals(monochromeScheme.outline, result.outline)
        assertEquals(monochromeScheme.outlineVariant, result.outlineVariant)
        assertEquals(monochromeScheme.scrim, result.scrim)
        assertEquals(monochromeScheme.surfaceBright, result.surfaceBright)
        assertEquals(monochromeScheme.surfaceContainer, result.surfaceContainer)
        assertEquals(monochromeScheme.surfaceContainerHigh, result.surfaceContainerHigh)
        assertEquals(monochromeScheme.surfaceContainerHighest, result.surfaceContainerHighest)
        assertEquals(monochromeScheme.surfaceContainerLow, result.surfaceContainerLow)
        assertEquals(monochromeScheme.surfaceContainerLowest, result.surfaceContainerLowest)
        assertEquals(monochromeScheme.surfaceDim, result.surfaceDim)
    }

    @Test
    fun testCreateLightColorScheme() {
        val seed = Color.Red.toArgb()
        val monochromeScheme = SchemeMonochrome(Hct.fromInt(Color.White.toArgb()), false, .0)
        val fidelityScheme = SchemeFidelity(Hct.fromInt(seed), false, .0)

        val hctSeed = Hct.fromInt(seed)
        val onSeed =  Hct.from(hctSeed.hue, hctSeed.chroma, DynamicColor.foregroundTone(hctSeed.tone, 7.0)).toInt()

        val result = ColorSchemeFactory.createLightColorScheme(seed)
        assertEquals(Color(seed), result.primary)
        assertEquals(Color(onSeed), result.onPrimary)
        assertEquals(Color(fidelityScheme.primaryContainer), result.primaryContainer)
        assertEquals(Color(fidelityScheme.onPrimaryContainer), result.onPrimaryContainer)
        assertEquals(Color(fidelityScheme.inversePrimary), result.inversePrimary)
        assertEquals(Color(fidelityScheme.secondary), result.secondary)
        assertEquals(Color(fidelityScheme.onSecondary), result.onSecondary)
        assertEquals(Color(fidelityScheme.secondaryContainer), result.secondaryContainer)
        assertEquals(Color(fidelityScheme.onSecondaryContainer), result.onSecondaryContainer)
        assertEquals(Color(fidelityScheme.tertiary), result.tertiary)
        assertEquals(Color(fidelityScheme.onTertiary), result.onTertiary)
        assertEquals(Color(fidelityScheme.tertiaryContainer), result.tertiaryContainer)
        assertEquals(Color(fidelityScheme.onTertiaryContainer), result.onTertiaryContainer)
        assertEquals(Color(monochromeScheme.background), result.background)
        assertEquals(Color(monochromeScheme.onBackground), result.onBackground)
        assertEquals(Color(monochromeScheme.onBackground), result.onBackground)
        assertEquals(Color(monochromeScheme.surface), result.surface)
        assertEquals(Color(monochromeScheme.onSurface), result.onSurface)
        assertEquals(Color(monochromeScheme.surfaceVariant), result.surfaceVariant)
        assertEquals(Color(monochromeScheme.onSurfaceVariant), result.onSurfaceVariant)
        assertEquals(Color(monochromeScheme.surfaceTint), result.surfaceTint)
        assertEquals(Color(monochromeScheme.inverseSurface), result.inverseSurface)
        assertEquals(Color(monochromeScheme.inverseOnSurface), result.inverseOnSurface)
        assertEquals(Color(monochromeScheme.error), result.error)
        assertEquals(Color(monochromeScheme.onError), result.onError)
        assertEquals(Color(fidelityScheme.errorContainer), result.errorContainer)
        assertEquals(Color(fidelityScheme.onErrorContainer), result.onErrorContainer)
        assertEquals(Color(monochromeScheme.outline), result.outline)
        assertEquals(Color(monochromeScheme.outlineVariant), result.outlineVariant)
        assertEquals(Color(monochromeScheme.scrim), result.scrim)
        assertEquals(Color(monochromeScheme.surfaceBright), result.surfaceBright)
        assertEquals(Color(monochromeScheme.surfaceContainer), result.surfaceContainer)
        assertEquals(Color(monochromeScheme.surfaceContainerHigh), result.surfaceContainerHigh)
        assertEquals(Color(monochromeScheme.surfaceContainerHighest), result.surfaceContainerHighest)
        assertEquals(Color(monochromeScheme.surfaceContainerLow), result.surfaceContainerLow)
        assertEquals(Color(monochromeScheme.surfaceContainerLowest), result.surfaceContainerLowest)
        assertEquals(Color(monochromeScheme.surfaceDim), result.surfaceDim)
    }

    @Test
    fun seedColorHasAlpha_createLightColorScheme_primaryColorHasNoAlpha() {
        val seed = Color(0x782A638A).toArgb()
        val monochromeScheme = SchemeMonochrome(Hct.fromInt(Color.White.toArgb()), false, .0)
        val fidelityScheme = SchemeFidelity(Hct.fromInt(seed), false, .0)

        val fidelitySeed = ColorUtils.setAlphaComponent(seed, 255)
        val hctSeed = Hct.fromInt(fidelitySeed)
        val onSeed =  Hct.from(hctSeed.hue, hctSeed.chroma, DynamicColor.foregroundTone(hctSeed.tone, 7.0)).toInt()

        val result = ColorSchemeFactory.createLightColorScheme(seed)
        assertEquals(Color(fidelitySeed), result.primary)
        assertEquals(Color(onSeed), result.onPrimary)
        assertEquals(Color(fidelityScheme.primaryContainer), result.primaryContainer)
        assertEquals(Color(fidelityScheme.onPrimaryContainer), result.onPrimaryContainer)
        assertEquals(Color(fidelityScheme.inversePrimary), result.inversePrimary)
        assertEquals(Color(fidelityScheme.secondary), result.secondary)
        assertEquals(Color(fidelityScheme.onSecondary), result.onSecondary)
        assertEquals(Color(fidelityScheme.secondaryContainer), result.secondaryContainer)
        assertEquals(Color(fidelityScheme.onSecondaryContainer), result.onSecondaryContainer)
        assertEquals(Color(fidelityScheme.tertiary), result.tertiary)
        assertEquals(Color(fidelityScheme.onTertiary), result.onTertiary)
        assertEquals(Color(fidelityScheme.tertiaryContainer), result.tertiaryContainer)
        assertEquals(Color(fidelityScheme.onTertiaryContainer), result.onTertiaryContainer)
        assertEquals(Color(monochromeScheme.background), result.background)
        assertEquals(Color(monochromeScheme.onBackground), result.onBackground)
        assertEquals(Color(monochromeScheme.onBackground), result.onBackground)
        assertEquals(Color(monochromeScheme.surface), result.surface)
        assertEquals(Color(monochromeScheme.onSurface), result.onSurface)
        assertEquals(Color(monochromeScheme.surfaceVariant), result.surfaceVariant)
        assertEquals(Color(monochromeScheme.onSurfaceVariant), result.onSurfaceVariant)
        assertEquals(Color(monochromeScheme.surfaceTint), result.surfaceTint)
        assertEquals(Color(monochromeScheme.inverseSurface), result.inverseSurface)
        assertEquals(Color(monochromeScheme.inverseOnSurface), result.inverseOnSurface)
        assertEquals(Color(monochromeScheme.error), result.error)
        assertEquals(Color(monochromeScheme.onError), result.onError)
        assertEquals(Color(fidelityScheme.errorContainer), result.errorContainer)
        assertEquals(Color(fidelityScheme.onErrorContainer), result.onErrorContainer)
        assertEquals(Color(monochromeScheme.outline), result.outline)
        assertEquals(Color(monochromeScheme.outlineVariant), result.outlineVariant)
        assertEquals(Color(monochromeScheme.scrim), result.scrim)
        assertEquals(Color(monochromeScheme.surfaceBright), result.surfaceBright)
        assertEquals(Color(monochromeScheme.surfaceContainer), result.surfaceContainer)
        assertEquals(Color(monochromeScheme.surfaceContainerHigh), result.surfaceContainerHigh)
        assertEquals(Color(monochromeScheme.surfaceContainerHighest), result.surfaceContainerHighest)
        assertEquals(Color(monochromeScheme.surfaceContainerLow), result.surfaceContainerLow)
        assertEquals(Color(monochromeScheme.surfaceContainerLowest), result.surfaceContainerLowest)
        assertEquals(Color(monochromeScheme.surfaceDim), result.surfaceDim)
    }

    @Test
    fun testCreateDarkColorScheme() {
        val seed = Color.Blue.toArgb()
        val monochromeScheme = SchemeMonochrome(Hct.fromInt(Color.White.toArgb()), true, .0)
        val fidelityScheme = SchemeFidelity(Hct.fromInt(seed), true, .0)

        val hctSeed = Hct.fromInt(seed)
        val onSeed =  Hct.from(hctSeed.hue, hctSeed.chroma, DynamicColor.foregroundTone(hctSeed.tone, 7.0)).toInt()

        val result = ColorSchemeFactory.createDarkColorScheme(seed)
        assertEquals(Color(seed), result.primary)
        assertEquals(Color(onSeed), result.onPrimary)
        assertEquals(Color(fidelityScheme.primaryContainer), result.primaryContainer)
        assertEquals(Color(fidelityScheme.onPrimaryContainer), result.onPrimaryContainer)
        assertEquals(Color(fidelityScheme.inversePrimary), result.inversePrimary)
        assertEquals(Color(fidelityScheme.secondary), result.secondary)
        assertEquals(Color(fidelityScheme.onSecondary), result.onSecondary)
        assertEquals(Color(fidelityScheme.secondaryContainer), result.secondaryContainer)
        assertEquals(Color(fidelityScheme.onSecondaryContainer), result.onSecondaryContainer)
        assertEquals(Color(fidelityScheme.tertiary), result.tertiary)
        assertEquals(Color(fidelityScheme.onTertiary), result.onTertiary)
        assertEquals(Color(fidelityScheme.tertiaryContainer), result.tertiaryContainer)
        assertEquals(Color(fidelityScheme.onTertiaryContainer), result.onTertiaryContainer)
        assertEquals(Color(monochromeScheme.background), result.background)
        assertEquals(Color(monochromeScheme.onBackground), result.onBackground)
        assertEquals(Color(monochromeScheme.onBackground), result.onBackground)
        assertEquals(Color(monochromeScheme.surface), result.surface)
        assertEquals(Color(monochromeScheme.onSurface), result.onSurface)
        assertEquals(Color(monochromeScheme.surfaceVariant), result.surfaceVariant)
        assertEquals(Color(monochromeScheme.onSurfaceVariant), result.onSurfaceVariant)
        assertEquals(Color(monochromeScheme.surfaceTint), result.surfaceTint)
        assertEquals(Color(monochromeScheme.inverseSurface), result.inverseSurface)
        assertEquals(Color(monochromeScheme.inverseOnSurface), result.inverseOnSurface)
        assertEquals(Color(monochromeScheme.error), result.error)
        assertEquals(Color(monochromeScheme.onError), result.onError)
        assertEquals(Color(fidelityScheme.errorContainer), result.errorContainer)
        assertEquals(Color(fidelityScheme.onErrorContainer), result.onErrorContainer)
        assertEquals(Color(monochromeScheme.outline), result.outline)
        assertEquals(Color(monochromeScheme.outlineVariant), result.outlineVariant)
        assertEquals(Color(monochromeScheme.scrim), result.scrim)
        assertEquals(Color(monochromeScheme.surfaceBright), result.surfaceBright)
        assertEquals(Color(monochromeScheme.surfaceContainer), result.surfaceContainer)
        assertEquals(Color(monochromeScheme.surfaceContainerHigh), result.surfaceContainerHigh)
        assertEquals(Color(monochromeScheme.surfaceContainerHighest), result.surfaceContainerHighest)
        assertEquals(Color(monochromeScheme.surfaceContainerLow), result.surfaceContainerLow)
        assertEquals(Color(monochromeScheme.surfaceContainerLowest), result.surfaceContainerLowest)
        assertEquals(Color(monochromeScheme.surfaceDim), result.surfaceDim)
    }

    @Test
    fun seedColorHasAlpha_createDarkColorScheme_primaryColorHasNoAlpha() {
        val seed = Color(0x782A638A).toArgb()
        val monochromeScheme = SchemeMonochrome(Hct.fromInt(Color.White.toArgb()), true, .0)
        val fidelityScheme = SchemeFidelity(Hct.fromInt(seed), true, .0)

        val fidelitySeed = ColorUtils.setAlphaComponent(seed, 255)
        val hctSeed = Hct.fromInt(fidelitySeed)
        val onSeed =  Hct.from(hctSeed.hue, hctSeed.chroma, DynamicColor.foregroundTone(hctSeed.tone, 7.0)).toInt()

        val result = ColorSchemeFactory.createDarkColorScheme(seed)
        assertEquals(Color(fidelitySeed), result.primary)
        assertEquals(Color(onSeed), result.onPrimary)
        assertEquals(Color(fidelityScheme.primaryContainer), result.primaryContainer)
        assertEquals(Color(fidelityScheme.onPrimaryContainer), result.onPrimaryContainer)
        assertEquals(Color(fidelityScheme.inversePrimary), result.inversePrimary)
        assertEquals(Color(fidelityScheme.secondary), result.secondary)
        assertEquals(Color(fidelityScheme.onSecondary), result.onSecondary)
        assertEquals(Color(fidelityScheme.secondaryContainer), result.secondaryContainer)
        assertEquals(Color(fidelityScheme.onSecondaryContainer), result.onSecondaryContainer)
        assertEquals(Color(fidelityScheme.tertiary), result.tertiary)
        assertEquals(Color(fidelityScheme.onTertiary), result.onTertiary)
        assertEquals(Color(fidelityScheme.tertiaryContainer), result.tertiaryContainer)
        assertEquals(Color(fidelityScheme.onTertiaryContainer), result.onTertiaryContainer)
        assertEquals(Color(monochromeScheme.background), result.background)
        assertEquals(Color(monochromeScheme.onBackground), result.onBackground)
        assertEquals(Color(monochromeScheme.onBackground), result.onBackground)
        assertEquals(Color(monochromeScheme.surface), result.surface)
        assertEquals(Color(monochromeScheme.onSurface), result.onSurface)
        assertEquals(Color(monochromeScheme.surfaceVariant), result.surfaceVariant)
        assertEquals(Color(monochromeScheme.onSurfaceVariant), result.onSurfaceVariant)
        assertEquals(Color(monochromeScheme.surfaceTint), result.surfaceTint)
        assertEquals(Color(monochromeScheme.inverseSurface), result.inverseSurface)
        assertEquals(Color(monochromeScheme.inverseOnSurface), result.inverseOnSurface)
        assertEquals(Color(monochromeScheme.error), result.error)
        assertEquals(Color(monochromeScheme.onError), result.onError)
        assertEquals(Color(fidelityScheme.errorContainer), result.errorContainer)
        assertEquals(Color(fidelityScheme.onErrorContainer), result.onErrorContainer)
        assertEquals(Color(monochromeScheme.outline), result.outline)
        assertEquals(Color(monochromeScheme.outlineVariant), result.outlineVariant)
        assertEquals(Color(monochromeScheme.scrim), result.scrim)
        assertEquals(Color(monochromeScheme.surfaceBright), result.surfaceBright)
        assertEquals(Color(monochromeScheme.surfaceContainer), result.surfaceContainer)
        assertEquals(Color(monochromeScheme.surfaceContainerHigh), result.surfaceContainerHigh)
        assertEquals(Color(monochromeScheme.surfaceContainerHighest), result.surfaceContainerHighest)
        assertEquals(Color(monochromeScheme.surfaceContainerLow), result.surfaceContainerLow)
        assertEquals(Color(monochromeScheme.surfaceContainerLowest), result.surfaceContainerLowest)
        assertEquals(Color(monochromeScheme.surfaceDim), result.surfaceDim)
    }
}