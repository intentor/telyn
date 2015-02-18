package org.escape2team.telyn.core;

/**
 * Listener de eventos do jogo.
 */
public interface GameInputListener {	
	/**
	 * Pausa o jogo.
	 */
	void pauseGame();
	
	/**
	 * Evento de movimenta��o do mouse.
	 * @param x Posi��o no eixo X, em unidades de tela.
	 * @param y Posi��o no eixo Y, em unidades de tela.
	 */
	void mouseMove(float x, float y);
	
	/**
	 * Despausa o jogo.
	 */
	void unpauseGame();
	
	/**
	 * Indica input de movimento para a direita.
	 */
	void moveLeft();

	/**
	 * Indica input de movimento para a esquerda.
	 */
	void moveRight();

	/**
	 * Indica input de pulo.
	 */
	void jump();
	
	/**
	 * Indica input para �nicio de sele��o de esta��o.
	 */
	void seasonStartSelection();
	
	/**
	 * Indica input de movimenta��o para mudan�a de esta��o.
	 * @param factor 	Porcentagem do movimento de mudan�a de esta��o (0 [0%] a 1 [100%]);
	 * @param current	Esta��o atual.
	 * @param over		Esta��o na qual o jogador est� com o mouse sobre.
	 */
	void seasonChanging(float factor, Seasons current, Seasons over);
	
	/**
	 * Indica mudan�a de esta��o.
	 * @param oldSeason Esta��o anterior.
	 * @param newSeason Esta��o nova.
	 */
	void seasonChanged(Seasons oldSeason, Seasons newSeason);
	
	/**
	 * Inicia o desenho de um objeto na posi��o informada.
	 * @param x Posi��o no eixo X.
	 * @param y Posi��o no eixo Y. 
	 */
	void drawBegin(float x, float y);
	
	/**
	 * Adiciona um segmento ao desenho de um objeto na posi��o informada.
	 * @param x Posi��o no eixo X.
	 * @param y Posi��o no eixo Y.
	 */
	void drawAddSegment(float x, float y);
	
	/**
	 * Finaliza o desenho de um objeto.
	 */
	void drawFinalyze();
	
	/**
	 * Indica que todos os desenhos devem ser destru�dos.
	 */
	void drawingsDestroyAll();
}
