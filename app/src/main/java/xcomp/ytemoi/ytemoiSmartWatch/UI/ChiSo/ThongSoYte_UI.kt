package xcomp.ytemoi.ytemoiSmartWatch.UI.ChiSo
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_thong_so_yte.*
import xcomp.ytemoi.ytemoiSmartWatch.InterFace.SensorLisstenner
import xcomp.ytemoi.ytemoiSmartWatch.InterFace.listennerUpdatechiso
import xcomp.ytemoi.ytemoiSmartWatch.Model.ThongSo
import xcomp.ytemoi.ytemoiSmartWatch.Model.ThongSoCoThe
import xcomp.ytemoi.ytemoiSmartWatch.R
import xcomp.ytemoi.ytemoiSmartWatch.Services.SenSorServices

class ThongSoYte_UI : FragmentActivity() , listennerUpdatechiso {
    companion object{
        public var onThongSoYte_UI = false
        public var adapter:ThongSoAdapter? = null
        public var listenner = object :SensorLisstenner{
            override fun onRespone() {
                adapter!!.setData(SenSorServices.lsThongSo!!)
            }

        }
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thong_so_yte)
        SenSorServices.sensorlistenner = listenner
        adapter = ThongSoAdapter(this , null)
        rc_thongso.layoutManager = LinearLayoutManager(this)
        rc_thongso.adapter = adapter
        if (SenSorServices.lsThongSo == null) {
            SenSorServices.initChiso()
        }
        SenSorServices.lsThongSo?.let { adapter!!.setData(it) }
        onThongSoYte_UI = true
        EditThongSo_UI.capNhat = this
    }

    override fun CapNhatUI() {
        adapter!!.setData(SenSorServices.lsThongSo!!)
    }

}