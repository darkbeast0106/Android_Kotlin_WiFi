package com.darkbeast0106.wifi

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

import android.net.wifi.WifiInfo

import android.net.wifi.WifiManager

import com.google.android.material.bottomnavigation.BottomNavigationView
import android.content.Intent

import android.annotation.SuppressLint
import android.net.ConnectivityManager

import android.os.Build
import android.provider.Settings
import android.text.format.Formatter


@SuppressLint("SetTextI18n")
class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var wifiManager: WifiManager
    private lateinit var wifiInfo: WifiInfo
    private lateinit var textViewInfo: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.wifi_on -> {
                    // Android 10-től (API 29) az alkalmazások nem kapcsolgathatják a wifit.
                    // Éppen ezért meg kell vizsgálnunk a telepített Android verzióját.
                    // Ha ez újabb akkor mást kell csinálnunk.
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        textViewInfo.text = "Nincs jogosultság a wifi állapot módosítására"
                        // Megnyitunk 1 beállítási panelt
                        val panelIntent = Intent(Settings.Panel.ACTION_WIFI)
                        // Panel bezárásakor szerentnénk valamit csinálni
                        startActivityForResult(panelIntent, 0)
                    } else {
                        // Szükséges engedély: CHANGE_WIFI_STATE
                        wifiManager.isWifiEnabled = true
                        textViewInfo.text = "Wifi bekapcsolva"
                    }
                }
                R.id.wifi_off -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        textViewInfo.text = "Nincs jogosultság a wifi állapot módosítására"
                        // Másik panelen is megtalálható a wifi kapcsolásához szükséges gomb.
                        val panelIntent = Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY)
                        startActivityForResult(panelIntent, 0)
                    } else {
                        wifiManager.isWifiEnabled = false
                        textViewInfo.text = "Wifi kikapcsolva"
                    }
                }
                R.id.wifi_info -> {
                    val conManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
                    // Szükséges engedély: ACCESS_NETWORK_STATE
                    val netInfo = conManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                    if (netInfo!!.isConnected) {
                        val ip_number = wifiInfo.ipAddress
                        val ip: String = Formatter.formatIpAddress(ip_number)
                        textViewInfo.text = "IP: $ip"
                    } else {
                        textViewInfo.text = "Nem csatlakoztál wifi hálózatra"
                    }
                }
            }
            return@setOnItemSelectedListener true
        }
    }

    // Akkor fog meghívódni amikor bezárjuk a megnyitott panelt.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // A requestCode az az érték amit mi adunk paraméterül startActivityForResult függvénynek.
        if (requestCode == 0) {
            if (wifiManager.wifiState == WifiManager.WIFI_STATE_ENABLED
                || wifiManager.wifiState == WifiManager.WIFI_STATE_ENABLING
            ) {
                textViewInfo.text = "Wifi bekapcsolva"
            } else if (wifiManager.wifiState == WifiManager.WIFI_STATE_DISABLED
                || wifiManager.wifiState == WifiManager.WIFI_STATE_DISABLING
            ) {
                textViewInfo.text = "Wifi kikapcsolva"
            }
        }
    }


    private fun init() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        textViewInfo = findViewById(R.id.textViewInfo)
        wifiManager = getSystemService(Context.WIFI_SERVICE) as WifiManager
        // Szükséges engedély: ACCESS_WIFI_STATE
        wifiInfo = wifiManager.connectionInfo
    }
}