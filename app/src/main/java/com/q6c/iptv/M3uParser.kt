package com.q6c.iptv
// Hafif M3U parser — UI thread dışında çağır
object M3uParser {
  fun parse(text: String): List<Channel> {
    val out = ArrayList<Channel>(2000); var name=""; var logo=""; var group="Genel"; var id=""; var hasMeta=false
    text.lineSequence().forEach { raw ->
      val l = raw.trim(); if (l.isEmpty()) return@forEach
      if (l.startsWith("#EXTINF")) {
        name = l.substringAfterLast(",").trim()
        group = Regex("group-title=\"([^\"]*)\"").find(l)?.groupValues?.get(1) ?: "Genel"
        logo = Regex("tvg-logo=\"([^\"]*)\"").find(l)?.groupValues?.get(1) ?: ""
        id = Regex("tvg-id=\"([^\"]*)\"").find(l)?.groupValues?.get(1)?.ifEmpty { name } ?: name
        hasMeta = true
      } else if (!l.startsWith("#") && hasMeta) {
        out.add(Channel(name, l, logo, group, id)); hasMeta = false
      }
    }
    return out
  }
}
