package com.oratakashi.resto.utils

import android.util.Log
import android.widget.ImageView
import com.facebook.shimmer.ShimmerFrameLayout
import com.oratakashi.resto.R
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

/**
 * This helper for loading images from URL, and on this helper can be return callback too
 *
 * Helper ini digunakan untuk mengambil gambar dari URL, dan
 * Helper ini juga di lengkapi callback untuk response jika terdapat
 * Gagal load ataupun ketika gambar berhasil terload
 */

object ImageHelper {
    fun getPicasso(
        imageView: ImageView,
        image_url: String?,
        callback: Return? = null,
        loading: ShimmerFrameLayout? = null
    ) {
        Picasso.get().load(image_url)
            .placeholder(R.drawable.img_no_images)
            .error(R.drawable.img_no_images)
            .into(imageView, object : Callback {
                override fun onSuccess() {
                    callback?.onImageLoaded(imageView, loading)
                }

                override fun onError(e: Exception) {
                    Log.e("Picasso", e.message!!)
                    Log.e("Picasso_URl", image_url)
                    callback?.onImageFailed(e.message!!)
                }
            })
    }


    interface Return {
        fun onImageLoaded(
            imageView: ImageView? = null,
            shimmerFrameLayout: ShimmerFrameLayout? = null
        )

        fun onImageFailed(
            error: String,
            shimmerFrameLayout: ShimmerFrameLayout? = null
        )
    }
}