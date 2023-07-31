package com.example.shareall;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.shareall.BroadcastReceiver.MyBroadcastReceiver;
import com.example.shareall.Interface.Broadcast;
import com.example.shareall.Interface.DeviceSelection;
import com.example.shareall.RecyclerViewAdapter.RecyclerViewAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Broadcast,WifiP2pManager.ConnectionInfoListener {
    private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    DeviceSelection deviceSelection;
    private FloatingActionButton send_btn;
    WifiP2pManager.Channel channel;
    WifiP2pManager manager;

    RecyclerViewAdapter adapter;
    RecyclerView recyclerView;
    private int selection=-1;

    MyBroadcastReceiver receiver;
    WifiP2pManager.PeerListListener peerListListener;
    private final IntentFilter intentFilter = new IntentFilter();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        WifiP2pManager.ActionListener actionListener;
        actionListener = new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                selection = 0;
                Toast.makeText(MainActivity.this, "discoverPeers : Success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reasonCode) {

                switch (reasonCode) {
                    case 0:
                        Toast.makeText(MainActivity.this, "Please turn on Location", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(MainActivity.this, "discoverPeers Failed " + reasonCode, Toast.LENGTH_SHORT).show();
                }

            }
        };


        deviceSelection= new DeviceSelection() {
            @Override
            public void onDeviceSelected(WifiP2pDevice divice) {
             connect(divice);
            }
        };

          peerListListener = new WifiP2pManager.PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList peerList) {

                Collection<WifiP2pDevice> refreshedPeers=new ArrayList<WifiP2pDevice>();
                refreshedPeers = peerList.getDeviceList();
                if (!refreshedPeers.equals(peers)) {
                    peers.clear();
                    peers.addAll(refreshedPeers);

                    List<WifiP2pDevice> mylist= new ArrayList<>();

                    for (WifiP2pDevice device : refreshedPeers) {
                        mylist.add(device);
                    }

                    if(adapter==null){
                        adapter = new RecyclerViewAdapter(getBaseContext(),mylist,deviceSelection);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                        recyclerView.setAdapter(adapter);
                    }
                    else {
                        adapter.update(mylist);
                    }

                  //  Toast.makeText(MainActivity.this, "Available peers "+peers, Toast.LENGTH_SHORT).show();
                }

                if (peers.size() == 0) {
                    Toast.makeText(MainActivity.this, "Peer not available", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        };



        send_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                manager.discoverPeers(channel,actionListener);


            }
        });

    }

    private void  init(){
        send_btn=findViewById(R.id.send);
        recyclerView=findViewById(R.id.recylcerView);

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);

    }

    @Override
    public void onPeerListChanged() {
      //  Toast.makeText(this, "onPeerListChanged", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPeerChangedAction() {

        // Request available peers from the wifi p2p manager. This is an
        // asynchronous call and the calling activity is notified with a
        // callback on PeerListListener.onPeersAvailable()

        if (manager != null) {
            manager.requestPeers(channel, peerListListener);
        }
     //   Toast.makeText(this, "onPeerChangedAction : P2P peers changed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectedActionChanged(Intent intent) {
        if(manager==null)
                return;

        NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

        if (networkInfo.isConnected()) {
            manager.requestConnectionInfo(channel, this);
        }


        Toast.makeText(this, "onConnectedActionChanged", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onThisDeviceChangedAction() {
      //  Toast.makeText(this, "onThisDeviceChangedAction", Toast.LENGTH_SHORT).show();
    }

    public void setIsWifiP2pEnabled(boolean bool){
   //     Toast.makeText(this, "setIsWifiP2pEnabled :"+bool, Toast.LENGTH_SHORT).show();
    }

    /** register the BroadcastReceiver with the intent values to be matched */
    @Override
    public void onResume() {
        super.onResume();
        receiver = new MyBroadcastReceiver(channel,manager, this);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }
    public void connect(WifiP2pDevice device) {

        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;

        manager.connect(channel, config, new WifiP2pManager.ActionListener() {


            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(MainActivity.this, "Connect failed. Retry.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        String groupOwnerAddress = info.groupOwnerAddress.getHostAddress();

        if (info.groupFormed && info.isGroupOwner) {

            Toast.makeText(MainActivity.this, "Host : "+info.groupOwnerAddress, Toast.LENGTH_SHORT).show();
        } else if (info.groupFormed) {
            Toast.makeText(MainActivity.this, "you are Client", Toast.LENGTH_SHORT).show();
        }
    }
}