package org.escape2team.telyn.states;

import org.escape2team.telyn.configuration.LayerConfiguration;
import org.escape2team.telyn.configuration.LevelConfiguration;
import org.escape2team.telyn.core.BaseGameState;
import org.escape2team.telyn.core.GameRenderer;
import org.escape2team.telyn.core.LogoDrawer;
import org.escape2team.telyn.core.OrbsCounterDrawer;
import org.escape2team.telyn.core.PackedSpriteSheet;
import org.escape2team.telyn.core.Player;
import org.escape2team.telyn.core.PlayerContactFilterHandler;
import org.escape2team.telyn.core.PlayerContactHandler;
import org.escape2team.telyn.core.PolygonDrawer;
import org.escape2team.telyn.core.SoundPlayer;
import org.escape2team.telyn.core.Utils;
import org.escape2team.telyn.objects.ObjectCreator;
import org.escape2team.telyn.parallax.ParallaxController;
import org.jbox2d.common.Vec2;
import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Estado para carregamento de assets.
 */
public class LoadingState extends BaseGameState {
	/** Quantidade de itens a serem carregados. */
	private static final int TOTAL_LOADING_RESOURCES = 24;
	/** Altura da linha com a fonte atual. */
	private static final int LINE_HEIGHT = 30;
	/**  Índice de carregamento de assets.  */
	private int loadingIndex;
	/** Estado de pause. */
	private PauseState pause;
	/** Estado do nível. */
	private LevelState level;
	/** Desenhista de efeito de loading. */
	private OrbsCounterDrawer drawer;
	/** Fonte principal do jogo. */
	private Font fontMain;
	/** Textos de disclaimer. */
	private String[] disclaimer;
	/** Pack de imagens do loading. */
	PackedSpriteSheet pack;
	
	/**
	 * Cria um novo estado de carregamento de objetos.
	 * @param id	ID do estado.
	 * @param level	Estado do nível.
	 * @param menu	Estado de pausa.
	 */
	public LoadingState(int id, LevelState level, PauseState pause) {
		super(id);
		this.level = level;
		this.pause = pause;
	}
	
	//MÉTODOS DE JOGO=================================================================

	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		this.pack = new PackedSpriteSheet("data/sprites/loading.def");
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		container.setMouseCursor("data/sprites/cursor-empty.png", 0, 0);
		this.disclaimer = Loader.LOCALIZATION.getString("disclaimer").split("\\\\n");
		
		this.drawer = new OrbsCounterDrawer(TOTAL_LOADING_RESOURCES, TOTAL_LOADING_RESOURCES, new Vec2(400, 240), 120, pack);
		this.drawer.init(container, game);
		
