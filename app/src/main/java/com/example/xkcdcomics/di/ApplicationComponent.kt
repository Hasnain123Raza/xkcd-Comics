package com.example.xkcdcomics.di

import android.app.Application
import com.example.xkcdcomics.screens.list.ListFragment
import com.example.xkcdcomics.screens.slide.SlideFragment
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [
    DatabaseModule::class,
    NetworkModule::class,
    ViewModelModule::class
])
interface ApplicationComponent {

    @Component.Builder
    interface Builder {
        fun build() : ApplicationComponent
        @BindsInstance fun application(application: Application): Builder
    }

    fun inject(viewModel: ListFragment)
    fun inject(viewModel: SlideFragment)
}