package raj.vpnmanager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.app.Activity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;

import static android.widget.Toast.*;

public class MainActivity extends Activity {
    // Remove the below line after defining your own ad unit ID.
    private static final String TOAST_TEXT = "Test ads are being shown. "
            + "To show live ads, replace the ad unit ID in res/values/strings.xml with your own ad unit ID.";

    private String _selectedProcess;
    private Integer _currentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        _currentPosition = null;
        _selectedProcess = null;

        // Load an ad into the AdMob banner view.
        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, getListOfProcess());
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(spinnerOnItemSelected);

        startTimer();
    }

    public void onClickBtn(View v)
    {
        Button button = (Button) v;
        button.setEnabled(false);
    }


    public void startTimer() {
        Timer timer = new Timer();
        TimerTask timerTask = initializeTimerTask();
        timer.schedule(timerTask, 1000, 600000); //
    }


    public TimerTask initializeTimerTask() {
        TimerTask task = new TimerTask() {
            public void run() {
                if(_selectedProcess != null){
                    ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                    Network[] networks = cm.getAllNetworks();
                    for(int i = 0; i < networks.length; i++) {
                        NetworkCapabilities caps = cm.getNetworkCapabilities(networks[i]);
                        NetworkCapabilities caps2 = cm.getNetworkCapabilities(networks[i]);
//                        Log.i(TAG, "Network " + i + ": " + networks[i].toString());
//                        Log.i(TAG, "VPN transport is: " + caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN));
//                        Log.i(TAG, "NOT_VPN capability is: " + caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_VPN));

                    }
                }
            }
        };
        return  task;
    }

    private AdapterView.OnItemSelectedListener spinnerOnItemSelected = new AdapterView.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Object item = parent.getItemAtPosition(position);
            changeButtonState(position);
            _currentPosition = position;
            _selectedProcess = item.toString();
        }

        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    private void changeButtonState(int position) {
        Button button = (Button) findViewById(R.id.monitor);
        if(_currentPosition == null
                || _currentPosition != position){
            button.setClickable(true);
        }
        else{
            button.setClickable(false);
        }
    }

    private static View.OnKeyListener spinnerOnKey = new View.OnKeyListener() {
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            //your code
            return keyCode == KeyEvent.KEYCODE_DPAD_CENTER;
        }
    };

    private String[] getListOfProcess() {
        ActivityManager actvityManager = (ActivityManager)this.getSystemService( ACTIVITY_SERVICE );

        ArrayList<String> processList = new ArrayList<String>();
        List<ActivityManager.RunningAppProcessInfo> procInfos = actvityManager.getRunningAppProcesses();
        for(ActivityManager.RunningAppProcessInfo runningProInfo:procInfos){
            if(!runningProInfo.processName.toLowerCase().contains("vpnmanager")){
                processList.add(runningProInfo.processName);
            }
        }
        if(processList.isEmpty()){
            processList.add("torrent");
            processList.add("torrent1");
            processList.add("torrent2");
            processList.add("torrent3");
            processList.add("torrent4");
        }

        return processList.toArray(new String[0]);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
