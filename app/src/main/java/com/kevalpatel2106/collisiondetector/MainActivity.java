package com.kevalpatel2106.collisiondetector;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.things.contrib.driver.ultrasonicsensor.DistanceListener;
import com.google.android.things.contrib.driver.ultrasonicsensor.UltrasonicSensorDriver;
import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;

/**
 * Created by Keval on 10-May-17.
 * This activity gets distance of the object from sensor and glow the LED based on the distance.
 *
 * @author Keval {https://github.com/kevalpatel2106}
 */

public class MainActivity extends Activity implements DistanceListener {
    private Gpio mRedPin;
    private Gpio mGreenPin;
    private Gpio mYellowPin;
    private UltrasonicSensorDriver mUltrasonicSensorDriver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PeripheralManagerService service = new PeripheralManagerService();

        mUltrasonicSensorDriver = new UltrasonicSensorDriver(BoardDefaults.getGPIOForTrig(),
                BoardDefaults.getGPIOForEcho(), this);

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

    /**
     * Glow the LED based on the distance.
     * If the distance is < 25cm, Red LED will glow.
     * If the distance is between 25cm and 50cm , Yellow LED will glow.
     * If the distance is > 50cm, Green LED will glow.
     *
     * @param distance distance in cm.
     */
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

    /**
     * Glow red LED.
     *
     * @throws IOException if GPIO is not initialized properly.
     */
    private void setRedLedOn() throws IOException {
        mRedPin.setValue(true);
        mYellowPin.setValue(false);
        mGreenPin.setValue(false);
    }

    /**
     * Glow yellow LED.
     *
     * @throws IOException if GPIO is not initialized properly.
     */
    private void setYellowLedOn() throws IOException {
        mRedPin.setValue(false);
        mYellowPin.setValue(true);
        mGreenPin.setValue(false);
    }

    /**
     * Glow green LED.
     *
     * @throws IOException if GPIO is not initialized properly.
     */
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
    public void onDistanceChange(double distanceInCm) {
        Log.d("Distance", distanceInCm + " cm");
        glowLed(distanceInCm);
    }
}
