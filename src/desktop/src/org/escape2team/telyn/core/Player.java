package org.escape2team.telyn.core;

import java.io.IOException;

import org.jbox2d.collision.shapes.PolygonDef;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.particles.ParticleSystem;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Representa o personagem jogador.
 */
public class Player implements GameObject {
	/** Frame no qual deverá ser tocado o som de passo. No frame 0 sempre é tocado. */
	private static final int RUNNING_STEP_FRAME = 16;	
	/** Frame no qual deverá ser tocado o som de empurrar. */
	private static final int PUSHING_STEP_FRAME = 1;
	/** Máximo tamanho de queda antes que o jogador morra, em unidades do mundo. */
	private static final float MAX_FALLING_SIZE = 6.0f;
	/** Atraso na avaliação de pulo do personagem, em milissegundos. */
	private static final int PLAYER_JUMP_DELAY = 1000;
	/** Duração da animação de morte, em milissegundos. */
	private static final int DYING_ANIMATION_DURATION = 1500;
	/** Tamanho da área de colisão do jogador em pixels. */
	private static final Vec2 PLAYER_SIZE = new Vec2(16, 64);
	/** Impulso de movimento do jogador nos eixos X e Y. */
	private static final Vec2 PLAYER_IMPULSE = new Vec2(2.8f, 6.0f);
	/** Mundo físico do jogo. */
	private World world;
	/** Objeto para conversões entre unidades de tela e mundo. */
	private ViewportTransform transform;
	/** Posição inicial do personagem. */
	private Vec2 initialPosition;
	/** Listener de colisões. */
	private PlayerContactHandler listener;
	/** Animação de personagem tocando harpa, sempre para a direita. */
	private Animation playing;
	/** Animação de personagem parado para a direita. */
	private Animation stoppedLeft;
	/** Animação de personagem parado para a esquerda. */
	private Animation stoppedRight;
	/** Animação de personagem correndo para a direita. */
	private Animation runningLeft;
	/** Animação de personagem correndo para a esquerda. */
	private Animation runningRight;
	/** Animação de personagem empurrando para a direita. */
	private Animation pushingLeft;
	/** Animação de personagem empurrando para a esquerda. */
	private Animation pushingRight;
	/** Animação de personagem subindo para a direita. */
	private Animation risingLeft;
	/** Animação de personagem subindo para a esquerda. */
	private Animation risingRight;
	/** Animação de personagem descendo para a direita. */
	private Animation fallingLeft;
	/** Animação de personagem descendo para a esquerda. */
	private Animation fallingRight;
	/** Animação atual do personagem. */
	private Animation currentAnimation;
	/** Efeitos de partículas de morte. */
	private ParticleSystem dying;
	/** Corpo do personagem. */
	private Body body;
	/** Indica se o personagem está com seu rosto voltado para a direita. **/
	private boolean isFacingRight;
	/** Indica se o personagem está tocando harpa. */
	private boolean isPlayingHarp;
	/** Indica se o personagem está em pulo. */
	private boolean isJumping;
	/** Indica se o personagem está morrendo (início da animação de morte). */
	private boolean isDying;
	/** Indica se o personagem está morto. */
	private boolean isDead;
	/** Contador para animações. */
	private int counter;
	/** Posição no eixo Y do início do pulo. */
	public float jumpStartY;
	
	/**
	 * Construtor da classe.
	 * @param world				Mundo físico do jogo.
	 * @param transform			Objeto para conversões entre unidades de tela e mundo.
	 * @param initialPosition	Posição inicial do personagem.
	 */
	public Player(World world, ViewportTransform transform, Vec2 initialPosition, PlayerContactHandler listener) {
		this.listener = listener;
		this.world = world;
		this.transform = transform;
		this.initialPosition = initialPosition;
	}
	
