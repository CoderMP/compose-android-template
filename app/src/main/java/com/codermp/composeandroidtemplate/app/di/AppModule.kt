package com.codermp.composeandroidtemplate.app.di

import com.codermp.composeandroidtemplate.app.TemplateApp
import kotlinx.coroutines.CoroutineScope
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

/**
 * Koin module that provides application-level dependencies.
 * This module can include application-wide services, configurations, or any other shared components.
 */
val appModule = module {
    single<CoroutineScope> {
        (androidApplication() as TemplateApp).applicationScope
    }
}