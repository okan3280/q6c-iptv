package com.q6c.iptv
data class Channel(val name: String, val url: String, val logo: String = "", val group: String = "Genel", val tvgId: String = name)
data class EpgItem(val start: Long, val stop: Long, val title: String, val desc: String = "")
