package raj.vpnmanager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {
    // Remove the below line after defining your own ad unit ID.
    private static final String TOAST_TEXT = "Test ads are being shown. "
            + "To show live ads, replace the ad unit ID in res/values/strings.xml with your own ad unit ID.";

    private String _selectedProcess;
    private Integer _currentPosition;
    private  Map<String, String> _processPackageMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        _currentPosition = null;
        _selectedProcess = null;
        _processPackageMap = new HashMap<String, String>();

        // Load an ad into the AdMob banner view.
        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, getListOfProcess());
        spinner.setAdapter(adapter);

        spinner.setOnItemClickListener(spinnerOnItemClicked);
        spinner.setOnItemSelectedListener(spinnerOnItemSelected);

        startTimer();
    }

    public void onClickBtn(View v)
    {
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        _selectedProcess = spinner.getSelectedItem().toString();
        changeButtonState(spinner.getSelectedItemPosition());
    }

    public void startTimer() {
        Timer vpnTimer = new Timer();
        TimerTask vpnTimerTask = initializeTimerTask();
        vpnTimer.schedule(vpnTimerTask, 1000, 10000);
    }


    public TimerTask initializeTimerTask() {
        TimerTask task = new TimerTask() {
            public void run() {
                if(_selectedProcess != null){
                    ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
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
                        killSelectedProcess();
                    }

                }
            }
        };
        return  task;
    }

    private  void killSelectedProcess()
    {
        ActivityManager am = (ActivityManager) getSystemService(Activity.ACTIVITY_SERVICE);
        am.killBackgroundProcesses(_processPackageMap.get(_selectedProcess));
    }

    private AdapterView.OnItemSelectedListener spinnerOnItemSelected = new AdapterView.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Object item = parent.getItemAtPosition(position);
            changeButtonState(position);
            _currentPosition = position;
        }

        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    private AdapterView.OnItemClickListener spinnerOnItemClicked = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
          /*  ArrayAdapter<String> adapter = new ArrayAdapter<String>(parent.getContext(),
                    android.R.layout.simple_spinner_item, getListOfProcess());
            parent.setAdapter(?);
            parent.setAdapter(adapter);
            adapter.notifyDataSetChanged();*/
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
        _processPackageMap.clear();
        ArrayList<String> applications = new ArrayList<String>();
        // Flags: See below
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> apps = pm.getInstalledApplications(0);

        List<ApplicationInfo> installedApps = new ArrayList<ApplicationInfo>();

        for(ApplicationInfo app : apps) {
            //checks for flags; if flagged, check if updated system app
            if((app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
                applications.add(app.processName);
                _processPackageMap.put(app.processName, app.packageName);
                //it's a system app, not interested
            } else if ((app.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                //Discard this one
                //in this case, it should be a user-installed app
            } else {
                applications.add(app.processName);
                _processPackageMap.put(app.processName, app.packageName);
            }
        }

        String[] mStringArray = new String[applications.size()];
        mStringArray = applications.toArray(mStringArray);
        return  mStringArray;
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
