package org.escape2team.telyn.core;

/**
 * Representa uma posição no touch screen do aparelho celular.
 */
public class TouchPosition {
	/** Posição no eixo X. */
	public float x;
	/** Posição no eixo Y. */
	public float y;
	/** Identificador da posição. */
	public int id;
	
	/**
	 * Construtor da classe.
	 * @param x		Posição no eixo X.
	 * @param y		Posição no eixo Y.
	 * @param id	Identificador da posição.
	 */
	public TouchPosition(float x, float y, int id) {
		this.x = x;
		this.y = y;
		this.id = id;
	}
	
	/**
	 * Define uma nova posição para o ponto.
	 * @param x Posição no eixo X.
	 * @param y Posição no eixo Y.
	 */
	public void set(float x, float y) {
		this.x = x;
		this.y = y;
	}
}
