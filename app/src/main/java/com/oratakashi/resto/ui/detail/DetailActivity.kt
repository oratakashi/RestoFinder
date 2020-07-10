package com.oratakashi.resto.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.snackbar.Snackbar
import com.oratakashi.resto.BuildConfig
import com.oratakashi.resto.R
import com.oratakashi.resto.core.Config
import com.oratakashi.resto.core.View
import com.oratakashi.resto.data.model.main.DataRestaurant
import com.oratakashi.resto.data.model.review.DataReviews
import com.oratakashi.resto.ui.detail.DetailState.*
import com.oratakashi.resto.utils.Converter
import com.oratakashi.resto.utils.ImageHelper
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*
import kotlinx.android.synthetic.main.loading_review.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject
import android.view.View as system

class DetailActivity : View(), ImageHelper.Return {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val listHighlight: MutableList<String> = ArrayList()
    private val listReviews: MutableList<DataReviews> = ArrayList()

    private val viewModel: DetailViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(DetailViewModel::class.java)
    }

    private val data: DataRestaurant by lazy {
        intent.getParcelableExtra<DataRestaurant>("data")
    }

    private val adapterHighlight: HighlightAdapter by lazy {
        HighlightAdapter(listHighlight)
    }
    private val adapterReviews: ReviewsAdapter by lazy {
        ReviewsAdapter(listReviews)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)
        super.title(supportActionBar!!, "")

        shImage.startShimmerAnimation()

        /**
         * Checking Build SDK if Bellow Android M, will do something
         */

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            setListener()
        } else {
            supportActionBar?.setHomeAsUpIndicator(
                ContextCompat.getDrawable(this, R.drawable.ic_arrow_back)
            )
            super.title(supportActionBar!!, data.name!!)
        }

        if (data.photos_url != null && data.photos_url!!.isNotEmpty()) {
            ImageHelper.getPicasso(ivImage, data.photos_url, this)
        } else {
            ImageHelper.getPicasso(ivImage, Config.DummyImage, this)
        }


        rbRatting.rating = data.rating?.rating!!.toFloat()

        tvRating.text = data.rating?.rating
        tvName.text = data.name
        tvCategory.text = data.cuisines
        tvAddress.text = data.location?.address
        tvOpenHour.text = data.timings

        tvCount.text = String.format(
            resources.getString(R.string.placeholder_review_count),
            Converter.numberFormat(data.rating?.votes!!)
        )
        tvPrice.text = String.format(
            resources.getString(R.string.placeholder_restaurant),
            data.currency,
            Converter.numberFormat(data.price!!),
            data.price_range
        )
        tvSubLocation.text = String.format(
            resources.getString(R.string.placeholder_address),
            data.location?.locality,
            data.location?.city
        )

        rvHighlight.also {
            it.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            it.adapter = adapterHighlight
        }
        rvReviews.also {
            it.layoutManager = LinearLayoutManager(this)
            it.adapter = adapterReviews
        }

        if (data.highlights.isNullOrEmpty()) {
            rvHighlight.visibility = system.GONE
        } else {
            listHighlight.addAll(data.highlights!!)
            adapterHighlight.notifyDataSetChanged()
        }

        viewModel.state.observe(this, Observer { state ->
            state?.let {
                when (it) {
                    is Loading -> {
                        shReview.visibility = system.VISIBLE
                        shReview.startShimmerAnimation()
                        rvReviews.visibility = system.GONE
                    }
                    is Result -> {
                        shReview.stopShimmerAnimation()
                        shReview.visibility = system.GONE
                        rvReviews.visibility = system.VISIBLE

                        listReviews.clear()
                        listReviews.addAll(it.data.reviews!!)
                        adapterReviews.notifyDataSetChanged()
                    }
                    is Error -> {
                        shReview.stopShimmerAnimation()
                        shReview.visibility = system.GONE
                        rvReviews.visibility = system.VISIBLE

                        val httpCode = it.error as HttpException

                        if (httpCode.code() != 400) {
                            if (BuildConfig.DEBUG) showMessage(it.error.message())

                            Snackbar.make(
                                clBase,
                                resources.getString(R.string.title_failed_load_data),
                                Snackbar.LENGTH_INDEFINITE
                            ).setAction(resources.getString(R.string.title_tryagain)) {
                                getData()
                            }.show()
                        }
                    }
                }
            }
        })

        fab.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("google.navigation:q=${data.location?.lat},${data.location?.long}")
            )
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                showMessage(resources.getString(R.string.title_failed_maps))
            }
        }

        getData()
    }

    private fun getData() {
        GlobalScope.launch(Dispatchers.Default) {
            viewModel.getData(data.id!!)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setListener() {
        app_bar.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            var isShow = true
            var scrollRange = -1
            override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout!!.totalScrollRange
                }
                if (scrollRange + verticalOffset == 0) {
                    ctlDetail.title = data.name
                    fab.hide()
                    supportActionBar?.setHomeAsUpIndicator(
                        ContextCompat.getDrawable(applicationContext, R.drawable.ic_arrow_back)
                    )
                    isShow = true
                } else if (isShow) {
                    ctlDetail.title = " "
                    fab.show()
                    supportActionBar?.setHomeAsUpIndicator(
                        ContextCompat.getDrawable(applicationContext, R.drawable.ic_back_overide)
                    )
                    isShow = false
                }
            }
        })
    }

    override fun onImageLoaded(imageView: ImageView?, shimmerFrameLayout: ShimmerFrameLayout?) {
        shImage.stopShimmerAnimation()
        shImage.visibility = system.GONE
    }

    override fun onImageFailed(error: String, shimmerFrameLayout: ShimmerFrameLayout?) {
        shImage.stopShimmerAnimation()
        shImage.visibility = system.GONE
        showMessage(error)
    }

    override fun darkStyle(): Int {
        return R.style.AppThemeCustomDark
    }

    override fun lightStyle(): Int {
        return R.style.AppThemeCustom
    }
}