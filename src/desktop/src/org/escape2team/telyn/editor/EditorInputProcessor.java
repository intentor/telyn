package org.escape2team.telyn.editor;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.escape2team.telyn.configuration.LayerConfiguration;
import org.escape2team.telyn.configuration.LevelConfiguration;
import org.escape2team.telyn.core.ObjectData;
import org.escape2team.telyn.core.TileData;
import org.escape2team.telyn.core.Utils;
import org.escape2team.telyn.core.DesktopInputProcessor;
import org.escape2team.telyn.core.GameInputListener;
import org.escape2team.telyn.core.GameInputProcessor;
import org.escape2team.telyn.core.PackedSpriteSheet;
import org.escape2team.telyn.core.Player;
import org.escape2team.telyn.core.Seasons;
import org.escape2team.telyn.core.ViewportTransform;
import org.escape2team.telyn.objects.LevelObject;
import org.escape2team.telyn.objects.ObjectCreator;
import org.escape2team.telyn.states.LevelState;
import org.jbox2d.collision.AABB;
import org.jbox2d.collision.shapes.PolygonDef;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.MouseListener;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

/**
 * InputProcessor do editor de níveis.
 */
public class EditorInputProcessor extends GameInputProcessor implements MouseListener, GameInputListener {
	/** Tempo de espera para análise de pressionamento de teclas. */
	private static final int KEY_DELTA = 100;
	/** Fator de movimentação da câmera do editor. */
	private static final float EDITOR_CAMERA_FACTOR = 0.5f;
	/** Espaço de tempo para análise de pressionamento de teclas. */
	private int keyDelta;
	/** Input processor do jogo. */
	private DesktopInputProcessor gameProcessor;
	/** Modo do editor. */
	private EditorMode mode;
	/** handler do modo atual. */
	private EditorModeHandler handler;
	/** Exibe a lista de comandos. */
	private boolean showCommandList;
	/** Indica se o debug draw está habilitado. */
	private boolean debugDrawEnable;
	/** Bounding box do mundo (limites do mundo). O que estiver dentro é regido pelo Box2D. */
	private AABB environment;
	/** Objeto que representa o mundo do jogo. */
	private World world;
	/** Objeto para conversões entre unidades de tela e mundo. */
	private ViewportTransform transform;
	/** Objeto que representa o jogador. */
	private Player player;
	/** Criador de objetos do jogo. */
	private ObjectCreator creator;
	/** Sprites dos objetos. */
	private Object[] sprites;
	/** Posição do mouse na tela. */
	private Vec2 mousePositionScreen;
	/** Posição do mouse na mundo. */
	private Vec2 mousePositionWorld;
	/** Posição atual da câmera no mundo. */
	private Vec2 currentWorldCameraPosition;
	/** Indica se o mouse está fora dos limites do mundo. */
	private boolean isMousePositionInLimits;
	/** Contador de exibição de dados em tela. */
	private int counter;
	/** Indica que os dados foram salvos. */
	private boolean saved;
	/** Posição do canto superior direito do nível em relação ao centro do mundo, em unidades do mundo. */
	public Vec2 levelUpperBound;
	/** Posição do canto inferior esquerdo do nível em relação ao centro do mundo, em unidades do mundo. */
	public Vec2 levelLowerBound;
	/** Posição inicial do jogador. */
	private Vec2 characterPosition;
	/** Pacotes de tiles do jogo. */
	private List<PackedSpriteSheet> packs;
	/** Lista de tiles do jogo. */
	private List<TileData> tiles;
	/** Conjuntos de pontos das áreas de colisão. */
	private List<List<Vec2>> collisions;
	/** Objetos do jogo. */
	public List<ObjectData> objects;
	/** Checkpoints do jogo. */
	public List<Vec2> checkpoints;
	
	/**
	 * Construtor da classe.
	 * @param state Estado de jogo dos níveis.
	 */
	public EditorInputProcessor(LevelState state) {
		super(state);
		this.environment = state.getPhysicsEnvironment();
		this.world = state.getPhysicsWorld();
		this.transform = state.getViewportTransform();
		this.player = state.getPlayer();
	}
	
	//HERDADOS=========================================================================

	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		CHANGE_SEASON_SIZE = 50;
		this.initProcessor(container, game);
		container.getInput().addMouseListener(this);
		
		this.gameProcessor = new DesktopInputProcessor(this.gameState, false);
		this.gameProcessor.init(container, game);		
		this.gameProcessor.addListener(this);		

