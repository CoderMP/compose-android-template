package com.codermp.composeandroidtemplate.app

import android.app.Application
import com.codermp.composeandroidtemplate.BuildConfig
import com.codermp.composeandroidtemplate.app.di.appModule
import com.codermp.composeandroidtemplate.core.di.coreModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import timber.log.Timber

/**
 * Root application class - plants the [Timber] debug tree and starts [Koin].
 */
class TemplateApp: Application() {
    val applicationScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(tree = Timber.DebugTree())
        }

        startKoin {
            androidContext(androidContext = this@TemplateApp)

            modules(
                appModule,
                coreModule
            )
        }
    }
}