package nl.codecentric.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import nl.codecentric.game.CodecentricGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "codecentric Game";
        config.width = 800;
        config.height = 480;
		new LwjglApplication(new CodecentricGame(), config);
	}
}
