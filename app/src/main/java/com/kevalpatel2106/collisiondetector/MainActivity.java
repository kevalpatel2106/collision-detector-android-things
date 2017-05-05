package com.kevalpatel2106.collisiondetector;

import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.GpioCallback;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final int INTERVAL_BETWEEN_TRIGGERS = 1000;

    private Gpio mEcho;
    private Gpio mTrigger;

    private Handler mTriggerHandler;

    private Runnable mTriggerRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                readDistanceSync();
                mTriggerHandler.postDelayed(mTriggerRunnable, INTERVAL_BETWEEN_TRIGGERS);
            } catch (IOException e) {
                Log.e(TAG, "Error on PeripheralIO API", e);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PeripheralManagerService service = new PeripheralManagerService();

        try {
            setEchoPin(service);
            setTriggerPin(service);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Start sending pulses
        HandlerThread triggerHandlerThread = new HandlerThread("TriggerHandlerThread");
        triggerHandlerThread.start();
        mTriggerHandler = new Handler(triggerHandlerThread.getLooper());
        mTriggerHandler.post(mTriggerRunnable);
    }

    private void setTriggerPin(PeripheralManagerService service) throws IOException {
        // Create GPIO connection.
        mTrigger = service.openGpio(BoardDefaults.getGPIOForTrig());
        // Configure as an output with default LOW (false) value.
        mTrigger.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
        mTrigger.setValue(false);
    }

    private void setEchoPin(PeripheralManagerService service) throws IOException {
        // Create GPIO connection.
        mEcho = service.openGpio(BoardDefaults.getGPIOForEcho());
        // Configure as an input.
        mEcho.setDirection(Gpio.DIRECTION_IN);
        // Enable edge trigger events.
        mEcho.setEdgeTriggerType(Gpio.EDGE_BOTH);
        // Set Active type to HIGH, then the HIGH events will be considered as TRUE
        mEcho.setActiveType(Gpio.ACTIVE_HIGH);

        // Prepare handler for GPIO callback
        HandlerThread handlerThread = new HandlerThread("EchoCallbackHandlerThread");
        handlerThread.start();
        mEcho.registerGpioCallback(mEchoCallback, new Handler(handlerThread.getLooper()));
    }

    protected void readDistanceSync() throws IOException, InterruptedException {
        // Just to be sure, set the trigger first to false
        mTrigger.setValue(false);
        Thread.sleep(0, 2000);

        // Hold the trigger pin high for at least 10 us
        mTrigger.setValue(true);
        Thread.sleep(0, 10000); //10 microsec

        // Reset the trigger pin
        mTrigger.setValue(false);
    }

    long broadcastTime;
    private GpioCallback mEchoCallback = new GpioCallback() {

        @Override
        public boolean onGpioEdge(Gpio gpio) {
            try {
                if (gpio.getValue()) {
                    // The pulse arrived on ECHO pin
                    broadcastTime = System.nanoTime();
                } else {
                    double distance =  TimeUnit.NANOSECONDS.toMicros(System.nanoTime() - broadcastTime) / 58.23; //cm
                    Log.i(TAG, "distance: " + distance + " cm");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        public void onGpioError(Gpio gpio, int error) {
            super.onGpioError(gpio, error);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            mEcho.unregisterGpioCallback(mEchoCallback);
            mEcho.close();
            mTrigger.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