		this.mousePositionScreen = new Vec2(0, 0);
		this.mousePositionWorld = new Vec2(0, 0);
		this.currentWorldCameraPosition = new Vec2(0, 0);

		try {
			//Carrega as configurações do jogo.
			LevelConfiguration config = new LevelConfiguration("data/states/level1/level.dat");
			this.levelUpperBound = config.layerUpperBound;
			this.levelLowerBound = config.layerLowerBound;
			this.characterPosition = config.characterPosition;
			this.packs = config.packs;
			this.collisions = config.collisions;
			this.objects = config.objects;
			this.checkpoints = config.checkpoints;
			
			//Carrega as configurações de tiles.
			LayerConfiguration layer0 = new LayerConfiguration("data/states/level1/layer0.dat", config);
			this.tiles = layer0.tiles;
		} catch (SlickException e) {
			e.printStackTrace();
		}
		
		this.createCollisions(this.world, this.collisions);
		this.createObjects(container, game);
		this.createCheckpoints();
		
		this.showCommandList = false;
		this.debugDrawEnable = true;
		this.gameState.setIsDebug(true, true);	
		
		this.counter = 0;

		this.setEditorMode(EditorMode.Game, container, game);
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {		
		container.setMouseCursor("data/sprites/cursor.png", 0, 0);
	}

	@Override
	public void leave(GameContainer container, StateBasedGame game) throws SlickException {
		container.getInput().removeMouseListener(this);
		this.gameProcessor.leave(container, game);
		if (this.handler != null) this.handler.leave(container, game);
		this.gameState.setIsDebug(true, true);	
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g, Color filter) throws SlickException {
		if (this.mode == EditorMode.Game) this.gameProcessor.render(container, game, g, filter);
		
		this.renderTiles(g, filter);
		this.renderCheckpoints(g, filter);
		this.renderTrigger(g, filter);
		
		if (this.handler != null) this.handler.render(container, game, g, filter);
		
		if (this.debugDrawEnable) {
			Vec2 center = this.transform.getCenter();
			this.world.getDebugDraw().setCamera(center.x, center.y, this.transform.getScale());		
			this.world.drawDebugData();
		}
		
		if (this.saved) {
			g.drawString("File saved successfully.", 400, 10);
		}
		
		this.renderEditorInfo(container, game, g);	
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		this.keyDelta += delta;
		Input kb = container.getInput();
		for (@SuppressWarnings("rawtypes") LevelObject obj : this.creator.objects) obj.update(container, game, delta);
		
		if (this.saved && this.counter++ > 50) {
			this.counter = 0;
			this.saved = false;
		}
		
		if (this.mode == EditorMode.Game) { 
			this.gameProcessor.update(container, game, delta);
			this.currentWorldCameraPosition.set(this.gameState.getLastCameraPosition());
		} else {
			if (this.handler != null) this.handler.update(container, game, delta);
			
			//Movimentações da câmera.
			if (kb.isKeyDown(Input.KEY_UP)) 
				this.currentWorldCameraPosition.y += EDITOR_CAMERA_FACTOR;
			if (kb.isKeyDown(Input.KEY_DOWN)) 
				this.currentWorldCameraPosition.y -= EDITOR_CAMERA_FACTOR;
			if (kb.isKeyDown(Input.KEY_LEFT)) 
				this.currentWorldCameraPosition.x -= EDITOR_CAMERA_FACTOR;
			if (kb.isKeyDown(Input.KEY_RIGHT)) 
				this.currentWorldCameraPosition.x += EDITOR_CAMERA_FACTOR;
			this.transform.setCamera(-this.currentWorldCameraPosition.x, this.currentWorldCameraPosition.y);
						
			//Não sendo modo de jogo, reseta o tempo para evitar que a tela fique escura.
			this.gameState.resetElapsedTime();
		}
		
		if (this.keyDelta > KEY_DELTA) {	
			this.keyDelta = 0;
			//Configurações do editor.
			if (kb.isKeyDown(Input.KEY_F1)) this.showCommandList = !this.showCommandList;
			if (kb.isKeyDown(Input.KEY_F2) && !this.saved) this.saveLevel();
			if (kb.isKeyDown(Input.KEY_F3)) this.setDebugDraw();
			if (kb.isKeyDown(Input.KEY_F12)) this.reloadLevel(container, game);
			
			//Zoom.
			if (kb.isKeyDown(Input.KEY_ADD) || kb.isKeyDown(Input.KEY_EQUALS)) this.zoom(true);
			if (kb.isKeyDown(Input.KEY_SUBTRACT) || kb.isKeyDown(Input.KEY_MINUS)) this.zoom(false);
	
			//Modos do editor.
			if (kb.isKeyDown(Input.KEY_F5)) this.setEditorMode(EditorMode.Tiles, container, game);
			if (kb.isKeyDown(Input.KEY_F6)) this.setEditorMode(EditorMode.Collision, container, game);
			if (kb.isKeyDown(Input.KEY_F7)) this.setEditorMode(EditorMode.CharacterPosition, container, game);
			if (kb.isKeyDown(Input.KEY_F8)) this.setEditorMode(EditorMode.ObjectPosition, container, game);
			if (kb.isKeyDown(Input.KEY_F9)) this.setEditorMode(EditorMode.Checkpoint, container, game);
			if (kb.isKeyDown(Input.KEY_F10)) this.setEditorMode(EditorMode.Game, container, game);
		}
	}
	
