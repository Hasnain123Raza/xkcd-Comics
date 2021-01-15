package com.example.xkcdcomics

import android.app.Application
import com.example.xkcdcomics.di.DaggerApplicationComponent

class XKCDComicsApplication : Application() {

    val applicationComponent = DaggerApplicationComponent.builder()
        .application(this)
        .build()

}