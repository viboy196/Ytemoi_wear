package xcomp.ytemoi.ytemoiSmartWatch.UI.ChiSo

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import xcomp.ytemoi.ytemoiSmartWatch.Model.ThongSo
import xcomp.ytemoi.ytemoiSmartWatch.R

class ThongSoAdapter(var mcontex:Context, var lsthongSo: ArrayList<ThongSo>?): RecyclerView.Adapter<ThongSoAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        public val tv_name: TextView
        public val tv_chiso: TextView
        public val item_thongso: LinearLayout
        init {
            tv_name = itemView.findViewById(R.id.tv_name)
            tv_chiso = itemView.findViewById(R.id.tv_chiso)
            item_thongso = itemView.findViewById(R.id.item_thongso)
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    public fun setData(mlsthongso:ArrayList<ThongSo>){
        lsthongSo = mlsthongso
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    public fun addData(thongSo:ThongSo){
        lsthongSo!!.add(thongSo)
        notifyDataSetChanged()
    }
    @SuppressLint("NotifyDataSetChanged")
    public fun editData(thongSo:ThongSo){
        lsthongSo!!.add(thongSo)
        notifyDataSetChanged()
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(mcontex)
        val heroView: View = inflater.inflate(R.layout.adapter_chiso, parent, false)
        return ViewHolder(heroView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val thongso = lsthongSo!!.get(position)
        holder.tv_name.text = thongso.tenThongSo
        if(thongso.loai != "" || thongso.chiso != "")
            holder.tv_chiso.text = "" + thongso.chiso + " " + thongso.kyhieu
        else
            holder.tv_chiso.text = "...."
        holder.item_thongso.setOnClickListener {
            StartEditThongSo(thongso)
        }

    }

    private fun StartEditThongSo(thongso: ThongSo) {
        Log.e("ThongSoAdapter", "StartEditThongSo: ")
        val intent = Intent(mcontex, EditThongSo_UI::class.java)
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("tenChiso", thongso.tenThongSo)
        intent.putExtra("chiso", thongso.chiso)
        intent.putExtra("kyhieu", thongso.kyhieu)

        mcontex.startActivity(intent)
    }

    override fun getItemCount(): Int {
        return lsthongSo!!.size
    }
}