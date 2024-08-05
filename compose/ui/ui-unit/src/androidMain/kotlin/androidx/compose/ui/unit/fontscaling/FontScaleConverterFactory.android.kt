/*
 * Copyright 2023 The Android Open Source Project
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
package androidx.compose.ui.unit.fontscaling

import androidx.annotation.AnyThread
import androidx.annotation.RestrictTo
import androidx.annotation.VisibleForTesting
import androidx.collection.SparseArrayCompat
import androidx.compose.ui.unit.checkPrecondition

/**
 * Creates [FontScaleConverter]s at various scales.
 *
 * Generally you shouldn't need this; you can use [android.util.TypedValue.applyDimension] directly
 * and it will do the scaling conversion for you. But for UI frameworks or other situations where
 * you need to do the conversion without an Android Context, you can use this class.
 */
// TODO(b/294384826): move these into core:core when the FontScaleConverter APIs are available.
//  These are temporary shims until core and platform are in a stable state.
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
object FontScaleConverterFactory {
    private const val ScaleKeyMultiplier = 100f

    private val CommonFontSizes = floatArrayOf(8f, 10f, 12f, 14f, 18f, 20f, 24f, 30f, 100f)

    // GuardedBy("LOOKUP_TABLES_WRITE_LOCK") but only for writes!
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    @VisibleForTesting
    @Volatile
    var sLookupTables = SparseArrayCompat<FontScaleConverter>()

    /**
     * This is a write lock only! We don't care about synchronization on reads; they can be a bit
     * out of date. But all writes have to be atomic, so we use this similar to a
     * CopyOnWriteArrayList.
     */
    private val LookupTablesWriteLock = arrayOfNulls<Any>(0)
    private const val MinScaleForNonLinear = 1.03f

    init {
        // These were generated by frameworks/base/tools/fonts/font-scaling-array-generator.js and
        // manually tweaked for optimum readability.
        synchronized(LookupTablesWriteLock) {
            putInto(
                sLookupTables,
                /* scaleKey= */ 1.15f,
                FontScaleConverterTable(
                    floatArrayOf(8f, 10f, 12f, 14f, 18f, 20f, 24f, 30f, 100f),
                    floatArrayOf(9.2f, 11.5f, 13.8f, 16.4f, 19.8f, 21.8f, 25.2f, 30f, 100f)
                )
            )
            putInto(
                sLookupTables,
                /* scaleKey= */ 1.3f,
                FontScaleConverterTable(
                    floatArrayOf(8f, 10f, 12f, 14f, 18f, 20f, 24f, 30f, 100f),
                    floatArrayOf(10.4f, 13f, 15.6f, 18.8f, 21.6f, 23.6f, 26.4f, 30f, 100f)
                )
            )
            putInto(
                sLookupTables,
                /* scaleKey= */ 1.5f,
                FontScaleConverterTable(
                    floatArrayOf(8f, 10f, 12f, 14f, 18f, 20f, 24f, 30f, 100f),
                    floatArrayOf(12f, 15f, 18f, 22f, 24f, 26f, 28f, 30f, 100f)
                )
            )
            putInto(
                sLookupTables,
                /* scaleKey= */ 1.8f,
                FontScaleConverterTable(
                    floatArrayOf(8f, 10f, 12f, 14f, 18f, 20f, 24f, 30f, 100f),
                    floatArrayOf(14.4f, 18f, 21.6f, 24.4f, 27.6f, 30.8f, 32.8f, 34.8f, 100f)
                )
            )
            putInto(
                sLookupTables,
                /* scaleKey= */ 2f,
                FontScaleConverterTable(
                    floatArrayOf(8f, 10f, 12f, 14f, 18f, 20f, 24f, 30f, 100f),
                    floatArrayOf(16f, 20f, 24f, 26f, 30f, 34f, 36f, 38f, 100f)
                )
            )
        }
        val minScaleBeforeCurvesApplied = getScaleFromKey(sLookupTables.keyAt(0)) - 0.01f
        checkPrecondition(minScaleBeforeCurvesApplied > MinScaleForNonLinear) {
            "You should only apply non-linear scaling to font scales > 1"
        }
    }

