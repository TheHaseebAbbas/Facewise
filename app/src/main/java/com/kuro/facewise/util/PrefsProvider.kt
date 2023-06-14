package com.kuro.facewise.util

import android.content.Context
import android.content.SharedPreferences
import com.kuro.facewise.domain.model.Ayah
import com.kuro.facewise.domain.model.Hadith
import com.kuro.facewise.domain.model.Incident
import com.kuro.facewise.domain.model.IslamicData
import com.kuro.facewise.util.constants.PrefsConstants
import com.kuro.facewise.util.constants.PrefsConstants.KEY_AYAH_ARABIC
import com.kuro.facewise.util.constants.PrefsConstants.KEY_AYAH_ENGLISH
import com.kuro.facewise.util.constants.PrefsConstants.KEY_AYAH_ID
import com.kuro.facewise.util.constants.PrefsConstants.KEY_AYAH_REFERENCE
import com.kuro.facewise.util.constants.PrefsConstants.KEY_HADITH
import com.kuro.facewise.util.constants.PrefsConstants.KEY_HADITH_ID
import com.kuro.facewise.util.constants.PrefsConstants.KEY_HADITH_REFERENCE
import com.kuro.facewise.util.constants.PrefsConstants.KEY_INCIDENT
import com.kuro.facewise.util.constants.PrefsConstants.KEY_INCIDENT_ID
import com.kuro.facewise.util.constants.PrefsConstants.KEY_INCIDENT_REFERENCE


class PrefsProvider(val context: Context) {
    companion object {
        const val PREF_NAME = "FaceWiseApp"
    }

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(
            PREF_NAME,
            Context.MODE_PRIVATE
        )

    fun getString(key: String) = sharedPreferences.getString(key, null)

    fun setString(key: String, value: String?) {
        sharedPreferences.edit().putString(key, value)
            .apply()
    }

    fun getInt(key: String) = sharedPreferences.getInt(key, -1)

    fun setInt(key: String, value: Int) {
        sharedPreferences.edit().putInt(key, value)
            .apply()
    }

    fun getFloat(key: String) = sharedPreferences.getFloat(key, -1F)

    fun setFloat(key: String, value: Float) {
        sharedPreferences.edit().putFloat(key, value)
            .apply()
    }

    fun getLong(key: String) = sharedPreferences.getLong(key, -1L)

    fun setLong(key: String, value: Long) {
        sharedPreferences.edit().putLong(key, value)
            .apply()
    }

    fun getBool(key: String) = sharedPreferences.getBoolean(key, false)

    fun setBool(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value)
            .apply()
    }

    fun setIslamicData(value: IslamicData) {
        with(
            sharedPreferences.edit()
        ) {
            putString(KEY_AYAH_ID, value.ayah.ayahId)
            putString(KEY_AYAH_ARABIC, value.ayah.ayahArabic)
            putString(KEY_AYAH_ENGLISH, value.ayah.ayahEnglish)
            putString(KEY_AYAH_REFERENCE, value.ayah.ayahReference)

            putString(KEY_HADITH_ID, value.hadith.hadithId)
            putString(KEY_HADITH, value.hadith.hadith)
            putString(KEY_HADITH_REFERENCE, value.hadith.hadithReference)

            putString(KEY_INCIDENT_ID, value.incident.incidentId)
            putString(KEY_INCIDENT, value.incident.incident)
            putString(KEY_INCIDENT_REFERENCE, value.incident.incidentReference)
            apply()
        }
    }

    fun getIslamicData(): IslamicData {
        return with(sharedPreferences) {
            val ayah = Ayah(
                ayahId = getString(KEY_AYAH_ID, "")!!,
                ayahArabic = getString(KEY_AYAH_ARABIC, "")!!,
                ayahEnglish = getString(KEY_AYAH_ENGLISH, "")!!,
                ayahReference = getString(KEY_AYAH_REFERENCE, "")!!
            )

            val hadith = Hadith(
                hadithId = getString(KEY_HADITH_ID, "")!!,
                hadith = getString(KEY_HADITH, "")!!,
                hadithReference = getString(KEY_HADITH_REFERENCE, "")!!
            )

            val incident = Incident(
                incidentId = getString(KEY_INCIDENT_ID, "")!!,
                incident = getString(KEY_INCIDENT, "")!!,
                incidentReference = getString(KEY_INCIDENT_REFERENCE, "")!!
            )
            IslamicData(ayah, hadith, incident)
        }
    }

    fun clear() {
        with(sharedPreferences.edit()) {
            clear().apply()
            putBoolean(PrefsConstants.ONBOARDING_COMPLETED, true)
            apply()
        }
    }
}