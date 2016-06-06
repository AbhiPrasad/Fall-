package com.abhijeetp.fall.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.abhijeetp.fall.Fall;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Fall!";
		config.width = 800;
		config.height = 480;
		new LwjglApplication(new Fall(), config);
	}
}
