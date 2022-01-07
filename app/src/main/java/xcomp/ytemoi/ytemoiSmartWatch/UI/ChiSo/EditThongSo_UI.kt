package xcomp.ytemoi.ytemoiSmartWatch.UI.ChiSo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentActivity
import kotlinx.android.synthetic.main.activity_edit_thong_so_ui.*
import xcomp.ytemoi.ytemoiSmartWatch.InterFace.listennerUpdatechiso
import xcomp.ytemoi.ytemoiSmartWatch.Model.ThongSo
import xcomp.ytemoi.ytemoiSmartWatch.R
import xcomp.ytemoi.ytemoiSmartWatch.Services.SenSorServices

class EditThongSo_UI : FragmentActivity() {
    companion object{
        var capNhat: listennerUpdatechiso? = null
    }
    private lateinit var thongso:ThongSo
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_thong_so_ui)

        val extras = intent.extras
        if (extras != null) {
            val tenChiso = extras.getString("tenChiso")
            val mchiso = extras.getString("chiso")
            val kyhieu = extras.getString("kyhieu")
            thongso = ThongSo(tenChiso!! ,mchiso!! ,kyhieu!!  )
            titlechiso.text = thongso.tenThongSo
            if(thongso.chiso != "")
                et_edit_chiso.setText(thongso.chiso.toString())
            else
                et_edit_chiso.setText("")
            Donvi.text = thongso.kyhieu
            //The key argument here must match that used in the other activity
        }
        capnhat.setOnClickListener(View.OnClickListener {
            val num = et_edit_chiso.text.toString()
            SenSorServices.lsThongSo!!.forEach { it ->
                if (it.tenThongSo.equals(thongso.tenThongSo)) {
                    it.chiso = num
                    it.loai = "Nhap"
                }
            }
            capNhat?.CapNhatUI()
            finish()
        })
    }
}