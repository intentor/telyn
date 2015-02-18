package org.escape2team.telyn.core;

import org.escape2team.telyn.states.Loader;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Desenha o logo do jogo.
 */
public class LogoDrawer implements GameObject {	
	/** Velocidade do fade do logo. */
	private static final float FADE_LOGO_SPEED = 0.02f;
	/** Velocidade do fade do fundo do logo. */
	private static final float FADE_LOGO_BACK_SPEED = 0.03f;
	/** Velocidade do fade do texto. */
	private static final float FADE_TEXT_SPEED = 0.05f;
	/** Frente do logo jogo. */
	private Image logoFront;
	/** Fundo do logo do jogo. */
	private Image logoBack;
	/**  Fonte da tela do logo. */
	private Font font;
	/** Cor para uso no fade da frente do logo. */
	private Color fadeFilterLogoFront;
	/** Cor para uso no fade do fundo do logo. */
	private Color fadeFilterLogoBack;
	/** Cor para uso no fade do texto. */
	private Color fadeFilterText;
	/** Fator para definição de fade in/out do texto. */
	private int factorText;
	/** Fator para definição de fade in/out do fundo do logo. */
	private int factorLogo;
	/** Indica se o renderizador está dormindo (para evitar render e update). */
	public boolean sleep;
	/** Indica se o logo está ativo na tela. */
	public boolean isActive;
	
	/**
	 * Cria um novo desenhista de logo.
	 * @param font Fonte da tela do logo.
	 */
	public LogoDrawer(Font font) {
		this.font = font;
	}
	
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		this.logoFront = new Image("data/sprites/logo.png");
		this.logoBack = new Image("data/sprites/logo_bg.png");
		this.fadeFilterText = new Color(Color.white);
		this.fadeFilterLogoFront = new Color(Color.white);
		this.fadeFilterLogoBack = new Color(Color.white);
		this.factorText = -1;
		this.factorLogo = -1;
		this.sleep = false;
		this.isActive = true;
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) {
	
	}

	@Override
	public void leave(GameContainer container, StateBasedGame game) throws SlickException {
		
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g, Color filter) throws SlickException {
		if (!this.sleep) {
			this.logoBack.draw((container.getWidth() - this.logoFront.getWidth()) / 2, 30, this.fadeFilterLogoBack);
			this.logoFront.draw((container.getWidth() - this.logoFront.getWidth()) / 2, 30, this.fadeFilterLogoFront);
			Utils.drawStringCenter(this.font, Loader.LOCALIZATION.getString("moveToStart"), 400, this.fadeFilterText);
		}
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		if (!this.sleep) {		
			/* Estando ativo, realiza apenas fade in/out do texto.
			 * Não estando mais ativo, realiza fade out do texto e
			 * do logo. */
			if (this.isActive) {
				//Fade in/out do texto.
				this.fadeFilterText.a += this.factorText * FADE_TEXT_SPEED;	
				if (this.fadeFilterText.a < 0 || this.fadeFilterText.a > 1.0f) this.factorText = -this.factorText;

				//Fade in/out do fundo do logo.
				this.fadeFilterLogoBack.a += this.factorLogo * FADE_LOGO_BACK_SPEED;	
				if (this.fadeFilterLogoBack.a < 0.2f || this.fadeFilterLogoBack.a > 1.0f) this.factorLogo = -this.factorLogo;
			} else {
				if (this.fadeFilterLogoFront.a > 0) this.fadeFilterLogoFront.a -= FADE_LOGO_SPEED;
				if (this.fadeFilterLogoBack.a > 0) this.fadeFilterLogoBack.a -= FADE_LOGO_SPEED;
				if (this.fadeFilterText.a > 0) this.fadeFilterText.a -= FADE_TEXT_SPEED;
				
				if (this.fadeFilterLogoFront.a < 0 && this.fadeFilterText.a < 0) this.sleep = true;
			}
		}
	}
}
