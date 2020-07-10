package com.oratakashi.resto.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.oratakashi.resto.R
import com.oratakashi.resto.core.Config
import com.oratakashi.resto.core.interfaces.MainInterface
import com.oratakashi.resto.data.model.collection.DataCollections
import com.oratakashi.resto.utils.ImageHelper
import kotlinx.android.synthetic.main.adapter_collection.view.*

class CollectionAdapter(
    private val data: List<DataCollections>,
    private val parent: MainInterface
) :
    RecyclerView.Adapter<CollectionAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.also {

            if (
                data[position].detail?.image_url != null &&
                data[position].detail?.image_url!!.isNotEmpty()
            ) {
                ImageHelper.getPicasso(it.ivImage, data[position].detail?.image_url)
            } else {
                ImageHelper.getPicasso(it.ivImage, Config.DummyImage)
            }

            it.tvName.text = data[position].detail?.title
            it.tvCount.text = String.format(
                it.context.resources.getString(R.string.placeholder_collection),
                data[position].detail?.res_count.toString()
            )

            it.setOnClickListener {
                parent.openCollection(data[position].detail)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.adapter_collection,
            parent, false
        )
    )

    override fun getItemCount(): Int = data.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}