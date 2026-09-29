package com.q6c.iptv
import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import okhttp3.OkHttpClient
// Kasma önleme: küçük buffer + worker + donanım decode, tek instance
class PlayerManager(ctx: Context) {
  private val ok = OkHttpClient.Builder().build()
  val player: ExoPlayer = ExoPlayer.Builder(ctx)
    .setLoadControl(DefaultLoadControl.Builder()
      .setBufferDurationsMs(15000, 30000, 1500, 3000).build()) // min/max/bufferForPlayback: hızlı zap
    .setMediaSourceFactory(DefaultMediaSourceFactory(ctx).setDataSourceFactory(OkHttpDataSource.Factory(ok)))
    .build().apply {
      // 4K tunneled / donanım öncelikli: videoScalingMode ve priority default donanım
      videoScalingMode = androidx.media3.common.C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
    }
  fun play(url: String) {
    player.setMediaItem(MediaItem.fromUri(url)); player.prepare(); player.playWhenReady = true
  }
  fun release() = player.release()
}
