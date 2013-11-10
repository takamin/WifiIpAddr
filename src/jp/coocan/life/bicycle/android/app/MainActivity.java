package jp.coocan.life.bicycle.android.app;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.View;
//import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {
	private BroadcastReceiver broadcastReceiver = null;
	private WifiInfo wifiConnectionInfo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		broadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if(intent == null) {
					Log.d(this.getClass().getCanonicalName(),
							"MainActivity.broadcastReceiver.onReceive intent null");
					return;
				}
				String action = intent.getAction();
				if(action == null) {
					Log.d(this.getClass().getCanonicalName(),
							"MainActivity.broadcastReceiver.onReceive action null");
					return;
				}
				Log.d(this.getClass().getCanonicalName(),
						"MainActivity.broadcastReceiver.onReceive action = " + action);
				WifiManager wifi_mng = (WifiManager)getSystemService(Context.WIFI_SERVICE);
				if(action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
					wifiConnectionInfo = wifi_mng.getConnectionInfo();
		    		updateView();
				} else if(action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
		    		int state = wifi_mng.getWifiState();
		    		switch(state) {
		    		case WifiManager.WIFI_STATE_ENABLED:
		    			break;
		    		case WifiManager.WIFI_STATE_DISABLED:
		    			wifiConnectionInfo = null;
		    			break;
		    		case WifiManager.WIFI_STATE_ENABLING:
		    			wifiConnectionInfo = null;
		    			break;
		    		case WifiManager.WIFI_STATE_UNKNOWN:
		    			wifiConnectionInfo = null;
		    			break;
		    		default:
		    			wifiConnectionInfo = null;
		    			break;
		    		}
		    		updateView();
				}
			}
		};
		registerReceiver(broadcastReceiver, new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));
		registerReceiver(broadcastReceiver, new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
    }
	
	@Override
	protected void onDestroy() {
		unregisterReceiver(broadcastReceiver);
		super.onDestroy();
	}

	private void updateView() {
		ListView  listView1 = (ListView)findViewById(R.id.listView1);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1) {
			
			@Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                TextView view = (TextView)super.getView(position, convertView, parent);
                view.setTextSize( 30 );
                return view;
            }			
		};
		adapter.clear();
		if(wifiConnectionInfo == null) {
			adapter.add("Wi-Fi Disabled.");
		} else {
			int ipaddr = wifiConnectionInfo.getIpAddress();
			StringBuilder sbIp = new StringBuilder("IP Address:")
			.append((ipaddr >> 0) & 0xff).append(".")
			.append((ipaddr >> 8) & 0xff).append(".")
			.append((ipaddr >> 16) & 0xff).append(".")
			.append((ipaddr >> 24) & 0xff).append("\n");
			adapter.add(sbIp.toString());
			
			String ssid = wifiConnectionInfo.getSSID();
			if(ssid == null) {
				ssid = "?";
			}
			StringBuilder sbSSID = new StringBuilder("SSID:\"").append(ssid).append("\"");
			adapter.add(sbSSID.toString());
			
			int nid = wifiConnectionInfo.getNetworkId();
			StringBuilder sbNid = new StringBuilder("NetworkId:").append(Integer.toString(nid));
			adapter.add(sbNid.toString());
			
		}
		listView1.setAdapter(adapter);
	}
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		// getMenuInflater().inflate(R.menu.main, menu);
//		return true;
//	}
}