	//APOIO=========================================================================
	
	/**
	 * Cria as colisões.
	 * @param world 		Objeto que representa o mundo do jogo.
	 * @param collisions	Vértices dos polígonos de colisão.
	 */
	private void createCollisions(World world, List<List<Vec2>> collisions) {
		for (List<Vec2> vertex : collisions) {			
			BodyDef bd = new BodyDef();
			Body b = world.createBody(bd);
			
			for (int i = 0; i < vertex.size() - 1; ++i) {
				PolygonDef sd = new PolygonDef();
				sd.friction = LevelConfiguration.GROUND_FRICTION;
				Utils.createStrokeRect(vertex.get(i), vertex.get(i + 1), LevelConfiguration.STROKE_RADIUS, b, sd);
			}
			
			//Adiciona o vértice a user data para posterior colisão.
			b.setUserData(vertex);
			b.setMassFromShapes();
		}
	}
	
	/**
	 * Cria os objetos do jogo.
	 * @param container	Container de jogo.
	 * @param game		Estado atual do jogo.
	 */
	private void createObjects(GameContainer container, StateBasedGame game) throws SlickException {
		//Carrega os sprites dos objetos.
		PackedSpriteSheet pack = new PackedSpriteSheet("data/sprites/tiles/main-tiles.def");		
		this.sprites = new Object[6];
		this.sprites[0] = Utils.loadSprites("orb", new PackedSpriteSheet("data/sprites/orb.def", Image.FILTER_NEAREST), 14);
		this.sprites[1] = pack.getSprite("Rock");
		this.sprites[2] = pack.getSprite("BigRock");
		this.sprites[3] = pack.getSprite("StemTree");
		this.sprites[4] = pack.getSprite("Pendulum");
		this.sprites[5] = pack.getSprite("Rotator");	
		
		//Cria o gerador de objetos.
		this.creator = new ObjectCreator(this.world, this.transform, this.sprites);
		
		//Cria os objetos.
		for (ObjectData obj : this.objects) {
			obj.object = this.creator.create(obj.type, obj.position, obj.triggerPosition);
		}
		this.creator.initAll(container, game);
	}
	
	/**
	 * Cria os checkpoints do jogo.
	 */
	private void createCheckpoints() throws SlickException {
		for (Vec2 position : this.checkpoints) Utils.createCheckpoint(this.world, position, this.gameState);
	}	
	
