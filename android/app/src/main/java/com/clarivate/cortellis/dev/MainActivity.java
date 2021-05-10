package com.clarivate.cortellis.dev;

import android.app.Activity;
import android.content.IntentSender;
import android.os.Bundle;
import android.view.View;


import com.facebook.react.ReactActivity;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.android.play.core.tasks.Task;


public class MainActivity extends ReactActivity implements OnSuccessListener<AppUpdateInfo> {
  private AppUpdateManager appUpdateManager;
  public static final int REQUEST_CODE = 1234;

  /**
   * Returns the name of the main component registered from JavaScript.
   * This is used to schedule rendering of the component.
   */
  @Override
  protected String getMainComponentName() {
    return "PushNotifications";
  }


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    appUpdateManager = AppUpdateManagerFactory.create(this.getBaseContext());

    Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

// Checks that the platform will allow the specified type of update.
    appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
      if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
              // For a flexible update, use AppUpdateType.FLEXIBLE
              && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {

        try {
          appUpdateManager.startUpdateFlowForResult(
                  // Pass the intent that is returned by 'getAppUpdateInfo()'.
                  appUpdateInfo,
                  // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                  AppUpdateType.IMMEDIATE,
                  // The current activity making the update request.
                  this,
                  // Include a request code to later monitor this update request.
                  REQUEST_CODE);
        } catch (IntentSender.SendIntentException e) {
          e.printStackTrace();
        }

      }
    });


  }

  @Override
  protected void onResume() {
    super.onResume();
    appUpdateManager.getAppUpdateInfo().addOnSuccessListener(this);
  }

  @Override
  public void onSuccess(AppUpdateInfo appUpdateInfo) {
    if (appUpdateInfo.updateAvailability()
            == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
      // If an in-app update is already running, resume the update.
      startUpdate(appUpdateInfo, AppUpdateType.IMMEDIATE);
    } else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
      // If the update is downloaded but not installed,
      // notify the user to complete the update.
    } else if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
      if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
        startUpdate(appUpdateInfo, AppUpdateType.IMMEDIATE);
      } else if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {

      }
    }
  }

  private void startUpdate(final AppUpdateInfo appUpdateInfo, final int appUpdateType) {
    final Activity activity = this;
    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          appUpdateManager.startUpdateFlowForResult(appUpdateInfo,
                  appUpdateType,
                  activity,
                  REQUEST_CODE);
        } catch (IntentSender.SendIntentException e) {
          e.printStackTrace();
        }
      }
    }).start();
  }
}