package com.oratakashi.resto.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.oratakashi.resto.R
import com.oratakashi.resto.core.Config
import com.oratakashi.resto.core.interfaces.MainInterface
import com.oratakashi.resto.data.model.main.DataMain
import com.oratakashi.resto.utils.ImageHelper
import kotlinx.android.synthetic.main.adapter_search.view.*

class SearchAdapter(
    private val data: List<DataMain>,
    private val parent: MainInterface
) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.also {
            if (
                data[position].detail?.photos_url != null &&
                data[position].detail?.photos_url!!.isNotEmpty()
            ) {
                ImageHelper.getPicasso(it.ivImage, data[position].detail?.photos_url)
            } else {
                ImageHelper.getPicasso(it.ivImage, Config.DummyImage)
            }

            it.tvName.text = data[position].detail?.name
            it.tvCategory.text = data[position].detail?.cuisines
            it.tvRatting.text = data[position].detail?.rating?.rating
            it.tvLocation.text = String.format(
                it.context.getString(R.string.placeholder_address),
                data[position].detail?.location?.locality,
                data[position].detail?.location?.city
            )

            it.rbRatting.rating = data[position].detail?.rating?.rating!!.toFloat()

            it.setOnClickListener {
                if (data[position].detail != null) {
                    parent.openDetail(data[position].detail)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.adapter_search,
            parent, false
        )
    )

    override fun getItemCount(): Int = data.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}