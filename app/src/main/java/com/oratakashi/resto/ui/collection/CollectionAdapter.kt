package com.oratakashi.resto.ui.collection

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.oratakashi.resto.R
import com.oratakashi.resto.core.Config
import com.oratakashi.resto.core.interfaces.MainInterface
import com.oratakashi.resto.data.model.main.DataMain
import com.oratakashi.resto.utils.Converter
import com.oratakashi.resto.utils.ImageHelper
import kotlinx.android.synthetic.main.adapter_resto.view.*

class CollectionAdapter(
    val data: List<DataMain>,
    val parent: MainInterface
) : RecyclerView.Adapter<CollectionAdapter.ViewHolder>() {

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

            it.tvName.text = when (data[position].detail?.name!!.length > 50) {
                true -> data[position].detail?.name!!.substring(0, 50)
                false -> data[position].detail?.name
            }
            it.tvRating.text = data[position].detail?.rating?.rating
            it.tvCategory.text = data[position].detail?.cuisines
            it.tvPrice.text = String.format(
                holder.itemView.context.getString(R.string.placeholder_restaurant),
                data[position].detail?.currency,
                Converter.numberFormat(data[position].detail?.price!!),
                data[position].detail?.price_range
            )

            it.rbRatting.rating = data[position].detail?.rating?.rating!!.toFloat()

            it.setOnClickListener {
                parent.openDetail(data[position].detail!!)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.adapter_resto,
            parent, false
        )
    )

    override fun getItemCount(): Int = data.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}