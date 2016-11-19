package sandbox;

import math.Quaternion;
import math.Vector4f;
import nullEngine.NullEngine;
import nullEngine.control.Application;
import nullEngine.control.LayerDeferred;
import nullEngine.control.LayerGUI;
import nullEngine.control.State;
import nullEngine.gl.Color;
import nullEngine.gl.Material;
import nullEngine.gl.font.Font;
import nullEngine.gl.model.Model;
import nullEngine.gl.postfx.BrightFilterBloomPostFX;
import nullEngine.gl.postfx.ContrastPostFX;
import nullEngine.gl.postfx.FogPostFX;
import nullEngine.gl.postfx.PostFX;
import nullEngine.gl.renderer.DeferredRenderer;
import nullEngine.gl.renderer.Renderer;
import nullEngine.gl.shader.deferred.DeferredTerrainShader;
import nullEngine.gl.shader.mousePick.MousePickTerrainShader;
import nullEngine.gl.texture.Texture2D;
import nullEngine.gl.texture.TextureGenerator;
import nullEngine.input.EventAdapter;
import nullEngine.input.Input;
import nullEngine.input.KeyEvent;
import nullEngine.input.MouseEvent;
import nullEngine.input.MousePickInfo;
import nullEngine.input.NotificationEvent;
import nullEngine.loading.Loader;
import nullEngine.object.GameComponent;
import nullEngine.object.GameObject;
import nullEngine.object.component.FlyCam;
import nullEngine.object.component.ModelComponent;
import nullEngine.object.component.gui.GuiText;
import nullEngine.object.component.light.DirectionalLight;
import nullEngine.object.wrapper.GeoclipmapTerrain;
import nullEngine.object.wrapper.HeightMap;
import nullEngine.util.logs.Logs;
import org.lwjgl.opengl.GL11;
import util.BitFieldInt;

public class Main {

