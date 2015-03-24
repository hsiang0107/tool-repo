package com.sean;

import com.android.uiautomator.testrunner.UiAutomatorTestCase;
import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiScrollable;
import com.android.uiautomator.core.UiSelector;

public class InstallMobileApp extends UiAutomatorTestCase{
	public void testDemo() throws UiObjectNotFoundException {
		// Press home button
		getUiDevice().pressHome();
		
		//Open app menu
		UiObject appmenu = new UiObject(new UiSelector().text("Apps").className("android.widget.TextView"));
		sleep(1000);
		assertTrue("App menu not found", appmenu.exists());
		appmenu.clickAndWaitForNewWindow();
		
		//Confirm the apps tap
		UiObject appstap = new UiObject(new UiSelector().text("Apps").className("android.widget.TextView"));
		appstap.clickAndWaitForNewWindow();
		
		// Find scroll object
		UiScrollable appViews = new UiScrollable(new UiSelector().scrollable(true));
		appViews.setAsHorizontalList();
		appViews.scrollToBeginning(5);
		
		// Find Play Store and launch it
		UiObject playstore=appViews.getChildByText(new UiSelector().className("android.widget.TextView"), "Play Store");
		assertTrue("Play Store doesn't exist", playstore.exists());
		playstore.clickAndWaitForNewWindow();
		
		// Get search field
		UiObject SearchField = new UiObject(new UiSelector().resourceId("com.android.vending:id/search_box_idle_text"));
		
		//search for Mobile Security & Antivirus
		SearchField.setText("Mobile Security & Antivirus");
		getUiDevice().pressEnter();
		
		//Select mobile security & antivirus
		UiObject app = new UiObject(
		new UiSelector().text("Mobile Security & Antivirus").resourceId("com.android.vending:id/li_title"));
		sleep(3000);
		assertTrue("mobile security & antivirus app not found", app.exists());
		app.clickAndWaitForNewWindow();
		
		//Start to install
		UiObject install = new UiObject(
		new UiSelector().text("INSTALL").resourceId("com.android.vending:id/buy_button"));
		install.clickAndWaitForNewWindow();
		
		//Accept to install
		UiObject accept = new UiObject(
		new UiSelector().text("ACCEPT").resourceId("com.android.vending:id/continue_button_label"));
		accept.clickAndWaitForNewWindow();
		
		sleep(20000);//Wait 20 seconds until installation complete
		
		//Open the app
		UiObject open = new UiObject(
		new UiSelector().text("OPEN").resourceId("com.android.vending:id/launch_button"));
		open.clickAndWaitForNewWindow();
		
		UiObject appvalidation=new UiObject(new UiSelector().packageName("com.avast.android.mobilesecurity"));
		sleep(3000);
		assertTrue("mobile security & antivirus app launched fail", appvalidation.exists());
		
	}

}
