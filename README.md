# Login App

This app requires users to meet specific conditions to successfully log in. The conditions are:
1. The user must enter the current percentage of the device's battery.
2. The device must be facing north.
3. The device must be connected to Wi-Fi.
4. The device must be connected to Bluetooth.

If all these conditions are met, a successful login is indicated by a Toast message. If any condition fails, a Toast message indicates which condition is not met.

## Features
- Login based on the battery percentage.
- Device orientation check (must face north).
- Wi-Fi connection verification.
- Bluetooth connection verification.
- Real-time feedback via Toast messages for each condition.

## Requirements
- Android device
- Android SDK
- Bluetooth enabled
- Wi-Fi enabled
- Device sensor for orientation
