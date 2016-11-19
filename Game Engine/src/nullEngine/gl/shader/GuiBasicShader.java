package nullEngine.gl.shader;

/**
 * Basic shader for the GUI
 */
public class GuiBasicShader extends GuiShader {
	/**
	 * Singleton instance
	 */
	public static final GuiBasicShader INSTANCE = new GuiBasicShader();

	private GuiBasicShader() {
		super("default/gui");
	}
}
