package com.oratakashi.resto.ui.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.oratakashi.resto.R
import com.oratakashi.resto.data.model.review.DataReviews
import com.oratakashi.resto.utils.ImageHelper
import kotlinx.android.synthetic.main.adapter_reviews.view.*

class ReviewsAdapter(val data: List<DataReviews>) :
    RecyclerView.Adapter<ReviewsAdapter.ViewHolder>(),
    ImageHelper.Return {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.also {

            it.shImage.startShimmerAnimation()

            ImageHelper.getPicasso(
                it.ivImage,
                data[position].detail?.user?.profile_image,
                this,
                it.shImage
            )

            it.tvName.text = data[position].detail?.user?.name
            it.tvTime.text = data[position].detail?.time
            it.tvRatting.text = data[position].detail?.rating.toString()
            it.tvComment.text = data[position].detail?.review_text

            it.rbRatting.rating = data[position].detail?.rating!!
        }
    }

    override fun onImageLoaded(imageView: ImageView?, shimmerFrameLayout: ShimmerFrameLayout?) {
        shimmerFrameLayout?.stopShimmerAnimation()
        shimmerFrameLayout?.visibility = View.GONE
    }

    override fun onImageFailed(error: String, shimmerFrameLayout: ShimmerFrameLayout?) {
        shimmerFrameLayout?.stopShimmerAnimation()
        shimmerFrameLayout?.visibility = View.GONE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.adapter_reviews,
            parent, false
        )
    )

    override fun getItemCount(): Int = data.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}