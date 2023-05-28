package com.kuro.facewise.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.auth.FirebaseAuth
import com.kuro.facewise.R
import com.kuro.facewise.util.PrefsProvider
import com.kuro.facewise.util.constants.PrefsConstants
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var prefsProvider: PrefsProvider
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        if (!prefsProvider.getBool(PrefsConstants.ONBOARDING_COMPLETED))
            navController.navigate(R.id.action_global_onBoardingFragment)
        else if (FirebaseAuth.getInstance().currentUser == null) {
            navController.navigate(R.id.action_global_signInFragment)
        }
    }
}