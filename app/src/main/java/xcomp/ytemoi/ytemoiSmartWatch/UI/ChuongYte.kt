package xcomp.ytemoi.ytemoiSmartWatch.UI
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.wear.ambient.AmbientModeSupport
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import kotlinx.android.synthetic.main.activity_chuongyte.*
import xcomp.ytemoi.ytemoiSmartWatch.R
import xcomp.ytemoi.ytemoiSmartWatch.Services.CallServices
import xcomp.ytemoi.ytemoiSmartWatch.Services.SenSorServices
import xcomp.ytemoi.ytemoiSmartWatch.UI.ChiSo.ThongSoYte_UI
import java.util.HashMap

class ChuongYte : FragmentActivity() , AmbientModeSupport.AmbientCallbackProvider {

    companion object{
    }

    private val TAG = "ChuongYte"
    // gửi thông báo
    private val urlactivateNumber = "https://ytemoi.com/api/ncb"
    // account
    private val ID_DEVICE  = "NCB0001005"
    private val ID_VOIP = "1005"
    // alway-on
    private lateinit var ambientController: AmbientModeSupport.AmbientController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chuongyte)
        ambientController = AmbientModeSupport.attach(this)
        bellCall.setOnClickListener {
            CallNuse()
        }
        btnmenu.setOnClickListener {
            showPopup(it)
        }
        requestPermissions()
    }
    private fun requestPermissions(){
        val pm: PermissionListener = object : PermissionListener{
            override fun onPermissionGranted() {
               //start Services call
                startServiceCall()
                startServiceSensor()

            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
            }

        }
        TedPermission.create()
            .setPermissionListener(pm)
            .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
            .setPermissions(
                Manifest.permission.RECORD_AUDIO ,
                Manifest.permission.USE_SIP ,
            Manifest.permission.BODY_SENSORS)
            .check()
    }

    private fun startServiceSensor() {
        if (!SenSorServices.isReady()) {
            val intent = Intent(applicationContext, SenSorServices::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.e(
                    TAG,
                    "onCreate startForegroundService"
                )
                applicationContext.startForegroundService(intent)
            } else {
                Log.e(TAG, "onCreate startService")
                applicationContext.startService(intent)
            }
        }
    }

    private fun startServiceCall() {
        if (!CallServices.isReady()) {
            CallServices.ID_VOIP = "1005"
            CallServices.ID_VOIP_PASS = "1005ytm"
            val intent = Intent(applicationContext, CallServices::class.java)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.e(
                    TAG,
                    "onCreate startForegroundService"
                )
                applicationContext.startForegroundService(intent)
            } else {
                Log.e(TAG, "onCreate startService")
                applicationContext.startService(intent)
            }
        }
    }
    fun canPlayAudio(context: Context): Boolean {
        val packageManager = context.packageManager
        val audioManager = context.getSystemService(AUDIO_SERVICE) as AudioManager

        // Check whether the device has a speaker.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Check FEATURE_AUDIO_OUTPUT to guard against false positives.
            if (!packageManager.hasSystemFeature(PackageManager.FEATURE_AUDIO_OUTPUT)) {
                return false
            }
            val devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
            for (device in devices) {
                if (device.type == AudioDeviceInfo.TYPE_BUILTIN_SPEAKER) {
                    return true
                }
            }
        }
        return false
    }
    private fun CallNuse() {
        if(canPlayAudio(this@ChuongYte))
        {
            val mPlayer: MediaPlayer = MediaPlayer.create(this@ChuongYte, R.raw.nusecall_noti)
            mPlayer.start()
        }


        val v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        v.vibrate(400)

        CallNuseAPI()
    }
    private fun CallNuseAPI() {
        val queue = Volley.newRequestQueue(this@ChuongYte)
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST, urlactivateNumber,
            Response.Listener {
                Toast.makeText(
                    this@ChuongYte,
                    "Gửi Thông báo trợ giúp thành công!",
                    Toast.LENGTH_LONG
                ).show()
                Log.e(TAG, ": Response onServiceReady ${it}")
            }, object : Response.ErrorListener {
                val TAG = "callNuse"
                override fun onErrorResponse(error: VolleyError) {
                    Log.e(TAG, "onErrorResponse: onServiceReady :$error")
                }
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>? {
                val params: MutableMap<String, String> = HashMap()
                params["id"] = ID_DEVICE
                params["value"] = ID_VOIP
                params["type"] = "NCBG_SVG_ButtonSignal_Click"
                return params
            }
        }
        queue.cache.clear()
        queue.add(stringRequest)
    }
    //layout menu
    fun showPopup(v: View) {
        val popup = PopupMenu(this, v)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.menu, popup.menu)
        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener{
            menuItemClicked(it)
        } )
        popup.show()
    }
    private fun menuItemClicked(it: MenuItem?):Boolean {
        when(it?.itemId){
            R.id.menuthongsoyte ->{
                Log.i(TAG, "menuItemClicked: menucanhan")
                val intent =  Intent(this , ThongSoYte_UI::class.java)
                startActivity(intent)
            }
        }
        return  true
    }
    override fun getAmbientCallback(): AmbientModeSupport.AmbientCallback = MyAmbientCallback()
    private inner class MyAmbientCallback : AmbientModeSupport.AmbientCallback() {

        /**
         * If the display is low-bit in ambient mode. i.e. it requires anti-aliased fonts.
         */
        private var isLowBitAmbient = false

        /**
         * If the display requires burn-in protection in ambient mode, rendered pixels need to be
         * intermittently offset to avoid screen burn-in.
         */
        private var doBurnInProtection = false

        /**
         * Prepares the UI for ambient mode.
         */
        override fun onEnterAmbient(ambientDetails: Bundle) {
            super.onEnterAmbient(ambientDetails)
            isLowBitAmbient = ambientDetails.getBoolean(AmbientModeSupport.EXTRA_LOWBIT_AMBIENT, false)
            doBurnInProtection =
                ambientDetails.getBoolean(AmbientModeSupport.EXTRA_BURN_IN_PROTECTION, false)

            // Cancel any active updates

            /*
             * Following best practices outlined in WatchFaces API (keeping most pixels black,
             * avoiding large blocks of white pixels, using only black and white, and disabling
             * anti-aliasing, etc.)
             */
//            binding.state.setTextColor(Color.WHITE)
//            binding.updateRate.setTextColor(Color.WHITE)
//            binding.drawCount.setTextColor(Color.WHITE)
//            if (isLowBitAmbient) {
//                binding.time.paint.isAntiAlias = false
//                binding.timeStamp.paint.isAntiAlias = false
//                binding.state.paint.isAntiAlias = false
//                binding.updateRate.paint.isAntiAlias = false
//                binding.drawCount.paint.isAntiAlias = false
//            }

        }

        /**
         * Updates the display in ambient mode on the standard interval. Since we're using a custom
         * refresh cycle, this method does NOT update the data in the display. Rather, this method
         * simply updates the positioning of the data in the screen to avoid burn-in, if the display
         * requires it.
         */
        override fun onUpdateAmbient() {
            super.onUpdateAmbient()

            /*
             * If the screen requires burn-in protection, views must be shifted around periodically
             * in ambient mode. To ensure that content isn't shifted off the screen, avoid placing
             * content within 10 pixels of the edge of the screen.
             *
             * Since we're potentially applying negative padding, we have ensured
             * that the containing view is sufficiently padded (see res/layout/activity_main.xml).
             *
             * Activities should also avoid solid white areas to prevent pixel burn-in. Both of
             * these requirements only apply in ambient mode, and only when this property is set
             * to true.
             */
            if (doBurnInProtection) {
//                binding.container.translationX =
//                    Random.nextInt(-BURN_IN_OFFSET_PX, BURN_IN_OFFSET_PX + 1).toFloat()
//                binding.container.translationY =
//                    Random.nextInt(-BURN_IN_OFFSET_PX, BURN_IN_OFFSET_PX + 1).toFloat()
            }
        }

        /**
         * Restores the UI to active (non-ambient) mode.
         */
        override fun onExitAmbient() {
            super.onExitAmbient()

            if (isLowBitAmbient) {
//                binding.time.paint.isAntiAlias = true
//                binding.timeStamp.paint.isAntiAlias = true
//                binding.state.paint.isAntiAlias = true
//                binding.updateRate.paint.isAntiAlias = true
//                binding.drawCount.paint.isAntiAlias = true
            }

            /* Reset any random offset applied for burn-in protection. */
            if (doBurnInProtection) {
//                binding.container.translationX = 0f
//                binding.container.translationY = 0f
            }
        }
    }

}