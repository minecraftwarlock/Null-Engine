package nullEngine.graphics.base;

import math.Vector4f;
import nullEngine.graphics.base.shader.deferred.DeferredBasicShader;
import nullEngine.graphics.base.shader.deferred.DeferredShader;
import nullEngine.graphics.base.texture.Texture2D;

import java.util.HashMap;

public class Material implements Cloneable {
	private HashMap<String, Float> floats;
	private HashMap<String, Vector4f> vectors;
	private HashMap<String, Texture2D> textures;

	private boolean usingLighting = true;
	private boolean alwaysRender = false;

	private DeferredShader shader = DeferredBasicShader.INSTANCE;

	private static HashMap<String, Float> defaultFloats = new HashMap<String, Float>();
	private static HashMap<String, Vector4f> defaultVectors = new HashMap<String, Vector4f>();
	private static HashMap<String, Texture2D> defaultTextures = new HashMap<String, Texture2D>();

	private Material(Object a) {}

	public Material() {
		floats = (HashMap<String, Float>) defaultFloats.clone();
		vectors = (HashMap<String, Vector4f>) defaultVectors.clone();
		textures = (HashMap<String, Texture2D>) defaultTextures.clone();
	}


	public void setFloat(String key, float f) {
		floats.put(key, f);
	}

	public void setVector(String key, Vector4f v) {
		vectors.put(key, v);
	}

	public void setTexture(String key, Texture2D t) {
		textures.put(key, t);
	}

	public boolean hasFloat(String key) {
		return floats.containsKey(key);
	}

	public boolean hasVector(String key) {
		return vectors.containsKey(key);
	}

	public boolean hasTexture(String key) {
		return textures.containsKey(key);
	}

	public float getFloat(String key) {
		return floats.get(key);
	}

	public Vector4f getVector(String key) {
		return vectors.get(key);
	}

	public Texture2D getTexture(String key) {
		return textures.get(key);
	}

	public void setReflectivity(float reflectivity) {
		setFloat("reflectivity", reflectivity);
	}

	public void setShineDamper(float shineDamper) {
		setFloat("shineDamper", shineDamper);
	}

	public void setDiffuse(Texture2D diffuse) {
		setTexture("diffuse", diffuse);
	}

	public static void setDefaultFloat(String key, float f) {
		defaultFloats.put(key, f);
	}

	public static void setDefaultVector(String key, Vector4f v) {
		defaultVectors.put(key, v);
	}

	public static void setDefaultTexture(String key, Texture2D texture) {
		defaultTextures.put(key, texture);
	}

	public boolean isUsingLighting() {
		return usingLighting;
	}

	public void setUsingLighting(boolean usingLighting) {
		this.usingLighting = usingLighting;
	}

	public DeferredShader getShader() {
		return shader;
	}

	public void setShader(DeferredShader shader) {
		this.shader = shader;
	}

	public boolean isAlwaysRender() {
		return alwaysRender;
	}

	public void setAlwaysRender(boolean alwaysRender) {
		this.alwaysRender = alwaysRender;
	}

	@Override
	public Material clone() {
		Material clone = new Material(null);
		clone.floats = (HashMap<String, Float>) floats.clone();
		clone.vectors = (HashMap<String, Vector4f>) vectors.clone();
		clone.textures = (HashMap<String, Texture2D>) textures.clone();
		clone.usingLighting = usingLighting;
		clone.alwaysRender = alwaysRender;
		clone.shader = shader;
		return clone;
	}
}