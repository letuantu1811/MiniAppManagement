package com.example.mysdkapplication

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.webkit.JavascriptInterface
import android.widget.Toast
import com.google.gson.Gson

class MiniAppManagement(
    val context: Context,
) {
    init {
        val listMiniAppPermissionType:List<MiniAppPermissionType>? =  miniPermissionRequest("VERSION_SDK_MINI_APP")

    }
    inner class MiniAppManagementCallback {

    }
    private fun miniPermissionRequest(version:String):List<MiniAppPermissionType>?{
        return null
    }
    enum class MiniAppPermissionType{
        LOCATION,
        USER_NAME,
        CAMERA,
        CONTACT_LIST
    }
}