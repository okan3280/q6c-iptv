package com.q6c.iptv
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
class ChannelAdapter(val items: List<Channel>, val epg: Map<String,List<EpgItem>>,
  val onPlay:(Channel)->Unit, val onFav:(Channel)->Unit): RecyclerView.Adapter<ChannelAdapter.H>() {
  inner class H(v: View): RecyclerView.ViewHolder(v){ val img: ImageView=v.findViewById(R.id.logo); val nm: TextView=v.findViewById(R.id.name); val pg: TextView=v.findViewById(R.id.prog) }
  override fun onCreateViewHolder(p: ViewGroup, t: Int) = H(LayoutInflater.from(p.context).inflate(R.layout.row_channel,p,false).apply{ isFocusable=true; isFocusableInTouchMode=true })
  override fun getItemCount() = items.size
  override fun onBindViewHolder(h: H, pos: Int) {
    val c = items[pos]; h.nm.text=c.name
    Glide.with(h.img).load(c.logo).placeholder(android.R.drawable.ic_media_play).into(h.img)
    val now=System.currentTimeMillis()
    h.pg.text = epg[c.tvgId]?.firstOrNull{now in it.start..it.stop}?.title ?: c.group
    h.itemView.setOnClickListener{ onPlay(c) }
    h.itemView.setOnLongClickListener{ onFav(c); true }
  }
}
