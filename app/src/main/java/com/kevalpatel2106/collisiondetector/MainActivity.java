package com.kevalpatel2106.collisiondetector;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;
import com.kevalpatel2106.ultrasonicsensordriver.DistanceListener;
import com.kevalpatel2106.ultrasonicsensordriver.UltrasonicSensorDriver;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements DistanceListener {
    private Gpio mRedPin;
    private Gpio mGreenPin;
    private Gpio mYellowPin;
    private UltrasonicSensorDriver mUltrasonicSensorDriver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PeripheralManagerService service = new PeripheralManagerService();

        mUltrasonicSensorDriver = new UltrasonicSensorDriver(
                BoardDefaults.getGPIOForTrig(),
                BoardDefaults.getGPIOForEcho(),
                this);
        try {
            mRedPin = service.openGpio(BoardDefaults.getGPIOForRedLED());
            mRedPin.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            mRedPin.setValue(false);

            mYellowPin = service.openGpio(BoardDefaults.getGPIOForYellowLED());
            mYellowPin.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            mYellowPin.setValue(false);

            mGreenPin = service.openGpio(BoardDefaults.getGPIOForGreenLED());
            mGreenPin.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            mGreenPin.setValue(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void glowLed(double distance) {
        try {
            if (distance < 25) {
                setRedLedOn();
            } else if (distance > 25 && distance < 50) {
                setYellowLedOn();
            } else {
                setGreenLedOn();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setRedLedOn() throws IOException {
        mRedPin.setValue(true);
        mYellowPin.setValue(false);
        mGreenPin.setValue(false);
    }

    private void setYellowLedOn() throws IOException {
        mRedPin.setValue(false);
        mYellowPin.setValue(true);
        mGreenPin.setValue(false);
    }

    private void setGreenLedOn() throws IOException {
        mRedPin.setValue(false);
        mYellowPin.setValue(false);
        mGreenPin.setValue(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            mUltrasonicSensorDriver.close();
            mRedPin.close();
            mGreenPin.close();
            mYellowPin.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDistanceChange(double distance) {
        Log.d("Distance", distance + " cm");
        glowLed(distance);
    }
}
