package com.sp45.kaze.utils

import android.content.Context
import javax.inject.Inject
import androidx.core.content.edit

class SharedPrefHelper @Inject constructor(context: Context){

    private val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object{
        private const val PREF_NAME = "kaze_pref"
        private const val USER_ID_KEY = "user_id_key"
    }

    fun getUserId(): String{
        val userId = sharedPref.getString(USER_ID_KEY, null)
        return if(userId.isNullOrEmpty()){
            val newUserId = java.util.UUID.randomUUID().toString().substring(0, 6)
            saveUserId(newUserId)
            newUserId
        } else{
            userId
        }
    }

    private fun saveUserId(userId: String){
        sharedPref.edit { putString(USER_ID_KEY, userId) }
    }
}