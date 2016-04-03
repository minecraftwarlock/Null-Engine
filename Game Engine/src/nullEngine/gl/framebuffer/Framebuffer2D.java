package nullEngine.gl.framebuffer;

import nullEngine.control.Application;
import nullEngine.gl.model.Quad;
import nullEngine.gl.model.TexturedModel;
import nullEngine.gl.texture.Texture2D;
import nullEngine.util.logs.Logs;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class Framebuffer2D {
	private int frameBufferID;
	private int colorTextureID;
	private int depthTexutreID;
	private int width, height;

	private TexturedModel model;

	private static final ArrayList<Framebuffer2D> framebuffers = new ArrayList<Framebuffer2D>();


	public Framebuffer2D(int width, int height) {
		this.width = width;
		this.height = height;

		colorTextureID = GL11.glGenTextures();
		depthTexutreID = GL11.glGenTextures();
		frameBufferID = GL30.glGenFramebuffers();

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, colorTextureID);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
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

		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);

		model = new TexturedModel(Quad.get(), new Texture2D(colorTextureID));
		framebuffers.add(this);
	}

	public void bind() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBufferID);
		GL11.glViewport(0, 0, width, height);
	}

	public static void unbind() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		GL11.glViewport(0, 0, Application.get().getWidth(), Application.get().getHeight());
	}

	public void render() {
		model.render();
	}

	public void delete() {
		GL30.glDeleteFramebuffers(frameBufferID);
		GL11.glDeleteTextures(colorTextureID);
		GL11.glDeleteTextures(depthTexutreID);
		framebuffers.remove(this);
	}

	public int getFrameBufferID() {
		return frameBufferID;
	}

	public int getColorTextureID() {
		return colorTextureID;
	}

	public int getDepthTexutreID() {
		return depthTexutreID;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public static void preContextChange() {
		for (Framebuffer2D framebuffer : framebuffers)
			GL30.glDeleteFramebuffers(framebuffer.getFrameBufferID());
	}

	public static void contextChanged() {
		for (Framebuffer2D framebuffer : framebuffers)
			framebuffer.reload();
	}

	private void reload() {
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