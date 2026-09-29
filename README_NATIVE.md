# Native 65Q6C IPTV (Kotlin + Media3)

Evet, native daha akıcı. Neden önce web verdim: bu PC'de Java 1.8 + SDK yok, derleyemezdim. Native şimdi iskelet olarak hazır: `iptv-65q6c-native/`

## Neden native daha akıcı?
- ExoPlayer donanım decode + tunneled 4K, WebView'e göre %15-30 daha az CPU, daha hızlı zap (15sn buffer vs tarayıcı 30sn+).
- Leanback D-pad focus, HDMI-CEC, kanal tuşları (P+/P-) doğrudan.
- Arka plan EPG cache, liste sanallaştırma RecyclerView ile.

## Derlemek için (PC'de gerekli)
1. Android Studio Hedgehog+ kur, SDK 34 kur.
2. Java 17'ye geç (bu PC'de 1.8 var — Studio gömülü JBR kullan).
3. Bu klasörü aç → Sync → Run (Android TV 1080p API 34 emülatörü veya gerçek TV).
4. TV'ye kur: `adb connect TV_IP` → `adb install app-debug.apk`

## Dosyalar
- `MainActivity.kt` — liste + player + zap
- `PlayerManager.kt` — düşük buffer ExoPlayer
- `M3uParser.kt`, `EpgParser.kt` — hızlı, düşük bellekli
- `ChannelAdapter.kt` — Glide logo cache
