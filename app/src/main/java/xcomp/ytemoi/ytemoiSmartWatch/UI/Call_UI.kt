package xcomp.ytemoi.ytemoiSmartWatch.UI
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentActivity
import kotlinx.android.synthetic.main.activity_call.*
import xcomp.ytemoi.ytemoiSmartWatch.InterFace.CallLisstenner
import xcomp.ytemoi.ytemoiSmartWatch.R
import xcomp.ytemoi.ytemoiSmartWatch.Services.CallServices

class Call_UI : FragmentActivity(),CallLisstenner {
    companion object{
        public var callOnActivity = false
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        callOnActivity = true
        setContentView(R.layout.activity_call)
        CallServices.calllisstenner = this
        CallServices.answer()
        startCall.visibility = View.GONE
        endCall.visibility = View.GONE
        endCallCentrer.visibility = View.VISIBLE
        startCall.setOnClickListener {


        }
        endCall.setOnClickListener {
            CallServices.hangup()
            CallServices.calllisstenner = null
            finish()

        }
        endCallCentrer.setOnClickListener {
            CallServices.hangup()
            CallServices.calllisstenner = null
            finish()
        }
    }
    override fun onStart() {
        super.onStart()
        callOnActivity = true
        tv_status_call.text = CallServices.sipStatus_old
    }
    override fun onDestroy() {
        super.onDestroy()
        callOnActivity = false
    }
    @SuppressLint("SetTextI18n")
    override fun onResume(status: String) {
        runOnUiThread(Runnable {
            tv_status_call.setText("    " + status)
        })
        when(status){
            "Ringing" -> {
            }
            "Finishing" -> {
                CallServices.calllisstenner = null
                finish()
            }
            "Registering" -> {
                CallServices.calllisstenner = null
                finish()
            }
        }
    }
}