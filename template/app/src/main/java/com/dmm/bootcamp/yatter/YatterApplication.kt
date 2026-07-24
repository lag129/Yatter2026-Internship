package com.dmm.bootcamp.yatter

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.util.DebugLogger
import com.dmm.bootcamp.yatter.di.domainImplModule
import com.dmm.bootcamp.yatter.di.infraModule
import com.dmm.bootcamp.yatter.di.useCaseModule
import com.dmm.bootcamp.yatter.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class YatterApplication :
  Application(),
  ImageLoaderFactory {
  override fun onCreate() {
    super.onCreate()

    startKoin {
      androidLogger()
      // Reference Android context
      androidContext(this@YatterApplication)
      modules(
        modules = listOf(
          domainImplModule,
          infraModule,
          useCaseModule,
          viewModelModule,
        ),
      )
    }
  }

  override fun newImageLoader(): ImageLoader = ImageLoader.Builder(applicationContext)
    .crossfade(true)
    .logger(DebugLogger())
    .build()
}
