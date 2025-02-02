package app.withyourwallet.vote.android

import com.android.volley.NetworkResponse
import com.android.volley.ParseError
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONException
import org.json.JSONObject
import java.io.UnsupportedEncodingException

class ImprovedJsonObjectRequest(
    method: Int,
    url: String?,
    jsonRequest: JSONObject?,
    listener: Response.Listener<JSONObject?>?,
    errorListener: Response.ErrorListener?
) : JsonObjectRequest(method, url, jsonRequest, listener, errorListener) {
    override fun parseNetworkResponse(response: NetworkResponse): Response<JSONObject> {
        try {
            // Patch the ability to parse with an Ok response to POST request.
            if (method == Method.POST && response.statusCode == 200) {
                return Response.success(
                    null,
                    HttpHeaderParser.parseCacheHeaders(response)
                )
            }

            val jsonString =
                String(response.data, charset(HttpHeaderParser.parseCharset(response.headers)))

            // Patch the ability to parse with an empty response.
            if (jsonString.isEmpty()) {
                return Response.success(null, HttpHeaderParser.parseCacheHeaders(response))
            }

            return Response.success(
                JSONObject(jsonString),
                HttpHeaderParser.parseCacheHeaders(response)
            )
        } catch (e: UnsupportedEncodingException) {
            return Response.error(ParseError(e))
        } catch (e: JSONException) {
            return Response.error(ParseError(e))
        }
    }
}
