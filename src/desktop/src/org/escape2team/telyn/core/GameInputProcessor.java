package org.escape2team.telyn.core;

import java.util.LinkedList;
import java.util.List;

import org.escape2team.telyn.states.LevelState;
import org.jbox2d.common.Vec2;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.PackedSpriteSheet;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Processador de inputs do jogo.
 */
public abstract class GameInputProcessor implements GameObject {
	/** Tamanho do movimento para troca de esta��o. */
	public static float CHANGE_SEASON_SIZE;
	/** Dist�ncia para exibi��o das imagens das esta��es. */
	public static float CHANGE_SEASON_PADDING;
	/** Tempo de espera, em milissegundos, do mouse parado para in�cio de desenho). */
	public static final float DRAWING_WAITING_TIME = 350;
	/** Raio do c�rculo ao redor do piv� de avalia��o de pressionamento de espera. */
	public static final float HOOK_CIRCLE_RADIUS = 10;
	/** Raio do c�rculo ao redor do piv� de avalia��o de pressionamento de espera. */
	public static final float SEASON_CIRCLE_RADIUS = 10;
	/** Estado do n�vel do jogo. */
	protected LevelState gameState;
	/** ID da esta��o do ano atual. Come�a sempre em 0 (NoSeason). */
	protected Seasons currentSeason;
	/** ID da esta��o do ano anterior. Come�a sempre em 0 (NoSeason). */
	protected Seasons lastSeason;
	/** Indica qual esta��o o jogador est� selecionando. */
	protected Seasons hookOnSeason;
	/** Posi��o de piv� para troca de esta��o. */
	protected Vec2 hook;
	/** Tamanho atual do movimento para troca de esta��o. */
	protected float hookSize;
	/** Tempo de espera sobre o piv� para desenho. */
	protected int waitTime;
	/** Indica se est� ocorrendo desenho de pontos de colis�o. */
	protected boolean isDrawing;
	/** Listeners de eventos de input. */
	protected List<GameInputListener> listeners;
	/** Imagens das esta��es. */
	protected Image[] seasons;
	/** Indica se o jogo est� pausado. */
	protected boolean isPaused;
	/** Indica se se est� selecionando esta��es. */
	protected boolean isSelectingSeason;
	
	/**
	 * Construtor da classe.
	 * @param state Estado de jogo dos n�veis.
	 */
	public GameInputProcessor(LevelState state) {
		this.gameState = state;
	}
		
	protected void initProcessor(GameContainer container, StateBasedGame game) throws SlickException {
		this.listeners = new LinkedList<GameInputListener>();
		
		this.seasons = new Image[4];
		PackedSpriteSheet pack = new PackedSpriteSheet("data/sprites/seasons.def", Image.FILTER_NEAREST);
		this.seasons[0] = pack.getSprite("spring");
		this.seasons[1] = pack.getSprite("summer");
		this.seasons[2] = pack.getSprite("autumn");
		this.seasons[3] = pack.getSprite("winter");
		
		//Inicia o jogo no ver�o.
		this.lastSeason = this.currentSeason = this.hookOnSeason = Seasons.Summer;
		
		this.isPaused = false;
	}
	
	/**
	 * Vibra o dispositivo de jogo, caso haja suporte.
	 * @param milliseconds Dura��o da vibra��o em millisegundos.
	 */
	public void vibrate(long milliseconds) {
		//Por padr�o, n�o executa a��o alguma.
	}
	
	/**
	 * Vibra o dispositivo de jogo, caso haja suporte.
	 * @param pattern 	Padr�o de vibra��o, no formato { espera, vibra��o, espera, vibra��o }
	 * @param repeat	Quantidade de repeti��es do padr�o.
	 */
	public void vibrate(long[] pattern, int repeat) {
		//Por padr�o, n�o executa a��o alguma.
	}
	
	/** 
	 * Obt�m a esta��o atual.
	 * @return Esta��o atual.
	 */
	public Seasons getCurrentSeason() {
		return this.currentSeason;
	}
	
