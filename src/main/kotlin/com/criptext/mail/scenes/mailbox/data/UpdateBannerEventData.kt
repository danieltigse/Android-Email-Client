package com.criptext.mail.scenes.mailbox.data

import org.json.JSONObject

data class UpdateBannerEventData(val messageCode: Int){
    companion object {
        fun fromJSON(jsonString: String): UpdateBannerEventData{
            val json = JSONObject(jsonString)
            return UpdateBannerEventData(
                    messageCode = json.getInt("code")
            )
        }
    }
}