package com.example.maya_exam_martin_avery

import android.app.Application
import com.example.maya_exam_martin_avery.domain.usecase.SeedDefaultUserUseCase
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

// Hilt uses this Application entry point to manage the app-wide dependency container.
@HiltAndroidApp
class MayaExamMartinAveryApplication : Application() {
    @Inject lateinit var seedDefaultUserUseCase: SeedDefaultUserUseCase

    override fun onCreate() {
        super.onCreate()

        // Seed a default user once; use case delegates to an atomic insert-if-empty repository operation.
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            seedDefaultUserUseCase()
        }
    }
}

