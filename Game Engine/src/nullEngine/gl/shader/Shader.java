package nullEngine.gl.shader;

import math.Matrix4f;
import math.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import nullEngine.exception.ShaderException;
import nullEngine.loading.ResourceLoader;
import nullEngine.util.logs.Logs;

import java.io.FileNotFoundException;
import java.nio.FloatBuffer;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Shader {

	private static final Pattern INCLUDE_PATTERN = Pattern.compile("\\s*#include\\s*\"(.*?)\"\\s*");

	private int program;
	private int vertexShader;
	private int fragmentShader;


	private FloatBuffer matrixbuffer = BufferUtils.createFloatBuffer(16);

	public Shader(String vertex, String fragment) {
		vertexShader = loadShader(vertex + ".vert", GL20.GL_VERTEX_SHADER);
		fragmentShader = loadShader(fragment + ".frag", GL20.GL_FRAGMENT_SHADER);

		program = GL20.glCreateProgram();
		GL20.glAttachShader(program, vertexShader);
		GL20.glAttachShader(program, fragmentShader);

		bindAttributes();

		GL20.glLinkProgram(program);
		if (GL20.glGetProgrami(program, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
			Logs.f("Failed to link " + vertex + " and " + fragment, new ShaderException(GL20.glGetProgramInfoLog(program, 1024)));
		}

		GL20.glValidateProgram(program);
		if (GL20.glGetProgrami(program, GL20.GL_VALIDATE_STATUS) == GL11.GL_FALSE) {
			Logs.f("Failed to validate " + vertex + " and " + fragment, new ShaderException(GL20.glGetProgramInfoLog(program, 1024)));
		}
		getUniformLocations();
	}

	protected abstract void bindAttributes();

	protected abstract void getUniformLocations();

	protected void bindAttribute(int attribute, String name) {
		GL20.glBindAttribLocation(program, attribute, name);

	}

	protected int getUniformLocation(String name) {
		return GL20.glGetUniformLocation(program, name);
	}

	protected void loadFloat(int location, float value) {
		GL20.glUniform1f(location, value);
	}

	public void loadVec2(int location, float x, float y) {
		GL20.glUniform2f(location, x, y);
	}

	public void loadVec3(int location, Vector4f vec) {
		GL20.glUniform3f(location, vec.x, vec.y, vec.z);
	}

	public void loadVec4(int location, Vector4f vec, float w) {
		GL20.glUniform4f(location, vec.x, vec.y, vec.z, w);
	}

	public void loadInt(int location, int value) {
		GL20.glUniform1i(location, value);
	}

	public void loadMat4(int location, Matrix4f mat) {
		mat.toFloatBuffer(matrixbuffer);
		GL20.glUniformMatrix4fv(location, true, matrixbuffer);
	}

	protected void loadBoolean(int location, boolean value) {
		loadFloat(location, value ? 1 : 0);
	}

	public void bind() {
		GL20.glUseProgram(program);
	}

	public static void unbind() {
		GL20.glUseProgram(0);
	}

	public void delete() {
		unbind();

		GL20.glDetachShader(program, vertexShader);
		GL20.glDetachShader(program, fragmentShader);

		GL20.glDeleteShader(vertexShader);
		GL20.glDeleteShader(fragmentShader);

		GL20.glDeleteProgram(program);
	}

	private static String loadShaderSource(String name) {
		try {
			StringBuilder src = new StringBuilder();

			Scanner in = new Scanner(ResourceLoader.getResource("res/shaders/" + name));

			String s;
			Matcher m;

			while (in.hasNextLine()) {
				s = in.nextLine();

				m = INCLUDE_PATTERN.matcher(s);

				if (m.find()) {
					Logs.d("including " + m.group(1) + " in " + name);
					s = loadShaderSource(m.group(1));
				}

				src.append(s);
				src.append("\n");
			}
			in.close();

			return src.toString();
		} catch (FileNotFoundException e) {
			Logs.f(e);
			return null;
		}
	}

	private static int loadShader(String name, int type) {
		String src = loadShaderSource(name);

		int shader = GL20.glCreateShader(type);
		GL20.glShaderSource(shader, src);
		GL20.glCompileShader(shader);
		if (GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			Logs.f("Failed to compile shader " + name, new ShaderException(GL20.glGetShaderInfoLog(shader, 1024)));
		}

		return shader;
	}
}