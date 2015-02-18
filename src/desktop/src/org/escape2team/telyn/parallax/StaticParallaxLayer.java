package org.escape2team.telyn.parallax;

import org.jbox2d.common.Vec2;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Representa um camada de imagens do jogo.
 */
public class StaticParallaxLayer extends ParallaxLayer {
	/** Imagem que representa a camada. */
	private Image layerImage;
	
	/**
	 * Cria uma nova camada de paralaxe.
	 * @param id				Identificador da camada. Ser� positivo se � frente da tela e negativo se atr�s.
	 * @param img				Imagem da camada.
	 * @param layerSize			Tamanho da camada.
	 * @param layerPosition		Posi��o da camada na tela.
	 * @param objectPosition	Posi��o do objeto na camada.
	 * @param speed				Velocidade da camada em fun��o do movimento do jogador (1 = exato movimento do jogador).
	 */
	public StaticParallaxLayer(int id, Image img, Vec2 layerSize, Vec2 layerPosition, Vec2 objectPosition, Vec2 speed) {
		super(id, layerSize, layerPosition, objectPosition, speed);
		this.layerImage = img;
	}
	
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) {
	
	}

	@Override
	public void leave(GameContainer container, StateBasedGame game) throws SlickException {
		this.layerImage.destroy();
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g, Color filter) throws SlickException {
		float posX = this.layerPosition.x + this.objectPosition.x;
		float posY = this.layerPosition.y + this.objectPosition.y;

		//Desenha a camada primeiramente na posi��o atual.
		this.layerImage.draw(posX - this.layerSize.x, posY, filter);
		//Desenha a camada primeiramente na posi��o atual.
		this.layerImage.draw(posX, posY, filter);
		//Desenha a camada novamente, no fim de sua posi��o (assegurando a impress�o de repeti��o).
		this.layerImage.draw(posX + this.layerSize.x, posY, filter);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		
	}
}