	/**
	 * Renderiza informações do editor.
	 */
	private void renderEditorInfo(GameContainer container, StateBasedGame game, Graphics g) {
		String currentMode = "EDITOR: " + String.valueOf(this.mode);
		g.drawString(currentMode, 792 - g.getFont().getWidth(currentMode), 25);
		
		if (this.showCommandList) {
			int pos = 10;
			g.drawString("LISTA DE COMANDOS", 100, pos); pos += EditorModeHandler.HELP_LINE_HEIGHT;
			g.drawString("F1: exibe esta lista de comandos", 120, pos); pos += EditorModeHandler.HELP_LINE_HEIGHT;
			g.drawString("F2: salva o cenário", 120, pos); pos += EditorModeHandler.HELP_LINE_HEIGHT;
			g.drawString("F3: habilita/desabilita debug draw", 120, pos); pos += EditorModeHandler.HELP_LINE_HEIGHT;
			
			g.drawString("F5: modo de edição de tiles", 120, pos); pos += EditorModeHandler.HELP_LINE_HEIGHT;
			g.drawString("F6: modo de edição de colisão", 120, pos); pos += EditorModeHandler.HELP_LINE_HEIGHT;
			g.drawString("F7: modo de posicionamento do Druida", 120, pos); pos += EditorModeHandler.HELP_LINE_HEIGHT;
			g.drawString("F8: modo de criação de objetos", 120, pos); pos += EditorModeHandler.HELP_LINE_HEIGHT;
			g.drawString("F9: modo de criação de checkpoints", 120, pos); pos += EditorModeHandler.HELP_LINE_HEIGHT;
			g.drawString("F10: modo de jogo", 120, pos); pos += EditorModeHandler.HELP_LINE_HEIGHT;
			
			g.drawString("F12: reinicia o nível", 120, pos); pos += EditorModeHandler.HELP_LINE_HEIGHT;
			
			g.drawString("+/-: zoom", 120, pos); pos += EditorModeHandler.HELP_LINE_HEIGHT;
			g.drawString("Clique wheel: reseta zoom", 120, pos); pos += EditorModeHandler.HELP_LINE_HEIGHT;
			g.drawString("Setas: movem a câmera", 120, pos); pos += EditorModeHandler.HELP_LINE_HEIGHT;
			
			if (this.handler != null) this.handler.renderHelp(container, game, g, new Vec2(120, pos));			
		}
	}
	
	/**
	 * Altera o modo do editor.
	 * @param mode Modo para o qual o editor será alterado.
	 * @throws SlickException 
	 */
	private void setEditorMode(EditorMode mode, GameContainer container, StateBasedGame game) throws SlickException {
		//Indica o novo modo.
		this.mode = mode;
		
		//Indica finalização do modo do editor.
		if (this.handler != null) this.handler.leave(container, game);
		
		//Configura o modo do editor.
		switch (this.mode) {
			case Tiles: {
				this.handler = new TilesEditorMode(this.environment, this.world, this.transform, this.packs, this.tiles);
			} break;
			case Collision: {
				this.handler = new CollisionEditorMode(this.environment, this.world, this.transform, this.collisions);
				this.debugDrawEnable = true;
			} break;
			case CharacterPosition: {
				this.handler = new CharacterPositionEditorMode(this.environment, this.world, this.transform, this.player, this.characterPosition);
			} break;
			case ObjectPosition: {
				this.handler = new ObjectPositionEditorMode(this.environment, this.world, this.transform, this.objects, this.creator, this.sprites);
			} break;
			case Checkpoint: {
				this.handler = new CheckpointEditorMode(this.environment, this.world, this.transform, this.gameState, this.checkpoints);
			} break;
			case Game: {
				this.handler = null;
				this.player.wakeUp();
			} break;
		}

		//Inicializa o modo do editor.
		if (this.handler != null) this.handler.init(container, game);
	}
	
	/**
	 * Salva dados do nível em disco.
	 */
	private void saveLevel() {
		this.saveLevelData();
		this.saveLayerData();
	}
	