	public static void main(String[] args) {
		try {
			NullEngine.init();
			Logs.setDebug(true);
			final Application application = new Application(1280, 720, false, "Sandbox");
			application.getWindow().setVsync(true);
			application.bind();
			
			State state = new State();
			int TEST_STATE = application.addState(state);
			application.setState(TEST_STATE);

			final FlyCam camera = new FlyCam();

			final LayerDeferred world = new LayerDeferred(camera, (float) Math.toRadians(90f), 0.1f, 300f);
			LayerGUI gui = new LayerGUI();
			state.addLayer(gui);
			state.addLayer(world);

			state.addListener(new EventAdapter() {
				@Override
				public boolean keyPressed(KeyEvent event) {
					if (event.key == Input.KEY_T) {
						((DeferredRenderer) world.getRenderer()).setWireframe(!((DeferredRenderer) world.getRenderer()).isWireframe());
						return true;
					} else if (event.key == Input.KEY_F1) {
						application.screenshot();
						return true;
					} else if (event.key == Input.KEY_F11) {
						application.setFullscreen(!application.isFullscreen(), application.isFullscreen() ? null : application.getBestFullscreenVideoMode());
					}
					return false;
				}
			});

			Loader loader = application.getLoader();
			loader.setAnisotropyEnabled(true);
			loader.setAnisotropyAmount(8);
			loader.setTextureLodBias(0);

			Material.DEFAULT_MATERIAL.setFloat("lightingAmount", 1);

			Font font = loader.loadFont("default/testsdf", 14);

			GuiText text = new GuiText(-1, -0.85f, 0.1f, "FPS: 0\nUPS:0\n0.0/0.0MB", font) {
				private double totalDelta = 1;

				@Override
				public void update(double delta, GameObject object) {
					totalDelta += delta;
					if (totalDelta > 0.25) {
						float maxMemory = Runtime.getRuntime().maxMemory() / 1048576f;
						float totalMemory = Runtime.getRuntime().totalMemory() / 1048576f;
						float freeMemory = Runtime.getRuntime().freeMemory() / 1048576f;
						setText(String.format("FPS: %d\nUPS: %d\n%.1f/%.1fMB",
								Math.round(1d / application.getLastFrameTime()),
								Math.round(1d / application.getLastUpdateTime()),
								totalMemory - freeMemory, maxMemory));
						totalDelta -= 0.25;
					}
				}
			};
			text.setColor(Color.WHITE);
			text.setBorderColor(Color.BLACK);
			text.setThickness(0.25f, 0.35f);
			text.setBorderThickness(0.4f, 0.4f);
			gui.getRoot().addComponent(text);
			Logs.d(GL11.glGetString(GL11.GL_RENDERER));

			final Model dragonModel = loader.loadModel("default/dragon");

			Texture2D gold = TextureGenerator.genColored(218, 165, 32, 255);

			final Material dragonMaterial = new Material();
			dragonMaterial.setTexture("diffuse", gold);
			dragonMaterial.setFloat("shineDamper", 32);
			dragonMaterial.setFloat("reflectivity", 1);

			final DeferredRenderer renderer = ((DeferredRenderer) world.getRenderer());
			FogPostFX fog = new FogPostFX(renderer.getLightOutput(), renderer.getPositionOutput());
			fog.setSkyColor(new Vector4f(0.529f, 0.808f, 0.922f));
			fog.setDensity(0.004f);
			fog.setGradient(4f);
			final PostFX bloom = new ContrastPostFX(new BrightFilterBloomPostFX(fog, 0.2f), 0.3f);
			final PostFX noBloom = new ContrastPostFX(fog, 0.3f);
			renderer.setPostFX(bloom);
			world.setAmbientColor(new Vector4f(0.2f, 0.2f, 0.2f));

			GameObject dragon = new GameObject();
			final GameObject cameraObject = new GameObject();
			GameObject terrainObject = new GameObject();
			world.getRoot().addChild(cameraObject);
			world.getRoot().addChild(dragon);
			world.getRoot().addChild(terrainObject);

			cameraObject.getTransform().setPos(new Vector4f(0, 1.5f, -5));
			cameraObject.getTransform().setRot(new Quaternion((float) Math.PI, Vector4f.UP));

//			GameObject lightObject = new GameObject();
//			lightObject.addComponent(new SpotLight(Color.WHITE, new Vector4f(0, -1, 0), 0.001f, 0, 0.6f, (float) (Math.PI / 4)) {
//				@Override
//				public void render(Renderer renderer, GameObject object, BitFieldInt flags) {
//					setDirection(cameraObject.getTransform().getRot().getForward(getDirection()).mul(-1));
//					object.getTransform().setPos(cameraObject.getTransform().getWorldPos());
//					super.render(renderer, object, flags);
//				}
//			});
//			lightObject.getTransform().setPos(new Vector4f(8, 50, -14));
//			world.getRoot().addChild(lightObject);
//			world.getRoot().addComponent(new DirectionalLight(new Vector4f(1, 1, 1), new Vector4f(-5, 0, 20, 0)));
			world.getRoot().addComponent(new DirectionalLight(new Vector4f(1, 1, 1), new Vector4f(0, -1, 0, 0)));

			Material terrainMaterial = new Material();
			terrainMaterial.setShader(DeferredTerrainShader.INSTANCE, Material.DEFERRED_SHADER_INDEX);
			terrainMaterial.setShader(MousePickTerrainShader.INSTANCE, Material.MOUSE_PICKING_SHADER_INDEX);
			terrainMaterial.setTexture("aTexture", loader.loadTexture("default/grass"));
			terrainMaterial.setTexture("rTexture", loader.loadTexture("default/dirt"));
			terrainMaterial.setTexture("gTexture", loader.loadTexture("default/flowers"));
			terrainMaterial.setTexture("bTexture", loader.loadTexture("default/path"));
			terrainMaterial.setTexture("blend", loader.loadTexture("default/blend"));
			terrainMaterial.setVector("reflectivity", new Vector4f(0, 0, 0.5f, 0));
			terrainMaterial.setVector("shineDamper", new Vector4f(1, 1, 8, 1));
			terrainMaterial.setVector("lightingAmount", new Vector4f(1, 1, 1, 1));
			terrainMaterial.setFloat("tileCount", 220);
			HeightMap heightMap = loader.generateHeightMap("default/heightmap", 80);
			ModelComponent.MOUSE_PICKING_ENABLED_DEFAULT = true; //FIXME This is terrible
			GeoclipmapTerrain terrain = new GeoclipmapTerrain(terrainMaterial, heightMap, 600, 128, 6, loader, cameraObject);
			ModelComponent.MOUSE_PICKING_ENABLED_DEFAULT = false;
			terrainObject.addChild(terrain);


			cameraObject.addComponent(camera);
			application.setCursorEnabled(false);
			cameraObject.addComponent(new GameComponent() {
				boolean cameraEnabled = true;

				@Override
				public void render(Renderer renderer, GameObject object, BitFieldInt flags) {
				}

				@Override
				public void update(double delta, GameObject object) {
				}

				@Override
				public boolean keyPressed(KeyEvent event) {
					if (event.key == Input.KEY_LEFT_ALT) {
						cameraEnabled = !cameraEnabled;
						application.setCursorEnabled(!cameraEnabled);
						camera.setCanRotate(cameraEnabled);
						camera.setCanMove(cameraEnabled);
						return true;

					}
					return false;
				}
			});
			dragon.getTransform().setPos(new Vector4f(8, terrain.getTerrainHeight(8, -14), -14));
			dragon.getTransform().setRot(new Quaternion((float) Math.PI / -2, Vector4f.UP));

			dragon.addComponent(new ModelComponent(dragonMaterial, dragonModel) {
				private boolean bloomEnabled = true;
				private boolean render = true;

				@Override
				public boolean mouseMoved(MouseEvent event) {
					if (application.getCursorEnabled()) {
						renderer.mousePick(Input.getMouseX(), application.getHeight() - 1 - Input.getMouseY(), new MousePickInfo(), this);
					}
					return false;
				}

				@Override
				public void notified(NotificationEvent event) {
					if (event.getNotificationType() == NotificationEvent.NOTIFICATION_MOUSE_PICK_COMPLETE) {
						MousePickInfo info = (MousePickInfo) event.getData();
						if (info.model != null) {
							render = true;
							getObject().getTransform().setPos(info.worldPosition);
						} else {
							render = false;
						}
					}
				}

				@Override
				public void update(double delta, GameObject object) {
//					object.getTransform().increaseRot(new Quaternion(delta / 2, new Vector4f(0, 1, 0)));

				}

				@Override
				public void render(Renderer renderer, GameObject object, BitFieldInt flags) {
					if (render) {
						super.render(renderer, object, flags);
					}

					if (application.getCursorEnabled()) {
						world.mousePickNextFrame();
					}
				}

			@Override
			public boolean keyPressed (KeyEvent event){
				if (Input.getKeyNumber(event.key) >= 0) {
					setLodBias(Input.getKeyNumber(event.key));
					return true;
				} else if (event.key == Input.KEY_B) {
					renderer.setPostFX((bloomEnabled = !bloomEnabled) ? bloom : noBloom);
				}
				return false;
			}
		});

		Throwable e = application.start();
		if (e != null) {
			Logs.e("Caught exception");
			e.printStackTrace();
			application.carefulDestroy();
		} else {
			application.destroy();
		}

		NullEngine.cleanup();
	} catch(
	Exception e)

	{
		Logs.e("Something went horribly wrong!", e);
		Logs.finish();
	}
}
}
