
package com.storytel.app

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory

class StorytelJobApplication : Application(), ImageLoaderFactory {
    override fun onCreate() {
        super.onCreate()
        Graph.provide(this)
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .respectCacheHeaders(false)
            .build()
    }
}
