package com.example.parkingqr.ui.components

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.parkingqr.R
import com.example.parkingqr.ui.components.userprofile.ConnectBankAccountFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.vnpay.authentication.VNP_AuthenticationActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    companion object {
        const val SCHEME_ACTIVITY = "main_activity"
        const val IS_SAND_BOX = true
    }

    private lateinit var appBarConfiguration: AppBarConfiguration
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fgvMainNavHost) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationMain)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        NavigationUI.setupWithNavController(bottomNavigationView, navController)
        supportActionBar?.hide()

        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true)
        }
        if (Build.VERSION.SDK_INT >= 19) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
            window.statusBarColor = Color.TRANSPARENT
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = Navigation.findNavController(this, R.id.fgvMainNavHost)
        return (NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp())
    }

    private fun setWindowFlag(bits: Int, on: Boolean) {
        val win = window
        val winParams = win.attributes
        if (on) {
            winParams.flags = winParams.flags or bits
        } else {
            winParams.flags = winParams.flags and bits.inv()
        }
        win.attributes = winParams
    }

    fun openSdk(
        url: String,
        successAction: (() -> Unit)? = null,
        webBackAction: (() -> Unit)? = null,
        failBackAction: (() -> Unit)? = null,
        appBackAction: (() -> Unit)? = null
    ) {
        val intent = Intent(this, VNP_AuthenticationActivity::class.java)
        intent.putExtra("url", url)
        intent.putExtra("tmn_code", getString(R.string.tmn_code))
        intent.putExtra("scheme", SCHEME_ACTIVITY)
        intent.putExtra("is_sandbox", IS_SAND_BOX)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            this.window.decorView.windowInsetsController?.setSystemBarsAppearance(
                APPEARANCE_LIGHT_STATUS_BARS,
                APPEARANCE_LIGHT_STATUS_BARS
            )
        }
        VNP_AuthenticationActivity.setSdkCompletedCallback { action ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                this.window.decorView.windowInsetsController?.setSystemBarsAppearance(
                    0,
                    APPEARANCE_LIGHT_STATUS_BARS
                )
            }
            when (action) {
                "AppBackAction" -> {
                    appBackAction?.invoke()
                }
                "FaildBackAction" -> {
                    failBackAction?.invoke()
                }
                "SuccessBackAction" -> {
                    successAction?.invoke()
                }
                "WebBackAction" -> {
                    webBackAction?.invoke()
                }
                "CallMobileBankingApp" -> {}
            }
        }
        startActivity(intent)
    }
}