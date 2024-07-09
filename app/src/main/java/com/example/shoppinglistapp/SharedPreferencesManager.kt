package com.example.shoppinglistapp

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class ShoppingItem(val id: Int,
                        var name: String,
                        var quantity: String,
                        var isEditing: Boolean = true)

//class - something like blueprint
class SharedPreferencesManager(val context: Context) {
    //private for being used just in this class cant get it from it outside of this class
    private val prefsname = "MyPrefsFile"
    private val keylistdata = "dataList"

    private val gson = Gson()

    fun saveDataList(dataList: List<ShoppingItem>) {
        val prefs: SharedPreferences = context.getSharedPreferences(prefsname, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val json = gson.toJson(dataList)
        editor.putString(keylistdata, json)
        editor.apply()
    }

    fun getDataList(): List<ShoppingItem> {
        val prefs: SharedPreferences = context.getSharedPreferences(prefsname, Context.MODE_PRIVATE)
        val json = prefs.getString(keylistdata, null)
        return if (json != null) {
            gson.fromJson(json, object : TypeToken<List<ShoppingItem>>() {}.type)
        } else {
            emptyList()
        }
    }
}