package nullEngine;

import nullEngine.exception.InitializationException;
import nullEngine.graphics.base.Window;
import nullEngine.loading.ResourceLoader;
import nullEngine.util.logs.Logs;
import org.lwjgl.system.Platform;

import java.io.File;

public class NullEngine {

	public static final String NATIVE_DIR = "lib/bin";

	public static void init() throws InitializationException {
		Logs.dummy();
		Logs.init();
		initNatives();
		Window.init();
		ResourceLoader.init();
	}

	public static void cleanup() {
		Window.destroy();
		Logs.finish();
	}

	private static void initNatives() throws InitializationException {
		Platform.get();
		System.setProperty("org.lwjgl.librarypath", new File(NATIVE_DIR).getAbsolutePath());
	}
}
