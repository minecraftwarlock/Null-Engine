package nullEngine.gl.shader.deferred;

public class DeferredTerrainShader extends DeferredShader {

	public static final DeferredTerrainShader INSTANCE = new DeferredTerrainShader();

	private DeferredTerrainShader() {
		super("default/deferred/deferred-terrain", "default/deferred/deferred-terrain");
	}

	@Override
	protected void getUniformLocations() {
		super.getUniformLocations();
		addUserVector("reflectivity");
		addUserVector("shineDamper");
		addUserFloat("tileCount");
		addUserTexture("blend");
		addUserTexture("aTexture");
		addUserTexture("rTexture");
		addUserTexture("gTexture");
		addUserTexture("bTexture");
		addUserTexture("height");
		addUserTexture("normals");
		addUserFloat("size");
	}
}
