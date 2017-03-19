package quad.quadsteuerung;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.channels.Channel;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    Button btnConnetInfo, btnSend;
    TextView txtConInfo, txtSend;
    WifiManager wifiManager;
    WifiP2pManager wifiP2pManager;
    BroadcastReceiver receiver;
    IntentFilter intentfilter;
    Channel channel;
    Socket socket;

    private final IntentFilter intentFilter = new IntentFilter();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnConnetInfo = (Button)findViewById(R.id.btnConnectionInfo);
        btnSend = (Button)findViewById(R.id.btnSend);
        txtConInfo = (TextView)findViewById(R.id.txtConInfo);
        txtSend = (TextView)findViewById(R.id.txtSendWIFI);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiP2pManager = (WifiP2pManager)getSystemService(Context.WIFI_P2P_SERVICE);
        intentfilter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);

        try {
            registerReceiver(receiver, intentfilter);
            String netSSID = "ESP8266";
            String netPASS = "vJ$e&5O;/%@LEXFs";
            WifiConfiguration WifiConfig = new WifiConfiguration();
            WifiConfig.SSID = "\"" + netSSID + "\"";

            WifiConfig.wepKeys[0] = "\"" + netPASS + "\"";
            WifiConfig.wepTxKeyIndex = 0;
            WifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            WifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            wifiManager.addNetwork(WifiConfig);

            List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
            if (list != null) {
                for (WifiConfiguration i :
                        list) {
                    if (i.SSID != null && i.SSID.equals("\"" + netSSID + "\"")) {
                        wifiManager.disconnect();
                        wifiManager.enableNetwork(i.networkId, true);
                        wifiManager.reconnect();
                        break;
                    }
                }
            }

        } catch (Exception e){
            showMsgBox("ERROR", e.toString());
        }


        btnConnetInfo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                try {

                    ConnectivityManager connMgr = (ConnectivityManager)
                            getSystemService(Context.CONNECTIVITY_SERVICE);

                    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                    if (wifiManager == null) {
                        showMsgBox("ERROR", "WIFI not supported");
                    }
                    if (!wifiManager.isWifiEnabled()) {
                        wifiManager.setWifiEnabled(true);
                    }
                    if (networkInfo != null && networkInfo.isConnected()) {
                        txtConInfo.setText(networkInfo.getDetailedState().toString());

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
                String textToSend = txtSend.getText().toString();
                try{
                    socket = new Socket();
                    OutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                    int count = textToSend.length();
                    outputStream.write(textToSend.getBytes(), 0, count);
                    socket.close();
                }catch(IOException e){
                    e.printStackTrace();
                }


            }
        });
    }

    public void showMsgBox(String titel, String s) {

        final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle(titel);
        alertDialog.setMessage(s);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.cancel();
            }
        });
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


    @Override
    public void onResume(){
        super.onResume();
        receiver = new QuadBroadcastReceiver(wifiP2pManager, channel, this);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onPause(){
        super.onPause();
        unregisterReceiver(receiver);
    }
}