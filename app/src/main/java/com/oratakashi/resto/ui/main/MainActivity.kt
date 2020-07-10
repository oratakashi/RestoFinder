package com.oratakashi.resto.ui.main

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.oratakashi.resto.BuildConfig
import com.oratakashi.resto.R
import com.oratakashi.resto.core.View
import com.oratakashi.resto.core.theme.ThemeList
import com.oratakashi.resto.core.theme.ThemeService
import com.oratakashi.resto.data.model.collection.DataCollections
import com.oratakashi.resto.data.model.main.DataMain
import com.oratakashi.resto.ui.main.MainState.*
import com.oratakashi.resto.ui.search.SearchActivity
import com.oratakashi.resto.ui.settings.SettingsActivity
import com.oratakashi.resto.utils.Greetings
import com.oratakashi.resto.utils.LocationServices
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.loading_collection.*
import kotlinx.android.synthetic.main.loading_restaurant.*
import kotlinx.coroutines.*
import retrofit2.HttpException
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import android.view.View as system
import com.oratakashi.resto.ui.main.CollectionState.Error as error
import com.oratakashi.resto.ui.main.CollectionState.Loading as loading
import com.oratakashi.resto.ui.main.CollectionState.Result as result


class MainActivity : View() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val data: MutableList<DataMain> = ArrayList()
    private val collection: MutableList<DataCollections> = ArrayList()

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
    }

    private val service: ThemeService by lazy {
        ViewModelProviders.of(this).get(ThemeService::class.java)
    }

    private val adapter: MainAdapter by lazy {
        MainAdapter(data, this)
    }

    private val adapterCollections: CollectionAdapter by lazy {
        CollectionAdapter(collection, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainActivity = this

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            setListener()
        }

        /**
         * Getting Current Location Asycnrounusly
         *
         * Mendapatkan Lokasi terbaru dengan Asycnrounusly
         */

        launch {

            /**
             * UI will show a Loading
             *
             * UI akan menampilkan Loading dan akan berhenti jika semua proses telah selesai
             */

            shCollection.startShimmerAnimation()
            shResto.startShimmerAnimation()

            /**
             * Call Method getLocation
             */

            getLocation()

        }

        /**
         * Binding Adapter into RecycleView and configure layout manager
         */
        rvResto.also {
            it.adapter = adapter
            it.layoutManager = StaggeredGridLayoutManager(
                2,
                StaggeredGridLayoutManager.VERTICAL
            )
        }

        rvCollection.also {
            it.adapter = adapterCollections
            it.layoutManager = LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL, false
            )
        }

        /**
         * Configure State management from Api
         * UI must observe all LiveData on ViewModel
         *
         * Mengkonfigurasi State management dari API
         * UI / Activity wajib mengobserve semua LiveData yang ada di ViewModel
         */

        viewModel.state.observe(this, Observer { state ->
            state?.let {
                when (it) {
                    is Loading -> {
                        shResto.visibility = system.VISIBLE
                        shResto.startShimmerAnimation()
                        rvResto.visibility = system.GONE
                    }
                    is Result -> {
                        shResto.visibility = system.GONE
                        shResto.stopShimmerAnimation()
                        rvResto.visibility = system.VISIBLE

                        data.clear()
                        data.addAll(it.data.data!!)
                        adapter.notifyDataSetChanged()
                    }
                    is Error -> {
                        shResto.visibility = system.GONE
                        shResto.stopShimmerAnimation()
                        rvResto.visibility = system.VISIBLE

                        if (BuildConfig.DEBUG) showMessage(it.error.message)


                        Snackbar.make(
                            clBase, resources.getString(R.string.title_failed_load_data),
                            Snackbar.LENGTH_INDEFINITE
                        )
                            .setAction(resources.getString(R.string.title_tryagain)) {
                                getData()
                            }.show()
                    }
                }
            }
        })
        viewModel.collection.observe(this, Observer { state ->
            state?.let {
                when (it) {
                    is loading -> {
                        shCollection.visibility = system.VISIBLE
                        shCollection.startShimmerAnimation()
                        rvCollection.visibility = system.GONE
                        tvCollectionEmpty.visibility = system.GONE
                    }
                    is result -> {
                        shCollection.startShimmerAnimation()
                        shCollection.visibility = system.GONE
                        rvCollection.visibility = system.VISIBLE

                        if (it.data.data.isNullOrEmpty()) {
                            tvCollectionEmpty.visibility = system.VISIBLE
                            rvCollection.visibility = system.GONE
                        } else {
                            collection.clear()
                            collection.addAll(it.data.data)
                            adapterCollections.notifyDataSetChanged()
                        }
                    }
                    is error -> {
                        shCollection.startShimmerAnimation()
                        shCollection.visibility = system.GONE
                        tvCollectionEmpty.visibility = system.VISIBLE
                        rvCollection.visibility = system.GONE

                        val httpCode = it.error as HttpException

                        if (httpCode.code() != 400) {
                            if (BuildConfig.DEBUG) showMessage(it.error.message)

                            Snackbar.make(
                                clBase, resources.getString(R.string.title_failed_load_data),
                                Snackbar.LENGTH_INDEFINITE
                            )
                                .setAction(resources.getString(R.string.title_tryagain)) {
                                    getData()
                                }.show()
                        }
                    }
                }
            }
        })

        service.currentTheme.observe(this, Observer { theme ->
            theme?.let {
                when (it) {
                    is ThemeList.System -> {
                        finish()
                    }
                    is ThemeList.Light -> {
                        finish()
                    }
                    is ThemeList.Dark -> {
                        finish()
                    }
                }
            }
        })

        etSearch.hint = String.format(
            resources.getString(R.string.placeholder_search_hint_welcome),
            Greetings.getGreetings(this),
            Greetings.getTimes(this).toLowerCase(Locale.getDefault())
        )
        etSearch.setOnClickListener {
            startActivity(Intent(applicationContext, SearchActivity::class.java))
        }
        tvLocation.setOnClickListener {
            launch { getLocation() }
        }
        ivSettings.setOnClickListener {
            startActivity(Intent(applicationContext, SettingsActivity::class.java))
        }

        getData()
    }

    private fun getData() {
        /**
         * Create new Thread for Main Content
         */

        GlobalScope.launch(Dispatchers.Default) {
            if (LocationServices.getLat(applicationContext).verify()) {
                viewModel.getNearby(
                    LocationServices.getLat(applicationContext),
                    LocationServices.getLang(applicationContext)
                )
                viewModel.getCollection(
                    LocationServices.getLat(applicationContext),
                    LocationServices.getLang(applicationContext)
                )
            } else {
                delay(1000)
                getData()
            }
        }
    }

    private suspend fun getLocation() {

        /**
         * Give time apps to get Real Coordinate from GPS, if failed geting location,
         * apps will get last known lat and long
         *
         * Memberi jeda waktu untuk aplikasi mendapatkan titik koordinat
         * yang akurat dari GPS, dan jika gagal mendapatkan lokasi terbaru melalui GPS
         * Aplikasi akan mengambil titik gps terakhir di perangkat
         */

        if (!LocationServices.getLat(applicationContext).verify()) {
            if (LocationServices.retry > 3) {
                LocationServices.getLastLocation(applicationContext)
                delay(1000)
                Toast.makeText(
                    applicationContext,
                    resources.getString(R.string.title_failed_get_location), Toast.LENGTH_SHORT
                ).show()
                getLocation()
            } else {
                delay(3000)
                getLocation()
            }
        } else {
            /**
             * Result Address store on Variables
             *
             * Hasil Alamat akan di simpan ke variabel dalam bentuk object
             */

            @Suppress("BlockingMethodInNonBlockingContext")
            val address = LocationServices.getLocationAddress(
                LocationServices.getLat(this@MainActivity).toDouble(),
                LocationServices.getLang(this@MainActivity).toDouble()
            )

            /**
             * Back to Main Thread, and Change UI
             *
             * Kembalikan ke Thread utama ketika proses sudah selesai
             * dan melakukan perubahan pada UI
             */

            withContext(Dispatchers.Main) {
                val fullAddress = address?.getAddressLine(0)?.split(",")
                debug(address?.getAddressLine(0))
                tvLocation2.text = address?.subAdminArea
                tvLocation.text = String.format(
                    resources.getString(
                        R.string.placeholder_address,
                        fullAddress!![0],
                        address.subAdminArea
                    )
                )
                if (tvLocation.text.toString().length > 25) {
                    tvLocation.text = String.format(
                        resources.getString(R.string.placeholder_address_substring),
                        fullAddress[0].substring(0, 15),
                        address.subAdminArea
                    )
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setListener() {
        nsMain.setOnScrollChangeListener { _, _, scrollY: Int, _, _ ->
            if (
                scrollY == 0 &&
                textView.isGone &&
                tvLocation.isGone
            ) {
                textView.animate()
                    .translationY(1f)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationStart(animation: Animator?) {
                            super.onAnimationStart(animation)
                            textView.visibility = system.VISIBLE
                        }
                    })
                tvLocation.animate()
                    .translationY(1f)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationStart(animation: Animator?) {
                            super.onAnimationStart(animation)
                            tvLocation.visibility = system.VISIBLE
                        }
                    })
                ivSettings.animate()
                    .translationY(1f)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationStart(animation: Animator?) {
                            super.onAnimationStart(animation)
                            ivSettings.visibility = system.VISIBLE
                        }
                    })
            } else if (
                scrollY > 500 &&
                textView.isVisible &&
                tvLocation.isVisible
            ) {
                textView.animate()
                    .translationY(0f)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            super.onAnimationEnd(animation)
                            textView.visibility = system.GONE
                        }
                    })
                tvLocation.animate()
                    .translationY(0f)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            super.onAnimationEnd(animation)
                            tvLocation.visibility = system.GONE
                        }
                    })
                ivSettings.animate()
                    .translationY(0f)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationStart(animation: Animator?) {
                            super.onAnimationStart(animation)
                            ivSettings.visibility = system.GONE
                        }
                    })
            }
        }
    }

    override fun darkStyle(): Int {
        return R.style.AppThemeDark_NoActionBar
    }

    override fun lightStyle(): Int {
        return R.style.AppTheme_NoActionBar
    }

    companion object {
        var mainActivity: MainActivity? = null
        fun getInstance(): MainActivity = mainActivity!!
    }
}