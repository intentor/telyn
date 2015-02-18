package org.escape2team.telyn.core;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

/**
 * Tocador de sons do jogo.
 */
public class SoundPlayer {
	/** Instância do tocador de som. */
	private static SoundPlayer INSTANCE; 
	
	//Arquivos de sons.
	private Sound sndMovStep;
	private Sound sndMovPushing;
	private Sound sndMovJumpRising;
	private Sound sndMovJumpOnGround;
	private Sound sndActCatchingOrb;
	private Sound sndActSeasonsWheel;
	private Sound sndActOnCreateDrawing;
	private Sound sndActFinishDrawing;
	private Sound sndActHarpOnChangingSeasons;
	private Sound sndActSeasonChanged;
	private Sound sndActTremor;
	private Sound sndplayActPlayerDeath;
	private Sound sndplayActPlayerRevive;
	private Sound sndObjPendulumImpulse;
	private Sound sndObjRotatorMotor;
	private Sound sndOthMenuOption;
	
	/**
	 * Cria um novo tocador de sons.
	 * @throws SlickException 
	 */
	public SoundPlayer() throws SlickException {
		//Cria todos os sons necessários.
		this.sndMovStep = new Sound("data/soundfx/mov-step.ogg");
		this.sndMovPushing = new Sound("data/soundfx/mov-pushing.ogg");
		this.sndMovJumpRising = new Sound("data/soundfx/mov-jump_rising.ogg");
		this.sndMovJumpOnGround = new Sound("data/soundfx/mov-jump_ground.ogg");
		this.sndActCatchingOrb = new Sound("data/soundfx/act-orb.ogg");
		this.sndActSeasonsWheel = new Sound("data/soundfx/act-wheel.ogg");
		this.sndActOnCreateDrawing = new Sound("data/soundfx/act-draw_create.ogg");
		this.sndActFinishDrawing = new Sound("data/soundfx/act-draw_finish.ogg");
		this.sndActHarpOnChangingSeasons = new Sound("data/soundfx/act-harp_seasons.ogg");
		this.sndActSeasonChanged = new Sound("data/soundfx/act-season_changed.ogg");
		this.sndActTremor = new Sound("data/soundfx/act_tremor.ogg");
		this.sndplayActPlayerDeath = new Sound("data/soundfx/play-death.ogg");
		this.sndplayActPlayerRevive = new Sound("data/soundfx/play-revive.ogg");
		this.sndObjPendulumImpulse = new Sound("data/soundfx/obj-pendulum.ogg");
		this.sndObjRotatorMotor = new Sound("data/soundfx/obj-rotator.ogg");
		this.sndOthMenuOption = new Sound("data/soundfx/oth-menu_option.ogg"); 
	}
	
	//MÉTODOS DE APOIO================================================================
	
	/**
	 * Cria a instância do tocador de som.
	 */
	public static void createInstance() throws SlickException {
		if (INSTANCE == null) INSTANCE = new SoundPlayer();
	}

	//SOM DE MOVIMENTAÇÃO (Mov)=======================================================
	
	/**
	 * Toca o som de passo.
	 * @param pitch Tonalidade do som.
	 */
	public static Sound playMovStep(float pitch) {
		if (!INSTANCE.sndMovStep.playing()) INSTANCE.sndMovStep.play(pitch, 1.0f);
		return INSTANCE.sndMovStep;
	}
	
	/**
	 * Toca o som de empurrar objetos
	 */
	public static Sound playMovPushing() {
		if (!INSTANCE.sndMovPushing.playing()) INSTANCE.sndMovPushing.play();
		return INSTANCE.sndMovPushing;
	}

	/**
	 * Toca o som de subida do pulo.
	 */
	public static Sound playMovJumpRising() {
		INSTANCE.sndMovJumpRising.play();
		return INSTANCE.sndMovJumpRising;
	}
	
