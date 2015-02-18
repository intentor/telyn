package org.escape2team.telyn.core;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.escape2team.telyn.configuration.LevelConfiguration;
import org.escape2team.telyn.states.Loader;
import org.jbox2d.collision.shapes.PolygonDef;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.particles.ConfigurableEmitter;
import org.newdawn.slick.particles.ParticleSystem;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Gere a geração de objetos em tela.
 */
public class PolygonDrawer implements GameObject {
	/** Máximo tamanho de desenho (quantidade de orbs). */
	private static final int MAX_DRAWING_LENGTH = 20;
	/** Máxima distância em unidades de mundo a qual o mouse deve se mover para criar um novo segmento. */
	private static final float DRAWING_SIZE = 0.5f;
	/** Diferença de distância a qual se permite a realização de desenho. */
	private static final float DRAWING_OFFSET = 0.1f;
	/** Raio do stroke (deve ser ao menos .2f para que a engine atue perfeitamente). */
	private static final float STROKE_HEIGHT = 0.05f;
	/** Referência ao mundo do jogo. */
	private World world;
	/** Objeto para conversões entre unidades de tela e mundo. */
	private ViewportTransform transform;
	/** Indica se o sistema de desenho de objetos com o mouse está ativo. */
    private boolean isDrawing;
    /** Posição atual de desenho, em coordenadas do mundo. */
    private Vec2 currentPosition;
    /** Vetores de desenho do mouse, em unidades do mundo. */
	private Vec2[] drawingPositions;
	/** Tamanho atual do desenho do mouse. */
	private int drawingLength;
	/** Sistema de partículas do movimento do desenho. */
	private ParticleSystem barks;
	/** Emissor de partículas do movimento do desenho. */
	private ConfigurableEmitter drawingEffectEmitter;
	/** Sistema de partículas dos segmentos. */
	private ParticleSystem segment;
	/** Desenhos criados. */
	private List<Body> drawings;
	/** Valor de alpha para finalização das partículas. */
	private float alphaEnding;
	/** Desenhista do HUD das orbs. */
	private OrbsCounterDrawer hud;
	/** Indica se se pode realizar desenho. */
	private boolean canDraw;

	/**
	 * Construtor da classe.
	 * @param world 		Mundo do jogo.
	 * @param transform		Objeto para conversões entre unidades de tela e mundo.
	 * @param totalOrbs 	Quantidade de orbs para início do jogo.
	 * @param alwaysDrawHud	Indica se se deve sempre desenhar o HUD.
	 */
	public PolygonDrawer(World world, ViewportTransform transform, int totalOrbs) throws SlickException {
		this.world = world;
		this.transform = transform;
		
		this.currentPosition = new Vec2();
		
		PackedSpriteSheet pack = new PackedSpriteSheet("data/sprites/orbhud.def");
		this.hud = new OrbsCounterDrawer(MAX_DRAWING_LENGTH, totalOrbs, new Vec2(0, 0), Loader.CONFIGURATIONS.radiusHudOrb, pack);
	}
	
	/**
	 * Adiciona uma orb ao desenhista do HUD.
	 */
	public void addOrb() {
		this.hud.addOrb();
	}
	
	/**
	 * Reseta a quantidade de orbs utilizadas.
	 */
	public void resetOrbs() {
		this.hud.resetOrbs();
	}
	
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		try {
			this.barks = Utils.loadParticleSystem("data/particles/drawing.xml");
			this.drawingEffectEmitter = (ConfigurableEmitter)this.barks.getEmitter(0);
			this.segment = Utils.loadParticleSystem("data/particles/segment.xml");
		} catch (IOException e) {
			e.printStackTrace();
		}		
		
		this.hud.init(container, game);
		
