package nullEngine.graphics.framebuffer;

import nullEngine.control.Application;
import nullEngine.graphics.model.Quad;
import nullEngine.util.logs.Logs;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Framebuffer with a color texture and depth texture
 */
public class Framebuffer3D {
	private int frameBufferID;
	private int colorTextureID;
	private int depthTexutreID;
	private int width, height;

	private static final List<Framebuffer3D> framebuffers = new ArrayList<>();


	/**
	 * Create a new framebuffer
	 * @param width The width
	 * @param height The height
	 */
	public Framebuffer3D(int width, int height) {
		this.width = width;
		this.height = height;

		colorTextureID = GL11.glGenTextures();
		depthTexutreID = GL11.glGenTextures();
		frameBufferID = GL30.glGenFramebuffers();

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, colorTextureID);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthTexutreID);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_DEPTH_COMPONENT32F, width, height, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (ByteBuffer) null);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBufferID);
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, colorTextureID, 0);
		GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, depthTexutreID, 0);

		if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE)
			throw new RuntimeException("Frame buffer is not complete");

		GL20.glDrawBuffers(GL30.GL_COLOR_ATTACHMENT0);

		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);

		framebuffers.add(this);
	}

	/**
	 * Bind this framebuffer
	 */
	public void bind() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBufferID);
		GL11.glViewport(0, 0, width, height);
	}

	/**
	 * Unbind the framebuffer
	 */
	public static void unbind() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		GL11.glViewport(0, 0, Application.get().getWidth(), Application.get().getHeight());
	}

	/**
	 * Render this framebuffer
	 */
	public void render() {
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, colorTextureID);
		Quad.get().render();
	}

	/**
	 * Delete this framebuffer
	 */
	public void delete() {
		GL30.glDeleteFramebuffers(frameBufferID);
		GL11.glDeleteTextures(colorTextureID);
		GL11.glDeleteTextures(depthTexutreID);
		framebuffers.remove(this);
	}

	/**
	 * Get the framebuffer id
	 * @return The framebuffer id
	 */
	public int getFrameBufferID() {
		return frameBufferID;
	}

	/**
	 * Get the color buffer
	 * @return The color texture id
	 */
	public int getColorTextureID() {
		return colorTextureID;
	}

	/**
	 * Get the depth buffer
	 * @return The depth texture id
	 */
	public int getDepthTexutreID() {
		return depthTexutreID;
	}

	/**
	 * Get the width
	 * @return The width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Get the height
	 * @return The height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Delete the framebuffers
	 */
	public static void preContextChange() {
		for (Framebuffer3D framebuffer : framebuffers)
			GL30.glDeleteFramebuffers(framebuffer.getFrameBufferID());
	}

	/**
	 * Recreate the frmaebuffers
	 */
	public static void contextChanged() {
		for (Framebuffer3D framebuffer : framebuffers)
			framebuffer.recreate();
	}

	private void recreate() {
		frameBufferID = GL30.glGenFramebuffers();

		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBufferID);
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, colorTextureID, 0);
		GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, depthTexutreID, 0);

		if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
			Logs.e("Framebuffer error: " + GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER));
			throw new RuntimeException("Frame buffer is not complete");
		}

		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	}
}
