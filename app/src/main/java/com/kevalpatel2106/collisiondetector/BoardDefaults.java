/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kevalpatel2106.collisiondetector;

import android.os.Build;

import com.google.android.things.pio.PeripheralManagerService;

import java.util.List;

@SuppressWarnings("WeakerAccess")
public class BoardDefaults {
    private static final String DEVICE_EDISON_ARDUINO = "edison_arduino";
    private static final String DEVICE_EDISON = "edison";
    private static final String DEVICE_JOULE = "joule";
    private static final String DEVICE_RPI3 = "rpi3";
    private static final String DEVICE_PICO = "imx6ul_pico";
    private static final String DEVICE_VVDN = "imx6ul_iopb";
    private static String sBoardVariant = "";

    /**
     * Return the GPIO pin for the trigger pin in HC-SR04 sensor.
     * <p>
     * See: https://pinout.xyz/pinout/
     */
    public static String getGPIOForTrig() {
        switch (getBoardVariant()) {
            case DEVICE_RPI3:
                return "BCM5";
            default:
                throw new IllegalStateException("Unknown Build.DEVICE " + Build.DEVICE);
        }
    }

    /**
     * Return the GPIO pin for the echo pin in HC-SR04 sensor.
     * <p>
     * See: https://pinout.xyz/pinout/
     */
    public static String getGPIOForEcho() {
        switch (getBoardVariant()) {
            case DEVICE_RPI3:
                return "BCM6";
            default:
                throw new IllegalStateException("Unknown Build.DEVICE " + Build.DEVICE);
        }
    }

    /**
     * Return the GPIO pin for the Red LED.
     * <p>
     * See: https://pinout.xyz/pinout/
     */
    public static String getGPIOForRedLED() {
        switch (getBoardVariant()) {
            case DEVICE_RPI3:
                return "BCM27";
            default:
                throw new IllegalStateException("Unknown Build.DEVICE " + Build.DEVICE);
        }
    }

    /**
     * Return the GPIO pin for the yellow LED.
     * <p>
     * See: https://pinout.xyz/pinout/
     */
    public static String getGPIOForYellowLED() {
        switch (getBoardVariant()) {
            case DEVICE_RPI3:
                return "BCM17";
            default:
                throw new IllegalStateException("Unknown Build.DEVICE " + Build.DEVICE);
        }
    }

    /**
     * Return the GPIO pin for the green LED.
     * <p>
     * See: https://pinout.xyz/pinout/
     */
    public static String getGPIOForGreenLED() {
        switch (getBoardVariant()) {
            case DEVICE_RPI3:
                return "BCM22";
            default:
                throw new IllegalStateException("Unknown Build.DEVICE " + Build.DEVICE);
        }
    }

    /**
     * Get board variant.
     *
     * @return Name of the board.
     */
    private static String getBoardVariant() {
        if (!sBoardVariant.isEmpty()) {
            return sBoardVariant;
        }
        sBoardVariant = Build.DEVICE;
        // For the edison check the pin prefix
        // to always return Edison Breakout pin name when applicable.
        if (sBoardVariant.equals(DEVICE_EDISON)) {
            PeripheralManagerService pioService = new PeripheralManagerService();
            List<String> gpioList = pioService.getGpioList();
            if (gpioList.size() != 0) {
                String pin = gpioList.get(0);
                if (pin.startsWith("IO")) {
                    sBoardVariant = DEVICE_EDISON_ARDUINO;
                }
            }
        }
        return sBoardVariant;
    }
}
