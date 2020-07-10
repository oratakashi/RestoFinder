package com.oratakashi.resto.core

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.oratakashi.resto.core.interfaces.MainInterface
import com.oratakashi.resto.core.theme.ThemeManager
import com.oratakashi.resto.data.model.collection.DataCollection
import com.oratakashi.resto.data.model.main.DataRestaurant
import com.oratakashi.resto.ui.collection.CollectionActivity
import com.oratakashi.resto.ui.detail.DetailActivity
import com.oratakashi.resto.utils.LocationServices
import com.oratakashi.resto.utils.PermissionManager
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

/**
 * Class Abstract for parent all Activities
 * in this class, is wrapped with Permission manager to get current location
 *
 * Ini Merupakan Kelas abstract yang akan di extends oleh semua activity
 * di kelas ini, sudah terbundle oleh Permission Manager untuk mendapatkan Location saat ini
 * dan semua activity yang mengextend class ini otomatis akan terinject Retrofit
 * dan Retrofit Interface di ViewModel mereka
 */
abstract class View : DaggerAppCompatActivity(),
    MainInterface,
    MultiplePermissionsListener,
    CoroutineScope {

    /**
     * All Activity can access Theme Manager class
     *
     * Semua activity yang mengextends class ini akan bisa mengakses theme manager
     *
     */
    protected val themeManager: ThemeManager by lazy {
        ThemeManager(App.sessions, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /**
         * Check if permission grant or denied
         *
         * Memeriksa apakah permission sudah di ijinkan atau ditolak
         * dan jika sudah di ijinkan, maka location service akan di jalankan
         */

        if (PermissionManager.cekPermission(this, this)) {
            LocationServices.callServices(this, this)
        } else {
            PermissionManager.requestPermission(this, this)
        }

        /**
         * Configure Themes, Apps will automatic detect if System use Dark Mode or Not
         *
         * Mengkonfigurasi Theme, Apps akan otomatis mendeteksi jika sistem menggunakan Dark Mode
         * atau tidak, dan otomatis akan merubah theme apps sesuai konfigurasi sistem
         */

        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> {
                setTheme(darkStyle())

                /**
                 * Configure Statusbar into Transparent
                 */

                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = Color.TRANSPARENT
                window.decorView.systemUiVisibility = android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            }
            Configuration.UI_MODE_NIGHT_NO -> setTheme(lightStyle())
        }
    }

    /**
     * Default Activity Result for Handle Permission
     * @param requestCode Int : code 9000 is default Location Manager
     * @param resultCode Int
     * @param data Intent?
     */

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 9000) {
            PermissionManager.requestPermission(this, this)
        }
    }

    /**
     * If Permission Denied
     * @param permissions MutableList<PermissionRequest>
     * @param token PermissionToken
     */

    override fun onPermissionRationaleShouldBeShown(
        permissions: MutableList<PermissionRequest>?,
        token: PermissionToken?
    ) {
        token!!.continuePermissionRequest()
    }

    /**
     * If Permission Accepted
     * @param report MultiplePermissionsReport
     */

    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
        if (report!!.areAllPermissionsGranted()) {
            LocationServices.callServices(this, this)
        } else if (report.isAnyPermissionPermanentlyDenied) {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri: Uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivityForResult(intent, 9000)
        }
    }

    /**
     * Event click from adapter
     * @param data DataRestaurant
     */

    override fun openDetail(data: DataRestaurant?) {
        val intent = Intent(applicationContext, DetailActivity::class.java)
        intent.putExtra("data", data)
        startActivity(intent)
    }

    override fun openCollection(data: DataCollection?) {
        val intent = Intent(applicationContext, CollectionActivity::class.java)
        intent.putExtra("data", data)
        startActivity(intent)
    }

    /**
     * For Verify Coordinate if lat / lang 0.0000 will return false value
     * @receiver String
     * @return Boolean
     */
    fun String.verify(): Boolean {
        return when (this) {
            "0.0000" -> false
            else -> true
        }
    }

    /**
     * Prepare for Coroutine
     */

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    /**
     * Default Function back button, If Activities not overide this,
     * System will detect this method
     * @return Boolean
     */

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    /**
     * Listening if system theme changed, and apps will automatic change too
     *
     * Mengamati jika terdapat perubahan pada system, maka aplikasi akan otomatis menyesuaikan
     * sesuai perubahan pada sistem
     * @param newConfig Configuration
     */

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        when (newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> {
                setTheme(lightStyle())
            } // Night mode is not active, we're using the light theme
            Configuration.UI_MODE_NIGHT_YES -> {
                setTheme(darkStyle())
            } // Night mode is active, we're using dark theme
        }
    }

    /**
     * Some Tools :v
     * @param msg String?
     */

    protected fun showMessage(msg: String?) {
        Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
    }

    protected fun title(actionBar: ActionBar?, title: String) {
        actionBar?.title = title
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    protected fun debug(msg: String?) {
        Log.e("ViewDebug", msg)
    }

    /**
     * A Abstract function must be override in Activity for configure themes
     *
     * Sebuah abstract method yang harus di override di semua activity untuk perubahan theme
     * secara otomatis
     * @return Int
     */

    abstract fun darkStyle(): Int
    abstract fun lightStyle(): Int
}