package com.qytech.gst813test

import android.app.Application

/**
 * Created by Jax on 2020-02-14.
 * Description :
 * Version : V1.0.0
 */
class MyApplication : Application() {
    companion object {
        lateinit var context: MyApplication
    }

    override fun onCreate() {
        super.onCreate()
        context = this
    }
}