    /**
     * Returns true if non-linear font scaling curves would be in effect for the given scale, false
     * if the scaling would follow a linear curve or for no scaling.
     *
     *
     * Example usage:
     * `isNonLinearFontScalingActive(getResources().getConfiguration().fontScale)`
     */
    @AnyThread
    fun isNonLinearFontScalingActive(fontScale: Float): Boolean {
        return fontScale >= MinScaleForNonLinear
    }

    /**
     * Finds a matching FontScaleConverter for the given fontScale factor.
     *
     * @param fontScale the scale factor, usually from [Configuration.fontScale].
     *
     * @return a converter for the given scale, or null if non-linear scaling should not be used.
     */
    @AnyThread
    fun forScale(fontScale: Float): FontScaleConverter? {
        if (!isNonLinearFontScalingActive(fontScale)) {
            return null
        }
        val lookupTable = FontScaleConverterFactory[fontScale]
        if (lookupTable != null) {
            return lookupTable
        }

        // Didn't find an exact match: interpolate between two existing tables
        val index = sLookupTables.indexOfKey(getKey(fontScale))
        if (index >= 0) {
            // This should never happen, should have been covered by get() above.
            return sLookupTables.valueAt(index)
        }
        // Didn't find an exact match: interpolate between two existing tables
        val lowerIndex = -(index + 1) - 1
        val higherIndex = lowerIndex + 1
        return if (higherIndex >= sLookupTables.size()) {
            // We have gone beyond our bounds and have nothing to interpolate between. Just give
            // them a straight linear table instead.
            // This works because when FontScaleConverter encounters a size beyond its bounds, it
            // calculates a linear fontScale factor using the ratio of the last element pair.
            val converter =
                FontScaleConverterTable(floatArrayOf(1f), floatArrayOf(fontScale))

            // Cache for next time.
            put(fontScale, converter)
            converter
        } else {
            val startTable: FontScaleConverter
            val startScale: Float
            if (lowerIndex < 0) {
                // if we're in between 1x and the first table, interpolate between them.
                // (See b/336720383)
                startScale = 1f
                startTable = FontScaleConverterTable(CommonFontSizes, CommonFontSizes)
            } else {
                startScale = getScaleFromKey(
                    sLookupTables.keyAt(lowerIndex)
                )
                startTable = sLookupTables.valueAt(lowerIndex)
            }
            val endScale = getScaleFromKey(
                sLookupTables.keyAt(higherIndex)
            )
            val interpolationPoint =
                MathUtils.constrainedMap(
                    rangeMin = 0f,
                    rangeMax = 1f,
                    startScale,
                    endScale,
                    fontScale
                )
            val converter = createInterpolatedTableBetween(
                startTable,
                sLookupTables.valueAt(higherIndex),
                interpolationPoint
            )

            // Cache for next time.
            put(fontScale, converter)
            converter
        }
    }

    private fun createInterpolatedTableBetween(
        start: FontScaleConverter,
        end: FontScaleConverter,
        interpolationPoint: Float
    ): FontScaleConverter {
        val dpInterpolated = FloatArray(CommonFontSizes.size)
        for (i in CommonFontSizes.indices) {
            val sp = CommonFontSizes[i]
            val startDp = start.convertSpToDp(sp)
            val endDp = end.convertSpToDp(sp)
            dpInterpolated[i] = MathUtils.lerp(startDp, endDp, interpolationPoint)
        }
        return FontScaleConverterTable(CommonFontSizes, dpInterpolated)
    }

    private fun getKey(fontScale: Float): Int {
        return (fontScale * ScaleKeyMultiplier).toInt()
    }

    private fun getScaleFromKey(key: Int): Float {
        return key.toFloat() / ScaleKeyMultiplier
    }

    private fun put(scaleKey: Float, fontScaleConverter: FontScaleConverter) {
        // Dollar-store CopyOnWriteSparseArray, since this is the only write op we need.
        synchronized(LookupTablesWriteLock) {
            val newTable = sLookupTables.clone()
            putInto(newTable, scaleKey, fontScaleConverter)
            sLookupTables = newTable
        }
    }

    private fun putInto(
        table: SparseArrayCompat<FontScaleConverter>,
        scaleKey: Float,
        fontScaleConverter: FontScaleConverter
    ) {
        table.put(getKey(scaleKey), fontScaleConverter)
    }

    private operator fun get(scaleKey: Float): FontScaleConverter? {
        return sLookupTables[getKey(scaleKey)]
    }
}