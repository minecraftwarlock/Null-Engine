package sandbox;

import math.Quaternion;
import math.Vector4f;
import nullEngine.NullEngine;
import nullEngine.control.Application;
import nullEngine.control.layer.LayerDeferred;
import nullEngine.control.layer.LayerGUI;
import nullEngine.control.State;
import nullEngine.control.physics.PhysicsEngine;
import nullEngine.graphics.Color;
import nullEngine.graphics.Material;
import nullEngine.graphics.font.Font;
import nullEngine.graphics.model.Model;
import nullEngine.graphics.postfx.BrightFilterBloomPostFX;
import nullEngine.graphics.postfx.ContrastPostFX;
import nullEngine.graphics.postfx.FogPostFX;
import nullEngine.graphics.postfx.PostFXOutput;
import nullEngine.graphics.renderer.DeferredRenderer;
import nullEngine.graphics.shader.deferred.DeferredTerrainShader;
import nullEngine.graphics.shader.mousePick.MousePickTerrainShader;
import nullEngine.graphics.texture.Texture2D;
import nullEngine.graphics.texture.TextureGenerator;
import nullEngine.input.EventAdapter;
import nullEngine.input.Input;
import nullEngine.input.KeyEvent;
import nullEngine.input.MouseEvent;
import nullEngine.input.MousePickInfo;
import nullEngine.input.NotificationEvent;
import nullEngine.loading.Loader;
import nullEngine.object.GameComponent;
import nullEngine.object.GameObject;
import nullEngine.object.component.graphics.camera.FlyCam;
import nullEngine.object.component.graphics.ModelComponent;
import nullEngine.object.component.graphics.gui.AnchorPos;
import nullEngine.object.component.graphics.gui.GuiDebugSlider;
import nullEngine.object.component.graphics.gui.GuiText;
import nullEngine.object.component.graphics.light.DirectionalLight;
import nullEngine.object.wrapper.GeoclipmapTerrain;
import nullEngine.object.wrapper.HeightMap;
import nullEngine.util.logs.Logs;

public class Main {