	/**
	 * Salva dados do nível atual em disco.
	 */
	private void saveLevelData() {
		StringBuilder builder = new StringBuilder();
		
		//Informações de limites do cenário.
		builder.append(String.format(Locale.US, "bounds [\n%f,%f;%f,%f\n]\n"
				, this.levelUpperBound.x, this.levelUpperBound.y
				, this.levelLowerBound.x, this.levelLowerBound.y));
		
		//Informações de jogador.
		builder.append(String.format(Locale.US, "character [\n%f,%f\n]\n", this.characterPosition.x, this.characterPosition.y));
				
		//Informações dos pacotes.
		builder.append("packs [\n");
		for (int i = 0; i < this.packs.size(); i++) {
			builder.append(String.format(Locale.US, "%d:%s\n", i, this.packs.get(i).getPath()));
		}
		builder.append("]\n");

		//Informações de colisão.
		builder.append("collisions [\n");
		for (List<Vec2> vectors: this.collisions) {
			boolean isFirst = true;
			for (Vec2 vertex : vectors) {
				if (isFirst) isFirst = false;
				else builder.append(";");
				builder.append(String.format(Locale.US, "%f,%f"
					, vertex.x
					, vertex.y));
			}
			builder.append("\n");
		}
		builder.append("]\n");
		
		//Objetos do jogo.
		builder.append("objects [\n");
		for (ObjectData obj: this.objects) {
			builder.append(String.format(Locale.US, "%d;%f,%f;%f,%f\n"
				, obj.type.getId()
				, obj.position.x
				, obj.position.y
				, (obj.triggerPosition == null ? 0 : obj.triggerPosition.x)
				, (obj.triggerPosition == null ? 0 : obj.triggerPosition.y)));
		}
		builder.append("]\n");
		
		//Checkpoints do jogo.
		builder.append("checkpoints [\n");
		for (int i = 0; i < this.checkpoints.size(); i++) {
			if (i > 0) builder.append(";");
			Vec2 pos = this.checkpoints.get(i);
			builder.append(String.format(Locale.US, "%f,%f"
				, pos.x
				, pos.y));
		}
		builder.append("\n]");
		
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter("data/states/level1/level.dat"));
			out.write(builder.toString());
		    out.close();
			this.saved = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			this.saved = false;
		} catch (IOException e) {
			e.printStackTrace();
			this.saved = false;
		}	
	}
	/**
	 * Salva dados da camada atual em disco.
	 */
	private void saveLayerData() {
		StringBuilder builder = new StringBuilder();
		
		//Informações dos tiles.
		builder.append("tiles [\n");
		for (TileData data : this.tiles) {
			builder.append(String.format(Locale.US, "%d;%s;%f,%f;%f;%s\n"
				, data.definitionFileId
				, data.spriteName
				, data.position.x
				, data.position.y
				, data.rotation
				, (data.flip ? "1" : "0")));
		}
		builder.append("]");
		
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter("data/states/level1/layer0.dat"));
			out.write(builder.toString());
		    out.close();
			this.saved = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			this.saved = false;
		} catch (IOException e) {
			e.printStackTrace();
			this.saved = false;
		}	
	}
	
	/**
	 * Recarrega o nível.
	 */
	private void reloadLevel(GameContainer container, StateBasedGame game) throws SlickException {
		this.gameState.restart(container, game);
	}
	
	/**
	 * Habilita/Desabilita o debug draw.
	 */
	private void setDebugDraw() {
		this.debugDrawEnable = !this.debugDrawEnable;
	}	
	
	/**
	 * Realiza zoom.
	 * @param more Indica se deve aumentar ou diminuir o zoom.
	 */
	private void zoom(boolean more) {
		float size = 0;
		
		if (more) size = 1.2f;
		else size = 0.8f;		
		
		float scale = this.transform.getScale();
		Vec2 center = this.transform.getCenter();
		this.transform.setCamera(center.x, center.y, scale * size);
		
		//Atualiza as coordenadas do mouse no mundo.
		this.setMousePosition(this.mousePositionScreen.x, this.mousePositionScreen.y);	
	}
	
	/**
	 * Define as posições do mouse.
	 * @param x Posição no eixo X.
	 * @param y Posição no eixo Y.
	 */
	private void setMousePosition(float x, float y) {
		this.mousePositionScreen.set(x, y);
		this.mousePositionWorld.set(this.transform.screenToWorld(this.mousePositionScreen));
		
		//Verifica se a posição do mouse está fora dos limites do mundo.
		this.isMousePositionInLimits = 
			(this.mousePositionWorld.x < this.environment.upperBound.x &&
			  this.mousePositionWorld.x > this.environment.lowerBound.x &&
			  this.mousePositionWorld.y < this.environment.upperBound.y &&
			  this.mousePositionWorld.y > this.environment.lowerBound.y);
	}
	
	/**
	 * Renderiza os tiles armazenados.
	 * @param g 		Objeto de desenho.
	 * @param filter	Filtro de cor dos tiles.
	 */	
	private void renderTiles(Graphics g, Color filter) {
		for (TileData data : this.tiles) {
			Image sprite = this.packs.get(data.definitionFileId).getSprite(data.spriteName).getFlippedCopy(data.flip, false);			
			Utils.drawImage(sprite, data.position, data.rotation, this.transform, g, filter);
		}
	}
	
	/**
	 * Renderiza informações sobre os checkpoints armazenados.
	 * @param g 		Objeto de desenho.
	 * @param filter	Filtro de cor dos tiles.
	 */	
	private void renderCheckpoints(Graphics g, Color filter) {
		for (Vec2 pos : this.checkpoints) {
			Vec2 scr = this.transform.worldToScreen(pos);
			g.drawString("CHECKPOINT", scr.x, scr.y);
		}
	}
	
	/**
	 * Renderiza informações sobre os triggers armazenados.
	 * @param g 		Objeto de desenho.
	 * @param filter	Filtro de cor dos tiles.
	 */	
	private void renderTrigger(Graphics g, Color filter) {
		for (ObjectData data : this.objects) {
			if (data.triggerPosition != null && !data.triggerPosition.equals(new Vec2(0,0))) {
				Vec2 scr = this.transform.worldToScreen(data.triggerPosition);
				
				g.drawString("TRIGGER", scr.x, scr.y);
				g.drawString(String.valueOf(data.type), scr.x, scr.y + 25);
			}
		}
	}

	//INPUTS=========================================================================
	
	@Override
	public void inputEnded() {
		if (this.mode == EditorMode.Game) this.gameProcessor.inputEnded();	
	}

	@Override
	public void inputStarted() {
		if (this.mode == EditorMode.Game) this.gameProcessor.inputStarted();
	}

	@Override
	public boolean isAcceptingInput() {
		return true;
	}

	@Override
	public void setInput(Input arg0) {
		if (this.mode == EditorMode.Game) this.gameProcessor.setInput(arg0);	
	}

	@Override
	public void mouseClicked(int arg0, int arg1, int arg2, int arg3) {
		if (this.mode == EditorMode.Game) this.gameProcessor.mouseClicked(arg0, arg1, arg2, arg3);
	}

	@Override
	public void mouseDragged(int oldx, int oldy, int x, int y) {
		if (this.mode == EditorMode.Game) this.gameProcessor.mouseDragged(oldx, oldy, x, y);
		this.setMousePosition(x, y);
		if (this.handler != null) this.handler.mouseMoved(this.mousePositionScreen, this.mousePositionWorld, !this.isMousePositionInLimits);
	}

	@Override
	public void mouseMoved(int oldx, int oldy, int x, int y) {
		if (this.mode == EditorMode.Game) this.gameProcessor.mouseMoved(oldx, oldy, x, y);
		this.setMousePosition(x, y);
		if (this.handler != null) this.handler.mouseMoved(this.mousePositionScreen, this.mousePositionWorld, !this.isMousePositionInLimits);
	}

	@Override
	public void mousePressed(int button, int x, int y) {
		if (this.mode == EditorMode.Game) this.gameProcessor.mousePressed(button, x, y);
		this.setMousePosition(x, y);
		if (this.handler != null) this.handler.mousePressed(button, this.mousePositionScreen, this.mousePositionWorld, !this.isMousePositionInLimits);

		if (button == 2) { //Wheel.
			//Reposiciona a câmera no centro da tela na escala inicial.
			this.transform.setCamera(0.0f, 0.0f, ViewportTransform.BASE_WORLD_SCALE);
		}
	}

	@Override
	public void mouseReleased(int button, int x, int y) {
		if (this.mode == EditorMode.Game) this.gameProcessor.mouseReleased(button, x, y);
		this.setMousePosition(x, y);
	}

	@Override
	public void mouseWheelMoved(int size) {
		if (this.mode == EditorMode.Game) this.gameProcessor.mouseWheelMoved(size);
		if (this.handler != null) this.handler.mouseWheelMoved(size);	
	}
	
	//DesktopInputProcessor=========================================================================

	@Override
	public void mouseMove(float x, float y) {
		this.fireMouseMoveEvent(x, y);
	}
	
	@Override
	public void pauseGame() {
		this.firePauseGameEvent();
	}

	@Override
	public void unpauseGame() {
		this.fireUnpauseGameEvent();
	}

	@Override
	public void moveLeft() {
		this.fireMoveLeftEvent();
	}

	@Override
	public void moveRight() {
		this.fireMoveRightEvent();
	}

	@Override
	public void jump() {
		this.fireJumpEvent();
	}
	
	@Override
	public void seasonStartSelection() {
		this.fireSeasonStartSelectionEvent();
	}


	@Override
	public void seasonChanging(float factor, Seasons current, Seasons over) {
		this.fireSeasonChangingEvent(factor, current, over);
	}

	@Override
	public void seasonChanged(Seasons oldSeason, Seasons newSeason) {
		this.fireSeasonChangedEvent(oldSeason, newSeason);
	}

	@Override
	public void drawBegin(float x, float y) {
		this.fireDrawBeginEvent(x, y);
	}

	@Override
	public void drawAddSegment(float x, float y) {
		this.fireDrawAddSegmentEvent(x, y);
	}

	@Override
	public void drawFinalyze() {
		this.fireDrawFinalyzeEvent();
	}

	@Override
	public void drawingsDestroyAll() {
		this.fireDrawingsDestroyAllEvent();
	}
}
