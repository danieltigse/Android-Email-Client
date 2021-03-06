package com.criptext.mail.api

import android.os.Build
import android.util.Log
import com.criptext.mail.BuildConfig
import com.criptext.mail.api.models.MultipartFormItem
import com.criptext.mail.utils.DeviceUtils
import com.criptext.mail.utils.LoggingInterceptor
import com.criptext.mail.utils.file.FileUtils
import okhttp3.*
import org.json.JSONObject
import java.io.FileInputStream
import java.io.InputStream
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by gabriel on 5/8/18.
 */

interface HttpClient {
    fun post(path: String, authToken: String?, body: Map<String, MultipartFormItem>): HttpResponseData
    fun put(path: String, authToken: String?, body: Map<String, MultipartFormItem>): HttpResponseData
    fun post(path: String, authToken: String?, body: JSONObject): HttpResponseData
    fun put(path: String, authToken: String?, body: JSONObject): HttpResponseData
    fun putFileStream(path: String, authToken: String?, filePath: String): HttpResponseData
    fun get(path: String, authToken: String?): HttpResponseData
    fun delete(path: String, authToken: String?, body: JSONObject): HttpResponseData
    fun getFile(path: String, authToken: String?): ByteArray
    fun postFileStream(path: String, authToken: String?, filePath: String, randomId: String): HttpResponseData
    fun getFileStream(path: String, authToken: String?, params: Map<String, String>): InputStream