	public static void main(String[] args) {
		try {
			NullEngine.init();   // Initialize the engine
			Logs.setDebug(true); // Set the logs to output debug info

			final Application application = new Application(1280, 720, false, "Sandbox"); // Create the application
			application.getWindow().setVsync(true);                                       // Set up vsync
			application.bind();                                                           // Make the application current

			State state = new State();                    // Create the state
			int TEST_STATE = application.addState(state); // Add it to the engine
			application.setState(TEST_STATE);             // Enter the state

			final FlyCam camera = new FlyCam(); // Create the camera

			final LayerDeferred world = new LayerDeferred(new PhysicsEngine(), camera, (float) Math.toRadians(90f), 0.1f, 300f, true); // Create the layer for the world with HDR enabled
			LayerGUI debug = new LayerGUI();                                                                  // Create the layer for the debug test
			debug.setEnabled(false);
			state.addLayer(debug);                                                                            // Add the GUI first so it is on top
			state.addLayer(world);

			final DeferredRenderer renderer = ((DeferredRenderer) world.getRenderer()); // Get the renderer
			renderer.setExposureTime(1.5f);                                             // Set HDR exposure time to 0.7

			Loader loader = application.getLoader(); // Get the loader

			loader.setAnisotropyEnabled(true);       // Set up anisotropic filtering
			loader.setAnisotropyAmount(8);

			Font font = loader.loadFont("default/testsdf", 14); // Load the font

			GuiText text = new GuiText(debug.BOTTOM_LEFT, AnchorPos.BOTTOM_LEFT, 3, "FPS: 0\nUPS:0\n0.0/0.0MB", font) { // Create gui text with custom update code
				private double totalDelta = 1;

				@Override
				public void update(GameObject object, double delta) {
					totalDelta += delta; // Increase timer
					if (totalDelta > 0.25) { // Update text 4 times per second
						float maxMemory = Runtime.getRuntime().maxMemory() / 1048576f;     // Get allocated memory
						float totalMemory = Runtime.getRuntime().totalMemory() / 1048576f; // Get memory in use
						float freeMemory = Runtime.getRuntime().freeMemory() / 1048576f;   // Get free memory

						setText(String.format("FPS: %d\nUPS: %d\n%.1f/%.1fMB",
								Math.round(1d / application.getLastFrameTime()),  // Get frame rate
								Math.round(1d / application.getLastUpdateTime()), // Get update rate
								totalMemory - freeMemory, maxMemory));            // Memory info
						totalDelta -= 0.25; // Reset timer
					}
				}
			};

			GuiText sliderLabel = new GuiText(debug.TOP_LEFT, AnchorPos.TOP_LEFT, 3, "Speed", font);
			GuiDebugSlider slider = new GuiDebugSlider(sliderLabel.BOTTOM, AnchorPos.TOP, camera::setSpeed, 1f, 20f, camera.getSpeed(), font);

			text.setTextColor(Color.WHITE);          // Set text color
			text.setBorderColor(Color.BLACK);    // Set text border
			text.setThickness(0.25f, 0.35f);     // Set up font thickness
			text.setBorderThickness(0.4f, 0.4f); // Set up font border thickness

			slider.setTextColor(Color.WHITE);          // Set text color
			slider.setBorderColor(Color.BLACK);    // Set text border
			slider.setThickness(0.25f, 0.35f);     // Set up font thickness
			sliderLabel.setBorderThickness(0.4f, 0.4f); // Set up font border thickness
			sliderLabel.setTextColor(Color.WHITE);          // Set text color
			sliderLabel.setBorderColor(Color.BLACK);    // Set text border
			sliderLabel.setThickness(0.25f, 0.35f);     // Set up font thickness
			sliderLabel.setBorderThickness(0.4f, 0.4f); // Set up font border thickness

			debug.getRoot().addComponent(text); // Add the text to the GUI root
			debug.getRoot().addComponent(sliderLabel);
			debug.getRoot().addComponent(slider); // Add the text to the GUI root

			final Model dragonModel = loader.loadModel("default/dragon"); // Load the dragon model


			Texture2D gold = TextureGenerator.genColored(new Vector4f(0.708f, 0.384f, 0.01f)); // Create the texture for the dragon

			final Material dragonMaterial = new Material(); // Create a material for the dragon
			dragonMaterial.setTexture("diffuse", gold);     // Set the diffuse color texture
			dragonMaterial.setFloat("shineDamper", 32);     // Set the specular highlight exponent
			dragonMaterial.setFloat("reflectivity", 1);     // Set the specular highlight strength

			FogPostFX fog = new FogPostFX(renderer.getLightOutput(), renderer.getPositionOutput()); // Create the fog with input from the lit scene and its positions
			fog.setSkyColor(new Vector4f(0.246f, 0.625f, 0.836f)); // Set the sky color
			fog.setDensity(0.004f);                                // Set how thick the fog is
			fog.setGradient(4f);                                   // Set how quickly the fog fades in
			final PostFXOutput bloom = new ContrastPostFX(new BrightFilterBloomPostFX(fog, 0.4f), 0.15f); // Create the bloom postfx
			final PostFXOutput noBloom = new ContrastPostFX(fog, 0.15f);                                  // Create the non bloom postfx
			renderer.setPostFX(bloom);                                                                    // Set default to bloom
			world.setAmbientColor(new Vector4f(0.2f, 0.2f, 0.2f));                                        // Set the brightness of the ambient light to 20%


			state.addListener(new EventAdapter() { // Add an event listener to the state
				boolean bloomEnabled = true; // Bloom enabled by default
				@Override
				public boolean keyPressed(KeyEvent event) {
					if (event.key == Input.KEY_T) { // Was the key T?
						renderer.setWireframe(!renderer.isWireframe()); // Toggle wireframe mode
						return true;                                    // Eat the event
					} else if (event.key == Input.KEY_F1) {  // Was the key F1?
						application.screenshot();            // Take a screenshot
						return true;                         // Eat the event
					} else if (event.key == Input.KEY_F11) { // Was the key F11?
						application.setFullscreen(!application.isFullscreen(), application.isFullscreen() ? null : application.getBestFullscreenVideoMode()); // Toggle fullscreen
						return true; // Eat the event
					} else if (event.key == Input.KEY_B) { // Was B pressed?
						renderer.setPostFX((bloomEnabled = !bloomEnabled) ? bloom : noBloom); // Toggle the bloom effect
						return true; // Eat the event
					} else if (event.key == Input.KEY_GRAVE_ACCENT) {
						application.setCursorEnabled(!debug.isEnabled());
						debug.setEnabled(!debug.isEnabled());
						return true;
					}
					return false; // Pass the event on
				}
			});

			GameObject dragon = new GameObject();             // Create an object for the dragon
			final GameObject cameraObject = new GameObject(); // Create an object for the camera
			GameObject terrainObject = new GameObject();      // Create an object for the terrain
			world.getRoot().addChild(cameraObject);           // Add the objects to the world
			world.getRoot().addChild(dragon);                 // Add the objects to the world
			world.getRoot().addChild(terrainObject);          // Add the objects to the world

			world.getRoot().addComponent(new DirectionalLight(new Vector4f(1, 1, 1), new Vector4f(0, -1, 0, 0))); // Add a directional light pointing straight down

			Material terrainMaterial = new Material();                                                       // Create a material for the terrain

			terrainMaterial.setShader(DeferredTerrainShader.INSTANCE, Material.DEFERRED_SHADER_INDEX);       // Use terrain shaders instead of defaults
			terrainMaterial.setShader(MousePickTerrainShader.INSTANCE, Material.MOUSE_PICKING_SHADER_INDEX); // Use terrain shaders instead of defaults
			terrainMaterial.setTexture("rTexture", loader.loadTexture("default/dirt"));                      // Set red channel texture
			terrainMaterial.setTexture("gTexture", loader.loadTexture("default/flowers"));                   // Set green channel texture
			terrainMaterial.setTexture("bTexture", loader.loadTexture("default/path"));                      // Set  blue channel texture
			terrainMaterial.setTexture("aTexture", loader.loadTexture("default/grass"));                     // Set alpha channel texture
			terrainMaterial.setTexture("blend", loader.loadTexture("default/blend"));                        // Set texture blending map
			terrainMaterial.setVector("reflectivity", new Vector4f(0, 0, 0.3f, 0));                          // Set specular strength of path to 0.5 and others to 0
			terrainMaterial.setVector("shineDamper", new Vector4f(1, 1, 128, 1));                              // Set specular exponent of path to 8 and others to 1
			terrainMaterial.setVector("lightingAmount", new Vector4f(1, 1, 1, 1));                           // Set lighting amount for all textures to 1
			terrainMaterial.setFloat("tileCount", 220);                                                      // Set how many times the textures are tiled across the whole terrain to 220

			HeightMap heightMap = loader.generateHeightMap("default/heightmap", 60);       // Load the terrain height map

			ModelComponent.MOUSE_PICKING_ENABLED_DEFAULT = true;                           // Set mouse picking to be enabled for the terrain FIXME This is terrible

			GeoclipmapTerrain terrain = new GeoclipmapTerrain(terrainMaterial, heightMap,  // Creae the terrain
					600,           // Terrain should have 600 side length
					64,            // 64x64 square of triangles for each level of detail
					5,             // 5 levels of detail
					cameraObject); // Pass it the camera object

			ModelComponent.MOUSE_PICKING_ENABLED_DEFAULT = false; // Set the mouse picking to be disabled for future objects

			terrainObject.addChild(terrain); // Add the terrain to the terrain object

			cameraObject.getTransform().setPos(new Vector4f(0, terrain.getTerrainHeight(0, 20) + 1.5f, 20)); // Set the cameras position to the hiehgt of the terrain + 1.5
			cameraObject.getTransform().setRot(new Quaternion((float) Math.toRadians(180), Vector4f.UP));    // Rotate the camera 180 degrees

			cameraObject.addComponent(camera);   // Add the camera to the camera object
			application.setCursorEnabled(false); // Hide the cursor
			cameraObject.addComponent(new GameComponent() { // Add a component to the camera
				boolean freeMove = true;                    // Camera isn't locked to ground by default
//				double totalDelta = 0;

				@Override
				public void render(GameObject object) {

				}

				@Override
				public void update(GameObject object, double delta) {
//					totalDelta += delta;
//					if (totalDelta > 0.25f) {
//						totalDelta = 0;
//						float maxMemory = Runtime.getRuntime().maxMemory() / 1048576f;     // Get allocated memory
//						float totalMemory = Runtime.getRuntime().totalMemory() / 1048576f; // Get memory in use
//						float freeMemory = Runtime.getRuntime().freeMemory() / 1048576f;   // Get free memory
//
//						Logs.d(String.format("FPS: %d\nUPS: %d\n%.1f/%.1fMB",
//								Math.round(1d / application.getLastFrameTime()),  // Get frame rate
//								Math.round(1d / application.getLastUpdateTime()), // Get update rate
//								totalMemory - freeMemory, maxMemory));            // Memory info
//					}

					camera.setCanMove(!debug.isEnabled());
					camera.setCanRotate(!debug.isEnabled());

					if (!freeMove) { // Is the camera locked to the ground? If so we should set its position to be close to the ground
						Vector4f pos = getObject().getTransform().getPos();    // Get the camera's current position
						pos.y = terrain.getTerrainHeight(pos.x, pos.z) + 1.5f; // Set the camera's position to the height of the terrain + 1.5
						getObject().getTransform().setPos(pos);                // Make sure the camera's position gets updated
					}
				}

				@Override
				public boolean keyPressed(KeyEvent event) {
					if (event.key == Input.KEY_C) { // Was C pressed?
						freeMove = !freeMove;              // Toggle the camera being locked to the ground
						return true;                       // Eat the event
					}
					return false; // Pass the event on
				}

				@Override
				public boolean mousePressed(MouseEvent event) {
					if (event.button == Input.MOUSE_RIGHT) {
						world.requestMousePick(event.x, event.y, new MousePickInfo(), this);
					}
					return true;
				}

				@Override
				public void notified(NotificationEvent event) {
					if (event.getNotificationType() == NotificationEvent.NOTIFICATION_MOUSE_PICK_COMPLETE && event.getData() instanceof MousePickInfo) {
						MousePickInfo info = (MousePickInfo) event.getData();
						if (info.model != null) {
							GameObject newDragonObject = new GameObject();
							newDragonObject.getTransform().setPos(info.worldPosition);
							newDragonObject.addComponent(new ModelComponent(dragonMaterial, dragonModel));
							world.getRoot().addChild(newDragonObject);
						}
					}
				}
			});

			dragon.getTransform().setPos(new Vector4f(8, terrain.getTerrainHeight(8, -14), -14)); // Set the dragon's position to the terrain height

			dragon.addComponent(new ModelComponent(dragonMaterial, dragonModel) {
				private boolean render = true;       // The dragon should render by default

				@Override
				public boolean mouseMoved(MouseEvent event) {
					if (application.getCursorEnabled()) { // If the mouse has moved and the cursor is enabled we should move the dragon to the mouse's position
						world.requestMousePick(Input.getMouseX(), Input.getMouseY(), new MousePickInfo(), this); // Request the world to do a mouse pick asychronously
					}
					return false;
				}

				@Override
				public void notified(NotificationEvent event) {
					if (event.getNotificationType() == NotificationEvent.NOTIFICATION_MOUSE_PICK_COMPLETE && event.getData() instanceof MousePickInfo) { // If the notifiication is a mouse pick complete notification then move the dragon
						MousePickInfo info = (MousePickInfo) event.getData(); // Get the mouse pick info
						if (info.model != null) { // Was the mouse over an object?
							render = true; // It was so make sure the dragon gets rendered
							getObject().getTransform().setPos(info.worldPosition); // Set the dragon's position to the mouses position
						} else {
							render = false; // The mouse wasn't over an object, presumably it was under the terrain or over the sky so don't render the dragon
						}
					}
				}

				@Override
				public void render(GameObject object) {
					if (render) // Should we render the dragon?
						super.render(object); // Render the dragon normally
				}

				@Override
				public boolean keyPressed(KeyEvent event) {
//					if (Input.getKeyNumber(event.key) >= 0) {      // Was a number pressed?
//						setLodBias(Input.getKeyNumber(event.key)); // Set the level of detail bias for the dragon to the number that was pressed
//						return true;                               // Eat the event
//					}
					return false; // Pass the event on
				}
			});

			Throwable e = application.start(); // Start the application
			if (e != null) { // Was there an exception?
				Logs.e("Caught exception");   // Log it
				e.printStackTrace();          // Log the exception
				application.carefulDestroy(); // make sure to do a careful destroy
			} else {
				application.destroy();  // Destroy the application normally
			}

			NullEngine.cleanup(); // Clean up the engine
		} catch (Exception e) { // Something is wrong!
			Logs.e("Something went horribly wrong!", e); // Log the danger message
			Logs.finish();                               // Complete the logs
		}
	}
}
