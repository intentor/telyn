package org.escape2team.telyn.states;

import java.util.LinkedList;
import java.util.List;

import org.escape2team.telyn.configuration.LevelConfiguration;
import org.escape2team.telyn.core.LogoDrawer;
import org.escape2team.telyn.core.PlayerContactFilterHandler;
import org.escape2team.telyn.core.PlayerContactHandler;
import org.escape2team.telyn.core.PlayerContactFilterListener;
import org.escape2team.telyn.core.SoundPlayer;
import org.escape2team.telyn.core.Utils;
import org.escape2team.telyn.core.BaseGameState;
import org.escape2team.telyn.core.GameRenderer;
import org.escape2team.telyn.core.GameInputListener;
import org.escape2team.telyn.core.GameInputProcessor;
import org.escape2team.telyn.core.PolygonDrawer;
import org.escape2team.telyn.core.MassData;
import org.escape2team.telyn.core.Player;
import org.escape2team.telyn.core.Seasons;
import org.escape2team.telyn.core.ViewportTransform;
import org.escape2team.telyn.debug.DebugSettings;
import org.escape2team.telyn.debug.SlickDebugDraw;
import org.escape2team.telyn.objects.LevelObject;
import org.escape2team.telyn.objects.ObjectCreator;
import org.escape2team.telyn.parallax.ParallaxController;
import org.jbox2d.collision.AABB;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.DebugDraw;
import org.jbox2d.dynamics.World;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Estado de jogo.
 */
public class LevelState extends BaseGameState implements GameInputListener, PlayerContactFilterListener {
	/** Identifica o modo de tela. */
	public static enum ScreenMode {
		  Starting
		, Playing
		, Paused
		, Freezed
		, Exit
	}

	/** Quantidade de tempo a ser simulada. */
	static final float BOX2D_TIME_STEP = 1.0f / 30;
	/** Quantidade de iterações do Box2D. */
	static final int BOX2D_ITERATIONS = 5;
	/** Tempo máximo de permanência em uma estação antes de dificultar completamente a visão do jogo (em milissegundos). */
	static final int MAX_TIME_PER_SEASON = 60000;
	/** Variação de alpha durante a passagem de tempo. */
	static final float MAX_TIME_FADE_VARIATION = 0.1f;
	/** Velocidade da variação de alpha do fade de passagem de tempo durante o jogo. */
	static final float TIME_FADE_GAME_SPEED = 0.005f;
	/** Velocidade da variação de alpha do fade de passagem de tempo após a troca de estação. */
	static final float TIME_FADE_SEASON_SPEED = 0.05f;
	/** Quantidade de frames de tela tremendo. */
	static final int CAM_SHAKE_FRAMES = 60;
	/** Fator de tremor da tela. */
	static final float CAM_SHAKE_FACTOR = 0.3f;
	/** Indica se o estado de nível de jogo já foi carregado anteriormente. */
	boolean haveBeenLoaded;
	/** Modo atual da tela. */
	ScreenMode currentScreenMode;
	/** Configurações do nível. */
	LevelConfiguration config;
	/** Bounding box do mundo (limites do mundo). O que estiver dentro é regido pelo Box2D. */
	AABB environment;
	/** Objeto que representa o mundo do jogo. */
	World world;
	/** Objeto para conversões entre unidades de tela e mundo. */
	ViewportTransform transform;
	/** Corpos físicos a serem destruídos na próxima iteração. */
	List<Body> bodiesToDestroy;
	/** Valores máximos nos eixos X e Y da câmera. */
	Vec2 maxCamera;
	/** Valores mínimos nos eixos X e Y da câmera. */
	Vec2 minCamera;
	/** Armazena a última posição da câmera para gerenciamento do movimento das paralaxes. */
	Vec2 lastCameraPosition;
	/** Posição atual do mouse em unidades de tela. */
	Vec2 currentMousePosition;
	/** Checkpoint atual do jogo. */
	Vec2 currentCheckpoint;
	/** Representa a quantidade de quadros a qual a câmera deve tremer. */
	private int camShake;
	/** Representa a quantidade de quadros a qual a câmera deve tremer, antes de ir para o inverno. */
	private int camShakeBeforeSleep;
	/** Contador para exibição de textos */
	int counter;
	/** Quantidade de tempo passado enquanto em uma estação. */
	int elapsedTime;
	/** Indicador de fade in (position) ou fade out (negativo) da passagem de tempo. */
	int elapsedTimeFade;
	/** Fator de passagem de tempo. */
	float elapsedTimeFactor;
	/** Renderizador de objetos do Box2D. */
	GameRenderer renderer;
	/** Criador de objetos. */
	ObjectCreator creator;
	/** Indica se o modo de debug está habiitado.  */
	boolean isDebug;
	/** Indica se o modo de debug do Box2D está habiitado.  */
	boolean isDebugDraw; 
	/** Drawer de objetos em tela. */
	PolygonDrawer drawer;
	/** Desenhista do logo. */
	LogoDrawer logo;
	/** Fonte principal do jogo. */
	Font fontMain;
	/** Foreground de passagem de tempo (estático). */
	Image fgTime;
	/** Imagem de desenho. */
	Image drawingImage;
	/** Camadas de paralaxe do jogo. */
	ParallaxController parallax;
	/** Controles de input de tela. */
	GameInputProcessor inputProcessor;
	/** Personagem do jogador. */
	Player player;
	/** Handler de colisões. */
	PlayerContactHandler playerContact;
	/** Handler de filtro de colisões. */
	PlayerContactFilterHandler playerFilter;
	/** Filtro de cor atual da estação. */
	Color currentFilter;
	/** Último filtro de cor de estação. */
	Color lastSeasonChangingFilter;
	/** Tocador de sons. */
	SoundPlayer sfx;
	/** Música do nível atual. */
	Music musicLevel;
	