    enum class AuthScheme { basic, jwt }
    class Default(private val baseUrl: String,
                  private val authScheme: AuthScheme,
                  private val connectionTimeout: Long,
                  private val readTimeout: Long): HttpClient {

        // This is the constructor most activities should use.
        // primary constructor is more for testing.
        constructor() : this(baseUrl = Hosts.restApiBaseUrl, authScheme = AuthScheme.jwt,
                connectionTimeout = 14000L, readTimeout = 7000L)

        private val JSON = MediaType.parse("application/json; charset=utf-8")
        private val MEDIA_TYPE_PLAINTEXT = MediaType.parse("text/plain; charset=utf-8")
        private val MEDIA_TYPE_IMAGE = MediaType.parse("image/jpeg")

        private val client = buildClient()

        private fun buildClient(): OkHttpClient {
            val okClient = OkHttpClient()
                    .newBuilder()
                    .connectTimeout(connectionTimeout, TimeUnit.MILLISECONDS)
                    .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
            if(BuildConfig.DEBUG){
                okClient.addInterceptor(LoggingInterceptor())
            }
            return okClient.build()
        }

        private fun Request.Builder.addAuthorizationHeader(authToken: String?) =
                if (authToken == null) this
                else when (authScheme) {
                    AuthScheme.basic -> this.addHeader("Authorization", "Basic $authToken")
                    AuthScheme.jwt -> this.addHeader("Authorization", "Bearer $authToken")
                }

        private fun Request.Builder.addApiVersionHeader(apiVersion: String = API_VERSION) =
                this.addHeader("criptext-api-version", apiVersion)

        private fun Request.Builder.addSystemHeader(): Request.Builder {
            this.addHeader("Accept-Language", Locale.getDefault().toString().toLowerCase())
            this.addHeader("App-Version", BuildConfig.VERSION_NAME)
            this.addHeader("OS", String.format("%s %s", DeviceUtils.getDeviceName(), DeviceUtils.getDeviceOS()))
            return this.head()
        }

        private fun deleteJSON(url: String, authToken: String?, json: JSONObject): Request {
            val newUrl = HttpUrl.parse(url)!!.newBuilder()
            val httpUrl = newUrl.build()
            val body = RequestBody.create(JSON, json.toString())
            return Request.Builder()
                    .addAuthorizationHeader(authToken)
                    .addApiVersionHeader()
                    .addSystemHeader()
                    .url(httpUrl)
                    .delete(body)
                    .build()
        }

        private fun postJSON(url: String, authToken: String?, json: JSONObject): Request {
            val body = RequestBody.create(JSON, json.toString())
            return Request.Builder()
                    .addAuthorizationHeader(authToken)
                    .addApiVersionHeader()
                    .addSystemHeader()
                    .url(url)
                    .post(body)
                    .build()
        }

        private fun postStream(url: String, authToken: String?, filePath: String,
                               randomId: String?): Request {
            val body = StreamRequest(MEDIA_TYPE_PLAINTEXT, filePath)
            val builder =  Request.Builder()
                    .addAuthorizationHeader(authToken)
                    .url(url)
                    .post(body)
            if(randomId != null)
                builder.addHeader("random-id", randomId)
            return builder.build()
        }

        private fun putStream(url: String, authToken: String?, filePath: String): Request {
            val body = StreamRequest(MEDIA_TYPE_IMAGE, filePath)
            val builder =  Request.Builder()
                    .addAuthorizationHeader(authToken)
                    .url(url)
                    .put(body)
            return builder.build()
        }

        private fun putJSON(url: String, authToken: String?, json: JSONObject): Request {
            val body = RequestBody.create(JSON, json.toString())
            return Request.Builder()
                    .addAuthorizationHeader(authToken)
                    .addApiVersionHeader()
                    .addSystemHeader()
                    .url(url)
                    .put(body)
                    .build()
        }

        private fun MultipartBody.Builder.addByteItem(name: String,
                                                      item: MultipartFormItem.ByteArrayItem)
                : MultipartBody.Builder {
            val mimeType = FileUtils.getMimeType(item.name)
            val fileBody = RequestBody.create(MediaType.parse(mimeType), item.value)
            return this.addFormDataPart(name, item.name, fileBody)
        }

        private fun MultipartBody.Builder.addFileItem(name: String,
                                                      item: MultipartFormItem.FileItem)
                : MultipartBody.Builder {
            val mimeType = FileUtils.getMimeType(item.name)
            val fileBody = RequestBody.create(MediaType.parse(mimeType), item.value)
            return this.addFormDataPart(name, item.name, fileBody)
        }

        private fun postMultipartFormData(url: String, authToken: String?,
                                          body: Map<String, MultipartFormItem>): Request {
            val multipartBody =
                    body.toList().fold(MultipartBody.Builder()) { builder, (name, item) ->
                        when (item) {
                            is MultipartFormItem.StringItem ->
                                builder.addFormDataPart(name, item.value)
                            is MultipartFormItem.ByteArrayItem ->
                                builder.addByteItem(name, item)
                            is MultipartFormItem.FileItem ->
                                builder.addFileItem(name, item)
                        }
                    }.build()

            return Request.Builder()
                    .addAuthorizationHeader(authToken)
                    .addApiVersionHeader()
                    .addSystemHeader()
                    .url(url)
                    .post(multipartBody)
                    .build()
        }

        private fun putMultipartFormData(url: String, authToken: String?,
                                         body: Map<String, MultipartFormItem>): Request {
            val multipartBody =
                    body.toList().fold(MultipartBody.Builder()) { builder, (name, item) ->
                        when (item) {
                            is MultipartFormItem.StringItem ->
                                builder.addFormDataPart(name, item.value)
                            is MultipartFormItem.ByteArrayItem ->
                                builder.addByteItem(name, item)
                            is MultipartFormItem.FileItem ->
                                builder.addFileItem(name, item)
                        }
                    }.build()

            return Request.Builder()
                    .addAuthorizationHeader(authToken)
                    .addApiVersionHeader()
                    .addSystemHeader()
                    .url(url)
                    .put(multipartBody)
                    .build()
        }

        private fun getUrl(url: String, authToken: String?): Request {
            return Request.Builder()
                    .addAuthorizationHeader(authToken)
                    .addApiVersionHeader()
                    .addSystemHeader()
                    .url(url)
                    .get()
                    .build()
        }

        private fun getUrlWithQueryParams(url: String, authToken: String?, params: Map<String, String>): Request {
            val newUrl = HttpUrl.parse(url)!!.newBuilder()
            for (key in params.keys) {
                newUrl.addQueryParameter(key, params[key])
            }
            return Request.Builder()
                    .addAuthorizationHeader(authToken)
                    .url(newUrl.build())
                    .get()
                    .build()
        }

        override fun post(path: String, authToken: String?, body: Map<String, MultipartFormItem>): HttpResponseData {
            val request = postMultipartFormData(baseUrl + path, authToken, body)
            return ApiCall.executeRequest(client, request)
        }

        override fun put(path: String, authToken: String?, body: Map<String, MultipartFormItem>): HttpResponseData {
            val request = postMultipartFormData(baseUrl + path, authToken, body)
            return ApiCall.executeRequest(client, request)
        }

        override fun post(path: String, authToken: String?, body: JSONObject): HttpResponseData {
            val request = postJSON(baseUrl + path, authToken, body)
            return ApiCall.executeRequest(client, request)
        }

        override fun put(path: String, authToken: String?, body: JSONObject): HttpResponseData {
            val request = putJSON(baseUrl + path, authToken, body)
            return ApiCall.executeRequest(client, request)
        }

        override fun putFileStream(path: String, authToken: String?, filePath: String): HttpResponseData {
            val request = putStream(url = baseUrl + path, authToken = authToken,
                    filePath = filePath)
            return ApiCall.executeRequest(client, request)
        }


        override fun get(path: String, authToken: String?): HttpResponseData {
            val request = getUrl(url = baseUrl + path, authToken = authToken)
            return ApiCall.executeRequest(client, request)
        }

        override fun delete(path: String, authToken: String?, body: JSONObject): HttpResponseData {
            val request = deleteJSON(url = baseUrl + path, authToken = authToken, json = body)
            return ApiCall.executeRequest(client, request)
        }

        override fun getFile(path: String, authToken: String?): ByteArray {
            val request = getUrl(url = baseUrl + path, authToken = authToken)
            return ApiCall.executeFileRequest(client, request)
        }

        override fun postFileStream(path: String, authToken: String?, filePath: String, randomId: String): HttpResponseData {
            val request = postStream(url = baseUrl + path, authToken = authToken,
                    filePath = filePath, randomId = randomId)
            return ApiCall.executeRequest(client, request)
        }

        override fun getFileStream(path: String, authToken: String?, params: Map<String, String>): InputStream {
            val request = getUrlWithQueryParams(url = baseUrl + path, authToken = authToken, params = params)
            return ApiCall.executeInputStreamRequest(client, request)
        }

        companion object {
            const val API_VERSION = "9.0.0"
        }
    }
}
