package com.clarivate.cortellis.dev;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
//import android.support.annotation.RequiresApi;

import androidx.annotation.RequiresApi;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import java.io.File;

import javax.annotation.Nonnull;

public class WebViewCookieRemover extends ReactContextBaseJavaModule {

    public WebViewCookieRemover(ReactApplicationContext reactContext) {
        super(reactContext);
    }


    @Nonnull
    @Override
    public String getName() {
        return "CookieRemover";
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @ReactMethod
    public void removeCookies(Callback cb) {

        try{
            File dataDir = getReactApplicationContext().getDataDir(); // or see https://stackoverflow.com/a/19630415/4070848 for older Android versions
            File appWebViewDir = new File(dataDir.getPath() + "/app_webview/");
            deleteDir(appWebViewDir);
            cb.invoke(null,"Cookie clear Done");

        }catch (Exception e){
            cb.invoke(e.toString(), null);

        }
    }

    private void deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                deleteDir(new File(dir, children[i]));
            }
            String fileName = new File(dir.getPath()).getName();
            if (!fileName.equals("app_webview")) {
                dir.delete();
            }
        } else if (dir != null && dir.isFile()) {
            dir.delete();
        }
    }
}
