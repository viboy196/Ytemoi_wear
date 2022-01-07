package xcomp.ytemoi.ytemoiSmartWatch.Services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.bson.*
import org.json.JSONException
import xcomp.ytemoi.ytemoiSmartWatch.InterFace.SensorLisstenner
import xcomp.ytemoi.ytemoiSmartWatch.Model.ThongSo
import xcomp.ytemoi.ytemoiSmartWatch.Model.ThongSoCoThe
import xcomp.ytemoi.ytemoiSmartWatch.R
import xcomp.ytemoi.ytemoiSmartWatch.UI.ChiSo.ThongSoYte_UI
import java.util.*

class SenSorServices : Service() , SensorEventListener {

    // health
    private var mSensorManager: SensorManager? = null
    private var mHeartRateSensor: Sensor? = null
    private var mCounterRateSensor: Sensor? = null

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val num = event!!.values[0].toInt()
        if (event.sensor.type == Sensor.TYPE_HEART_RATE) {
            val msg = "onSensorChanged TYPE_HEART_RATE : $num"
            Log.d(TAG, msg)
            if (thongso.nhipTim != num) {
                thongso.nhipTim = num
                thongso.date = Date()
                for( it in lsThongSo!!){
                    if(it.tenThongSo == "Nhịp tim" ){
                        it.loai = "Sensor"
                        it.chiso = thongso.nhipTim.toString()
                        break
                    }
                }
                if(sensorlistenner != null && ThongSoYte_UI.onThongSoYte_UI)
                    sensorlistenner?.onRespone()
            }
        }
        if (event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
            val msg = "onSensorChanged TYPE_STEP_COUNTER : $num"
            Log.d(TAG, msg)
            if (thongso.buocdi != num) {
                thongso.buocdi = num
                thongso.date = Date()
                for( it in lsThongSo!!){
                    if(it.tenThongSo == "Số bước" ){
                        it.loai = "Sensor"
                        it.chiso = thongso.buocdi.toString()
                        break
                    }
                }
                if(sensorlistenner != null && ThongSoYte_UI.onThongSoYte_UI)
                    sensorlistenner?.onRespone()
            }
        }

    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onCreate() {
        super.onCreate()
        startForeground()
        initChiso()
        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        mHeartRateSensor = mSensorManager!!.getDefaultSensor(Sensor.TYPE_HEART_RATE)
        mCounterRateSensor = mSensorManager!!.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        //mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE); // using Sensor Lib (Samsung Gear Live)

        //mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE); // using Sensor Lib (Samsung Gear Live)
        if (mHeartRateSensor == null) Log.d(
            TAG,
            "heart rate sensor is null"
        )
        if (mCounterRateSensor == null) Log.d(
            TAG,
            "counter rate sensor is null"
        )

        mSensorManager!!.registerListener(this, mHeartRateSensor, SensorManager.SENSOR_DELAY_NORMAL)
        mSensorManager!!.registerListener(this, mCounterRateSensor, SensorManager.SENSOR_DELAY_NORMAL)

    }



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
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        startForeground(102, notification)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(): String{
        val channelId = "Services SenSor"
        val channelName = "Background Service Sensor"
        val chan = NotificationChannel(channelId,
            channelName, NotificationManager.IMPORTANCE_HIGH)
        chan.lightColor = Color.BLUE
        chan.importance = NotificationManager.IMPORTANCE_NONE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }
    companion object{
        // account
        var ID_DEVICE = "NCB0001005"
        private const val TAG = "SenSorServices"
        private  var Instance:SenSorServices? = null
        public var sensorlistenner: SensorLisstenner = ThongSoYte_UI.listenner
        public fun isReady():Boolean{
            return Instance != null
        }
        public var thongso:ThongSoCoThe = ThongSoCoThe()
        public var lsThongSo:ArrayList<ThongSo>? = null
        public fun initChiso() {
            lsThongSo = ArrayList()
            lsThongSo!!.add(ThongSo("Nhịp tim" , "","bmp"))
            lsThongSo!!.add(ThongSo("Spo2" ,"","%"))
            lsThongSo!!.add(ThongSo("Huyết áp" ,"","mmHg"))
            lsThongSo!!.add(ThongSo("Nhiệt độ" ,"","°C"))
            lsThongSo!!.add(ThongSo("Đường máu" ,"","mg/DL"))
            lsThongSo!!.add(ThongSo("Vị trí" ,"",""))
            lsThongSo!!.add(ThongSo("Số bước" , "","step"))
        }

    }
}