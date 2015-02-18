package org.escape2team.telyn.parallax;

import org.escape2team.telyn.core.GameObject;
import org.jbox2d.common.Vec2;

/**
 * Representa um camada de paralaxe do jogo.
 */
public abstract class ParallaxLayer implements GameObject {
	/** Identificador da camada. Ser� positivo se � frente da tela e negativo se atr�s. */
	protected int layerID;
	/** Tamanho da camada. */
	protected Vec2 layerSize;
	/** Posi��o da camada na tela. */
	protected Vec2 layerPosition;
	/** Posi��o do objeto na camada. */
	protected Vec2 objectPosition;
	/** Velocidade da camada. */
	protected Vec2 speed;
	
	/**
	 * Cria uma nova camada de paralaxe.
	 * @param id				Identificador da camada. Ser� positivo se � frente da tela e negativo se atr�s.
	 * @param layerSize			Tamanho da camada.
	 * @param layerPosition		Posi��o da camada na tela.
	 * @param objectPosition	Posi��o do objeto na camada.
	 * @param speed				Velocidade da camada em fun��o do movimento do jogador (1 = exato movimento do jogador).
	 */
	public ParallaxLayer(int id, Vec2 layerSize, Vec2 layerPosition, Vec2 objectPosition, Vec2 speed) {
		this.layerID = id;
		this.layerSize = layerSize;
		this.layerPosition = layerPosition;
		this.objectPosition = objectPosition;
		this.speed = speed;
	}
	
	/**
	 * Obt�m o identificador da camada.
	 * @return N�mero que identifica a camada.
	 */
	public int getLayerID() {
		return this.layerID;
	}
	
	/**
	 * Obt�m a posi��o da camada.
	 * @return N�mero que identifica a camada.
	 */
	public Vec2 getPosition() {
		return this.layerPosition;
	}

	/**
	 * Obt�m a largura da camada.
	 * @return Largura da camada.
	 */
	public float getLayerWidth() {
		return this.layerSize.x;
	}

	/**
	 * Obt�m a altura da camada.
	 * @return Altura da camada.
	 */
	public float getLayerHeight() {
		return this.layerSize.y;
	}
	
	/**
	 * Movimenta a camada.
	 * @param size Quantidade de movimento, em pixels.
	 */
	public void move(Vec2 size) {
		this.layerPosition.x -= this.speed.x * size.x;
		this.layerPosition.x = this.layerPosition.x % this.getLayerWidth();
		this.layerPosition.y += this.speed.y * size.y;
	}
}
