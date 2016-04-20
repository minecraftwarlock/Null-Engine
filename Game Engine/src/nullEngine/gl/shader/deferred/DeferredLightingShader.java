package nullEngine.gl.shader.deferred;

import nullEngine.gl.shader.Shader;

public class DeferredLightingShader extends Shader {
	private int location_colors;
	private int location_positions;
	private int location_normals;
	private int location_specular;

	public DeferredLightingShader(String vertex, String fragment) {
		super(vertex, fragment);
	}

	@Override
	protected void bindAttributes() {
		bindAttribute(0, "inPosition");
		bindAttribute(1, "inTexCoord");
	}

	@Override
	protected void getUniformLocations() {
		location_colors = getUniformLocation("colors");
		location_positions = getUniformLocation("positions");
		location_normals = getUniformLocation("normals");
		location_specular = getUniformLocation("specular");
		setSystemTextures(4);
	}

	@Override
	public void bind() {
		super.bind();
		loadInt(location_colors, 0);
		loadInt(location_positions, 1);
		loadInt(location_normals, 2);
		loadInt(location_specular, 3);
	}
}