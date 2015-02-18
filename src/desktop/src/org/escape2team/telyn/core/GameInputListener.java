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
	 * Evento de movimentação do mouse.
	 * @param x Posição no eixo X, em unidades de tela.
	 * @param y Posição no eixo Y, em unidades de tela.
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
	 * Indica input para ínicio de seleção de estação.
	 */
	void seasonStartSelection();
	
	/**
	 * Indica input de movimentação para mudança de estação.
	 * @param factor 	Porcentagem do movimento de mudança de estação (0 [0%] a 1 [100%]);
	 * @param current	Estação atual.
	 * @param over		Estação na qual o jogador está com o mouse sobre.
	 */
	void seasonChanging(float factor, Seasons current, Seasons over);
	
	/**
	 * Indica mudança de estação.
	 * @param oldSeason Estação anterior.
	 * @param newSeason Estação nova.
	 */
	void seasonChanged(Seasons oldSeason, Seasons newSeason);
	
	/**
	 * Inicia o desenho de um objeto na posição informada.
	 * @param x Posição no eixo X.
	 * @param y Posição no eixo Y. 
	 */
	void drawBegin(float x, float y);
	
	/**
	 * Adiciona um segmento ao desenho de um objeto na posição informada.
	 * @param x Posição no eixo X.
	 * @param y Posição no eixo Y.
	 */
	void drawAddSegment(float x, float y);
	
	/**
	 * Finaliza o desenho de um objeto.
	 */
	void drawFinalyze();
	
	/**
	 * Indica que todos os desenhos devem ser destruídos.
	 */
	void drawingsDestroyAll();
}
