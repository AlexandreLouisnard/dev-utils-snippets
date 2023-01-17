package com.portalp.technician.model.utils;

import static com.activeandroid.Cache.getContext;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.portalp.com_library.model.settings.SuperEnums;
import com.portalp.com_library.user.User;
import com.portalp.technician.PortalpApplication;
import com.portalp.technician.web_services.LicenceUpdateBroadcastReceiver;
import com.portalp.technician.web_services.LicenceUpdateWorker;
import com.portalp.technician.web_services.WebServicesHelper;


public class BootReceiver extends BroadcastReceiver implements WebServicesHelper.RefreshTokenListener, WebServicesHelper.GetLicencesListener {

    private static final String TAG = BootReceiver.class.getSimpleName();

    //region (dec) Fields
    private User user;
    private WebServicesHelper webServicesHelper;
    //endregion

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive(): " + intent.getAction());
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
//            JavaUtils.logToExtFile(context, "licence_update_background", TAG, "onReceive(): ACTION_BOOT_COMPLETED");
            Log.d(TAG, "onReceive(): ACTION_BOOT_COMPLETED");

            this.user = PortalpApplication.getUserFromDb(getContext());
            this.webServicesHelper = WebServicesHelper.getInstance(getContext());

            // Update licence right now
//            JavaUtils.logToExtFile(context, "licence_update_background", TAG, "onReceive(): starting refreshToken()");
            Log.d(TAG, "onReceive(): starting refreshToken()");
            webServicesHelper.refreshToken(user, this);

            // Schedule periodic licence updates
            LicenceUpdateWorker.scheduleWorker(context);
            LicenceUpdateBroadcastReceiver.scheduleBroadcast(context);
        }
    }

    //region (imp) WebServicesHelper settingListeners
    @Override
    public void onRefreshToken(WebServicesHelper.ResultCode resultCode, User user) {
        if (resultCode == WebServicesHelper.ResultCode.SUCCESS) {
//            JavaUtils.logToExtFile(getContext(), "licence_update_background", TAG, "onRefreshToken(): OK => starting getLicences()");
            Log.d(TAG, "onRefreshToken(): OK => starting getLicences()");
            webServicesHelper.getLicences(user, SuperEnums.PeriphTypeSEnum.INSTALLER_DEVICE, this);
        } else {
//            JavaUtils.logToExtFile(getContext(), "licence_update_background", TAG, "onRefreshToken(): failed: " + resultCode + "(" + resultCode.getServerResponseCode() + ")");
            Log.d(TAG, "onRefreshToken(): failed: " + resultCode + "(" + resultCode.getServerResponseCode() + ")");
        }
    }

    @Override
    public void onGetLicences(WebServicesHelper.ResultCode resultCode, User user) {
        if (resultCode == WebServicesHelper.ResultCode.SUCCESS) {
//            JavaUtils.logToExtFile(getContext(), "licence_update_background", TAG, "onGetLicences(): OK => sending pending door JSON configs");
            Log.d(TAG, "onGetLicences(): OK => sending pending door JSON configs");
            WebServicesHelper.postPendingDoorConfigs(user);
        } else {
//            JavaUtils.logToExtFile(getContext(), "licence_update_background", TAG, "onGetLicences(): failed: " + resultCode + "(" + resultCode.getServerResponseCode() + ")");
            Log.d(TAG, "onGetLicences(): failed: " + resultCode + "(" + resultCode.getServerResponseCode() + ")");
        }
    }
    //endregion
}
