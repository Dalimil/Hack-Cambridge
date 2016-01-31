package tk.clearpass.biostat;

import android.annotation.SuppressLint;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.ImageView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.samsung.android.sdk.pass.SpassInvalidStateException;
import java.util.ArrayList;
import java.util.List;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.util.Log;
import android.util.SparseArray;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.pass.Spass;
import com.samsung.android.sdk.pass.SpassFingerprint;


import java.security.MessageDigest;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Authenticate extends AppCompatActivity {

    private SpassFingerprint mSpassFingerprint;
    private Spass mSpass;
    private Context mContext;
    private ListView mListView;
    private List<String> mItemArray = new ArrayList<String>();
    private ArrayAdapter<String> mListAdapter;
    private boolean onReadyIdentify = false;
    private boolean onReadyEnroll = false;
    boolean isFeatureEnabled = false;

    private BroadcastReceiver mPassReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (SpassFingerprint.ACTION_FINGERPRINT_RESET.equals(action)) {
                Toast.makeText(mContext, "all fingerprints are removed", Toast.LENGTH_SHORT).show();
            } else if (SpassFingerprint.ACTION_FINGERPRINT_REMOVED.equals(action)) {
                int fingerIndex = intent.getIntExtra("fingerIndex", 0);
                Toast.makeText(mContext, fingerIndex + " fingerprints is removed",Toast.LENGTH_SHORT).show();
            } else if (SpassFingerprint.ACTION_FINGERPRINT_ADDED.equals(action)) {
                int fingerIndex = intent.getIntExtra("fingerIndex", 0);
                Toast.makeText(mContext, fingerIndex + " fingerprints is added", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void registerBroadcastReceiver(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(SpassFingerprint.ACTION_FINGERPRINT_RESET);
        filter.addAction(SpassFingerprint.ACTION_FINGERPRINT_REMOVED);
        filter.addAction(SpassFingerprint.ACTION_FINGERPRINT_ADDED);
        mContext.registerReceiver(mPassReceiver, filter);
    };

    private void unregisterBroadcastReceiver() {
        try {
            if (mContext != null) {
                mContext.unregisterReceiver(mPassReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private SpassFingerprint.IdentifyListener listener = new SpassFingerprint.IdentifyListener() {
        @Override
        public void onFinished(int eventStatus) {
            log("identify finished : reason=" + getEventStatusName(eventStatus));
            onReadyIdentify = false;
            int FingerprintIndex = 0;
            try {
                FingerprintIndex = mSpassFingerprint.getIdentifiedFingerprintIndex();
            } catch (IllegalStateException ise) {
                log(ise.getMessage());
            }
            if (eventStatus == SpassFingerprint.STATUS_AUTHENTIFICATION_SUCCESS) {

                // Fingerprint SUCCESS case
                log("onFinished() : Identify authentification Success with FingerprintIndex : " + FingerprintIndex);
                Button button = (Button)findViewById(R.id.button);
                button.setVisibility(View.GONE);
                GenerateQR(Integer.toString(FingerprintIndex));

            } else if (eventStatus == SpassFingerprint.STATUS_AUTHENTIFICATION_PASSWORD_SUCCESS) {
                log("onFinished() : Password authentification Success");
            } else {
                log("onFinished() : Authentification Fail for identify");
                Identify();
            }
        }

        @Override
        public void onReady() {
            log("identify state is ready");
        }

        @Override
        public void onStarted() {
            log("User touched fingerprint sensor!");
        }
    };

    private SpassFingerprint.RegisterListener mRegisterListener = new SpassFingerprint.RegisterListener() {

        @Override
        public void onFinished() {
            onReadyEnroll = false;
            log("RegisterListener.onFinished()");

        }
    };
    private static String getEventStatusName(int eventStatus) {
        switch (eventStatus) {
            case SpassFingerprint.STATUS_AUTHENTIFICATION_SUCCESS:
                return "STATUS_AUTHENTIFICATION_SUCCESS";
            case SpassFingerprint.STATUS_AUTHENTIFICATION_PASSWORD_SUCCESS:
                return "STATUS_AUTHENTIFICATION_PASSWORD_SUCCESS";
            case SpassFingerprint.STATUS_TIMEOUT_FAILED:
                return "STATUS_TIMEOUT";
            case SpassFingerprint.STATUS_SENSOR_FAILED:
                return "STATUS_SENSOR_ERROR";
            case SpassFingerprint.STATUS_USER_CANCELLED:
                return "STATUS_USER_CANCELLED";
            case SpassFingerprint.STATUS_QUALITY_FAILED:
                return "STATUS_QUALITY_FAILED";
            case SpassFingerprint.STATUS_USER_CANCELLED_BY_TOUCH_OUTSIDE:
                return "STATUS_USER_CANCELLED_BY_TOUCH_OUTSIDE";
            case SpassFingerprint.STATUS_AUTHENTIFICATION_FAILED:
            default:
                return "STATUS_AUTHENTIFICATION_FAILED";
        }

    }

    private String salt = "salt2j4Eo";
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;

    private View mContentView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_authenticate);
        mContentView = findViewById(R.id.fullscreen_content);

        ////////////////////////
        mContext = this;
        mListAdapter = new ArrayAdapter<String>(this, R.layout.list_entry, mItemArray);
        mListView = (ListView)findViewById(R.id.listView1);

        if (mListView != null) {
            mListView.setAdapter(mListAdapter);
        }
        mSpass = new Spass();

        try {
            mSpass.initialize(Authenticate.this);
        } catch (SsdkUnsupportedException e) {
            log("Exception: " + e);
        } catch (UnsupportedOperationException e){
            log("Fingerprint Service is not supported in the device");
        }
        isFeatureEnabled = mSpass.isFeatureEnabled(Spass.DEVICE_FINGERPRINT);

        if(isFeatureEnabled){
            mSpassFingerprint = new SpassFingerprint(Authenticate.this);
            log("Fingerprint Service is supported in the device.");
            log("SDK version : " + mSpass.getVersionName());
        } else {
            log("Fingerprint Service is not supported in the device.");
        }
        SparseArray<View.OnClickListener> listeners = new SparseArray<View.OnClickListener>();
        registerBroadcastReceiver();
        listeners.put(R.id.buttonHasRegisteredFinger, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    boolean hasRegisteredFinger = mSpassFingerprint.hasRegisteredFinger();
                    log("hasRegisteredFinger() = " + hasRegisteredFinger);
                } catch (UnsupportedOperationException e) {
                    log("Fingerprint Service is not supported in the device");
                }
            }
        });
        try {
            if (!mSpassFingerprint.hasRegisteredFinger()) {
                log("Please register finger first");
            } else {
                if (onReadyIdentify == false) {
                    try {
                        onReadyIdentify = true;
                        mSpassFingerprint.startIdentify(listener);
                        log("Please identify finger to verify you");
                    } catch (SpassInvalidStateException ise) {
                        onReadyIdentify = false;
                        if (ise.getType() == SpassInvalidStateException.STATUS_OPERATION_DENIED) {
                            log("Exception: " + ise.getMessage());
                        }
                    } catch (IllegalStateException e) {
                        onReadyIdentify = false;
                        log("Exception: " + e);
                    }
                } else {
                    log("Please cancel Identify first");
                }
            }
        } catch (UnsupportedOperationException e) {
            log("Fingerprint Service is not supported in the device");
        }
            mContext = this;
            mListAdapter = new ArrayAdapter<String>(this, R.layout.list_entry, mItemArray);
            mListView = (ListView) findViewById(R.id.listView1);

            if (mListView != null) {
                mListView.setAdapter(mListAdapter);
            }
            mSpass = new Spass();

            try {
                mSpass.initialize(Authenticate.this);
            } catch (SsdkUnsupportedException ex) {
                log("Exception: " + ex);
            } catch (UnsupportedOperationException ex) {
                log("Fingerprint Service is not supported in the device");
            }
            isFeatureEnabled = mSpass.isFeatureEnabled(Spass.DEVICE_FINGERPRINT);

            if (isFeatureEnabled) {
                mSpassFingerprint = new SpassFingerprint(Authenticate.this);
                log("Fingerprint Service is supported in the device.");
                log("SDK version : " + mSpass.getVersionName());
            } else {
                log("Fingerprint Service is not supported in the device.");
            }

        final int N = listeners.size();
        for (int i = 0; i < N; i++) {
            int id = listeners.keyAt(i);
            Button button = (Button)findViewById(id);
            if (button != null) {
                button.setOnClickListener(listeners.valueAt(i));
                if (!isFeatureEnabled) {
                    button.setEnabled(false);
                }
            }
        }


        ///////////////////////

        Identify();
    }
    // END OF MAIN
    ////////////////////////

    private void GenerateQR(String fingerprintid) {
        String qrData = timeHash(hashStrong(fingerprintid));

        // ImageView to display the QR code in.  This should be defined in
        // your Activity's XML layout file
        ImageView imageView = (ImageView) findViewById(R.id.qrCode);

        int qrCodeDimention = 700;

        QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(qrData, null, Contents.Type.TEXT, BarcodeFormat.QR_CODE.toString(), qrCodeDimention);

        try {
            Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
            imageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

        private void Identify() {
        try {
            if (!mSpassFingerprint.hasRegisteredFinger()) {
                log("Please register finger first");
            } else {
                if (onReadyIdentify == false) {
                    try {
                        onReadyIdentify = true;
                        mSpassFingerprint.startIdentify(listener);
                        log("Please identify finger to verify you");
                    } catch (SpassInvalidStateException ise) {
                        onReadyIdentify = false;
                        if (ise.getType() == SpassInvalidStateException.STATUS_OPERATION_DENIED) {
                            log("Exception: " + ise.getMessage());
                        }
                    } catch (IllegalStateException e) {
                        onReadyIdentify = false;
                        log("Exception: " + e);
                    }
                } else {
                    log("Please cancel Identify first");
                }
            }
        } catch (UnsupportedOperationException e) {
            log("Fingerprint Service is not supported in the device");
        }
    }

    private void RegisterFinger() {
        try {
            if (onReadyIdentify == false) {
                if (onReadyEnroll == false) {
                    onReadyEnroll = true;
                    mSpassFingerprint.registerFinger(Authenticate.this, mRegisterListener);
                    log("Jump to the Enroll screen");
                } else {
                    log("Please wait and try to register again");
                }
            } else {
                log("Please cancel Identify first");
            }
        } catch (UnsupportedOperationException e){
            log("Fingerprint Service is not supported in the device");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterBroadcastReceiver();
    }

    public void log(String text) {
        Log.v("Fingerprint", text);
    }



        ///////////////////////


    public void LaunchRegister(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    private String hash(String input) {
        String output = "";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String saltyInput = input + salt;
            md.update(saltyInput.getBytes("UTF-8"));
            output = bytesToHex(md.digest());
        } catch (Exception e) {
            // You are an idiot
        }
        return output;
    }

    private String hashStrong(String input) {
        for (int i = 0; i < 100; i++) {
            input = hash(input);
        }
        return input;
    }

    private static String bytesToHex(byte[] in) {
        final StringBuilder builder = new StringBuilder();
        for (byte b : in) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }

    private String timeHash(String input) {
        long seconds = System.currentTimeMillis() / 1000l;
        long time = seconds / 300;
        return hash(input + time);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }


    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
        }
    };

    private final Handler mHideHandler = new Handler();
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }


}
