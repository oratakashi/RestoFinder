package com.oratakashi.resto.utils

import android.Manifest
import android.app.Activity
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

object PermissionManager {
    fun requestPermission(activity: Activity, listener: MultiplePermissionsListener) {

        val perms = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        val permission: MutableList<String> = ArrayList()

        perms.forEach {
            permission.add(it)
        }

        Dexter.withActivity(activity)
            .withPermissions(permission)
            .withListener(listener)
            .onSameThread()
            .check()
    }

    fun cekPermission(activity: Activity, listener: MultiplePermissionsListener): Boolean {

        var returnType = false

        val perms = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        val permission: MutableList<String> = ArrayList()

        perms.forEach {
            permission.add(it)
        }

        Dexter.withActivity(activity)
            .withPermissions(permission)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report!!.areAllPermissionsGranted()) {
                        listener.onPermissionsChecked(report)
                        returnType = true
                    }
                    if (report.isAnyPermissionPermanentlyDenied) {
                        returnType = false
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token!!.continuePermissionRequest()
                }
            })
            .onSameThread()
            .check()

        return returnType
    }
}