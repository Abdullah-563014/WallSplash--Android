package com.wallsplash.bdapp.notification;

import android.app.Application;
import android.content.Context;

import com.onesignal.OneSignal;

/**
 * Created by admin on 2/10/2018.
 */

public class MyApplication extends Application {
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
        context = this;
    }
}
