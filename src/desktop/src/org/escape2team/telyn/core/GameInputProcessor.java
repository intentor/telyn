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
	/** Tamanho do movimento para troca de estação. */
	public static float CHANGE_SEASON_SIZE;
	/** Distância para exibição das imagens das estações. */
	public static float CHANGE_SEASON_PADDING;
	/** Tempo de espera, em milissegundos, do mouse parado para início de desenho). */
	public static final float DRAWING_WAITING_TIME = 350;
	/** Raio do círculo ao redor do pivô de avaliação de pressionamento de espera. */
	public static final float HOOK_CIRCLE_RADIUS = 10;
	/** Raio do círculo ao redor do pivô de avaliação de pressionamento de espera. */
	public static final float SEASON_CIRCLE_RADIUS = 10;
	/** Estado do nível do jogo. */
	protected LevelState gameState;
	/** ID da estação do ano atual. Começa sempre em 0 (NoSeason). */
	protected Seasons currentSeason;
	/** ID da estação do ano anterior. Começa sempre em 0 (NoSeason). */
	protected Seasons lastSeason;
	/** Indica qual estação o jogador está selecionando. */
	protected Seasons hookOnSeason;
	/** Posição de pivô para troca de estação. */
	protected Vec2 hook;
	/** Tamanho atual do movimento para troca de estação. */
	protected float hookSize;
	/** Tempo de espera sobre o pivô para desenho. */
	protected int waitTime;
	/** Indica se está ocorrendo desenho de pontos de colisão. */
	protected boolean isDrawing;
	/** Listeners de eventos de input. */
	protected List<GameInputListener> listeners;
	/** Imagens das estações. */
	protected Image[] seasons;
	/** Indica se o jogo está pausado. */
	protected boolean isPaused;
	/** Indica se se está selecionando estações. */
	protected boolean isSelectingSeason;
	
	/**
	 * Construtor da classe.
	 * @param state Estado de jogo dos níveis.
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
		
		//Inicia o jogo no verão.
		this.lastSeason = this.currentSeason = this.hookOnSeason = Seasons.Summer;
		
		this.isPaused = false;
	}
	
	/**
	 * Vibra o dispositivo de jogo, caso haja suporte.
	 * @param milliseconds Duração da vibração em millisegundos.
	 */
	public void vibrate(long milliseconds) {
		//Por padrão, não executa ação alguma.
	}
	
	/**
	 * Vibra o dispositivo de jogo, caso haja suporte.
	 * @param pattern 	Padrão de vibração, no formato { espera, vibração, espera, vibração }
	 * @param repeat	Quantidade de repetições do padrão.
	 */
	public void vibrate(long[] pattern, int repeat) {
		//Por padrão, não executa ação alguma.
	}
	
	/** 
	 * Obtém a estação atual.
	 * @return Estação atual.
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
	 * Executa evento de movimentação do mouse.
	 * @param x Posição no eixo X, em unidades de tela.
	 * @param y Posição no eixo Y, em unidades de tela.
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
	 * Executa input para ínicio de seleção de estação.
	 */
    public void fireSeasonStartSelectionEvent() {
        for (GameInputListener l : this.listeners) l.seasonStartSelection();
	}
	
	/**
	 * Executa input de movimentação para mudança de estação.
	 * @param factor 	Porcentagem do movimento de mudança de estação (0 [0%] a 1 [100%]);
	 * @param current	Estação atual.
	 * @param over		Estação na qual o jogador está com o mouse sobre.
	 */
    public void fireSeasonChangingEvent(float factor, Seasons current, Seasons over) {
        for (GameInputListener l : this.listeners) l.seasonChanging(factor, current, over);
	}
	
	/**
	 * Executa mudança de estação.
	 * @param oldSeason Estação anterior.
	 * @param newSeason Estação nova.
	 */
    public void fireSeasonChangedEvent(Seasons oldSeason, Seasons newSeason) {
        for (GameInputListener l : this.listeners) l.seasonChanged(oldSeason, newSeason);
	}
    
    /**
	 *Executa input de início de desenho de um objeto na posição informada.
	 * @param x Posição no eixo X.
	 * @param y Posição no eixo Y. 
	 */
    public void fireDrawBeginEvent(float x, float y) {
        for (GameInputListener l : this.listeners) l.drawBegin(x, y);
	}
	
	/**
	 * Executa input para adição de um segmento ao desenho de um objeto na posição informada.
	 * @param x Posição no eixo X.
	 * @param y Posição no eixo Y. 
	 */
    public void fireDrawAddSegmentEvent(float x, float y) {
        for (GameInputListener l : this.listeners) l.drawAddSegment(x, y);
	}
	
	/**
	 * Executa input para finalização de desenho de um objeto.
	 */
    public void fireDrawFinalyzeEvent() {
        for (GameInputListener l : this.listeners) l.drawFinalyze();
	}
	
	/**
	 * Executa input para indicar que todos os desenhos devem ser destruídos.
	 */
    public void fireDrawingsDestroyAllEvent() {
        for (GameInputListener l : this.listeners) l.drawingsDestroyAll();
	}
}
