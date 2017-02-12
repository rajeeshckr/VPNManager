package raj.vpnmanager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.app.ActivityManager;
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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    // Remove the below line after defining your own ad unit ID.
    private static final String TOAST_TEXT = "Test ads are being shown. "
            + "To show live ads, replace the ad unit ID in res/values/strings.xml with your own ad unit ID.";

    private String _selectedProcess;
    private Integer currentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        currentPosition = null;

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
        spinner.setOnItemClickListener(spinnerOnItemClicked);

    }

    public void onClickBtn(View v)
    {
        Toast.makeText(this, "Clicked on Button", Toast.LENGTH_LONG).show();
    }

    private AdapterView.OnItemClickListener spinnerOnItemClicked = new AdapterView.OnItemClickListener() {

        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            changeButtonState(position);
            currentPosition = position;
        }

        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    private AdapterView.OnItemSelectedListener spinnerOnItemSelected = new AdapterView.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Object item = parent.getItemAtPosition(position);
            changeButtonState(position);
            currentPosition = position;
            _selectedProcess = item.toString();
        }

        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    private void changeButtonState(int position) {
        Button button = (Button) findViewById(R.id.monitor);
        if(currentPosition != position){
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