	/**
	 * Cria um novo estado de jogo.
	 * @param id 		ID do estado.
	 * @param config 	Configurações do nível.
	 */
	public LevelState(int id) {
		super(id);
	}
	
	//MÉTODOS DE JOGO=================================================================
	
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		this.isDebug = this.isDebugDraw = Loader.CONFIGURATIONS.debugEnable;
		this.currentScreenMode = ScreenMode.Starting;
		this.currentMousePosition = new Vec2(0, 0);
		this.bodiesToDestroy = new LinkedList<Body>();
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		if (this.currentScreenMode == ScreenMode.Playing) {
			/* Somente atualiza o fator caso este seja maior que 0.
			 * Isso ocorre pois, enquanto o tempo for 0, ocorre o fade
			 * de troca de estação, que é o responsável por somar 1
			 * ao elapsed time para que o tempo comece a passar. */
			if (this.elapsedTime > 0) {
				this.elapsedTime += delta;
			}
		}

		this.player.update(container, game, delta);
		this.adjustCamera();
		this.inputProcessor.update(container, game, delta);
		this.logo.update(container, game, delta);
		
		if (this.currentScreenMode != ScreenMode.Freezed) {			
			//Verifica se algum objeto do nível deve ser destruído.
			for (Body b : this.bodiesToDestroy) {
				@SuppressWarnings("rawtypes")
				LevelObject obj = (LevelObject)b.m_userData;
				obj.destroy();
				if (this.creator != null) this.creator.objects.remove(obj);
			}
			this.bodiesToDestroy.clear();
			
			//Atualiza os objetos do nível.
			if (this.creator != null) {
				for (@SuppressWarnings("rawtypes") LevelObject obj : this.creator.objects) obj.update(container, game, delta);
			}			

			this.world.step(BOX2D_TIME_STEP, BOX2D_ITERATIONS);
			this.parallax.update(container, game, delta);
			this.drawer.update(container, game, delta);
		}
		
