Ultrasonic Ranging Module driver for Android Things
===================================================

This driver is to get proximity of the object using HC-SR04.

How to use the driver
---------------------

### Gradle dependency

To use the `ultrasonic-sensor` driver, simply add the line below to your project's `build.gradle`,
where `<version>` matches the last version of the driver available on [jcenter][jcenter].

```
dependencies {
    compile 'com.google.android.things.contrib:ultrasonic-sensor:<version>'
}
```
### Sample usage

Use the `UltrasonicSensorDriver` class to easily get the distance using HC-SR04. 
```java
import com.google.android.things.contrib.driver.tm1637.NumericDisplay;

// Access the display:

NumericDisplay mDisplay;

try {
    mDisplay = new NumericDisplay(dataGpioPinName, clockGpioPinName);
    mDisplay.setEnabled(true);
} catch (IOException e) {
    // couldn't configure the display...
}

// Display a number or a time:

try {
    // displays "42"
    mDisplay.display(42);

    // displays "12:00"
    mDisplay.setColonEnabled(true);
    mDisplay.display("1200");
} catch (IOException e) {
    // error setting display
}

// Close the display when finished:

try {
    mDisplay.close();
} catch (IOException e) {
    // error closing display
}
```



License
-------
Copyright 2016 Google Inc.

Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.