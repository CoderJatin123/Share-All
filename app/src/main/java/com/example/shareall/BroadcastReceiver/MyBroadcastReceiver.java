package com.example.shareall.BroadcastReceiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import com.example.shareall.Interface.Broadcast;

public class MyBroadcastReceiver extends BroadcastReceiver {
    WifiP2pManager.Channel channel;
    WifiP2pManager manager;
    Broadcast mainActivity;

    MyBroadcastReceiver receiver;

    public MyBroadcastReceiver(WifiP2pManager.Channel channel, WifiP2pManager manager,Broadcast activity) {
        this.channel = channel;
        this.manager = manager;
        this.mainActivity=activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Determine if Wi-Fi Direct mode is enabled or not, alert
            // the Activity.
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {

                mainActivity.setIsWifiP2pEnabled(true);
//                activity.setIsWifiP2pEnabled(true);
                Log.d("TAG","activity.setIsWifiP2pEnabled(true)");

            } else {
                mainActivity.setIsWifiP2pEnabled(false);
                Log.d("TAG","activity.setIsWifiP2pEnabled(false);");
//                activity.setIsWifiP2pEnabled(false);

            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

            mainActivity.onPeerChangedAction();
            // The peer list has changed! We should probably do something about
            // that.

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

            mainActivity.onConnectedActionChanged(intent);
            // Connection state changed! We should probably do something about
            // that.

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {


            mainActivity.onThisDeviceChangedAction();

//            DeviceListFragment fragment = (DeviceListFragment) activity.getFragmentManager()
//                    .findFragmentById(R.id.frag_list);
//            fragment.updateThisDevice((WifiP2pDevice) intent.getParcelableExtra(
//                    WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));

        }
    }
}
