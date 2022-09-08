package com.example.recaptchatesting.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.recaptchatesting.R
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.safetynet.SafetyNet
import org.json.JSONObject


class MainActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var loginBtn : Button;
    lateinit var queue: RequestQueue;
    var SITE_KEY = "6LeaN24UAxxxxx_YOUR_SITE_KEY"
    var SECRET_KEY = "6LeaN24UAxxxxx_YOUR_SECRET_KEY"

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
                if (response.tokenResult?.isNotEmpty() == true) {
//                    handleSiteVerify(response.tokenResult)
                }
            }
            .addOnFailureListener(this) { e ->
                if (e is ApiException) {
                    Log.d(
                        "TAG", "Error message: " +
                                CommonStatusCodes.getStatusCodeString(e.statusCode)
                    )
                } else {
                    Log.d("TAG", "Unknown type of error: " + e.message)
                }
            }
    }

    fun handleSiteVerify() {
        // it is google recaptcha siteverify server
        //you can place your server url
        val url = "https://www.google.com/recaptcha/api/siteverify";
        val request = StringRequest(
            Request.Method.POST, url,
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
                     Log.d("TAG", "JSON exception: " + ex.message)
                 }
             },
            { error ->
                Log.d("TAG", "Error message: " + error.message);
            })
        // TODO: ADD PARAM, REF: https://www.javatpoint.com/kotlin-using-google-recaptcha-in-android-application

        request.retryPolicy = DefaultRetryPolicy(
            50000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        queue.add(request);
    }

}