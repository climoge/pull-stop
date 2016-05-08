package com.pullstop.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.pullstop.game.pullStop;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Pull & Stop";
		config.width = 1280;
		config.height = 720;
		new LwjglApplication(new pullStop(), config);
	}
}
