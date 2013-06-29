package com.vi.watchuchild;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.*;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

import java.util.HashMap;
import java.util.Map;

@ContentView(R.layout.main)
public class MainActivity extends RoboActivity implements SoundPool.OnLoadCompleteListener, SeekBar.OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener {
    private static final String TAG = "TAG-MainActivity";
    //public static final String SPY_KEY_ADDR = "DA:7B:89:C2:2F:BB";
    //public static final String SPY_KEY_ADDR = "28:98:7B:26:06:A6";

    // bluetooth moudle.
    //public static final String SPY_KEY_ADDR = "00:13:EF:00:0E:7D";

    // zidan
    //public static final String SPY_KEY_ADDR = "14:10:9F:F2:0E:F1";

    // my book
    public static final String SPY_KEY_ADDR = "00:88:65:40:7D:0B";
    public static final int MAX_RSSI = 50;
    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;
    private volatile boolean mWatchDog;
    private SoundPool mSoundPool;
    private int mSoundID;
    private short mDistance;

    @InjectView(R.id.info_text)
    private TextView mInfoText;

    @InjectView(R.id.start_discovery)
    private ToggleButton mBtnStartRescovery;

    @InjectView(R.id.canvas)
    private DrawableHolderView mCanvas;

    @InjectView(R.id.seekbar)
    private SeekBar mSeekBar;

    @InjectView(R.id.mark)
    private ImageView mMark;

    private boolean mSoundReady;
    private int mStream;
    private short mRssi;
    private Map<String, DrawableHolder> mObjects = new HashMap<String, DrawableHolder>();
    private DrawableHolder mHolder;
    private boolean mRunning;
    private ValueAnimator mFlicker;

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

        mFlicker = ObjectAnimator.ofFloat(mMark,
                "alpha", 0.05f, 1.0f);
        mFlicker.setDuration(450);
        mFlicker.setRepeatCount(ValueAnimator.INFINITE);
        mFlicker.setRepeatMode(ValueAnimator.REVERSE);
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
        //filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy

        if (mRunning) { // Continue
            mBluetoothAdapter.startDiscovery();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopAlarm();
        mBluetoothAdapter.cancelDiscovery();
        unregisterReceiver(mReceiver);
    }

    private void setupViews() {
        mBtnStartRescovery.setOnCheckedChangeListener(this);
        mSeekBar.setOnSeekBarChangeListener(this);
        mDistance = (short) -(mSeekBar.getProgress() + 50);
    }

    @Override
    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
        mSoundReady = true;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        mDistance = (short) (-50 - progress);
        updateStatictis();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            startMeasuring();
        } else {
            stopMesasuring();
        }
    }

    private void startMeasuring() {
        mRunning = true;
        mBluetoothAdapter.startDiscovery();
        mCount = 0;
    }

    private void stopMesasuring() {
        mRunning = false;
        mBluetoothAdapter.cancelDiscovery();
        mRssi = 0;
        mCount = 0;
        updateStatictis();
    }

    private void playAlarm() {
        if (mSoundReady) {
            mStream = mSoundPool.play(mSoundID, 1.0f, 1.0f, 1, -1, 1.0f);
            Log.e(TAG, "Sound stream id =" + mStream);
        }
    }

    private void pauseAlarm() {
        mSoundPool.pause(mStream);
    }

    private void resumeAlarm() {
        mSoundPool.resume(mStream);
    }

    private void stopAlarm() {
        mSoundPool.stop(mStream);
        mStream = 0;
    }

    private void updateStatictis() {
        mInfoText.setText(String.format(getString(R.string.info_msg,
                Math.abs(mDistance) / 10.0f, Math.abs(mRssi) / 10.0f, mCount)));
    }

    private int mCount;
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
                // Add the name and address to an array adapter to show in a ListView
                //mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                String addr = device.getAddress();
                if (device.getAddress().equals(SPY_KEY_ADDR)) {
                    int width = mCanvas.getWidth();
                    int height = mCanvas.getHeight();
                    mRssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, (short) 0);
                    if (!mObjects.containsKey(addr)) {
                        BitmapDrawable drawable = (BitmapDrawable)getResources().getDrawable(R.drawable.tag);
                        mHolder = new BitmapDrawableHolder(drawable);
                        mHolder.setAnchorPoint(0.5f, 1.0f);
                        mObjects.put(addr, mHolder);
                        mCanvas.addHolder(mHolder);

                    }
                    mHolder.setPosition(width / 2,
                            height - (Math.abs(mRssi) - 50) * height / MAX_RSSI);
                    mCanvas.invalidate();

                    mWatchDog =  mRssi < mDistance ? false : true;
                    mBluetoothAdapter.cancelDiscovery();
                }
                Log.e(TAG, device.getName() + device.getAddress());
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                mHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        ++mCount;
                        updateStatictis();
                        if (!mWatchDog) {
                            if (0 != mStream) {
                                resumeAlarm();
                            } else {
                                playAlarm();
                            }
                            if (null != mHolder) mHolder.setVisibility(false);
                            mMark.setVisibility(View.VISIBLE);
                            if (!mFlicker.isRunning()) mFlicker.start();
                        } else {
                            pauseAlarm();
                            if (null != mHolder) mHolder.setVisibility(true);
                            if (mFlicker.isRunning()) mFlicker.cancel();
                            mMark.setVisibility(View.GONE);
                        }

                        if (mRunning) {
                            mBluetoothAdapter.startDiscovery();
                        }
                    }
                });
            }
        }
    };
}
