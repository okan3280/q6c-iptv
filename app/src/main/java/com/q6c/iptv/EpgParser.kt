package com.q6c.iptv
import android.util.Xml
// XMLTV EPG — büyük dosyada bile düşük bellek (XmlPullParser)
object EpgParser {
  fun parse(xml: String): Map<String, List<EpgItem>> {
    val map = HashMap<String, MutableList<EpgItem>>()
    val p = Xml.newPullParser(); p.setInput(xml.reader())
    var ev = p.eventType; var ch=""; var start=0L; var stop=0L; var title=""; var desc=""; var inProg=false
    fun ts(s: String): Long = try {
      // "20260101180000 +0300" -> epoch
      val y=s.substring(0,4).toInt(); val mo=s.substring(4,6).toInt(); val d=s.substring(6,8).toInt()
      val h=s.substring(8,10).toInt(); val mi=s.substring(10,12).toInt()
      val c = java.util.Calendar.getInstance(); c.set(y,mo-1,d,h,mi,0); c.timeInMillis
    } catch(_: Exception){ 0L }
    while (ev != org.xmlpull.v1.XmlPullParser.END_DOCUMENT) {
      when(ev){
        org.xmlpull.v1.XmlPullParser.START_TAG -> when(p.name){
          "programme" -> { inProg=true; ch=p.getAttributeValue(null,"channel")?:""; start=ts(p.getAttributeValue(null,"start")?:""); stop=ts(p.getAttributeValue(null,"stop")?:""); title=""; desc="" }
          "title" -> if(inProg) title = readText(p)
          "desc" -> if(inProg) desc = readText(p)
        }
        org.xmlpull.v1.XmlPullParser.END_TAG -> if(p.name=="programme" && inProg){
          if(ch.isNotEmpty()) map.getOrPut(ch){ArrayList()}.add(EpgItem(start,stop,title,desc)); inProg=false
        }
      }
      ev = p.next()
    }
    return map
  }
  private fun readText(p: org.xmlpull.v1.XmlPullParser): String {
    return try { p.next(); if(p.eventType==org.xmlpull.v1.XmlPullParser.TEXT) p.text ?: "" else "" } catch(_: Exception){ "" }
  }
}
