package id.net.gmedia.perkasaapp.Utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.maulana.custommodul.ApiVolley;
import com.maulana.custommodul.ItemValidation;
import com.maulana.custommodul.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import id.net.gmedia.perkasaapp.ActivityHome;
import id.net.gmedia.perkasaapp.BuildConfig;
import id.net.gmedia.perkasaapp.R;

public class MocLocChecker {

    private Context context;
    private SessionManager session;
    private ItemValidation iv = new ItemValidation();

    public MocLocChecker(Context context){

        this.context = context;
        session = new SessionManager(context);
        getMockLocation();
    }

    private void getMockLocation() {

        boolean isMockLocation = false;
        try {
            //if marshmallow
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                AppOpsManager opsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                //isMockLocation = (opsManager.checkOp(AppOpsManager.OPSTR_MOCK_LOCATION, android.os.Process.myUid(), BuildConfig.APPLICATION_ID)== AppOpsManager.MODE_ALLOWED);
                isMockLocation = this.areThereMockPermissionApps(context);
            } else {
                // in marshmallow this will always return true
                isMockLocation = !android.provider.Settings.Secure.getString(context.getContentResolver(), "mock_location").equals("0");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(isMockLocation){


            ApiVolley request = new ApiVolley(context, new JSONObject(), "POST", ServerURL.saveMockLocation, new ApiVolley.VolleyCallback() {
                @Override
                public void onSuccess(String result) {

                    try {

                        JSONObject response = new JSONObject(result);
                        String status = response.getJSONObject("metadata").getString("status");
                        String message = response.getJSONObject("metadata").getString("message");

                        if(iv.parseNullInteger(status) == 200){

                            String flag = response.getJSONObject("response").getString("flag");

                            if(flag.equals("1")){

                                message = "Anda terdeteksi menggunakan aplikasi yang mengganggu proses " + context.getResources().getString(R.string.app_name);
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(context, ActivityHome.class);
                                intent.putExtra("exit", true);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                ((Activity)context).startActivity(intent);
                                ((Activity)context).finish();
                            }else{
                                //Toast.makeText(context, "Anda terdeteksi menggunakan aplikasi yang mengganggu proses " + context.getResources().getString(R.string.app_name), Toast.LENGTH_LONG).show();
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(String result) {

                }
            });
        }
    }

    private boolean isMockLocationOn(Context context) {
        /*if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return location.isFromMockProvider();
        } else {
            String mockLocation = "0";
            try {
                mockLocation = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return !mockLocation.equals("0");
        }*/

        if (Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION).equals("0"))
            return false;
        else
            return true;
    }

    private boolean areThereMockPermissionApps(Context context) {
        int count = 0;

        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> packages =
                pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo applicationInfo : packages) {
            try {
                PackageInfo packageInfo = pm.getPackageInfo(applicationInfo.packageName,
                        PackageManager.GET_PERMISSIONS);

                // Get Permissions
                String[] requestedPermissions = packageInfo.requestedPermissions;

                if (requestedPermissions != null) {
                    for (int i = 0; i < requestedPermissions.length; i++) {
                        if (requestedPermissions[i]
                                .equals("android.permission.ACCESS_MOCK_LOCATION")
                                && !applicationInfo.packageName.equals(context.getPackageName())
                                && !applicationInfo.packageName.toLowerCase().contains("messag")) {
                            count++;
                        }
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.e("Got exception " , e.getMessage());
            }
        }

        if (count > 0)
            return true;
        return false;
    }

    public static List<String> getListOfFakeLocationApps(Context context) {
        List<String> runningApps = getRunningApps(context, false);
        for (int i = runningApps.size() - 1; i >= 0; i--) {
            String app = runningApps.get(i);
            if(!hasAppPermission(context, app, "android.permission.ACCESS_MOCK_LOCATION")){
                runningApps.remove(i);
            }
        }
        return runningApps;
    }

    public static List<String> getRunningApps(Context context, boolean includeSystem) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        List<String> runningApps = new ArrayList<>();

        List<ActivityManager.RunningAppProcessInfo> runAppsList = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo processInfo : runAppsList) {
            for (String pkg : processInfo.pkgList) {
                runningApps.add(pkg);
            }
        }

        try {
            //can throw securityException at api<18 (maybe need "android.permission.GET_TASKS")
            List<ActivityManager.RunningTaskInfo> runningTasks = activityManager.getRunningTasks(1000);
            for (ActivityManager.RunningTaskInfo taskInfo : runningTasks) {
                runningApps.add(taskInfo.topActivity.getPackageName());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        List<ActivityManager.RunningServiceInfo> runningServices = activityManager.getRunningServices(1000);
        for (ActivityManager.RunningServiceInfo serviceInfo : runningServices) {
            runningApps.add(serviceInfo.service.getPackageName());
        }

        runningApps = new ArrayList<>(new HashSet<>(runningApps));

        if(!includeSystem){
            for (int i = runningApps.size() - 1; i >= 0; i--) {
                String app = runningApps.get(i);
                if(isSystemPackage(context, app)){
                    runningApps.remove(i);
                }
            }
        }
        return runningApps;
    }

    public static boolean isSystemPackage(Context context, String app){
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo pkgInfo = packageManager.getPackageInfo(app, 0);
            return (pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean hasAppPermission(Context context, String app, String permission){
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo;
        try {
            packageInfo = packageManager.getPackageInfo(app, PackageManager.GET_PERMISSIONS);
            if(packageInfo.requestedPermissions!= null){
                for (String requestedPermission : packageInfo.requestedPermissions) {
                    if (requestedPermission.equals(permission)) {
                        return true;
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }
}
