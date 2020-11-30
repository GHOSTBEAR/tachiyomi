package eu.kanade.tachiyomi.ui.reader.viewer.pager

import eu.kanade.tachiyomi.data.preference.PreferencesHelper
import eu.kanade.tachiyomi.ui.reader.viewer.ViewerConfig
import eu.kanade.tachiyomi.ui.reader.viewer.ViewerNavigation
import eu.kanade.tachiyomi.ui.reader.viewer.navigation.KindlishNavigation
import eu.kanade.tachiyomi.ui.reader.viewer.navigation.LNavigation
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

/**
 * Configuration used by pager viewers.
 */
class PagerConfig(private val viewer: PagerViewer, preferences: PreferencesHelper = Injekt.get()) :
    ViewerConfig(preferences) {

    var imageScaleType = 1
        private set

    var imageZoomType = ZoomType.Left
        private set

    var imageCropBorders = false
        private set

    override var navigator: ViewerNavigation = defaultViewerNavigation()

    init {
        preferences.imageScaleType()
            .register({ imageScaleType = it }, { imagePropertyChangedListener?.invoke() })

        preferences.zoomStart()
            .register({ zoomTypeFromPreference(it) }, { imagePropertyChangedListener?.invoke() })

        preferences.cropBorders()
            .register({ imageCropBorders = it }, { imagePropertyChangedListener?.invoke() })

        preferences.navigationModePager()
            .register(
                { navigationMode = it },
                {
                    navigationMode = it
                    viewerNavigation()
                }
            )
    }

    private fun zoomTypeFromPreference(value: Int) {
        imageZoomType = when (value) {
            // Auto
            1 -> when (viewer) {
                is L2RPagerViewer -> ZoomType.Left
                is R2LPagerViewer -> ZoomType.Right
                else -> ZoomType.Center
            }
            // Left
            2 -> ZoomType.Left
            // Right
            3 -> ZoomType.Right
            // Center
            else -> ZoomType.Center
        }
    }

    override fun defaultViewerNavigation(): ViewerNavigation {
        return when (viewer) {
            is VerticalPagerViewer -> VerticalPagerDefaultNavigation()
            else -> PagerDefaultNavigation()
        }
    }

    override fun viewerNavigation() {
        navigator = when (navigationMode) {
            0 -> defaultViewerNavigation()
            1 -> LNavigation()
            2 -> KindlishNavigation()
            else -> defaultViewerNavigation()
        }
        super.viewerNavigation()
    }

    enum class ZoomType {
        Left, Center, Right
    }
}
