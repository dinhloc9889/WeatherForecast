package com.example.weatherapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.widget.Toast;


public class BroadcastReceiverWeather extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String internet = context.getResources().getString(R.string.internet);
        String notinternet = context.getResources().getString(R.string.notinternet);
        if(ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())){
            if(isNetworkAvailable(context)){
                Toast.makeText(context, internet, Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(context, notinternet, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Network network = connectivityManager.getActiveNetwork();
                if (network == null) {
                    return  false;
                }
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
                return  capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
            } else {
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                return networkInfo != null && networkInfo.isConnected();
            }
        }
    }

}
