package com.q6c.iptv
import android.app.Dialog; import android.os.Bundle; import android.widget.EditText
import androidx.appcompat.app.AlertDialog; import androidx.fragment.app.DialogFragment
import androidx.preference.PreferenceManager
class SettingsDialog: DialogFragment(){
  override fun onCreateDialog(s: Bundle?): Dialog {
    val v = layoutInflater.inflate(R.layout.dialog_settings,null)
    val m3u=v.findViewById<EditText>(R.id.inM3u); val epg=v.findViewById<EditText>(R.id.inEpg)
    val p=PreferenceManager.getDefaultSharedPreferences(requireContext())
    m3u.setText(p.getString("m3u","")); epg.setText(p.getString("epg",""))
    return AlertDialog.Builder(requireContext()).setTitle("M3U + EPG").setView(v)
      .setPositiveButton("Kaydet"){_,_-> p.edit().putString("m3u",m3u.text.toString()).putString("epg",epg.text.toString()).apply(); (activity as? MainActivity)?.let{ it.recreate() } }
      .setNegativeButton("Kapat",null).create()
  }
}
