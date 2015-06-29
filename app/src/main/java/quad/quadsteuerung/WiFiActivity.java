package quad.quadsteuerung;

import android.app.AlertDialog;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;


public class WiFiActivity extends ActionBarActivity {

    WifiManager wifiManager;
    ArrayAdapter adapter;
    ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wi_fi);

        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);
        listview = (ListView)findViewById(R.id.listView);
        listview.setAdapter(adapter);
        if(wifiManager == null){
            showMsgBox("ERROR", "WIFI not supported");
            this.finish();
        }
    }

    public void onToggleClicked(View view) {
        wifiManager.setWifiEnabled(true);
        wifiManager.startScan();
    }

    class WifiScanReceiver extends BroadcastReceiver{
        public void onReceive(Context context, Intent intent){
            String action = intent.getAction();
            if(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)){
                List<android.net.wifi.ScanResult> wifiScanResultList = wifiManager.getScanResults();
                for(int i = 0; i < wifiScanResultList.size(); i++){
                    String hotspot = (wifiScanResultList.get(i)).toString();
                    adapter.add(hotspot);
                }
            }
        }
    }

    private void showMsgBox(String titel, String s) {

        AlertDialog alertDialog = new AlertDialog.Builder(WiFiActivity.this).create();
        alertDialog.setTitle(titel);
        alertDialog.setMessage(s);
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_wi_fi, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
