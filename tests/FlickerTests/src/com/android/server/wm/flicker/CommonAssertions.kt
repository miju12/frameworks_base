/*
 * Copyright (C) 2020 The Android Open Source Project
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

package com.android.server.wm.flicker

import android.platform.helpers.IAppHelper
import com.android.server.wm.flicker.dsl.EventLogAssertionBuilder
import com.android.server.wm.flicker.dsl.LayersAssertionBuilder
import com.android.server.wm.flicker.dsl.WmAssertionBuilder
import com.android.server.wm.flicker.helpers.WindowUtils

const val NAVIGATION_BAR_WINDOW_TITLE = "NavigationBar"
const val STATUS_BAR_WINDOW_TITLE = "StatusBar"
const val DOCKED_STACK_DIVIDER = "DockedStackDivider"
const val WALLPAPER_TITLE = "Wallpaper"

@JvmOverloads
fun WmAssertionBuilder.statusBarWindowIsAlwaysVisible(
    bugId: Int = 0,
    enabled: Boolean = bugId == 0
) {
    all("statusBarWindowIsAlwaysVisible", bugId, enabled) {
        this.showsAboveAppWindow(STATUS_BAR_WINDOW_TITLE)
    }
}

@JvmOverloads
fun WmAssertionBuilder.navBarWindowIsAlwaysVisible(
    bugId: Int = 0,
    enabled: Boolean = bugId == 0
) {
    all("navBarWindowIsAlwaysVisible", bugId, enabled) {
        this.showsAboveAppWindow(NAVIGATION_BAR_WINDOW_TITLE)
    }
}

fun WmAssertionBuilder.visibleWindowsShownMoreThanOneConsecutiveEntry(
    ignoreWindows: List<String> = emptyList(),
    bugId: Int = 0,
    enabled: Boolean = bugId == 0
) {
    all("visibleWindowsShownMoreThanOneConsecutiveEntry", bugId, enabled) {
        this.visibleWindowsShownMoreThanOneConsecutiveEntry(ignoreWindows)
    }
}

fun WmAssertionBuilder.launcherReplacesAppWindowAsTopWindow(
    testApp: IAppHelper,
    bugId: Int = 0,
    enabled: Boolean = bugId == 0
) {
    all("launcherReplacesAppWindowAsTopWindow", bugId, enabled) {
        this.showsAppWindowOnTop(testApp.getPackage())
                .then()
                .showsAppWindowOnTop("Launcher")
    }
}

fun WmAssertionBuilder.wallpaperWindowBecomesVisible(
    bugId: Int = 0,
    enabled: Boolean = bugId == 0
) {
    all("wallpaperWindowBecomesVisible", bugId, enabled) {
        this.hidesBelowAppWindow(WALLPAPER_TITLE)
                .then()
                .showsBelowAppWindow(WALLPAPER_TITLE)
    }
}

fun WmAssertionBuilder.wallpaperWindowBecomesInvisible(
    bugId: Int = 0,
    enabled: Boolean = bugId == 0
) {
    all("wallpaperWindowBecomesInvisible", bugId, enabled) {
        this.showsBelowAppWindow("Wallpaper")
                .then()
                .hidesBelowAppWindow("Wallpaper")
    }
}

fun WmAssertionBuilder.appWindowAlwaysVisibleOnTop(
    packageName: String,
    bugId: Int = 0,
    enabled: Boolean = bugId == 0
) {
    all("appWindowAlwaysVisibleOnTop", bugId, enabled) {
        this.showsAppWindowOnTop(packageName)
    }
}

fun WmAssertionBuilder.appWindowBecomesVisible(
    appName: String,
    bugId: Int = 0,
    enabled: Boolean = bugId == 0
) {
    all("appWindowBecomesVisible", bugId, enabled) {
        this.hidesAppWindow(appName)
                .then()
                .showsAppWindow(appName)
    }
}

@JvmOverloads
fun LayersAssertionBuilder.noUncoveredRegions(
    beginRotation: Int,
    endRotation: Int = beginRotation,
    allStates: Boolean = true,
    bugId: Int = 0,
    enabled: Boolean = bugId == 0
) {
    val startingBounds = WindowUtils.getDisplayBounds(beginRotation)
    val endingBounds = WindowUtils.getDisplayBounds(endRotation)
    if (allStates) {
        all("noUncoveredRegions", bugId, enabled) {
            if (startingBounds == endingBounds) {
                this.coversAtLeastRegion(startingBounds)
            } else {
                this.coversAtLeastRegion(startingBounds)
                        .then()
                        .coversAtLeastRegion(endingBounds)
            }
        }
    } else {
        start("noUncoveredRegions_StartingPos") {
            this.coversAtLeastRegion(startingBounds)
        }
        end("noUncoveredRegions_EndingPos") {
            this.coversAtLeastRegion(endingBounds)
        }
    }
}

@JvmOverloads
fun LayersAssertionBuilder.navBarLayerIsAlwaysVisible(
    rotatesScreen: Boolean = false,
    bugId: Int = 0,
    enabled: Boolean = bugId == 0
) {
    if (rotatesScreen) {
        all("navBarLayerIsAlwaysVisible", bugId, enabled) {
            this.showsLayer(NAVIGATION_BAR_WINDOW_TITLE)
                    .then()
                    .hidesLayer(NAVIGATION_BAR_WINDOW_TITLE)
                    .then()
                    .showsLayer(NAVIGATION_BAR_WINDOW_TITLE)
        }
    } else {
        all("navBarLayerIsAlwaysVisible", bugId, enabled) {
            this.showsLayer(NAVIGATION_BAR_WINDOW_TITLE)
        }
    }
}

@JvmOverloads
fun LayersAssertionBuilder.statusBarLayerIsAlwaysVisible(
    rotatesScreen: Boolean = false,
    bugId: Int = 0,
    enabled: Boolean = bugId == 0
) {
    if (rotatesScreen) {
        all("statusBarLayerIsAlwaysVisible", bugId, enabled) {
            this.showsLayer(STATUS_BAR_WINDOW_TITLE)
                    .then()
                    hidesLayer(STATUS_BAR_WINDOW_TITLE)
                    .then()
                    .showsLayer(STATUS_BAR_WINDOW_TITLE)
        }
    } else {
        all("statusBarLayerIsAlwaysVisible", bugId, enabled) {
            this.showsLayer(STATUS_BAR_WINDOW_TITLE)
        }
    }
}

@JvmOverloads
fun LayersAssertionBuilder.navBarLayerRotatesAndScales(
    beginRotation: Int,
    endRotation: Int = beginRotation,
    bugId: Int = 0,
    enabled: Boolean = bugId == 0
) {
    val startingPos = WindowUtils.getNavigationBarPosition(beginRotation)
    val endingPos = WindowUtils.getNavigationBarPosition(endRotation)

    start("navBarLayerRotatesAndScales_StartingPos", bugId, enabled) {
        this.hasVisibleRegion(NAVIGATION_BAR_WINDOW_TITLE, startingPos)
    }
    end("navBarLayerRotatesAndScales_EndingPost", bugId, enabled) {
        this.hasVisibleRegion(NAVIGATION_BAR_WINDOW_TITLE, endingPos)
    }

    if (startingPos == endingPos) {
        all("navBarLayerRotatesAndScales", enabled = false, bugId = 167747321) {
            this.hasVisibleRegion(NAVIGATION_BAR_WINDOW_TITLE, startingPos)
        }
    }
}

@JvmOverloads
fun LayersAssertionBuilder.statusBarLayerRotatesScales(
    beginRotation: Int,
    endRotation: Int = beginRotation,
    bugId: Int = 0,
    enabled: Boolean = bugId == 0
) {
    val startingPos = WindowUtils.getStatusBarPosition(beginRotation)
    val endingPos = WindowUtils.getStatusBarPosition(endRotation)

    start("statusBarLayerRotatesScales_StartingPos", bugId, enabled) {
        this.hasVisibleRegion(STATUS_BAR_WINDOW_TITLE, startingPos)
    }
    end("statusBarLayerRotatesScales_EndingPos", bugId, enabled) {
        this.hasVisibleRegion(STATUS_BAR_WINDOW_TITLE, endingPos)
    }
}

fun LayersAssertionBuilder.visibleLayersShownMoreThanOneConsecutiveEntry(
    ignoreLayers: List<String> = emptyList(),
    bugId: Int = 0,
    enabled: Boolean = bugId == 0
) {
    all("visibleLayersShownMoreThanOneConsecutiveEntry", bugId, enabled) {
        this.visibleLayersShownMoreThanOneConsecutiveEntry(ignoreLayers)
    }
}

fun LayersAssertionBuilder.appLayerReplacesWallpaperLayer(
    appName: String,
    bugId: Int = 0,
    enabled: Boolean = bugId == 0
) {
    all("appLayerReplacesWallpaperLayer", bugId, enabled) {
        this.showsLayer("Wallpaper")
                .then()
                .replaceVisibleLayer("Wallpaper", appName)
    }
}

fun LayersAssertionBuilder.wallpaperLayerReplacesAppLayer(
    testApp: IAppHelper,
    bugId: Int = 0,
    enabled: Boolean = bugId == 0
) {
    all("appLayerReplacesWallpaperLayer", bugId, enabled) {
        this.showsLayer(testApp.getPackage())
                .then()
                .replaceVisibleLayer(testApp.getPackage(), WALLPAPER_TITLE)
    }
}

fun LayersAssertionBuilder.layerAlwaysVisible(
    packageName: String,
    bugId: Int = 0,
    enabled: Boolean = bugId == 0
) {
    all("layerAlwaysVisible", bugId, enabled) {
        this.showsLayer(packageName)
    }
}

fun LayersAssertionBuilder.layerBecomesVisible(
    packageName: String,
    bugId: Int = 0,
    enabled: Boolean = bugId == 0
) {
    all("layerBecomesVisible", bugId, enabled) {
        this.hidesLayer(packageName)
                .then()
                .showsLayer(packageName)
    }
}

fun LayersAssertionBuilder.layerBecomesInvisible(
    packageName: String,
    bugId: Int = 0,
    enabled: Boolean = bugId == 0
) {
    all("layerBecomesInvisible", bugId, enabled) {
        this.showsLayer(packageName)
                .then()
                .hidesLayer(packageName)
    }
}

fun EventLogAssertionBuilder.focusChanges(
    vararg windows: String,
    bugId: Int = 0,
    enabled: Boolean = bugId == 0
) {
    all("focusChanges", bugId, enabled) {
        this.focusChanges(windows)
    }
}

fun EventLogAssertionBuilder.focusDoesNotChange(
    bugId: Int = 0,
    enabled: Boolean = bugId == 0
) {
    all("focusDoesNotChange", bugId, enabled) {
        this.focusDoesNotChange()
    }
}