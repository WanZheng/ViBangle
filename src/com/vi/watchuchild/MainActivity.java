package com.vi.watchuchild;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.main)
public class MainActivity extends RoboActivity implements View.OnClickListener, SoundPool.OnLoadCompleteListener {
    private static final String TAG = "TAG-MainActivity";
    //public static final String SPY_KEY_ADDR = "DA:7B:89:C2:2F:BB";
    public static final String SPY_KEY_ADDR = "28:98:7B:26:06:A6";
    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;
    private volatile boolean mWatchDog;
    private SoundPool mSoundPool;
    private int mSoundID;

    @InjectView(R.id.info_text)
    private TextView mInfoText;

    @InjectView(R.id.start_rescovery)
    private Button mBtnStartRescovery;
    private boolean mSoundReady;
    private int mStream;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
        mSoundPool = new SoundPool(8, AudioManager.STREAM_MUSIC, 100);
        mSoundID = mSoundPool.load(this, R.raw.alarm, 1);
        mSoundPool.setOnLoadCompleteListener(this);
        setupViews();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Enable Bluetooth function.
        if (!mBluetoothAdapter.isEnabled()) {
            Intent discoverableIntent = new
                    Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            //discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }

        // Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopAlarm();
        unregisterReceiver(mReceiver);
    }

    private void setupViews() {
        mBtnStartRescovery.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_rescovery:
                mBluetoothAdapter.startDiscovery();
                break;
        }
    }

    @Override
    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
        mSoundReady = true;
    }

    private void playAlarm() {
        if (mSoundReady) {
            mStream = mSoundPool.play(mSoundID, 1.0f, 1.0f, 1, -1, 1.0f);
        }
    }

    private void stopAlarm() {
        mSoundPool.stop(mStream);
    }

    /** =============================================
     * Broadcast receiver.
     */
    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                short rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, (short) 0);
                Log.e(TAG, device.getName() + device.getAddress() + " <> " + rssi);
                // Add the name and address to an array adapter to show in a ListView
                //mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                if (device.getAddress().equals(SPY_KEY_ADDR)) {
                    mWatchDog = true;
                    mBluetoothAdapter.cancelDiscovery();
                    mInfoText.setText(String.format("RSSI = %d", rssi));
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!mWatchDog) {
                            playAlarm();
                        } else {
                            mWatchDog = false;
                        }
                        mBluetoothAdapter.startDiscovery();
                    }
                });
            }
        }
    };
}
