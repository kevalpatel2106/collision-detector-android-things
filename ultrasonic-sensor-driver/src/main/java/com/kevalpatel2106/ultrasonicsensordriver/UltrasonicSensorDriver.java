package com.kevalpatel2106.ultrasonicsensordriver;

import android.os.Handler;
import android.os.HandlerThread;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.GpioCallback;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by Keval on 05-May-17.
 */

public final class UltrasonicSensorDriver implements AutoCloseable {
    private static final int INTERVAL_BETWEEN_TRIGGERS = 500;
    private final DistanceListener mListener;
    private Gpio mEcho;
    private Gpio mTrigger;
    private Handler mTriggerHandler;

    private Runnable mTriggerRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                readDistanceSync();
                mTriggerHandler.postDelayed(mTriggerRunnable, INTERVAL_BETWEEN_TRIGGERS);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    private GpioCallback mEchoCallback = new GpioCallback() {
        long broadcastTime;

        @Override
        public boolean onGpioEdge(Gpio gpio) {
            try {
                if (gpio.getValue()) {
                    // The pulse arrived on ECHO pin
                    broadcastTime = System.nanoTime();
                } else {
                    double distance = TimeUnit.NANOSECONDS.toMicros(System.nanoTime() - broadcastTime) / 58.23; //cm
                    if (mListener != null) mListener.onDistanceChange(distance);
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

    public UltrasonicSensorDriver(String triggerPin, String echoPin, DistanceListener listener) {
        PeripheralManagerService service = new PeripheralManagerService();

        try {
            setTriggerPin(service, triggerPin);
            setEchoPin(service, echoPin);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Invalid pin name.");
        }
        mListener = listener;
        if (mListener == null)
            throw new IllegalArgumentException("DistanceListener cannot be null.");

        //Start sending pulses
        HandlerThread triggerHandlerThread = new HandlerThread("TriggerHandlerThread");
        triggerHandlerThread.start();
        mTriggerHandler = new Handler(triggerHandlerThread.getLooper());
        mTriggerHandler.post(mTriggerRunnable);
    }

    private void setTriggerPin(PeripheralManagerService service, String triggerPin) throws IOException {
        // Create GPIO connection.
        mTrigger = service.openGpio(triggerPin);
        // Configure as an output with default LOW (false) value.
        mTrigger.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
        mTrigger.setValue(false);
    }

    private void setEchoPin(PeripheralManagerService service, String echoPin) throws IOException {
        // Create GPIO connection.
        mEcho = service.openGpio(echoPin);
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

    private void readDistanceSync() throws IOException, InterruptedException {
        // Just to be sure, set the trigger first to false
        mTrigger.setValue(false);
        Thread.sleep(0, 2000);

        // Hold the trigger pin high for at least 10 us
        mTrigger.setValue(true);
        Thread.sleep(0, 10000); //10 microsec

        // Reset the trigger pin
        mTrigger.setValue(false);
    }

    @Override
    public void close() throws Exception {
        try {
            mEcho.unregisterGpioCallback(mEchoCallback);
            mEcho.close();
            mTrigger.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
