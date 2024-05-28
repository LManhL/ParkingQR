package com.example.parkingqr.ui.components.securitycamera

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.rtsp.RtspMediaSource
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.parkingqr.R
import com.example.parkingqr.domain.model.parkinglot.SecurityCamera
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CameraAdapter(
    private val items: MutableList<SecurityCamera>,
    private val onClickDel: (SecurityCamera) -> Unit
) :
    Adapter<CameraAdapter.CameraViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CameraViewHolder {
        return LayoutInflater.from(parent.context)
            .inflate(R.layout.item_security_camera_list, parent, false).let {
                CameraViewHolder(it)
            }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: CameraViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun addAll(data: List<SecurityCamera>) {
        items.clear()
        items.addAll(data.toMutableList())
        notifyDataSetChanged()
    }

    fun remove(securityCamera: SecurityCamera) {
        items.remove(securityCamera)
        notifyDataSetChanged()
    }

    @OptIn(UnstableApi::class)
    inner class CameraViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val url = itemView.findViewById<TextView>(R.id.tvUrlSecurityCamera)
        private val del = itemView.findViewById<ImageView>(R.id.ivDelSecurityCamera)
        private val video = itemView.findViewById<PlayerView>(R.id.plvItemSecurityCamera)
        private var exoPlayer: ExoPlayer = ExoPlayer.Builder(itemView.context).build()

        fun bind(securityCamera: SecurityCamera) {
            url.text = securityCamera.uri
            video.player = exoPlayer

            try {
                CoroutineScope(Dispatchers.Main).launch {
                    if (securityCamera.uri.contains("rtsp")) {
                        val mediaSource =
                            RtspMediaSource.Factory()
                                .createMediaSource(MediaItem.fromUri(securityCamera.uri))
                        exoPlayer.setMediaSource(mediaSource)
                    } else {
                        exoPlayer.setMediaItem(MediaItem.fromUri(securityCamera.uri))
                    }
                    exoPlayer.prepare()
                    exoPlayer.play()
                }
            } catch (e: Exception) {
                Log.e("TAG", "Error : $e");
            }

            del.setOnClickListener {
                onClickDel.invoke(securityCamera)
            }
        }
    }

}