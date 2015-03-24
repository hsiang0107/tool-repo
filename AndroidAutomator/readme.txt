====== Tool Condition ======
1. Google account should be added
2. Mobile security & Antivirus app should not been installed on the device
3. Connect to Wi-Fi before start
4. Tool was developed based on Samsung Galaxy Note II



====== Build steps ======
1. Android SDK 1.6 above is required

2. Apache ANT is required

3. Generate build configuration file by below command:
	# \android-sdk\tools>android.bat create uitest-project -n AndroidAutomator -t 1 -p <path to AndroidAutomator root>

4. Build the jar using ant:
	# <path to AndroidAutomator root>\ant build

5. adb push the "<path to AndroidAutomator root>\bin\AndroidAutomator.jar" to the device

6. Run the tool using below command:
	# adb shell uiautomator runtest <path to the jar>/AndroidAutomator.jar -c com.sean.InstallMobileApp