		this.loadingIndex = 0;
	}

	@Override
	public void leave(GameContainer container, StateBasedGame game) throws SlickException {
		
	}
	
	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		if (this.loadingIndex >= TOTAL_LOADING_RESOURCES) {
			//Exibe o disclaimer.
			int start = 80;
			for (int i = 0; i < this.disclaimer.length; i++) {
				Utils.drawStringCenter(this.fontMain, this.disclaimer[i], (start + i * LINE_HEIGHT));
			}
		} else {
			//Exibe o jogo.
			this.drawer.render(container, game, g, Color.white);
		}
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		if (this.loadingIndex >= TOTAL_LOADING_RESOURCES) {
			//Inicia o jogo se o botão esquerdo do mouse foi pressionado.
			if (container.getInput().isMousePressed(0)) {
				Loader.GAME.enterState(Loader.GAMEPLAYSTATE);
			}
		} else {
			this.drawer.usedOrbs = ++this.loadingIndex;
			this.drawer.update(container, game, delta);
			
			switch(this.loadingIndex) {
				case 1:
					this.fontMain = new AngelCodeFont("data/fonts/main.fnt", "data/fonts/main.png");
					this.level.fontMain = this.fontMain;
					if (pause != null) this.pause.fontMain = this.fontMain;
				break;
				case 2:
					this.level.drawingImage = new Image("data/sprites/draw.png");
				break;
				case 3:
					this.level.fgTime = new Image("data/sprites/fgtime.png");
				break;
				case 4:
					if (pause != null) this.pause.howPlay = new Image("data/states/pause/how_" + Loader.CONFIGURATIONS.language.substring(0, 2) + ".png");
				break;
				case 5:
					this.level.config = new LevelConfiguration("data/states/level1/level.dat");
				break;
				case 6:
					this.level.createWorld(container);
				break;
				case 7:
					SoundPlayer.createInstance();
				break;
				case 8:
					this.level.playerContact = new PlayerContactHandler(this.level);
					this.level.playerFilter = new PlayerContactFilterHandler();
					this.level.world.setContactListener(this.level.playerContact);
					this.level.world.setContactFilter(this.level.playerFilter);
				break;
				case 9:
					this.level.player = new Player(this.level.world, this.level.transform, this.level.config.characterPosition, this.level.playerContact);
					this.level.player.init(container, game);
				break;
				case 10:	
					this.level.drawer = new PolygonDrawer(this.level.world, this.level.transform, 4);
					this.level.drawer.init(container, game);
				break;
				case 11:
					this.level.renderer = new GameRenderer(container, this.level.world, this.level.transform, this.level.drawingImage);	
				break;
				case 12:
					//Somente cria os objetos se não for editor.
					if (!Loader.CONFIGURATIONS.editorEnable) {
						//Obtém o pack "main-tiles.def".
						for (PackedSpriteSheet pack : this.level.config.packs) {
							if (pack.getPackName().equals("main-tiles")) {
								Object[] sprites = new Object[6];
								sprites[0] = Utils.loadSprites("orb", new PackedSpriteSheet("data/sprites/orb.def", Image.FILTER_NEAREST), 14);
								sprites[1] = pack.getSprite("Rock");
								sprites[2] = pack.getSprite("BigRock");
								sprites[3] = pack.getSprite("StemTree");
								sprites[4] = pack.getSprite("Pendulum");
								sprites[5] = pack.getSprite("Rotator");
								
								this.level.creator = new ObjectCreator(this.level.world, this.level.transform, sprites); 
								break;
							}
						}
	
						this.level.creator.create(this.level.config.objects);
						this.level.creator.initAll(container, game);
					}
				break;
				case 13:
					this.level.logo = new LogoDrawer(this.level.fontMain);
					this.level.logo.init(container, game);
					//Não exibe o logo se o jogo já tiver sido anteriormente carregado ou o editor estiver habilitado.
					if (this.level.haveBeenLoaded || Loader.CONFIGURATIONS.editorEnable) this.level.logo.sleep = true;
				break;
				case 14:
					this.level.parallax = new ParallaxController();
				break;
				case 15:
					this.level.parallax.addLayer(-4, new Image("data/states/level1/layer-4.png"), new Vec2(0, 0), new Vec2(0, 0), new Vec2(0, 0));
				break;
				case 16:
					if (!Loader.CONFIGURATIONS.isMobile) {
						Animation layerMinus3 = Utils.loadAnimation("data/states/level1/layer-3.def", "sun", 12, 150);
						this.level.parallax.addLayer(-3, layerMinus3, new Vec2(800, 480), new Vec2(0, 0), new Vec2(608, 48), new Vec2(0, 0));
					}
				break;
				case 17:
					if (!Loader.CONFIGURATIONS.isMobile) {
						this.level.parallax.addLayer(-2, new Image("data/states/level1/layer-2.png"), new Vec2(0, 0), new Vec2(0, 0), new Vec2(0.2f, 0));
					}
				break;
				case 18:
					if (!Loader.CONFIGURATIONS.isMobile) {
						this.level.parallax.addLayer(-1, new Image("data/states/level1/layer-1.png"), new Vec2(0, 0), new Vec2(0, 0), new Vec2(0.5f, 0));
					}
				break;
				case 19:
					if (Loader.CONFIGURATIONS.editorEnable) {
						//Sendo editor, cria a camada 0 vazia.
						this.level.parallax.addLayer(0, new Image("data/sprites/empty.png"), new Vec2(800, 480), new Vec2(0, 0), new Vec2(0, 0), new Vec2(1.0f, 1.0f));
					} else {
						//Não sendo editor, cria a camada 0 com o conjunto de tiles correto.
						LayerConfiguration layer0 = new LayerConfiguration("data/states/level1/layer0.dat", this.level.config);
						this.level.parallax.addLayer(0, layer0, this.level.world, this.level.transform, new Vec2(0, 0), new Vec2(0, 0), new Vec2(1f, 1f));
					}
				break;
				case 20:
					LayerConfiguration layer1 = new LayerConfiguration("data/states/level1/layer1.dat", this.level.config);
					this.level.parallax.addLayer(1, layer1, this.level.world, this.level.transform, new Vec2(0, 0), new Vec2(0, 0), new Vec2(1f, 1f));		
				break;
				case 21:
					this.level.parallax.init(container, game);
				break;
				case 22:
					this.level.inputProcessor = Loader.CONFIGURATIONS.getInputProcessor(this.level);
					this.level.inputProcessor.init(container, game);
					this.level.inputProcessor.addListener(this.level);
				break;
				case 23:
					if (this.level.musicLevel == null) this.level.musicLevel = new Music("data/states/level1/level.ogg", true);
				case 24:
					this.level.haveBeenLoaded = true;
				break;
			}
		}
	}
}
