package nullEngine.graphics.shader.deferred.lighting;

import math.Matrix4f;
import nullEngine.object.component.graphics.light.SpotLight;

/**
 * Deferred spot light shader
 */
public class DeferredSpotLightShader extends DeferredLightShader {

	/**
	 * Singleton instance
	 */
	public static final DeferredSpotLightShader INSTANCE = new DeferredSpotLightShader();

	private int location_lightColor;
	private int location_direction;
	private int location_attenuation;
	private int location_viewMatrix;
	private int location_modelMatrix;

	private DeferredSpotLightShader() {
		super("default/deferred/lighting/deferred-spot");
	}

	/**
	 * Get the uniform locations
	 */
	@Override
	protected void getUniformLocations() {
		super.getUniformLocations();
		location_lightColor = getUniformLocation("lightColor");
		location_direction = getUniformLocation("direction");
		location_modelMatrix = getUniformLocation("modelMatrix");
		location_attenuation = getUniformLocation("attenuation");
		location_viewMatrix = getUniformLocation("viewMatrix");

	}

	/**
	 * Load a light
	 * @param light The light
	 */
	public void loadLight(SpotLight light) {
		loadVec3(location_lightColor, light.getLightColor());
		Matrix4f matrix = light.getObject().getRenderMatrix();
		loadVec3(location_direction, matrix.m02, matrix.m12, matrix.m22);
		loadVec4(location_attenuation, light.getSquared(), light.getLinear(), light.getConstant(), light.getCutoff());
		loadMat4(location_modelMatrix, matrix);
	}

	/**
	 * Load the view matrix
	 * @param viewMarix The view matrix
	 */
	public void loadViewMatrix(Matrix4f viewMarix) {
		loadMat4(location_viewMatrix, viewMarix);
	}
}
