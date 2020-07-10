package com.oratakashi.resto.ui.collection

import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.snackbar.Snackbar
import com.oratakashi.resto.BuildConfig
import com.oratakashi.resto.R
import com.oratakashi.resto.core.View
import com.oratakashi.resto.data.model.collection.DataCollection
import com.oratakashi.resto.data.model.main.DataMain
import com.oratakashi.resto.ui.collection.CollectionState.*
import com.oratakashi.resto.utils.ImageHelper
import kotlinx.android.synthetic.main.activity_collection.*
import kotlinx.android.synthetic.main.content_collection.*
import kotlinx.android.synthetic.main.loading_restaurant.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject
import android.view.View as system

class CollectionActivity : View(), ImageHelper.Return {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val listResto: MutableList<DataMain> = ArrayList()

    private val viewModel: CollectionViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(CollectionViewModel::class.java)
    }

    private val data: DataCollection by lazy {
        intent.getParcelableExtra<DataCollection>("data")
    }

    private val adapter: CollectionAdapter by lazy {
        CollectionAdapter(listResto, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collection)
        setSupportActionBar(toolbar)
        super.title(supportActionBar, data.title!!)

        shImage.startShimmerAnimation()

        /**
         * Checking Build SDK if Bellow Android M, will do something
         */

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setListener()
        } else {
            supportActionBar?.setHomeAsUpIndicator(
                ContextCompat.getDrawable(this, R.drawable.ic_arrow_back)
            )
            super.title(supportActionBar!!, data.title!!)
        }

        ImageHelper.getPicasso(ivImage, data.image_url, this)

        rvResto.also {
            it.layoutManager = StaggeredGridLayoutManager(
                2,
                StaggeredGridLayoutManager.VERTICAL
            )
            it.adapter = adapter
        }

        viewModel.state.observe(this, Observer { state ->
            state?.let {
                when (it) {
                    is Loading -> {
                        shResto.visibility = system.VISIBLE
                        shResto.startShimmerAnimation()
                        rvResto.visibility = system.GONE
                    }
                    is Result -> {
                        shResto.stopShimmerAnimation()
                        shResto.visibility = system.GONE
                        rvResto.visibility = system.VISIBLE

                        listResto.clear()
                        listResto.addAll(it.data.data!!)
                        adapter.notifyDataSetChanged()
                    }
                    is Error -> {
                        shResto.stopShimmerAnimation()
                        shResto.visibility = system.GONE
                        rvResto.visibility = system.VISIBLE

                        val httpCode = it.error as HttpException

                        if (httpCode.code() != 400) {
                            if (BuildConfig.DEBUG) showMessage(it.error.message)

                            Snackbar.make(
                                clBase,
                                resources.getString(R.string.title_failed_load_data),
                                Snackbar.LENGTH_INDEFINITE
                            ).setAction(
                                resources.getString(R.string.title_tryagain)
                            ) {
                                getData()
                            }.show()
                        }
                    }
                }
            }
        })

        getData()
    }

    private fun getData() {
        GlobalScope.launch(Dispatchers.Default) {
            viewModel.getData(data.id!!.toString())
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
                isShow = if (scrollRange + verticalOffset == 0) {
                    supportActionBar?.setHomeAsUpIndicator(
                        ContextCompat.getDrawable(applicationContext, R.drawable.ic_arrow_back)
                    )
                    true
                } else {
                    supportActionBar?.setHomeAsUpIndicator(
                        ContextCompat.getDrawable(applicationContext, R.drawable.ic_back_overide)
                    )
                    false
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