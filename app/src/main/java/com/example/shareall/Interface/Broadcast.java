package com.example.shareall.Interface;

import android.content.Intent;

public interface Broadcast {
    void onPeerListChanged();
    void onPeerChangedAction();
    void onConnectedActionChanged(Intent intent);
    void onThisDeviceChangedAction();
    void setIsWifiP2pEnabled(boolean bool);
}
