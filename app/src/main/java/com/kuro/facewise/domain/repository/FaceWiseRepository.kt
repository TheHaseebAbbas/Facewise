package com.kuro.facewise.domain.repository

import android.net.Uri
import com.kuro.facewise.domain.model.EmotionResponse
import com.kuro.facewise.util.Resource
import kotlinx.coroutines.flow.Flow

interface FaceWiseRepository {

    fun getEmotions(imageUri: Uri): Flow<Resource<EmotionResponse>>
}