		this.drawingPositions = new Vec2[MAX_DRAWING_LENGTH];
		for (int i = 0; i < this.drawingPositions.length; i++) this.drawingPositions[i] = new Vec2();
		this.drawings = new LinkedList<Body>();
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) {
	
	}

	@Override
	public void leave(GameContainer container, StateBasedGame game) throws SlickException {
		this.hud.leave(container, game);
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g, Color filter) throws SlickException {
		Vec2 currentScreen = this.transform.worldToScreen(this.currentPosition);
		
		if (this.alphaEnding > 0) {			
			//Efeito de desenho.
			this.drawingEffectEmitter.setPosition(currentScreen.x, currentScreen.y, false);
			this.barks.render();
			
			if (this.isDrawing) {
				//Desenha as orbs.
				for (int i = 0; i < this.drawingLength; i++) {
					Vec2 pos = this.transform.worldToScreen(this.drawingPositions[i]);
					this.segment.render(pos.x, pos.y);
				}

				//Somente renderiza o HUD se não for para sempre exibi-lo.
				if (!Loader.CONFIGURATIONS.alwaysDrawHud) this.drawHud(container, game, g, filter, currentScreen);
			}
		}
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		if (this.alphaEnding > 0) {
			this.barks.update(delta);
			this.segment.update(delta);
			if (!this.isDrawing) {
				this.alphaEnding -= 0.01;
				float factor = 1000 / (1 - this.alphaEnding);
				this.drawingEffectEmitter.spawnInterval.setMin(factor);
				this.drawingEffectEmitter.spawnInterval.setMax(factor);
			}
		}
	}
	
	/**
	 * Desenha o HUD das orbs.
	 * @param container
	 * @param game
	 * @param g
	 * @param filter
	 * @param currentScreen Posição atual do mouse.
	 */
	public void drawHud(GameContainer container, StateBasedGame game, Graphics g, Color filter, Vec2 currentScreen) throws SlickException {
		this.hud.center.set(currentScreen);
		this.hud.render(container, game, g, filter);
	}
	
	/**
	 * Inicia um novo desenho.
	 * @param position Posição de início do desenho, em unidades do mundo.
	 */
	public void startDrawing(Vec2 position) {
		this.isDrawing = true;
		this.canDraw = true;
		this.drawingLength = 0;
		this.alphaEnding = 1.0f;

		Vec2 currentScreen = this.transform.worldToScreen(position);
		this.drawingEffectEmitter.setPosition(currentScreen.x, currentScreen.y, false);
		this.drawingEffectEmitter.spawnInterval.setMin(70);
		this.drawingEffectEmitter.spawnInterval.setMax(70);
		this.drawingEffectEmitter.replay();
		
		this.drawSegment(position);
	}
	
	/**
	 * Cria um segmento do desenho.
	 * @param position Posição de desenho, em unidades do mundo.
	 */
	public void drawSegment(Vec2 position) {	
		this.currentPosition = position;
		
		if (this.canDraw) {
			if (this.hud.usedOrbs < this.hud.totalOrbs) {
				//Verifica se no ponto existe algum elemento de colisão.
				Body b = Utils.getBodyAtPosition(this.world, position, new Vec2(1.0f,1.0f));
				if (b == null || b.getShapeList().isSensor()) {					
					if (this.drawingLength == 0) {
						this.hud.usedOrbs++;
						this.drawingPositions[this.drawingLength++].set(position);
						SoundPlayer.playActOnCreateDrawing(0.5f + Utils.getRandomNumber(5) / 10);
					} else {
						Vec2 diff = this.drawingPositions[this.drawingLength - 1].sub(position);
						float norm = diff.length();
						if (norm > DRAWING_SIZE - DRAWING_OFFSET && norm < DRAWING_SIZE + DRAWING_OFFSET) {
							this.hud.usedOrbs++;
							this.drawingPositions[this.drawingLength++].set(position);
							SoundPlayer.playActOnCreateDrawing(0.5f + Utils.getRandomNumber(5) / 10);
						}
					}
				} else {
					this.canDraw = false;
				}
			}
		}
	}
	
	/**
	 * Finaliza um desenho.
	 */
	public void finalizeDrawing() {
		if (this.drawingLength == 1) {
			this.hud.usedOrbs--;
		} else {
			
			BodyDef def = new BodyDef();
			def.isBullet = false;
			Body body = this.world.createBody(def);
			
			for (int i = 0; i < this.drawingLength - 1; i++) {
				PolygonDef sd = new PolygonDef();
				MassData data = new MassData(5.0f, LevelConfiguration.GROUND_FRICTION, 0);
				data.setObjectMass(sd);
				sd.userData = data;
				Utils.createStrokeRect(this.drawingPositions[i], this.drawingPositions[i + 1], STROKE_HEIGHT, body, sd);
			}
			body.setMassFromShapes();
			body.setUserData("draw");
			this.drawings.add(body);
			
			SoundPlayer.playActFinishDrawing();
		}
		
		this.isDrawing = false;
	}
	
	/**
	 * Destrói todos os desenhos criados.
	 */
	public void destroyAllDrawings() {
		for (Body b : this.drawings) this.world.destroyBody(b);
		this.drawings.clear();
		this.resetOrbs();
	}
}
