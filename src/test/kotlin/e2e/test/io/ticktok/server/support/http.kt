package e2e.test.io.ticktok.server.support

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.apache.http.HttpResponse
import org.apache.http.client.utils.URIBuilder
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils

const val ACCESS_TOKEN = "ct-auth-token"

fun createAuthenticatedUrlFor(slag: String, params: Map<String, String> = mapOf()): String {
    return URIBuilder(App.APP_URL)
            .setPath(slag)
            .setParameters(params.map { BasicNameValuePair(it.key, it.value) })
            .setParameter("access_token", ACCESS_TOKEN).build().toString()
}

fun bodyOf(response: HttpResponse) = EntityUtils.toString(response.entity)

inline fun <reified T> Gson.fromJson(json: String) =
        this.fromJson<T>(json, object : TypeToken<T>() {}.type)!!