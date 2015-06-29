package quad.quadsteuerung;

import android.app.AlertDialog;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


public class MainActivity extends ActionBarActivity {

    Button btnConnetInfo, btnSend, btnWiFi;
    TextView txtConInfo;
    WifiManager wifiManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnConnetInfo = (Button)findViewById(R.id.btnConnectionInfo);
        btnSend = (Button)findViewById(R.id.btnSend);
        btnWiFi = (Button)findViewById(R.id.btnWiFi);
        txtConInfo = (TextView)findViewById(R.id.txtConInfo);

        wifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);

        btnConnetInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ConnectivityManager connMgr = (ConnectivityManager)
                            getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isConnected()) {
                        txtConInfo.setText(networkInfo.toString());
                        //TODO: Netzwerkname vergleichen und auf Steuerung wechseln
                    } else {
                        showMsgBox("ERROR", "networkInfo: " + networkInfo.toString());
                    }
                } catch (Exception e) {
                    showMsgBox("ERROR", e.toString());
                }

            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void getWiFiActivity(View v){
        Intent intent = new Intent(getApplicationContext(), WiFiActivity.class);
        startActivity(intent);
    }

    private void showMsgBox(String titel, String s) {

        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle(titel);
        alertDialog.setMessage(s);
        alertDialog.show();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