	//GAME OBJECT=====================================================================

	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		this.createPlayer();
		this.loadAnimations();
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) {
		this.jumpStartY = this.body.getWorldCenter().y;
	}

	@Override
	public void leave(GameContainer container, StateBasedGame game) throws SlickException {
		
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g, Color filter) throws SlickException {
		if (this.isDying) {
			Vec2 pos = this.transform.worldToScreen(this.getCurrentWorldCenterPosition());
			this.dying.render(pos.x, pos.y);
		}
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		this.counter += delta;
		
		if (this.isDying) {
			this.dying.update(delta);
			if (this.counter > DYING_ANIMATION_DURATION) {
				this.isDead = true;
			}
		} else if (this.body.isFrozen() || this.listener.isDead()) { 
			/* Caso o personagem tenha saído da área 
			 * do mundo ou colidido com algo que cause
			 * morte, indica morte.*/
			this.dye();
		} else {
			if (this.isPlayingHarp) {
				this.currentAnimation = this.playing;
			} else if (!this.isJumping) {
				//Não estando em pulo, exibe animação conforme movimento do personagem.
				if (this.listener.isPushable()) {
					if (this.isFacingRight) {
						this.currentAnimation = this.pushingRight;
					} else {
						this.currentAnimation = this.pushingLeft;
					}
					
					//Estando parado, não atualiza animação e retorna-a ao primeiro quadro.
					if (this.body.getLinearVelocity().x > -0.1f && this.body.getLinearVelocity().x < 0.1f) {
						this.currentAnimation.restart();
					} else {
						//Caso a animação esteja em um determinado frame, toca o som.
						if (this.currentAnimation.getFrame() == PUSHING_STEP_FRAME) {
							SoundPlayer.playMovPushing();
						}						
						this.currentAnimation.update(delta);
					}
				} else {
					if (this.body.getLinearVelocity().x > -0.1f && this.body.getLinearVelocity().x < 0.1f) { //Parado.
						this.body.putToSleep();
						if (this.isFacingRight) {
							this.currentAnimation = this.stoppedRight;
						} else {
							this.currentAnimation = this.stoppedLeft;
						}
					} else { //Movimento.
						if (this.isFacingRight) {
							this.currentAnimation = this.runningRight;
						} else {
							this.currentAnimation = this.runningLeft;
						}
						
						//Caso a animação esteja em um determinado frame, toca o som.
						int frame = this.currentAnimation.getFrame();
						if (frame == 0 || this.currentAnimation.getFrame() == RUNNING_STEP_FRAME) {
							SoundPlayer.playMovStep((frame == RUNNING_STEP_FRAME ? 0.8f : 1.0f));
						}
					}
					
					this.currentAnimation.update(delta);
				}
	
			} else {
				//Estando em pulo, exibe animação de pulo.
				if (this.isFacingRight) {
					if (this.body.m_linearVelocity.y > 0) { //Subindo.
						this.currentAnimation = this.risingRight;
						this.risingLeft.update(delta);
						this.risingRight.update(delta);
					} else { //Descendo.
						this.currentAnimation = this.fallingRight;
						this.fallingLeft.update(delta);
						this.fallingRight.update(delta);
					}
				} else {
					if (this.body.m_linearVelocity.y > 0) { //Subindo.
						this.currentAnimation = this.risingLeft;
						this.risingLeft.update(delta);
						this.risingRight.update(delta);
					} else { //Descendo.
						this.currentAnimation = this.fallingLeft;
						this.fallingLeft.update(delta);
						this.fallingRight.update(delta);
					}
				}
			}
			
			/** Identifica se o personagem está em pulo a partir
			 * da análise do contato do sensor do corpo do personagem
			 * com o solo. */
			if (this.counter > PLAYER_JUMP_DELAY) {
				if (this.listener.canJump()) {
					//Caso o jogador tenha acabado de tocar o solo de um pulo, toca som.
					if (this.isJumping) {
						SoundPlayer.playMovJumpOnGround();
						this.isJumping = false;
					}
					
					this.counter = 0;
					this.jumpStartY = this.body.getWorldCenter().y;
					
				} else {
					//Verifica se o personagem morreu por altura.
					if (Math.abs(this.body.getWorldCenter().y - this.jumpStartY) > MAX_FALLING_SIZE) {
						this.dye();
					}
					
					this.body.wakeUp();
					this.isJumping = true;
				}
			}
		}
	}
	
	//MÉTODOS DE MOVIMENTAÇÃO=========================================================
	
	/**
	 * Inicia a animação do personagem tocando harpa.
	 */
	public void startPlayHarp() {
		this.playing.restart();
		this.isPlayingHarp = true;
	}
	
	/**
	 * Finaliza a animação do personagem tocando harpa.
	 */
	public void stopPlayHarp() {
		this.isPlayingHarp = false;
	}
	
	/**
	 * Atualiza a animação de tocar harpa.
	 */
	public void playHarp() {
		int frame = this.playing.getFrame() + 1;
		if (frame >= this.playing.getFrameCount()) frame = 0;
		this.playing.setCurrentFrame(frame);
	}
	
	/**
	 * Movimenta o personagem para a esquerda.
	 */
	public void moveLeft() {
		if (!this.isDying) {
			this.isFacingRight = false;
				
			if (!this.listener.isBlockedLeft()) {
				this.body.wakeUp();
				if (this.body.m_linearVelocity.x > -PLAYER_IMPULSE.x) {
					this.body.m_linearVelocity.x = -PLAYER_IMPULSE.x;
					//this.body.applyImpulse(new Vec2(-PLAYER_IMPULSE.x, 0.0f), this.body.getWorldCenter());
				}
			}
		}
	}
	
	/** 
	 * Movimenta o personagem para a direita.
	 */
	public void moveRight() {
		if (!this.isDying) {
			this.isFacingRight = true;
	
			if (!this.listener.isBlockedRight()) {
				this.body.wakeUp();
				if (this.body.m_linearVelocity.x < PLAYER_IMPULSE.x) {
					this.body.m_linearVelocity.x = PLAYER_IMPULSE.x;
					//this.body.applyImpulse(new Vec2(PLAYER_IMPULSE.x, 0.0f), this.body.getWorldCenter());
				}
			}
		}
	}
	
	/**
	 * Realiza pulo do personagem.
	 * @return Indica se o jogador pode ou não pular.
	 */
	public boolean jump() {
		boolean canJump = false;
		
		if (!this.isDying) {
			if (this.listener.canJump() && !this.isJumping) {
				//Toca o som de pulo.
				SoundPlayer.playMovJumpRising();
				
				this.risingLeft.setLooping(false);
				this.risingRight.setLooping(false);
				this.risingRight.restart();
				this.risingLeft.restart();
						
				this.fallingLeft.setLooping(false);
				this.fallingRight.setLooping(false);
				this.fallingLeft.restart();
				this.fallingRight.restart();
				
				this.isJumping = true;
				this.jumpStartY = this.body.getWorldCenter().y;
				this.body.applyImpulse(new Vec2(0.0f, PLAYER_IMPULSE.y), this.body.getWorldCenter());				
				
				this.counter = 0;
				
				canJump = true;
			}
		}
		
		return canJump;
	}

	//MÉTODOS DE APOIO================================================================
	
	/**
	 * Indica morte do personagem.
	 */
	public void dye() {
		SoundPlayer.playActPlayerDeath();
		this.counter = 0;
		this.isDying = true;
	}
	
	/**
	 * Revive o jogador.
	 */
	public void revive() {
		SoundPlayer.playActPlayerRevive();
		this.listener.revive();
		this.counter = 0;
		this.isDying = false;
		this.isDead = false;
		this.jumpStartY = this.body.getWorldCenter().y;
		
		//Caso o personagem tenha caído fora do mundo, recria-o.
		if (this.body.isFrozen()) {
			this.world.destroyBody(this.body);
			this.createPlayer();
		}		
		
		this.wakeUp();
	}
	
	/**
	 * Indica se o personagem está morto.
	 * @return Valor booleano indicando se o personagem está morto.
	 */
	public boolean isDead() {
		return this.isDead;
	}
	
	/**
	 * Obtém a posição do centro de massa do personagem no mundo.
	 * return Vetor em unidades do mundo com a posição do personagem.
	 */
	public Vec2 getCurrentWorldCenterPosition() {
		return this.body.getWorldCenter();
	}
	
	/**
	 * Coloca o jogador para dormir no mundo do jogo.
	 */
	public void putToSleep() {
		this.body.putToSleep();
	}
	
	/**
	 * Acorda o jogador no mundo do jogo.
	 */
	public void wakeUp() {
		this.body.wakeUp();
	}
	
	/**
	 * Define a posição do jogador, em coordenadas do mundo.
	 * @param pos Posição do jogador.
	 */
	public void setPlayerPosition(Vec2 pos) {
		this.body.setXForm(pos, 0);
		this.jumpStartY = pos.y;
	}
	
	/**
	 * Obtém a imagem que representa o frame atual.
	 * return Imagem do frame atual.
	 */
	public Image getCurrentFrame() {
		return this.currentAnimation.getCurrentFrame();
	}
	
	/** 
	 * Cria o objeto físico que representa o jogador.
	 */
	private void createPlayer() {
		float width = this.transform.convertPixelsInWorldScale(PLAYER_SIZE.x / 2);
		float height = this.transform.convertPixelsInWorldScale(PLAYER_SIZE.y / 2);
		
		BodyDef bd = new BodyDef();
		bd.position.set(this.initialPosition.x, this.initialPosition.y);
		bd.fixedRotation = true; //Para evitar rotação do personagem.
		
		//Definições do corpo.
		
		//Parte de cima do corpo.
		PolygonDef shapeTop = new PolygonDef();
		shapeTop.vertices.add(new Vec2(-width, -height * 0.8f));
		shapeTop.vertices.add(new Vec2(width, -height * 0.8f));
		shapeTop.vertices.add(new Vec2(width, height));
		shapeTop.vertices.add(new Vec2(-width, height));
		shapeTop.density = 1.0f;
		shapeTop.friction = 0.9f;
		shapeTop.restitution = 0.0f;
		
		//Base do corpo.
		PolygonDef shapeBottom = new PolygonDef();
		shapeBottom.vertices.add(new Vec2(-width * 0.3f, -height));
		shapeBottom.vertices.add(new Vec2(width * 0.3f, -height));
		shapeBottom.vertices.add(new Vec2(width, -height * 0.8f));
		shapeBottom.vertices.add(new Vec2(-width, -height * 0.8f));
		shapeBottom.density = shapeTop.density;
		shapeBottom.friction = shapeTop.friction;
		shapeBottom.restitution = shapeTop.restitution;
		
		//Tamanho do sensor (largura ou altura).
		float sensorSize = this.transform.convertPixelsInWorldScale(2);
		//Porcentagem do tamanho do sensor.
		float size = 0.8f;
		
		//Sensor de colisão de solo.
		PolygonDef groundSensor = new PolygonDef();
		groundSensor.isSensor = true;
		groundSensor.userData = "groundsensor";
		groundSensor.setAsBox(width * 0.75f, sensorSize, new Vec2(0, -1.0f * (sensorSize + height)), 0);
		
		//Sensor de colisão direito.
		PolygonDef walkingSensorLeft = new PolygonDef();
		walkingSensorLeft.isSensor = true;
		walkingSensorLeft.userData = "sensorleft";
		walkingSensorLeft.setAsBox(sensorSize, height * size, new Vec2(-(sensorSize + width), 0), 0);
		
		//Sensor de colisão esquerdo.
		PolygonDef walkingSensorRight = new PolygonDef();
		walkingSensorRight.isSensor = true;
		walkingSensorRight.userData = "sensorright";
		walkingSensorRight.setAsBox(sensorSize, height * size, new Vec2(sensorSize + width, 0), 0);
		
		this.body = this.world.createBody(bd);
		this.body.setBullet(true); //Tratado como bullet para contínua detecção de colisão.
		this.body.createShape(shapeTop);
		this.body.createShape(shapeBottom);
		this.body.createShape(groundSensor);
		this.body.createShape(walkingSensorLeft);
		this.body.createShape(walkingSensorRight);
		this.body.setMassFromShapes();
		this.body.setUserData(this);
		
		this.isFacingRight = true;
		this.isDead = false;
		this.isJumping = false;
		this.counter = 0;
	}
	
	/**
	 * Carrega as animações do personagem.
	 * @throws SlickException 
	 */
	private void loadAnimations() throws SlickException {
		PackedSpriteSheet pack = new PackedSpriteSheet("data/sprites/druid.def", Image.FILTER_NEAREST);
		
		this.playing = Utils.loadAnimation(pack, "playing", 21, 15);		
		
		this.risingLeft = Utils.loadAnimation(pack, "rising-left", 8, 36);
		this.risingRight = Utils.loadAnimation(pack, "rising-right", 8, 36);
		
		this.fallingLeft = Utils.loadAnimation(pack, "falling-left", 14, 120);
		this.fallingRight = Utils.loadAnimation(pack, "falling-right", 14, 120);
		
		this.pushingLeft = Utils.loadAnimation(pack, "pushing-left", 14, 75);
		this.pushingRight = Utils.loadAnimation(pack, "pushing-right", 14, 75);
		
		this.runningLeft = Utils.loadAnimation(pack, "running-left", 33, 30);
		this.runningRight = Utils.loadAnimation(pack, "running-right", 33, 30);
		
		this.stoppedLeft = Utils.loadAnimation(pack, "stopped-left", 25, 150);
		this.stoppedRight = Utils.loadAnimation(pack, "stopped-right", 25, 150);
		
		try {
			this.dying = Utils.loadParticleSystem("data/particles/dying.xml");
		} catch (IOException e) {
			e.printStackTrace();
		}
				
		currentAnimation = this.stoppedRight;
	}
}
