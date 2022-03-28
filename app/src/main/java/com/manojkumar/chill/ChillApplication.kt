package com.manojkumar.chill

import android.app.Application
import com.manojkumar.chill.di.AppComponent
import com.manojkumar.chill.di.DaggerAppComponent

class ChillApplication : Application() {


    companion object {
        const val TAG = "ChillApplication"

        private var appComponent: AppComponent? = null

        fun getAppComponent(): AppComponent? {
            return appComponent
        }
    }
    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder().application(this).build()

    }
}