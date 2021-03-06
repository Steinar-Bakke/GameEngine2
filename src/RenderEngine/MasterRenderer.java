package RenderEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

import Shaders.StaticShader;
import Shaders.TerrainShader;
import SkyBox.SkyboxRenderer;
import Terrains.Terrain;
import normalMappingRenderer.NormalMappingRenderer;
import Models.TexturedModel;
import Enteties.Camera;
import Enteties.Entity;
import Enteties.Light;

public class MasterRenderer {
	
	private static final float FOV = 70;
	private static final float NEAR_PLANE = 0.1f;
	private static final float FAR_PLANE = 1000;
	
	// Determine Sky Color
	// Fog is in Terrain/Vertex Shader.txt
	public static float RED = 0.3f;
	public static float GREEN = 0.5f;
	public static float BLUE = 0.7f;
	
	private Matrix4f projectionMatrix;
	
	private StaticShader shader = new StaticShader();
	private EntityRenderer renderer;
	
	
	private TerrainRenderer terrainRenderer;
	private TerrainShader terrainShader = new TerrainShader();
	
	private NormalMappingRenderer normalMapRenderer;
	
	
	private Map<TexturedModel,List<Entity>> entities = new HashMap<TexturedModel,List<Entity>>();
	private Map<TexturedModel,List<Entity>> normalMapEntities = new HashMap<TexturedModel,List<Entity>>();
	private List<Terrain> terrains = new ArrayList<Terrain>();
	
	private SkyboxRenderer skyboxRenderer;
	
	
	public MasterRenderer(Loader loader){
		//to not render inside of objects
		enableCulling();
		
		createProjectionMatrix();
		renderer = new EntityRenderer(shader, projectionMatrix);
		terrainRenderer = new TerrainRenderer(terrainShader,projectionMatrix);
		skyboxRenderer = new SkyboxRenderer(loader, projectionMatrix);
		normalMapRenderer = new NormalMappingRenderer(projectionMatrix);
	}
	
	// Projection Matrix
	public Matrix4f getProjectionMatrix(){
		return projectionMatrix;
	}
	
	// All the rendering
	public void renderScene(List<Entity> entities, List<Entity> normalEntities, Terrain[][] terrains, List<Light> lights,
			Camera camera, Vector4f clipPlane){
		for (int i=0; i<2; i++){
			for (int y=0; y<2; y++){
				processTerrain(terrains[i][y]);
			}
		}
		for (Entity entity : entities){
			processEntity(entity);
		}
		for (Entity entity : normalEntities){
			processNormalMapEntity(entity);
		}
		render(lights, camera, clipPlane);
	}
	//
	
	public static void enableCulling(){
		//to not render inside of objects
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}
	
	public static void disableCulling(){
		//to not render inside of objects
		GL11.glDisable(GL11.GL_CULL_FACE);
	}
	
	
	public void render(List<Light> suns, Camera camera, Vector4f clipPlane){
		prepare();
		shader.start();
		shader.loadClipPlane(clipPlane);
		shader.loadSkyColor(RED, GREEN, BLUE);
		shader.loadLights(suns);
		shader.loadViewMatrix(camera);
		renderer.render(entities);
		shader.stop();
		normalMapRenderer.render(normalMapEntities,clipPlane, suns, camera);
		terrainShader.start();
		terrainShader.loadClipPlane(clipPlane);
		terrainShader.loadSkyColor(RED, GREEN, BLUE);
		terrainShader.loadLights(suns);
		terrainShader.loadViewMatrix(camera);
		terrainRenderer.render(terrains);
		terrainShader.stop();
		skyboxRenderer.render(camera,RED, GREEN, BLUE);
		terrains.clear();
		entities.clear();
		normalMapEntities.clear();
	}
	
	public void processTerrain(Terrain terrain){
		terrains.add(terrain);
	}
	
	
	
	public void processEntity(Entity entity){
		TexturedModel entityModel = entity.getModel();
		List<Entity> batch = entities.get(entityModel);
		if(batch!=null){
			batch.add(entity);
		}else{
			List<Entity> newBatch = new ArrayList<Entity>();
			newBatch.add(entity);
			entities.put(entityModel, newBatch);
		}
	}
	
	// For Normal Mapping
	
	public void processNormalMapEntity(Entity entity){
		TexturedModel entityModel = entity.getModel();
		List<Entity> batch = normalMapEntities.get(entityModel);
		if(batch!=null){
			batch.add(entity);
		}else{
			List<Entity> newBatch = new ArrayList<Entity>();
			newBatch.add(entity);
			normalMapEntities.put(entityModel, newBatch);
		}
	}
	
	public void cleanUp(){
		shader.cleanUp();
		terrainShader.cleanUp();
		normalMapRenderer.cleanUp();
	}
	
	public void prepare(){
		//test which triangle is infront
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		//prepare opengl to render game
		GL11.glClearColor(RED,GREEN,BLUE,1); 
		// green background
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);
	}
	
	private void createProjectionMatrix(){
		
		float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
		float x_scale = y_scale / aspectRatio;
		float frustrum_length = FAR_PLANE - NEAR_PLANE;
		
		
		projectionMatrix = new Matrix4f();
		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustrum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustrum_length);
		projectionMatrix.m33 = 0; 
		
	}
	
}