		if (this.player.isDead()) this.revivePlayer();		
	}
	
	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		//Renderiza as camadas anteriores ao jogador.		
		this.parallax.renderBackground(container, game, g, this.currentFilter);				
		this.parallax.renderMain(container, game, g, this.currentFilter);
		//Renderiza os objetos do Box2D.
		this.renderer.render(container, game, g, this.currentFilter);
		//Renderiza o jogador.
		this.player.render(container, game, g, this.currentFilter);
		//Renderiza as camadas posteriores ao jogador.
		this.parallax.renderForeground(container, game, g, this.currentFilter);
		//Renderiza objetos desenhados, se houver algum.
		this.drawer.render(container, game, g, this.currentFilter);
		//Renderiza o filtro de passagem de tempo.
		if (this.currentScreenMode == ScreenMode.Playing) this.renderElapsedTime(g);			
		//Modo de debug.
		if (this.isDebug) this.renderDebug(container, game, g);
		//Renderiza o logo do jogo.
		this.logo.render(container, game, g, this.currentFilter);
		//Renderiza controles de tela, caso haja algum.
		this.inputProcessor.render(container, game, g, this.currentFilter);
		//Renderiza o hud de orbs, caso seja para sempre exibi-lo.
		if (Loader.CONFIGURATIONS.alwaysDrawHud) this.drawer.drawHud(container, game, g, this.currentFilter, this.currentMousePosition);
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		container.setMouseCursor("data/sprites/cursor-empty.png", 0, 0);
		
		if (this.currentScreenMode == ScreenMode.Paused) {
			this.inputProcessor.enter(container, game);
			this.inputProcessor.fireUnpauseGameEvent();
			if (!Loader.CONFIGURATIONS.editorEnable) this.musicLevel.resume();
		} else {
			//Reseta contadores.
			this.resetElapsedTime();
			this.counter = 0;
			
			//Eventos de entrada no estado.
			this.parallax.enter(container, game);
			this.inputProcessor.enter(container, game);
			this.drawer.enter(container, game);
			this.player.enter(container, game);
			this.logo.enter(container, game);
			
			//Define a posição do checkpoint.
			this.currentCheckpoint = new Vec2(this.player.getCurrentWorldCenterPosition());
			
			//Listeners de eventos.
			this.playerFilter.addListener(this);

			//Habilita a música.
			if (!Loader.CONFIGURATIONS.editorEnable) this.musicLevel.loop(1.0f, 0.25f);
			
			//Esconde o mouse da tela.
			this.currentMousePosition.set(-100, -100);
			//Define a última posição da câmera como a posição do personagem.
			this.lastCameraPosition = this.player.getCurrentWorldCenterPosition();		
			//Define o filtro atual como sendo o de verão.
			this.currentFilter = Seasons.Summer.getColor();
			
			//Define o modo de cena atual como JOGO.
			this.currentScreenMode = ScreenMode.Starting;
		}
	}

	@Override
	public void leave(GameContainer container, StateBasedGame game) throws SlickException {
		//Somente descarta os recursos se a tela atual não for de pause.
		if (this.currentScreenMode == ScreenMode.Paused) {
			this.musicLevel.pause();
		} else {
			//Eventos de saída do estado.
			this.parallax.leave(container, game);
			this.inputProcessor.leave(container, game);
			this.drawer.leave(container, game);
			this.player.leave(container, game);
			this.logo.leave(container, game);
			
			//Listeners de eventos.
			this.playerFilter.removeListener(this);
			
			//Destruição de objetos.
			if (!Loader.CONFIGURATIONS.editorEnable) this.musicLevel.stop();
			this.fgTime.destroy();
			this.drawingImage.destroy();
			this.maxCamera = this.minCamera = this.lastCameraPosition = null;
		}
	}
	
	//MÉTODOS DE RENDERIZAÇÃO=========================================================
	
	/**
	 * Renderiza elementos de debug.
	 */
	public void renderDebug(GameContainer container, StateBasedGame game, Graphics g) {
		if (this.isDebugDraw) {
			Vec2 center = this.transform.getCenter();
			this.world.getDebugDraw().setCamera(center.x, center.y, this.transform.getScale());		
			this.world.drawDebugData();
		}
		
		g.drawString("DEBUG MODE", 700, 5);	
		g.drawString("FPS: " + String.valueOf(container.getFPS()), 10, 5);
		g.drawString(String.valueOf(this.inputProcessor.getCurrentSeason()), 10, 25);
	}
	
	/**
	 * Renderiza a passagem de tempo.
	 * @param g Objeto gráfico para desenho.
	 */
	public void renderElapsedTime(Graphics g) {
		Color filter = new Color(Color.white);
		
		/**
		 * Primeiramente, verifica se se está na troca de estação
		 * (this.elapsedTime = 0 e this.elapsedTimeFactor > 0).
		 * Estando na troca de estação, faz fade de saída de estação.
		 * Caso this.elapsedTime = 0 e this.elapsedTimeFactor <= 0,
		 * o efeito de saída de estação terminou.
		 * Assim, inicia a contagem de passagem de tempo.
		 */
		if (this.elapsedTime == 0 && this.elapsedTimeFactor > 0) {
			this.elapsedTimeFactor -= TIME_FADE_SEASON_SPEED;
			filter.a = this.elapsedTimeFactor;
		} else if (this.elapsedTime == 0) {
			this.elapsedTime = 1;
			this.elapsedTimeFactor = 0;
			filter.a = 0;
		} else {
			filter.a = this.getElapsedTimeFactor();
		}
		
		this.fgTime.draw(0, 0, filter);
	}
	
	//OBTENÇÂO E ATUALIZAÇÂO DE VARIÁVEIS=============================================

	/**
	 * Reinicia o jogo.
	 */
	public void restart(GameContainer container, StateBasedGame game) throws SlickException {
		//Entra no modo de loading.
		game.enterState(Loader.LOADINGSTATE);
	}
	
	/** 
	 * Revive o jogador no checkpoint mais próximo.
	 */
	public void revivePlayer() {
		this.player.revive();
		this.player.setPlayerPosition(this.currentCheckpoint);
		this.camShake = 0;
		this.elapsedTime = MAX_TIME_PER_SEASON;
		this.resetElapsedTime();	
	}
	
	/**
	 * Treme a tela.
	 */
	public void shakeViewport() {
		if (this.camShake == 0) {
			SoundPlayer.playActTremor();
			this.inputProcessor.vibrate(new long[] { 0, 350, 50, 250, 80, 350, 50, 250, 80, 350, 50, 250, 80 }, -1);
			this.camShake = CAM_SHAKE_FRAMES;
		}
	}
	
	/**
	 * Indica se se está em modo de debug.
	 * @param isDebug		Indica se se está em modo de debug.
	 * @param isDebugDraw	Indica se se deve permitir debug do Box2D.
	 */
	public void setIsDebug(boolean isDebug, boolean isDebugDraw) {
		this.isDebug = isDebug;
		this.isDebugDraw = isDebugDraw;
	}
	
	/**
	 * Reseta o tempo passado no jogo.
	 */
	public void resetElapsedTime() {
		this.elapsedTimeFactor = this.getElapsedTimeFactor();
		this.elapsedTime = 0;
		this.elapsedTimeFade = -1;
	}
	
	/**
	 * Obtém a bounding box do mundo. 
	 */
	public AABB getPhysicsEnvironment() {
		return this.environment;
	}
	
	/** 
	 * Obtém o objeto que representa o mundo do jogo.
	 */
	public World getPhysicsWorld() {
		return this.world;
	}
	
	/** 
	 * Obtém o objeto para conversões entre unidades de tela e mundo.
	 */
	public ViewportTransform getViewportTransform() {
		return this.transform;
	}
	
	/**
	 * Obtém o objeto que representa o jogador.
	 */
	public Player getPlayer() {
		return this.player;
	}
	
	/**
	 * Obtém a última posição da câmera do jogador.
	 * @return Posição da câmera.
	 */
	public Vec2 getLastCameraPosition() {
		return this.lastCameraPosition;
	}
	
	//MÉTODOS DE APOIO================================================================
	
	/**
	 * Inicia o jogo.
	 */
	private void startGame() {
		this.currentScreenMode = ScreenMode.Playing;
		this.logo.isActive = false;
	}
	
	/**
	 * Indica que o jogador passou em um checkpoint.
	 * @param position
	 */
	public void checkpointActivated(Vec2 position) {
		this.currentCheckpoint = position;
	}
	
	/**
	 * Cria o mundo do jogo.
	 * @param container Container do jogo.
	 */
	void createWorld(GameContainer container) {		
		this.environment = new AABB();
		this.environment.upperBound.set(this.config.layerUpperBound);
		this.environment.lowerBound.set(this.config.layerLowerBound);
		Vec2 gravity = new Vec2(0.0f, -10.0f);
		boolean doSleep = true;
		this.world = new World(this.environment, gravity, doSleep);
		
		this.transform = new ViewportTransform(container);
		
		//Configurações de localização da câmera.
		
		//Obtém as metades do tamanho da tela em unidades do mundo.
		float halfWidth = this.transform.convertPixelsInWorldScale(container.getWidth() / 2);
		float halfHeight = this.transform.convertPixelsInWorldScale(container.getHeight() / 2);		
		//Define as posições máximas e mínimas.
		this.maxCamera = new Vec2(this.environment.upperBound.x - halfWidth, this.environment.upperBound.y - halfHeight);
		this.minCamera = new Vec2(this.environment.lowerBound.x + halfWidth, this.environment.lowerBound.y + halfHeight);
		
		//Cria as colisões e checkpoints do jogo somente caso não seja o editor.
		if (!Loader.CONFIGURATIONS.editorEnable) {
			Utils.createCollisions(this.world, this.config.collisions);
			for (Vec2 position : this.config.checkpoints) Utils.createCheckpoint(this.world, position, this);
		}
		
		if (isDebugDraw) {
			DebugDraw debug = new SlickDebugDraw(container);
			debug.setFlags(0);
			DebugSettings settings = new DebugSettings();
			if (settings.drawShapes) debug.appendFlags(DebugDraw.e_shapeBit);
			if (settings.drawJoints) debug.appendFlags(DebugDraw.e_jointBit);
			if (settings.drawCoreShapes) debug.appendFlags(DebugDraw.e_coreShapeBit);
			if (settings.drawAABBs) debug.appendFlags(DebugDraw.e_aabbBit);
			if (settings.drawOBBs) debug.appendFlags(DebugDraw.e_obbBit);
			if (settings.drawPairs) debug.appendFlags(DebugDraw.e_pairBit);
			if (settings.drawCOMs) debug.appendFlags(DebugDraw.e_centerOfMassBit);
			if (settings.drawControllers) debug.appendFlags(DebugDraw.e_controllerBit);		
			this.world.setDebugDraw(debug);
		}
	}
	
	/**
	 * Ajusta a posição da câmera de acordo com a posição do personagem.
	 */
	private void adjustCamera() {
		/* Inicialmente, a posição da câmera é sempre o centro de massa
		 * do personagem do jogador.
		 * Os ajustes servem para manter a câmera fixa quando esta estiver 
		 * em algum dos cantos da tela. */
		Vec2 pos = this.player.getCurrentWorldCenterPosition();
		if (pos.x >= this.maxCamera.x) pos.x = this.maxCamera.x;
		else if (pos.x <= this.minCamera.x) pos.x = this.minCamera.x;
		if (pos.y >= this.maxCamera.y) pos.y = this.maxCamera.y;
		else if (pos.y <= this.minCamera.y) pos.y = this.minCamera.y;
		
		//Verifica se a câmera deve tremer, o que ocorre sempre em valores pares maiores que 0.
		if (this.currentScreenMode != ScreenMode.Freezed && this.camShake > 0 && this.camShake-- % 2 == 0) {
			pos.x += (Utils.getRandomNumber(2) == 1 ? 1 : -1) * CAM_SHAKE_FACTOR;
			pos.y += (Utils.getRandomNumber(2) == 1 ? 1 : -1) * CAM_SHAKE_FACTOR;
		}
		
		//Define a câmera, colocando a posição em X sempre negativa.
		this.transform.setCamera(-pos.x, pos.y);
		
		//Movimenta as camadas de paralaxe na mesma razão do movimento.
		Vec2 size = new Vec2(
				  this.transform.convertWorldScaleInPixels(pos.x - this.lastCameraPosition.x)
				, this.transform.convertWorldScaleInPixels(pos.y - this.lastCameraPosition.y)
			);
		this.parallax.move(size);
		
		//Define a última posição de câmera como a posição atual.
		this.lastCameraPosition = pos;
	}
	
	/**
	 * Coloca para dormir todos os objetos do jogo, incluindo animações de paralaxe.
	 */
	@SuppressWarnings("rawtypes")
	private void sleepObjects() {
		for (Body b = this.world.getBodyList(); b != null; b = b.m_next) {
			//Somente acorda objetos que não sejam o jogador.
			if (!(b.m_userData instanceof Player)) {
				if (b.m_userData instanceof LevelObject) {
					((LevelObject)b.m_userData).sleep();
				} else {
					for (Shape s = b.getShapeList(); s != null; s = s.m_next) {
						if (s.m_userData instanceof MassData) {
							MassData.resetShapeMass(s);
						}
					}
					b.setMassFromShapes();
					b.putToSleep();
				}
			}
		}
		
		this.parallax.animationStop();
	}
	
	/**
	 * Acorda todos os objetos do jogo, incluindo animações de paralaxe.
	 */
	@SuppressWarnings("rawtypes")
	private void wakeUpObjects() {
		for (Body b = this.world.getBodyList(); b != null; b = b.m_next) {
			//Somente acorda objetos que não sejam o jogador.
			if (!(b.m_userData instanceof Player)) {
				if (b.m_userData instanceof LevelObject) {
					((LevelObject)b.m_userData).wakeUp();
				} else {
					for (Shape s = b.getShapeList(); s != null; s = s.m_next) {
						if (s.m_userData instanceof MassData) {
							((MassData)s.m_userData).setShapeMass(s);
						}
					}
					b.setMassFromShapes();
					b.wakeUp();
				}
			}
		}
		
		this.parallax.animationStart();
	}
	
	/**
	 * Obtém o fato de passagem de tempo.
	 * @return Fator de passagem de tempo.
	 */
	private float getElapsedTimeFactor() {
		float value = 0;
		
		float fade = ((float)this.elapsedTime / MAX_TIME_PER_SEASON);
		if (fade > 1.0f) fade = 1.0f;
		
		this.elapsedTimeFactor += this.elapsedTimeFade * TIME_FADE_GAME_SPEED;
		value = fade + this.elapsedTimeFactor;
		
		if (value < fade - MAX_TIME_FADE_VARIATION) {
			value = fade - MAX_TIME_FADE_VARIATION;
			this.elapsedTimeFade = 1;
		} else if (value > fade) {
			value = fade;
			this.elapsedTimeFade = -1;
		}
		
		return value;
	}

	//GAME INPUT LISTENER=============================================================

	@Override
	public void mouseMove(float x, float y) {
		this.currentMousePosition.set(x, y);
	}
	
	@Override
	public void pauseGame() {
		this.currentScreenMode = ScreenMode.Paused;
		Loader.GAME.enterState(Loader.PAUSESTATE);
	}

	@Override
	public void unpauseGame() {
		//Caso o logo ainda esteja na tela, indica que o modo de jogo atual é Starting.
		if (this.logo.isActive) {
			this.currentScreenMode = ScreenMode.Starting;
		} else {
			this.currentScreenMode = ScreenMode.Playing;
		}
	}
	
	@Override
	public void moveLeft() {
		//Caso o modo seja Starting e o jogador se moveu, vai para o modo Playing.
		if (this.currentScreenMode == ScreenMode.Starting) this.startGame();
		this.player.moveLeft();
	}

	@Override
	public void moveRight() {
		//Caso o modo seja Starting e o jogador se moveu, inicia o jogo.
		if (this.currentScreenMode == ScreenMode.Starting) this.startGame();
		this.player.moveRight();
	}

	@Override
	public void jump() {
		//Caso o modo seja Starting e o jogador se moveu, inicia o jogo.
		if (this.currentScreenMode == ScreenMode.Starting) this.startGame();
		if (this.player.jump()) {
			this.inputProcessor.vibrate(100);
		}
	}
	
	@Override
	public void seasonStartSelection() {
		this.currentScreenMode = ScreenMode.Freezed;
		this.lastSeasonChangingFilter = new Color(this.currentFilter);
		this.player.startPlayHarp();
		SoundPlayer.playActSeasonsWheel(1.0f);
	}

	@Override
	public void seasonChanging(float factor, Seasons current, Seasons over) {
		Color cCur = current.getColor();
		Color cOver = over.getColor();
		
		this.currentFilter.r = cCur.r + (cOver.r - cCur.r) * factor;
		this.currentFilter.g = cCur.g + (cOver.g - cCur.g) * factor;
		this.currentFilter.b = cCur.b + (cOver.b - cCur.b) * factor;		

		if (this.currentFilter.r != this.lastSeasonChangingFilter.r ||
			this.currentFilter.g != this.lastSeasonChangingFilter.g ||
			this.currentFilter.b != this.lastSeasonChangingFilter.b) {
			this.player.playHarp();
			float pitch;
			
			//Define os pitches iniciais de cada estação.
			switch (over) {
				case Summer:
				default: {
					pitch = 0.5f; //Mais agudo.
				} break;
				case Spring: {
					pitch = 0.4f;
				} break;
				case Autumn: {
					pitch = 0.3f;
				} break;
				case Winter: {
					pitch = 0.2f; //Mais grave.
				} break;
			}
			
			if (Math.ceil(factor * 10) % 2 == 0) SoundPlayer.playActHarpOnChangingSeasons(pitch + 0.5f * factor);
		}
		
		this.lastSeasonChangingFilter = new Color(this.currentFilter);
	}

	@Override
	public void seasonChanged(Seasons oldSeason, Seasons newSeason) {
		if (oldSeason != newSeason) {
			SoundPlayer.playActSeasonChanged();
			this.resetElapsedTime();
		} else {
			SoundPlayer.playActSeasonsWheel(1.2f);
		}
		
		//Verifica se se deve parar ou mover objetos.
		if (newSeason == Seasons.Summer) {
			//Insere o fato de tremor que poderia haver no inverno.
			this.camShake = this.camShakeBeforeSleep;
			this.camShakeBeforeSleep = 0;
			this.wakeUpObjects();
		} else if (newSeason == Seasons.Winter) {
			//Armazena o fator de tremor da câmera atual e reseta-o.
			this.camShakeBeforeSleep = this.camShake;
			this.camShake = 0;
			this.sleepObjects();			
		}

		this.currentFilter = newSeason.getColor();
		if (this.logo.isActive) {
			this.currentScreenMode = ScreenMode.Starting;
		} else {
			this.currentScreenMode = ScreenMode.Playing;
		}		
		this.player.stopPlayHarp();
	}
	
	@Override
	public void drawBegin(float x, float y) {
		this.drawer.startDrawing(this.transform.screenToWorld(new Vec2(x, y)));
	}

	@Override
	public void drawAddSegment(float x, float y) {
		this.drawer.drawSegment(this.transform.screenToWorld(new Vec2(x, y)));
	}

	@Override
	public void drawFinalyze() {
		this.drawer.finalizeDrawing();
	}

	@Override
	public void drawingsDestroyAll() {
		this.drawer.destroyAllDrawings();
	}

	//PLAYER CONTACT LISTENER=========================================================

	@Override
	public void onOrbCollision(Body orb) {
		this.bodiesToDestroy.add(orb);
		this.drawer.addOrb();
	}
}