package com.example.recaptchatesting

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.DefaultRetryPolicy
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.safetynet.SafetyNet
import org.json.JSONObject


class MainActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var loginBtn : Button;
    lateinit var queue: RequestQueue;
    var SITE_KEY = "6Lf3R-IhAAAAAHyFKiN94fVHY5VISC2nOqIYGJal"
    var SECRET_KEY = "6Lf3R-IhAAAAALvMzkaR0iBzanxqvk_XsKfKGASu"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loginBtn = findViewById(R.id.login_button)

        queue = Volley.newRequestQueue(applicationContext);

        loginBtn.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        SafetyNet.getClient(this).verifyWithRecaptcha(SITE_KEY)
            .addOnSuccessListener(this) { response ->
                val tokenResult = response.tokenResult
                if (!tokenResult.isNullOrBlank()) {
                    handleSiteVerify(tokenResult)
                }
            }
            .addOnFailureListener(this) { e ->
                if (e is ApiException) {
                    Log.d(
                        "<TAG>", "Error message: " +
                                CommonStatusCodes.getStatusCodeString(e.statusCode)
                    )
                } else {
                    Log.d("<TAG>", "Unknown type of error: " + e.message)
                }
            }
    }

    private fun handleSiteVerify(responseToken: String) {
        val url = "https://www.google.com/recaptcha/api/siteverify";
        val request = object: StringRequest(
            Method.POST, url,
             { response ->
                 try {
                     val jsonObject = JSONObject(response)
                     if (jsonObject.getBoolean("success")) {
                         //code logic when captcha returns true
                        Toast.makeText(
                            applicationContext,
                            jsonObject.getBoolean("success").toString(),
                            Toast.LENGTH_LONG
                        ).show();
                     } else {
                         Toast.makeText(
                             applicationContext,
                             jsonObject.getString("error-codes").toString(),
                             Toast.LENGTH_LONG
                         ).show()
                     }
                 } catch (ex: Exception) {
                     Log.d("<TAG>", "JSON exception: " + ex.message)
                 }
             },
            { error ->
                Log.d("<TAG>", "Error message: " + error.message);
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["secret"] = SECRET_KEY
                params["response"] = responseToken
                return params
            }
        }

        request.retryPolicy = DefaultRetryPolicy(
            50000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        queue.add(request);
    }

}