	/**
	 * Toca o som de quando o personagem atinge o solo após um pulo.
	 */
	public static Sound playMovJumpOnGround() {
		INSTANCE.sndMovJumpOnGround.play();
		return INSTANCE.sndMovJumpOnGround;
	}
	
	//SOM DE AÇÕES (Act)==============================================================
	
	/**
	 * Toca o som de quando se pega uma orb.
	 */
	public static Sound playActCatchingOrb() {
		INSTANCE.sndActCatchingOrb.play();
		return INSTANCE.sndActCatchingOrb;
	}
	
	/**
	 * Toca o som de abertura da roda das estações.
	 * @param pitch Tonalidade do som.
	 */
	public static Sound playActSeasonsWheel(float pitch) {
		if (INSTANCE.sndActSeasonsWheel.playing()) INSTANCE.sndActSeasonsWheel.stop();
		INSTANCE.sndActSeasonsWheel.play(pitch, 1.0f);
		return INSTANCE.sndActSeasonsWheel;
	}
	
	/**
	 * Toca o som de inserção de uma nova junção de desenho.
	 * @param pitch Tonalidade do som.
	 */
	public static Sound playActOnCreateDrawing(float pitch) {
		if (!INSTANCE.sndActOnCreateDrawing.playing()) INSTANCE.sndActSeasonsWheel.stop();
		INSTANCE.sndActOnCreateDrawing.play(pitch, 1.0f);
		return INSTANCE.sndActOnCreateDrawing;
	}
	
	/**
	 * Toca o som de término de desenho.
	 */
	public static Sound playActFinishDrawing() {
		INSTANCE.sndActFinishDrawing.play();
		return INSTANCE.sndActFinishDrawing;
	}
	
	/**
	 * Toca o som da harpa ao trocar de estações.
	 * @param pitch Tonalidade do som.
	 */
	public static Sound playActHarpOnChangingSeasons(float pitch) {
		if (INSTANCE.sndActHarpOnChangingSeasons.playing()) INSTANCE.sndActHarpOnChangingSeasons.stop(); 
		INSTANCE.sndActHarpOnChangingSeasons.play(pitch, 1.0f);
		return INSTANCE.sndActHarpOnChangingSeasons;
	}
	
	/**
	 * Toca o som de troca de estação.
	 */
	public static Sound playActSeasonChanged() {
		if (INSTANCE.sndActSeasonChanged.playing()) INSTANCE.sndActSeasonChanged.stop();
		INSTANCE.sndActSeasonChanged.play();
		return INSTANCE.sndActSeasonChanged;
	}
	
	/**
	 * Toca o som de morte do jogador.
	 */
	public static Sound playActPlayerDeath() {
		INSTANCE.sndplayActPlayerDeath.play();
		return INSTANCE.sndplayActPlayerDeath;
	}
	
	/**
	 * Toca o som de retorno do jogador à vida.
	 */
	public static Sound playActPlayerRevive() {
		INSTANCE.sndplayActPlayerRevive.play();
		return INSTANCE.sndplayActPlayerRevive;
	}
	
	/**
	 * Toca o som de tela tremendo.
	 */
	public static Sound playActTremor() {
		INSTANCE.sndActTremor.play();
		return INSTANCE.sndActTremor;
	}
	
	//SOM DE OBJETOS (Obj)============================================================
	
	/**
	 * Toca o som de impulso do pêndulo.
	 */
	public static Sound playObjPendulumImpulse() {
		return INSTANCE.sndObjPendulumImpulse;
	}
	
	/**
	 * Toca o som de rotação do rotator.
	 */
	public static Sound playObjRotatorMotor() {
		return INSTANCE.sndObjRotatorMotor;
	}
	
	//SOM DIVERSOS (Oth)==============================================================
	
	/**
	 * Toca o som de opção de menu.
	 */
	public static Sound playOthMenuOption() {
		INSTANCE.sndOthMenuOption.play();
		return INSTANCE.sndOthMenuOption;
	}
}
