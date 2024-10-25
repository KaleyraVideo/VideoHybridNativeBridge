package com.kaleyra.video_common_ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontFamily
import com.kaleyra.material_color_utilities.hct.Hct
import com.kaleyra.material_color_utilities.scheme.SchemeFidelity
import com.kaleyra.material_color_utilities.scheme.SchemeMonochrome
import com.kaleyra.video_common_ui.KaleyraVideo
import com.kaleyra.video_common_ui.theme.resource.ColorResource
import com.kaleyra.video_common_ui.theme.resource.URIResource

/**
 * Represents a theme configuration for the application.
 *
 * This data class encapsulates various aspects of the theme, including:
 * - **logo:** The logo resources for the theme.
 * - **palette:** The color palette for the theme.
 * - **typography:** The typography settings for the theme.
 * - **config:** General configuration options for the theme.
 */
data class Theme(
    val logo: Logo? = null,
    val palette: Palette? = null,
    val typography: Typography? = null,
    val config: Config = Config()
): KaleyraVideo.Theme {

    /**
     * Configuration options for the theme.
     *
     * @property style The style of the theme. Defaults to [Style.System].
     */
    data class Config(val style: Style = Style.System) {
        /**
         * Represents the different styles that the theme can have.
         */
        enum class Style {
            /**
             * Uses the system theme (light or dark based on device settings).
             */
            System,

            /**
             * Forces the theme to be light.
             */
            Light,

            /**
             * Forces the theme to be dark.
             */
            Dark
        }
    }

    /**
     * Represents the typography settings for the theme.
     */
    class Typography private constructor(val typography: androidx.compose.material3.Typography) {
        companion object {
            /**
             * Creates a new [Typography] instance with the specified font family applied to all text styles.
             *
             * @param fontFamily The font family to use for all text styles.
             * @return A new [Typography] instance.
             */
            operator fun invoke(fontFamily: FontFamily): Typography {
                val typography = androidx.compose.material3.Typography()
                return Typography(
                    typography.copy(
                        displayLarge = typography.displayLarge.copy(fontFamily = fontFamily),
                        displayMedium = typography.displayMedium.copy(fontFamily = fontFamily),
                        displaySmall = typography.displaySmall.copy(fontFamily = fontFamily),

                        headlineLarge = typography.headlineLarge.copy(fontFamily = fontFamily),
                        headlineMedium = typography.headlineMedium.copy(fontFamily = fontFamily),
                        headlineSmall = typography.headlineSmall.copy(fontFamily = fontFamily),

                        titleLarge = typography.titleLarge.copy(fontFamily = fontFamily),
                        titleMedium = typography.titleMedium.copy(fontFamily = fontFamily),
                        titleSmall = typography.titleSmall.copy(fontFamily = fontFamily),

                        bodyLarge = typography.bodyLarge.copy(fontFamily = fontFamily),
                        bodyMedium = typography.bodyMedium.copy(fontFamily = fontFamily),
                        bodySmall = typography.bodySmall.copy(fontFamily = fontFamily),

                        labelLarge = typography.labelLarge.copy(fontFamily = fontFamily),
                        labelMedium = typography.labelMedium.copy(fontFamily = fontFamily),
                        labelSmall = typography.labelSmall.copy(fontFamily = fontFamily)
                    )
                )
            }
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Typography

            return typography == other.typography
        }

        override fun hashCode(): Int {
            return typography.hashCode()
        }

        override fun toString(): String {
            return "Typography(typography=$typography)"
        }
    }

    /**
     * Represents the logo resources for the theme.
     */
    class Logo private constructor(
        internal val compact: URIResource,
        internal val extended: URIResource,
    ) {
        /**
         * The logo resource
         */
        val resource = compact

        /**
         * Creates a new [Logo] instance.
         *
         * @param resource The URI resource the logo.
         */
        constructor(resource: URIResource) : this(resource, resource)

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Logo

            if (compact != other.compact) return false
            if (extended != other.extended) return false

            return true
        }

        override fun hashCode(): Int {
            var result = compact.hashCode()
            result = 31 * result + extended.hashCode()
            return result
        }

        override fun toString(): String {
            return "Logo(compact=$compact, extended=$extended)"
        }
    }

    /**
     * Represents the color palette for the theme.
     *
     * This data class holds color resources for various UI elements in both light and dark themes.
     */
    data class Palette(
        var primary: ColorResource,
        var onPrimary: ColorResource,
        var secondary: ColorResource,
        var onSecondary: ColorResource,
        var secondaryContainer: ColorResource,
        var onSecondaryContainer: ColorResource,
        var surface: ColorResource,
        var onSurface: ColorResource,
        var surfaceVariant: ColorResource,
        var onSurfaceVariant: ColorResource,
        var surfaceTint: ColorResource,
        var inverseSurface: ColorResource,
        var inverseOnSurface: ColorResource,
        var error: ColorResource,
        var onError: ColorResource,
        var outline: ColorResource,
        var outlineVariant: ColorResource,
        var surfaceContainer: ColorResource,
        var surfaceContainerHigh: ColorResource,
        var surfaceContainerHighest: ColorResource,
        var surfaceContainerLow: ColorResource,
        var surfaceContainerLowest: ColorResource,
    ) {
        /**
         * Creates a new [Palette] instance using a single seed color for both light and dark themes.
         *
         * @param seed The seed color in ARGB format.
         */
        constructor(seed: Int) : this(
            ColorSchemeFactory.createLightColorScheme(seed),
            ColorSchemeFactory.createDarkColorScheme(seed)
        )

        /**
         * Creates a new [Palette] instance using separate seed colors for light and dark themes.
         *
         * @param lightSeed The seed color for the light theme in ARGB format.
         * @param darkSeed The seed color for the dark theme in ARGB format.
         */
        internal constructor(lightSeed: Int, darkSeed: Int) : this(
            ColorSchemeFactory.createLightColorScheme(lightSeed),
            ColorSchemeFactory.createDarkColorScheme(darkSeed)
        )

        /**
         * Creates a new [Palette] instance using pre-generated [ColorScheme] objects for light and dark themes.
         *
         * @param lightColorScheme The color scheme for the light theme.
         * @param darkColorScheme The color scheme for the dark theme.
         */
        constructor(lightColorScheme: ColorScheme, darkColorScheme: ColorScheme) : this(
            ColorResource(lightColorScheme.primary.toArgb(), darkColorScheme.primary.toArgb()),
            ColorResource(lightColorScheme.onPrimary.toArgb(), darkColorScheme.onPrimary.toArgb()),
            ColorResource(lightColorScheme.secondary.toArgb(), darkColorScheme.secondary.toArgb()),
            ColorResource(
                lightColorScheme.onSecondary.toArgb(),
                darkColorScheme.onSecondary.toArgb()
            ),
            ColorResource(
                lightColorScheme.secondaryContainer.toArgb(),
                darkColorScheme.secondaryContainer.toArgb()
            ),
            ColorResource(
                lightColorScheme.onSecondaryContainer.toArgb(),
                darkColorScheme.onSecondaryContainer.toArgb()
            ),
            ColorResource(lightColorScheme.surface.toArgb(), darkColorScheme.surface.toArgb()),
            ColorResource(lightColorScheme.onSurface.toArgb(), darkColorScheme.onSurface.toArgb()),
            ColorResource(
                lightColorScheme.surfaceVariant.toArgb(),
                darkColorScheme.surfaceVariant.toArgb()
            ),
            ColorResource(
                lightColorScheme.onSurfaceVariant.toArgb(),
                darkColorScheme.onSurfaceVariant.toArgb()
            ),
            ColorResource(
                lightColorScheme.surfaceTint.toArgb(),
                darkColorScheme.surfaceTint.toArgb()
            ),
            ColorResource(
                lightColorScheme.inverseSurface.toArgb(),
                darkColorScheme.inverseSurface.toArgb()
            ),
            ColorResource(
                lightColorScheme.inverseOnSurface.toArgb(),
                darkColorScheme.inverseOnSurface.toArgb()
            ),
            ColorResource(lightColorScheme.error.toArgb(), darkColorScheme.error.toArgb()),
            ColorResource(lightColorScheme.onError.toArgb(), darkColorScheme.onError.toArgb()),
            ColorResource(lightColorScheme.outline.toArgb(), darkColorScheme.outline.toArgb()),
            ColorResource(
                lightColorScheme.outlineVariant.toArgb(),
                darkColorScheme.outlineVariant.toArgb()
            ),
            ColorResource(
                lightColorScheme.surfaceContainer.toArgb(),
                darkColorScheme.surfaceContainer.toArgb()
            ),
            ColorResource(
                lightColorScheme.surfaceContainerHigh.toArgb(),
                darkColorScheme.surfaceContainerHigh.toArgb()
            ),
            ColorResource(
                lightColorScheme.surfaceContainerHighest.toArgb(),
                darkColorScheme.surfaceContainerHighest.toArgb()
            ),
            ColorResource(
                lightColorScheme.surfaceContainerLow.toArgb(),
                darkColorScheme.surfaceContainerLow.toArgb()
            ),
            ColorResource(
                lightColorScheme.surfaceContainerLow.toArgb(),
                darkColorScheme.surfaceContainerLow.toArgb()
            )
        )
    }
}

