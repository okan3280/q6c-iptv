package com.q6c.iptv
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.media3.ui.PlayerView
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.q6c.iptv.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
// Tek aktiviteli TV UI: sol kategori+liste, sağ ExoPlayer. D-pad native focus ile kasmaz.
class MainActivity : AppCompatActivity() {
  private lateinit var b: ActivityMainBinding
  private lateinit var pm: PlayerManager
  private var all: List<Channel> = emptyList()
  private var shown: List<Channel> = emptyList()
  private var epg: Map<String, List<EpgItem>> = emptyMap()
  private var idx = -1
  private val ok = OkHttpClient()
  private val favs get() = PreferenceManager.getDefaultSharedPreferences(this).getStringSet("fav", mutableSetOf()) ?: mutableSetOf()

  override fun onCreate(s: Bundle?) {
    super.onCreate(s); b = ActivityMainBinding.inflate(layoutInflater); setContentView(b.root)
    pm = PlayerManager(this); b.playerView.player = pm.player
    b.channels.layoutManager = LinearLayoutManager(this)
    b.btnLoad.setOnClickListener { showSettings() }
    b.search.setOnEditorActionListener { v,_,_ -> filter(v.text.toString()); true }
    load()
  }
  private fun load() = lifecycleScope.launch(Dispatchers.IO) {
    val prefs = PreferenceManager.getDefaultSharedPreferences(this@MainActivity)
    val m3u = prefs.getString("m3u","") ?: ""
    if (m3u.isEmpty()) { withContext(Dispatchers.Main){ Toast.makeText(this@MainActivity,"Ayarlardan M3U gir",Toast.LENGTH_LONG).show(); showSettings() }; return@launch }
    try {
      val txt = ok.newCall(Request.Builder().url(m3u).build()).execute().body!!.string()
      all = M3uParser.parse(txt)
      val e = prefs.getString("epg","") ?: ""
      if (e.isNotEmpty()) try { epg = EpgParser.parse(ok.newCall(Request.Builder().url(e).build()).execute().body!!.string()) } catch(_: Exception){}
      withContext(Dispatchers.Main){ filter(""); Toast.makeText(this@MainActivity,"${all.size} kanal",Toast.LENGTH_SHORT).show() }
    } catch(e: Exception){ withContext(Dispatchers.Main){ Toast.makeText(this@MainActivity,"M3U hata: ${e.message}",Toast.LENGTH_LONG).show() } }
  }
  private fun filter(q: String) {
    shown = if(q.isBlank()) all else all.filter{ it.name.contains(q,true) }.take(2000)
    b.channels.adapter = ChannelAdapter(shown, epg, { play(it) }, { toggleFav(it) })
    b.count.text = "${shown.size} / ${all.size}"
  }
  private fun play(c: Channel) {
    idx = shown.indexOf(c); pm.play(c.url)
    b.info.text = "▶ ${c.name}"
  }
  private fun toggleFav(c: Channel) {
    val s = favs.toMutableSet(); if(!s.add(c.url)) s.remove(c.url)
    PreferenceManager.getDefaultSharedPreferences(this).edit().putStringSet("fav",s).apply()
  }
  override fun onKeyDown(k: Int, e: KeyEvent?): Boolean {
    // P+ / P- ve kanal tuşları ile zap — web'den farkı: anında, buffer beklemez
    if(k==KeyEvent.KEYCODE_CHANNEL_UP||k==KeyEvent.KEYCODE_PAGE_UP){ zap(1); return true }
    if(k==KeyEvent.KEYCODE_CHANNEL_DOWN||k==KeyEvent.KEYCODE_PAGE_DOWN){ zap(-1); return true }
    return super.onKeyDown(k,e)
  }
  private fun zap(d: Int){ if(shown.isEmpty())return; idx=((idx+d)+shown.size)%shown.size; play(shown[idx]) }
  private fun showSettings(){ SettingsDialog().show(supportFragmentManager,"s"); }
  override fun onDestroy(){ pm.release(); super.onDestroy() }
}
