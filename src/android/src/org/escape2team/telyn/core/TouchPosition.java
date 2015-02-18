package org.escape2team.telyn.core;

/**
 * Representa uma posi��o no touch screen do aparelho celular.
 */
public class TouchPosition {
	/** Posi��o no eixo X. */
	public float x;
	/** Posi��o no eixo Y. */
	public float y;
	/** Identificador da posi��o. */
	public int id;
	
	/**
	 * Construtor da classe.
	 * @param x		Posi��o no eixo X.
	 * @param y		Posi��o no eixo Y.
	 * @param id	Identificador da posi��o.
	 */
	public TouchPosition(float x, float y, int id) {
		this.x = x;
		this.y = y;
		this.id = id;
	}
	
	/**
	 * Define uma nova posi��o para o ponto.
	 * @param x Posi��o no eixo X.
	 * @param y Posi��o no eixo Y.
	 */
	public void set(float x, float y) {
		this.x = x;
		this.y = y;
	}
}
