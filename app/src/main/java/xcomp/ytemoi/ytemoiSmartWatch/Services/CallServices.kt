package xcomp.ytemoi.ytemoiSmartWatch.Services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_MIN
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.mizuvoip.jvoip.SipStack
import org.bson.*
import org.json.JSONException
import xcomp.ytemoi.ytemoiSmartWatch.InterFace.CallLisstenner
import xcomp.ytemoi.ytemoiSmartWatch.R
import xcomp.ytemoi.ytemoiSmartWatch.UI.Call_UI
import java.util.*

class CallServices : Service() {
    private  var sipClient: SipStack? = null
    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onCreate() {
        super.onCreate()
        if(Instance == null){
            Instance = this
        }
        setUpVOIP()
        startForeground()
        val thread = object : Thread() {
            override fun run() {
                while (true) {
                    try {
                        SendInfo()
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    try {
                        sleep(5000)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
            }

        }
        thread.priority = 10
        thread.start()
    }

    //volley
    internal class VolleySingleton private constructor(context: Context) {
        var requestQueue: RequestQueue? = null

        companion object {
            private const val TAG = "VolleySingleton"
            private var sInstance: VolleySingleton? = null
            @Synchronized
            fun getInstance(context: Context): VolleySingleton? {
                if (sInstance == null) sInstance = VolleySingleton(context)
                return sInstance
            }
        }

        init {
            if (requestQueue == null) {
                requestQueue = Volley.newRequestQueue(context.applicationContext)
            }
        }
    }
    private val urlactivateNumber = "https://ytemoi.com/api/ncb"
    @Throws(JSONException::class)
    private fun SendInfo() {
        val bson = BsonDocument()
        //        bson.append("health" , health.toBsonDocument());
        bson.append("time", BsonDateTime(Date().time))
        val location = BsonDocument()
        location.append("lat", BsonInt32(0))
        location.append("long", BsonInt32(0))
        val chilocation = BsonDocument()
        chilocation.append("ten", BsonString("Vị trí"))
        chilocation.append("data", location)
        chilocation.append("nguon", BsonString("nhap"))
        val anhdan = BsonArray()
        if (SenSorServices.lsThongSo != null) SenSorServices.lsThongSo!!.forEach { it ->
            val document = it.toBsonDocument()
            if (document != null) anhdan.add(
                document
            )
        }
        //        anhdan.add(chilocation);
        bson.append("data", anhdan)
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST, urlactivateNumber,
            Response.Listener { response ->
                Log.e(
                    TAG, "StringRequest onResponse: $response"
                )
            },
            Response.ErrorListener { error ->
                Log.e(
                    TAG, "StringRequest onErrorResponse: " + error.message
                )
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>? {
                val params: MutableMap<String, String> = HashMap()
                params["id"] = SenSorServices.ID_DEVICE
                params["value"] = bson.toString()
                params["type"] = "IoT_SVG_Data"
                return params
            }
        }
        VolleySingleton.getInstance(this)!!.requestQueue!!.cache.clear()
        VolleySingleton.getInstance(this)!!.requestQueue!!.add(stringRequest)
    }
    //
    private fun startForeground() {

        val channelId =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel()
            } else {
                // If earlier version channel ID is not used
                // https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html#NotificationCompat.Builder(android.content.Context)
                ""
            }
        val notificationBuilder = NotificationCompat.Builder(this, channelId )
        val notification = notificationBuilder.setOngoing(true)
            .setSmallIcon(R.drawable.ytemoilogo)
            .setPriority(PRIORITY_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        startForeground(101, notification)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(): String{
        val channelId = "Services Call VOIP"
        val channelName = "Background Service Call VOIP"
        val chan = NotificationChannel(channelId,
            channelName, NotificationManager.IMPORTANCE_HIGH)
        chan.lightColor = Color.BLUE
        chan.importance = NotificationManager.IMPORTANCE_NONE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }
    private fun setUpVOIP() {

        val params = "serveraddress=171.244.133.171\r\nusername=${ID_VOIP}\r\npassword=${ID_VOIP_PASS}\r\nloglevel=5";
        sipClient = SipStack()
        sipClient!!.Init(Instance!!.applicationContext)
        sipClient!!.SetParameters(params)
        val thread = object : Thread() {
            override fun run() {
                ListenEventCall()
            }

        }.start()
       sipClient!!.Start()
    }
    private fun ListenEventCall() {
        var sipStatus = ""
        while (true) {
            if (sipClient != null) {
                sipStatus = sipClient!!.GetStatus(-2)
                if (sipStatus_old != sipStatus) {
                    Log.e(TAG, "run: sipStatus :$sipStatus")
                    sipStatus_old = sipStatus
                    if (!Call_UI.callOnActivity){
                        if (sipStatus == "Ringing") {
                            val intent = Intent(Instance, Call_UI::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }
                    else
                        if(calllisstenner != null)
                            calllisstenner!!.onResume(sipStatus)
                }
            }
            try {
                Thread.sleep(300)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }
    companion object{
        private const val TAG = "Services_Call"
        private  var Instance:CallServices? = null
        var ID_VOIP = ""
        var ID_VOIP_PASS = ""
        var sipStatus_old = ""
        public var calllisstenner:CallLisstenner? = null

        public fun isReady():Boolean{
            return Instance != null
        }

        public fun answer() {
            Instance?.sipClient?.Accept(1)
        }
        public fun hangup() {
            Instance?.sipClient?.Hangup(-2)
        }

    }
}