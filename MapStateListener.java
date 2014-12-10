package dk.composed.mapstate;

import android.app.Activity;
import android.os.Handler;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;

public abstract class MapStateListener 
{
    private static final int SETTLE_TIME = 500;

    private boolean mMapTouched = false;
    private boolean mMapSettled = false;

    private Handler mHandler;

    private GoogleMap mMap;
    private Activity mActivity;

    private Runnable settleMapTask = new Runnable() {
        @Override
        public void run() {
            settleMap();
        }
    };

    public MapStateListener(GoogleMap map, TouchableMapFragment touchableMapFragment, Activity activity) {
        mMap = map;
        mActivity = activity;

	mHandler = new Handler();

        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                unsettleMap();
                if (!mMapTouched) {
                    runSettleTimer();
                }
            }
        });

        touchableMapFragment.setTouchListener(new TouchableWrapper.OnTouchListener() {
            @Override
            public void onTouch() {
                touchMap();
                unsettleMap();
            }

            @Override
            public void onRelease() {
                releaseMap();
                runSettleTimer();
            }
        });
    }

    private void runSettleTimer() {
        mHandler.removeCallbacks(null);
        mHandler.postDelayed(settleMapTask, SETTLE_TIME);
    }

    private synchronized void releaseMap() {
        if (mMapTouched) {
            mMapTouched = false;
            onMapReleased();
        }
    }

    private void touchMap() {
        if (!mMapTouched) {
            mHandler.removeCallbacks(null);
            mMapTouched = true;
            onMapTouched();
        }
    }

    public void unsettleMap() {
        if (mMapSettled) {
            mHandler.removeCallbacks(null);
            mMapSettled = false;
            onMapUnsettled();
        }
    }

    public void settleMap() {
        if (!mMapSettled) {
            mMapSettled = true;
            onMapSettled();
        }
    }

    public abstract void onMapTouched();
    public abstract void onMapReleased();
    public abstract void onMapUnsettled();
    public abstract void onMapSettled();
}
