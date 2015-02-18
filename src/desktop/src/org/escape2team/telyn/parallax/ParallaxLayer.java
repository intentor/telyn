package org.escape2team.telyn.parallax;

import org.escape2team.telyn.core.GameObject;
import org.jbox2d.common.Vec2;

/**
 * Representa um camada de paralaxe do jogo.
 */
public abstract class ParallaxLayer implements GameObject {
	/** Identificador da camada. Será positivo se à frente da tela e negativo se atrás. */
	protected int layerID;
	/** Tamanho da camada. */
	protected Vec2 layerSize;
	/** Posição da camada na tela. */
	protected Vec2 layerPosition;
	/** Posição do objeto na camada. */
	protected Vec2 objectPosition;
	/** Velocidade da camada. */
	protected Vec2 speed;
	
	/**
	 * Cria uma nova camada de paralaxe.
	 * @param id				Identificador da camada. Será positivo se à frente da tela e negativo se atrás.
	 * @param layerSize			Tamanho da camada.
	 * @param layerPosition		Posição da camada na tela.
	 * @param objectPosition	Posição do objeto na camada.
	 * @param speed				Velocidade da camada em função do movimento do jogador (1 = exato movimento do jogador).
	 */
	public ParallaxLayer(int id, Vec2 layerSize, Vec2 layerPosition, Vec2 objectPosition, Vec2 speed) {
		this.layerID = id;
		this.layerSize = layerSize;
		this.layerPosition = layerPosition;
		this.objectPosition = objectPosition;
		this.speed = speed;
	}
	
	/**
	 * Obtém o identificador da camada.
	 * @return Número que identifica a camada.
	 */
	public int getLayerID() {
		return this.layerID;
	}
	
	/**
	 * Obtém a posição da camada.
	 * @return Número que identifica a camada.
	 */
	public Vec2 getPosition() {
		return this.layerPosition;
	}

	/**
	 * Obtém a largura da camada.
	 * @return Largura da camada.
	 */
	public float getLayerWidth() {
		return this.layerSize.x;
	}

	/**
	 * Obtém a altura da camada.
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
