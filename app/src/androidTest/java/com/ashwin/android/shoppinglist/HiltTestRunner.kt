package com.ashwin.android.shoppinglist

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication

/**
 * This class is required to set the Application class as HiltTestApplication.
 * This is used internally by Hilt-test library.
 */
class HiltTestRunner : AndroidJUnitRunner() {
    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        // className is your original Application class, replace it with HiltTestApplication
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }
}
