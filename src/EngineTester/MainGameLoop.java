package EngineTester;
 
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
 
import Models.RawModel;
import Models.TexturedModel;
import ObjConverter.ModelData;
import ObjConverter.OBJFileLoader;

import org.lwjgl.openal.AL10;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import Audio.AudioMaster;
import Audio.Source;
import RenderEngine.DisplayManager;
import RenderEngine.Loader;
import RenderEngine.MasterRenderer;
import RenderEngine.OBJLoader;
import Terrains.Terrain;
import Textures.ModelTexture;
import Textures.TerrainTexture;
import Textures.TerrainTexturePack;
import ToolBox.MousePicker;
import Water.WaterFrameBuffers;
import Water.WaterRenderer;
import Water.WaterShader;
import Water.WaterTile;
import normalMappingObjConverter.NormalMappedObjLoader;
import Enteties.Camera;
import Enteties.Entity;
import Enteties.Light;
import Enteties.Player;
import GUIs.GuiRenderer;
import GUIs.GuiTexture;
 
public class MainGameLoop {
 
    public static void main(String[] args) {
 
        DisplayManager.createDisplay();
        Loader loader = new Loader();
         
         
        
        //******************* TERRAIN *********************
        
        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("SandGrey"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("grassFlowers"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("bottom"));
        
        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture,
        		rTexture, gTexture, bTexture);
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));
        TerrainTexture blendMap2 = new TerrainTexture(loader.loadTexture("blendMap2"));
        
        
        
        
        // ************************************************
        
        
        //*********************** SOUNDS ****************
        
        //AudioMaster.init();
		//AudioMaster.setListenerData(0,0,0);
		//AL10.alDistanceModel(AL10.AL_INVERSE_DISTANCE_CLAMPED);
		
		//int animals = AudioMaster.loadSound("audio/animals.wav");
		//Source source = new Source();
		//source.setLooping(true);
		
		//***********************************************
        
        
        
        
        
        //***************** OBJECTS *****************
        
        //Tree
        ModelData treedata = OBJFileLoader.loadOBJ("tree");
        RawModel treeModel = loader.loadToVAO(treedata.getVertices(), treedata.getTextureCoords(), treedata.getNormals(), treedata.getIndices());
        TexturedModel treestaticModel = new TexturedModel(OBJLoader.loadObjModel("tree", loader), new ModelTexture(loader.loadTexture("tree")));
        
        
        //Grass
        ModelTexture grassTextureAtlas = new ModelTexture(loader.loadTexture("fern"));
        grassTextureAtlas.setNumberOfRows(2);
        TexturedModel grass = new TexturedModel(OBJLoader.loadObjModel("fern", loader),grassTextureAtlas); 
        grass.getTexture().setHasTransparency(true);
        grass.getTexture().setUseFakeLighting(true);
        //Sword
        RawModel swordmodel = OBJLoader.loadObjModel("sword", loader);
        TexturedModel swordstaticModel = new TexturedModel(swordmodel,new ModelTexture(loader.loadTexture("Stone")));
        
        //MetalHelmet
        //RawModel MHelmetmodel = OBJLoader.loadObjModel("MetalHelmet", loader);
        //TexturedModel MHelmetstaticModel = new TexturedModel(MHelmetmodel,new ModelTexture(loader.loadTexture("HelmetColor5")));
        //MHelmetstaticModel.getTexture().setReflectivity(0.3f);
        //Spaceship
        
        // Boulder added with Normal Mapping
        TexturedModel boulderModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("boulder", loader), 
        		new ModelTexture(loader.loadTexture("boulder")));
        boulderModel.getTexture().setNormalMap(loader.loadTexture("boulderNormal"));
        boulderModel.getTexture().setShineDamper(10);
        boulderModel.getTexture().setReflectivity(0.5f);
        
        
        
        TexturedModel spaceshipModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("spaceship", loader), new ModelTexture(loader.loadTexture("moon")));
        //TexturedModel spaceshipstaticModel = new TexturedModel(spaceship,new ModelTexture(loader.loadTexture("moon")));
        
        //Totem
        TexturedModel totemModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("totem", loader), new ModelTexture(loader.loadTexture("water")));
        //RawModel totem = OBJLoader.loadObjModel("totem", loader);
        //TexturedModel totemstaticModel = new TexturedModel(totem,new ModelTexture(loader.loadTexture("water")));
        
        //Table
        RawModel tablemodel = OBJLoader.loadObjModel("BasicTable", loader);
        TexturedModel tablestaticModel = new TexturedModel(tablemodel,new ModelTexture(loader.loadTexture("wood")));
        
        //Houses
        RawModel housemodel = OBJLoader.loadObjModel("BasicHouse", loader);
        TexturedModel housestaticModel = new TexturedModel(housemodel,new ModelTexture(loader.loadTexture("sand")));
        
        //Pyramid
        RawModel pyramidmodel = OBJLoader.loadObjModel("Pyramid", loader);
        TexturedModel pyramidstaticModel = new TexturedModel(pyramidmodel,new ModelTexture(loader.loadTexture("stones")));
        
        //Lamp
        RawModel lampmodel = OBJLoader.loadObjModel("lamp", loader);
        TexturedModel lampstaticModel = new TexturedModel(lampmodel,new ModelTexture(loader.loadTexture("stones")));
        
        //jordkloden
        RawModel jordklodenmodel = OBJLoader.loadObjModel("jordkloden", loader);
        TexturedModel jordklodenstaticModel = new TexturedModel(jordklodenmodel, new ModelTexture(loader.loadTexture("jordkloden")));
      
        //for rotation
        Entity jordkloden = new Entity(jordklodenstaticModel, new Vector3f(-15,40,-20), 0,0,0,10);
        //Cylinder
        //RawModel cylinderfacemodel = OBJLoader.loadObjModel("CylinderFace", loader);
        //TexturedModel cylinderfacestaticModel = new TexturedModel(cylinderfacemodel, new ModelTexture(loader.loadTexture("CylinderFace")));
        
        
        //********************* PLAYER *****************
        
        RawModel playerModel = OBJLoader.loadObjModel("person", loader);
        TexturedModel playerTexture = new TexturedModel(playerModel, new ModelTexture(loader.loadTexture("playerTexture")));
        Player player = new Player(playerTexture, new Vector3f(5,0,-50),0,0,0,0.5f);
        
        
        //**********************************************
        
        
        
        
        //********************* TERRAIN ******************
        
        Terrain terrain = new Terrain(0, 0, loader, texturePack, blendMap2, "heightmap");
        Terrain terrain2 = new Terrain(1, 0, loader, texturePack, blendMap2, "heightmap");
        Terrain terrain3 = new Terrain(0, 1, loader, texturePack, blendMap2, "heightmap");
        Terrain terrain4 = new Terrain(1, 1, loader, texturePack, blendMap2, "heightmap");
        
        
        Terrain[][] terrains;
        terrains = new Terrain[2][2];
        terrains[0][0] = terrain;
        terrains[1][0] = terrain2;
        terrains[0][1] = terrain3;
        terrains[1][1] = terrain4;
        
        
        //Terrain currentTerrain;
        
       // if(player.getPosition().x>800 || player.getPosition().z>800){
       // 	currentTerrain = terrain2;
       // }
       // else{
       Terrain currentTerrain = terrain;
       // }
        
       //tree and grass
       Random random = new Random();
       List<Entity> entities = new ArrayList<Entity>();
       List<Entity> normalMapEntities = new ArrayList<Entity>();
       
       for(int i=0;i<5000;i++){
       	if (i % 20 == 0){
       		float x = random.nextFloat() * 800 -400;
       		float z = random.nextFloat() * -600;
       		currentTerrain = terrains[(int) (x/Terrain.SIZE + 1)][(int) (z/Terrain.SIZE + 1)];
       		float y = currentTerrain.getHeightOfTerrain(x, z);
       		entities.add(new Entity(treestaticModel, new Vector3f(x,y,z),0,random.nextFloat() * 360,0,4));
       		x = random.nextFloat() * 800 -400;
       		z = random.nextFloat() * -600;
       		currentTerrain = terrains[(int) (x/Terrain.SIZE + 1)][(int) (z/Terrain.SIZE + 1)];
       		y = currentTerrain.getHeightOfTerrain(x, z);
            entities.add(new Entity(grass, random.nextInt(4), new Vector3f(x,y,z),0,random.nextFloat() * 360,0,0.5f));
            x = random.nextFloat() * 800 -400;
       		z = random.nextFloat() * -600;
       		currentTerrain = terrains[(int) (x/Terrain.SIZE + 1)][(int) (z/Terrain.SIZE + 1)];
       		y = currentTerrain.getHeightOfTerrain(x, z);
       		normalMapEntities.add(new Entity(boulderModel, new Vector3f(x,y+2,z),0,random.nextFloat() * 360,0,0.3f));
       	}
       }
       
       
        //Table
        //List<Entity> tables = new ArrayList<Entity>();
        for(int i=0;i<200;i++){
        	if (i % 20 == 0){
        		float x = random.nextFloat() * 800 -400;
        		float z = random.nextFloat() * -600;
        		currentTerrain = terrains[(int) (x/Terrain.SIZE + 1)][(int) (z/Terrain.SIZE + 1)];
        		float y = currentTerrain.getHeightOfTerrain(x, z);
        			entities.add(new Entity(tablestaticModel, new Vector3f(x,y,z),0,random.nextFloat() * 360,0,0.5f));
        
        	}
    }
        
        //houses
        //List<Entity> houses = new ArrayList<Entity>();
        for(int i=0;i<50;i++){
        	if (i % 20 == 0){
        		float x = random.nextFloat() * 800 -400;
        		float z = random.nextFloat() * -600;
        		currentTerrain = terrains[(int) (x/Terrain.SIZE + 1)][(int) (z/Terrain.SIZE + 1)];
        		float y = currentTerrain.getHeightOfTerrain(x, z);
        		entities.add(new Entity(housestaticModel, new Vector3f(x,y,z),0,random.nextFloat() * 360,0,6));
        
        	}
        }

        
		
        //helmet
        //entities.add(new Entity(MHelmetstaticModel, new Vector3f(10,0,-10),0,250,0,5));
		//spaceship
        normalMapEntities.add(new Entity(spaceshipModel, new Vector3f(0,25,-80),0,250,0,1));
        //totem
        
        normalMapEntities.add(new Entity(totemModel, new Vector3f(-50,20,-400),0,0,0,0.5f));
        //pyramid
        entities.add(new Entity(pyramidstaticModel, new Vector3f(150,-50,-600),0,0,0,50));
        //Sword
        entities.add(new Entity(swordstaticModel, new Vector3f(50,0.1f,-20),-90,0,0,0.6f));
        //Cylinderface
        //entities.add(new Entity(cylinderfacestaticModel, new Vector3f(5,5,-20), 0,0,0,5));
        // cube
        //entities.add(new Entity(jordklodenstaticModel, new Vector3f(-5,10,-20), 0,0,0,5));
        
        
        
        
        //not working atm
        //AudioMaster.setListenerData(-50,2,-400);
        //source.play(animals);

        
        //********************** LIGHTS ************** 
        
        // sun = lights.get(0)
        
        //Currently have 4 light sources possible
        //Light sun = new Light(new Vector3f(0,1000,-7000), new Vector3f(0.6f,0.6f,0.6f)); // SUN
        List<Light> lights = new ArrayList<Light>();
        lights.add(new Light(new Vector3f(400,400,200), new Vector3f(0.6f,0.6f,0.6f))); // SUN
     // Attenuations / Objects / Range of Lights											not sure, brigthness, 
        lights.add(new Light(new Vector3f(185,10,-293), new Vector3f(10,0,0), new Vector3f(1, 0.01f, 0.002f))); 
        lights.add(new Light(new Vector3f(370,17,-300), new Vector3f(0,0,10), new Vector3f(1, 0.01f, 0.002f)));
        lights.add(new Light(new Vector3f(293,7,-305), new Vector3f(0,10,0), new Vector3f(1, 0.01f, 0.002f)));
        
        // ADD ENTETIES WITH LAMP HERE 
        entities.add(new Entity(lampstaticModel, new Vector3f(185,0,-293),0,0,0,1));
        entities.add(new Entity(lampstaticModel, new Vector3f(370,-14,-300),0,0,0,1));
        entities.add(new Entity(lampstaticModel, new Vector3f(293,-6.8f,-305),0,0,0,1));
        //*******************************************
       
     
        MasterRenderer renderer = new MasterRenderer(loader);
         

        Camera camera = new Camera(player); 
        
        
        //GUIS
        List<GuiTexture> guis = new ArrayList<GuiTexture>();
        GuiTexture gui = new GuiTexture(loader.loadTexture("face"), new Vector2f(-0.75f, -0.75f), new Vector2f(0.25f, 0.25f));
        GuiTexture gui2 = new GuiTexture(loader.loadTexture("HealthBar"), new Vector2f(-0.4f, -0.9f), new Vector2f(0.30f, 0.40f));
        //Order here matters
        guis.add(gui);
        guis.add(gui2);
        
        GuiRenderer guiRenderer = new GuiRenderer(loader);
        
        //**************** MOUSE PROJECTION ********
        MousePicker picker = new MousePicker(camera,renderer.getProjectionMatrix());
        
        //******************************************
        
        //**************** WATER **************
        WaterFrameBuffers buffers = new WaterFrameBuffers();
        WaterShader waterShader = new WaterShader();
        WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), buffers);
        List<WaterTile> waters = new ArrayList<WaterTile>();
        WaterTile water = new WaterTile(75,-75, -5);
        waters.add(water);
        
        
        // take in texture coords (see tutorial 3-4)
        // For minimaps / reflection,refraction tests
        //GuiTexture refraction = new GuiTexture(buffers.getRefractionTexture(), new Vector2f(0.5f,0.5f), new Vector2f (0.25f, 0.25f));
        //GuiTexture reflection = new GuiTexture(buffers.getReflectionTexture(), new Vector2f(-0.5f,0.5f), new Vector2f (0.25f, 0.25f));
        //guis.add(refraction);
        //guis.add(reflection);
        //GuiTexture minigui = new GuiTexture(fbos.getReflectionTexture(), new Vector2f(-0.5f,0.5f), new Vector2f (0.5f, 0.5f));
        // If I want to add etc Minimap, add to this
        //guis.add(minigui);
        // Go to Water Tile Shader to go back to tutorial
        //*****************************************
        
        
        while(!Display.isCloseRequested()){
            camera.move();
            
            //Movement
            int gridX = (int) (player.getPosition().x / Terrain.SIZE + 1);
            int gridZ = (int) (player.getPosition().z / Terrain.SIZE + 1);
            player.move(terrains[gridX][gridZ]);
            //For Multiple Terrain
          
            
            //updating picker
            picker.update();
            
            // For reflection
            // to not render items above/below ground etc
            GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
            float distance = 2*(camera.getPosition().y - water.getHeight());
            camera.getPosition().y -= distance;
            //camera.getPosition().z += distance;
            camera.invertPitch();
            camera.invertRoll();
            renderer.renderScene(entities, normalMapEntities, terrains, lights, camera, new Vector4f(0,1,0, -water.getHeight()+1f));
            camera.getPosition().y += distance;
            //camera.getPosition().z -= distance;
            camera.invertPitch();
            camera.invertRoll();
            
            
            //System.out.println(picker.getCurrentRay());
            //Player renderer
            //renderer.processEntity(player);
            //Player
            
            // Texture of everything
            // reflection texture
            buffers.bindReflectionFrameBuffer(); // kan forandre -waterHeight - 15 etc
            renderer.renderScene(entities, normalMapEntities, terrains, lights, camera, new Vector4f(0, 1, 0, -water.getHeight()));
            //refraction
            buffers.bindRefractionFrameBuffer();
            renderer.renderScene(entities, normalMapEntities, terrains, lights, camera, new Vector4f(0, -1, 0, water.getHeight()+1));
           
            // all objects
            GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
            buffers.unbindCurrentFrameBuffer();
            renderer.processEntity(player);
            // rotation try
            renderer.processEntity(jordkloden);
            jordkloden.increaseRotation(0.3f, 0.3f, 0.3f);
            //
            renderer.renderScene(entities, normalMapEntities, terrains, lights, camera, new Vector4f(0, -1, 0, 10000));
            waterRenderer.render(waters,  camera, lights.get(0));
            guiRenderer.render(guis);
            
            // put everything on screen
            DisplayManager.updateDisplay();
        }
 
        buffers.cleanUp();
        waterShader.cleanUp();
        guiRenderer.cleanUp();
        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();
        
        //SOUND
		//source.delete();
		//AudioMaster.cleanUp();
 
    }
 
}