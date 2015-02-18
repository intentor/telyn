package org.escape2team.telyn.parallax;

import org.jbox2d.common.Vec2;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Representa um camada de imagens do jogo.
 */
public class AnimatedParallaxLayer extends ParallaxLayer {
	/** Animação a ser utilizada na camada. */
	private Animation layerAnimation;
	
	/**
	 * Cria uma nova camada de paralaxe.
	 * @param id				Identificador da camada. Será positivo se à frente da tela e negativo se atrás.
	 * @param anim				Animação da camada.
	 * @param layerSize			Tamanho da camada.
	 * @param layerPosition		Posição da camada na tela.
	 * @param objectPosition	Posição do objeto na camada.
	 * @param speed				Velocidade da camada em função do movimento do jogador (1 = exato movimento do jogador).
	 */
	public AnimatedParallaxLayer(int id, Animation anim, Vec2 layerSize, Vec2 layerPosition, Vec2 objectPosition, Vec2 speed) {
		super(id, layerSize, layerPosition, objectPosition, speed);
		this.layerAnimation = anim;
	}

	/**
	 * Inicia a animação da camada.
	 */
	public void animationStart() {
		if (this.layerAnimation != null) this.layerAnimation.start();
	}
	
	/**
	 * Pára a animação da camada.
	 */
	public void animationStop() {
		if (this.layerAnimation != null) this.layerAnimation.stop();
	}
	
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) {
		this.animationStart();
	}

	@Override
	public void leave(GameContainer container, StateBasedGame game) throws SlickException {
		this.animationStop();
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g, Color filter) throws SlickException {
		float posX = this.layerPosition.x + this.objectPosition.x;
		float posY = this.layerPosition.y + this.objectPosition.y;

		//Desenha a camada primeiramente na posição atual.
		this.layerAnimation.getCurrentFrame().draw(posX, posY, filter);		
		//Desenha a camada novamente, no fim de sua posição (assegurando a impressão de repetição).
		this.layerAnimation.getCurrentFrame().draw(posX + this.layerSize.x, posY, filter);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		this.layerAnimation.update(delta);
	}
}
