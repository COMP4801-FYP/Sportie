package hk.hkucs.sportieapplication.utils

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import hk.hkucs.sportieapplication.R
import java.io.IOException

class SGlideLoader(val context: Context) {
    fun loadUserPicture (imageURI: Uri, imageView: ImageView) {
        try {
            Glide
                .with(context)
                .load(imageURI)
                .centerCrop ()
                .placeholder(R.drawable.ic_user_placeholder)
                .into(imageView)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}