	/**
     * Adiciona listener de eventos.
     * @param l Listener do evento.
     */
    public void addListener(GameInputListener l) {
        this.listeners.add(l);
    }
    
    /**
     * Remove listener de eventos.
     * @param l Listener do evento.
     */
    public void removeListener(GameInputListener l) {
        this.listeners.remove(l);
    }
    
    /**
	 * Executa evento de movimenta��o do mouse.
	 * @param x Posi��o no eixo X, em unidades de tela.
	 * @param y Posi��o no eixo Y, em unidades de tela.
	 */
    public void fireMouseMoveEvent(float x, float y) {
        for (GameInputListener l : this.listeners) l.mouseMove(x, y);
	}
    
    /**
	 * Executa input de pausa do jogo.
	 */
    public void firePauseGameEvent() {
    	this.isPaused = true;
        for (GameInputListener l : this.listeners) l.pauseGame();
	}

	/**
	 * Executa input de despausa do jogo.
	 */
    public void fireUnpauseGameEvent() {
    	this.isPaused = false;
        for (GameInputListener l : this.listeners) l.unpauseGame();
	}
    
    /**
	 * Executa input de movimento para a direita.
	 */
    public void fireMoveLeftEvent() {
        for (GameInputListener l : this.listeners) l.moveLeft();
	}

	/**
	 * Executa input de movimento para a esquerda.
	 */
    public void fireMoveRightEvent() {
        for (GameInputListener l : this.listeners) l.moveRight();
	}

	/**
	 * Executa input de pulo.
	 */
    public void fireJumpEvent() {
        for (GameInputListener l : this.listeners) l.jump();
	}
	
	/**
	 * Executa input para �nicio de sele��o de esta��o.
	 */
    public void fireSeasonStartSelectionEvent() {
        for (GameInputListener l : this.listeners) l.seasonStartSelection();
	}
	
	/**
	 * Executa input de movimenta��o para mudan�a de esta��o.
	 * @param factor 	Porcentagem do movimento de mudan�a de esta��o (0 [0%] a 1 [100%]);
	 * @param current	Esta��o atual.
	 * @param over		Esta��o na qual o jogador est� com o mouse sobre.
	 */
    public void fireSeasonChangingEvent(float factor, Seasons current, Seasons over) {
        for (GameInputListener l : this.listeners) l.seasonChanging(factor, current, over);
	}
	
	/**
	 * Executa mudan�a de esta��o.
	 * @param oldSeason Esta��o anterior.
	 * @param newSeason Esta��o nova.
	 */
    public void fireSeasonChangedEvent(Seasons oldSeason, Seasons newSeason) {
        for (GameInputListener l : this.listeners) l.seasonChanged(oldSeason, newSeason);
	}
    
    /**
	 *Executa input de in�cio de desenho de um objeto na posi��o informada.
	 * @param x Posi��o no eixo X.
	 * @param y Posi��o no eixo Y. 
	 */
    public void fireDrawBeginEvent(float x, float y) {
        for (GameInputListener l : this.listeners) l.drawBegin(x, y);
	}
	
	/**
	 * Executa input para adi��o de um segmento ao desenho de um objeto na posi��o informada.
	 * @param x Posi��o no eixo X.
	 * @param y Posi��o no eixo Y. 
	 */
    public void fireDrawAddSegmentEvent(float x, float y) {
        for (GameInputListener l : this.listeners) l.drawAddSegment(x, y);
	}
	
	/**
	 * Executa input para finaliza��o de desenho de um objeto.
	 */
    public void fireDrawFinalyzeEvent() {
        for (GameInputListener l : this.listeners) l.drawFinalyze();
	}
	
	/**
	 * Executa input para indicar que todos os desenhos devem ser destru�dos.
	 */
    public void fireDrawingsDestroyAllEvent() {
        for (GameInputListener l : this.listeners) l.drawingsDestroyAll();
	}
}
