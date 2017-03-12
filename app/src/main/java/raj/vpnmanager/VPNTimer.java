package raj.vpnmanager;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiManager;
import android.support.v4.app.NotificationCompat;

import java.util.Timer;
import java.util.TimerTask;

import static android.app.PendingIntent.FLAG_ONE_SHOT;

/**
 * Created by Rajeesh on 22/02/2017.
 */

public class VPNTimer {
    private Context _context;
    private Timer _vpnTimer = null;
    private TimerTask _vpnTimerTask = null;
    private static VPNTimer _instance = null;
    private Boolean _scheduled = false;
    private VPNTimer(Context context){
        _context = context;
    }

    public static VPNTimer getInstance(Context context) {
        if(_instance  == null) {
            _instance  = new VPNTimer(context);
        }
        return _instance;
    }

    public Boolean StartOrExecute()
    {
        Boolean success = true;
        try {
            if (_vpnTimer == null) {
                _vpnTimer = new Timer();
            }

            if (_vpnTimerTask == null) {
                _vpnTimerTask = initializeTimerTask();
            }

            _vpnTimerTask.run();

            if (!_scheduled) {
                scheduleTimerTask();
            }
        }
        catch (Exception ex)
        {
            success = false;
        }
        finally {
            return success;
        }
    }

    public Boolean Stop(){
        Boolean success = true;
        try{
            _vpnTimer.cancel();
            _scheduled = false;
        }
        catch ( Exception ex)
        {
            success = false;
        }
        finally {
            return success;
        }
    }

    public  void scheduleTimerTask(){
        _vpnTimer.schedule(_vpnTimerTask, 1000, 5000);
        _scheduled = true;
    }

    public TimerTask initializeTimerTask() {
        TimerTask task = new TimerTask() {
            public void run() {
                ConnectivityManager cm = (ConnectivityManager)_context.getSystemService(Context.CONNECTIVITY_SERVICE);
                Boolean hasVpn = false;
                Network[] networks = cm.getAllNetworks();
                for (Network network : networks) {
                    NetworkCapabilities caps = cm.getNetworkCapabilities(network);
                    if(!caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_VPN)
                            &&  caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN)){
                        hasVpn = true;
                        break;
                    }
                }

                if(!hasVpn){
                    killNetworkConnection();
                }
            }
        };
        return  task;
    }

    private void killNetworkConnection()
    {
        WifiManager wifi = (WifiManager) _context.getSystemService(Context.WIFI_SERVICE);
        try{
            if(wifi.disconnect()) {
                showNotification();
            }
        }
        catch (Exception e){

        }
        finally {

        }
    }

    private void showNotification(){
        NotificationCompat.Builder mBuilder =   new NotificationCompat.Builder(_context)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark) // notification icon
                .setContentTitle("Notification!") // title for notification
                .setContentText("Wifi is disconnected") // message for notification
                .setAutoCancel(true); // clear notification after click
        Intent intent = new Intent(_context, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(_context,0,intent,FLAG_ONE_SHOT);
        mBuilder.setContentIntent(pi);
        NotificationManager mNotificationManager =
                (NotificationManager) _context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());
    }

}
