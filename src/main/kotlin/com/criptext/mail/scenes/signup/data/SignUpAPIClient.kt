package com.criptext.mail.scenes.signup.data

import com.criptext.mail.api.HttpClient
import com.criptext.mail.api.HttpResponseData
import com.criptext.mail.signal.PreKeyBundleShareData
import com.criptext.mail.scenes.signup.IncompleteAccount
import com.criptext.mail.utils.DeviceUtils
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by sebas on 2/26/18.
 */

class SignUpAPIClient(private val httpClient: HttpClient) {

    fun createUser(
            account: IncompleteAccount,
            keyBundle : PreKeyBundleShareData.UploadBundle
    ): HttpResponseData {
        val jsonObject = JSONObject()
        jsonObject.put("name", account.name)
        jsonObject.put("password", account.password)
        jsonObject.put("recipientId", account.username)
        jsonObject.put("keybundle", keyBundle.toJSON())
        if (account.recoveryEmail != null)
            jsonObject.put("recoveryEmail", account.recoveryEmail)

        return httpClient.post("/user", null, jsonObject)
    }

    fun userCanLogin(username: String, domain: String): HttpResponseData {
        return httpClient.get("/user/canlogin?username=$username&domain=$domain", null)
    }

    fun isUsernameAvailable(username: String): HttpResponseData {
        return httpClient.get("/user/available?username=$username", null)
    }

    fun postForgotPassword(recipientId: String, domain: String): HttpResponseData{
        val jsonPut = JSONObject()
        jsonPut.put("recipientId", recipientId)
        jsonPut.put("domain", domain)

        return httpClient.post(path = "/user/password/reset", authToken = null, body = jsonPut)
    }

    fun postFindDevices(recipientId: String, domain: String, password: String): HttpResponseData{
        val jsonPut = JSONObject()
        jsonPut.put("recipientId", recipientId)
        jsonPut.put("domain", domain)
        jsonPut.put("password", password)

        return httpClient.post(path = "/device/find", authToken = null, body = jsonPut)
    }

    fun deleteDevices(devicesIds: List<Int>, token: String, recipientId: String, domain: String): HttpResponseData{
        return httpClient.delete(path = "/device/$recipientId/$domain/$token?${devicesIds.map { "deviceId=$it" }.joinToString(separator = "&")}", authToken = null, body = JSONObject())
    }

    fun putFirebaseToken(pushToken: String, jwt: String): HttpResponseData {
        val jsonPut = JSONObject()
        jsonPut.put("devicePushToken", pushToken)

        return httpClient.put(path = "/keybundle/pushtoken", authToken = jwt, body = jsonPut)
    }
}
