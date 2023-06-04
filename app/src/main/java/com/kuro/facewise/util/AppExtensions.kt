package com.kuro.facewise.util

import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.google.android.material.snackbar.Snackbar
import com.kuro.facewise.MainNavGraphDirections
import com.kuro.facewise.R

infix fun View.click(v: View.OnClickListener) {
    setOnClickListener(v)
}

fun AppCompatActivity.showShortToast(message: String) {
    Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
}

fun Fragment.showLongToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
}

fun Fragment.showShortToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}

fun String?.isNotNullOrEmpty() = this.isNullOrEmpty().not()

fun View.gone() {
    this.visibility = View.GONE
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.showLongSnackBar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG).show()
}

fun View.showShortSnackBar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_SHORT).show()
}

fun NavController.showPopUpMenu(view: View) {
    PopupMenu(view.context, view).apply {
        inflate(R.menu.profile_popup_menu)
        show()
        setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.profile -> {
                    navigate(MainNavGraphDirections.actionGlobalProfileFragment())
                }

                R.id.privacyPolicy -> {
                    navigate(
                        MainNavGraphDirections.actionGlobalPrivacyAndTermsFragment(
                            stringResTitleId = R.string.facewise_privacy_policy_tittle,
                            stringResBodyId = R.string.facewise_privacy_policy
                        )
                    )
                }

                R.id.termsCondition -> {
                    navigate(
                        MainNavGraphDirections.actionGlobalPrivacyAndTermsFragment(
                            stringResTitleId = R.string.facewise_terms_conditions_title,
                            stringResBodyId = R.string.facewise_terms_codition
                        )
                    )

                }

                R.id.aboutUs -> {

                }

                else -> {
                    //TODO
                }
            }
            true
        }
    }
}