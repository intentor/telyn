package org.escape2team.telyn.core;

import org.escape2team.telyn.states.LevelState;
import org.jbox2d.common.Vec2;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.MouseListener;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class DesktopInputProcessor extends GameInputProcessor implements MouseListener {
	/** Tempo de espera para análise de pressionamento da tecla ESC. */
	private static final int ESC_DELTA = 500;
	/** Espaço de tempo para análise de pressionamento do ESC. */
	private int currentEscDelta;
	/** Indica se se deve adicionar o listener do mouse. */
	private boolean addMouseListener;
	/** Botão atual do mouse pressionado. */
	private int currentMouseButton;
	/** Posição do mouse na tela. */
	private Vec2 mousePosition;
	
	/**
	 * Construtor da classe.
	 * @param state Estado de jogo dos níveis.
	 */
	public DesktopInputProcessor(LevelState state) {
		this(state, true);
	}
	
	/**
	 * Construtor da classe.
	 * @param state Estado de jogo dos níveis.
	 */
	public DesktopInputProcessor(LevelState state, boolean addMouseListener) {
		super(state);
		this.addMouseListener = addMouseListener;
	}
	
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		CHANGE_SEASON_SIZE = 30;
		CHANGE_SEASON_PADDING = 75;
		
		this.initProcessor(container, game);

		this.currentEscDelta = 0;
		this.isSelectingSeason = false;
		this.mousePosition = new Vec2();
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) {
		if (this.addMouseListener) container.getInput().addMouseListener(this);
	}

	@Override
	public void leave(GameContainer container, StateBasedGame game) throws SlickException {
		if (this.addMouseListener) container.getInput().removeMouseListener(this);
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g, Color filter) throws SlickException {		
		if (this.isSelectingSeason) {
			int seasonId = this.hookOnSeason.getId();
			float factor = this.getChangingSeasonFactor();
					
			this.drawSeasonIcon(0
				, seasonId
				, new Vec2(this.hook.x - CHANGE_SEASON_PADDING, this.hook.y - CHANGE_SEASON_PADDING)
				, factor);
			this.drawSeasonIcon(1
				, seasonId
				, new Vec2(this.hook.x, this.hook.y - CHANGE_SEASON_PADDING)
				, factor);
			this.drawSeasonIcon(3
				, seasonId
				, new Vec2(this.hook.x - CHANGE_SEASON_PADDING, this.hook.y)
				, factor);
			this.drawSeasonIcon(2
				, seasonId
				, new Vec2(this.hook.x, this.hook.y)
				, factor);
		}
	}	

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		Input kb = container.getInput();
		
		//Avaliação de pressionamento da tecla ESC.
		if (this.currentEscDelta <= ESC_DELTA) this.currentEscDelta += delta;		
		if (kb.isKeyDown(Input.KEY_ESCAPE)) {
			if (this.currentEscDelta > ESC_DELTA) {
				this.firePauseGameEvent();
				//Checa fim de desenho, independente de o mouse ter sido liberado ou não.
				if (this.isDrawing) this.fireDrawFinalyzeEvent();
				
				this.currentEscDelta = 0;
			}
		}
		
		if (!this.isPaused) {
			//Quando em desenho, trava todos os controles.
			if (!this.isDrawing) {
				if (this.isSelectingSeason) {
					//Verifica em qual estação o mouse está.
					/* Verifica se a posição do mouse está acima (Spring e Summer)
					 * ou abaixo (Winter e Autumn). */
					
					if (this.mousePosition.y <= this.hook.y) { //Spring e Summer.
						if (this.mousePosition.x <= this.hook.x) this.hookOnSeason = Seasons.Spring;
						else  this.hookOnSeason = Seasons.Summer;
					} else { //Winter e Autumn.
						if (this.mousePosition.x <= this.hook.x) this.hookOnSeason = Seasons.Winter;
						else  this.hookOnSeason = Seasons.Autumn;
					}
					
					this.hookSize = (float) Math.sqrt(Math.pow((this.hook.x - this.mousePosition.x), 2) + Math.pow((this.hook.y - this.mousePosition.y), 2));	
					this.fireSeasonChangingEvent(this.getChangingSeasonFactor(), this.currentSeason, this.hookOnSeason);	
				} else {
					if (kb.isKeyDown(Input.KEY_LEFT) || kb.isKeyDown(Input.KEY_A)) this.fireMoveLeftEvent();
					if (kb.isKeyDown(Input.KEY_RIGHT) || kb.isKeyDown(Input.KEY_D)) this.fireMoveRightEvent();
					if (kb.isKeyDown(Input.KEY_UP) || kb.isKeyDown(Input.KEY_W)) this.fireJumpEvent();
				}
			}
		}
	}


	//MÉTODOS DE AVALIAÇÃO DE AÇÕES===================================================
	
	/**
	 * Desenha o ícone de uma estação.
	 * @param seasonIndex	Índice da estação no vetor de estações.
	 * @param seasonId		ID da estação.
	 * @param position		Posição de desenho do ícone.
	 * @param factor		Fator de seleção da estação.
	 */
	protected void drawSeasonIcon(int seasonIndex, int seasonId, Vec2 position, float factor) {
		Color filter = new Color(Color.white);
		if (seasonIndex == seasonId) filter.a = (float) (0.6 + 0.4 * factor);
		else filter.a = (float) (0.6f - 0.4 * factor);
		this.seasons[seasonIndex].draw(position.x, position.y, filter);
	}
	
	/**
	 * Obtém o fator de mudança de estação.
	 * @return Fator de mudança da estação.
	 */
	protected float getChangingSeasonFactor() {
		float factor = this.hookSize / CHANGE_SEASON_SIZE;
		if (factor < 0) factor = 0;
		else if (factor > 1) factor = 1;
		
		return factor;
	}

	//MÉTODOS DE INPUT================================================================

	@Override
	public void inputEnded() {
	}

	@Override
	public void inputStarted() {
	}

	@Override
	public boolean isAcceptingInput() {
		return true;
	}

	@Override
	public void setInput(Input arg0) {
	}

	@Override
	public void mouseClicked(int arg0, int arg1, int arg2, int arg3) {	
	}
	
	@Override
	public void mouseDragged(int oldx, int oldy, int x, int y) {
		this.fireMouseMoveEvent(x, y);
		this.mousePosition.set(x, y);
		
		if (!this.isPaused) {
			if (this.currentMouseButton == 0) { //Botão esquerdo.
				if (this.isDrawing) {
					//Reporta evento de desenho.
					this.fireDrawAddSegmentEvent(x, y);
				}
			}
		}
	}

	@Override
	public void mouseMoved(int oldx, int oldy, int x, int y) {
		this.fireMouseMoveEvent(x, y);
		this.mousePosition.set(x, y);
	}

	@Override
	public void mousePressed(int button, int x, int y) {
		this.currentMouseButton = button;
		this.mousePosition.set(x, y);
		
		if (!this.isPaused) {
			if (this.currentMouseButton == 0) { //Botão esquerdo.
				if (!this.isSelectingSeason && this.currentSeason == Seasons.Spring) {
					this.isDrawing = true;
					this.fireDrawBeginEvent(x, y);
				}
			} else if (this.currentMouseButton == 1) { //Botão direito.
				if (this.isDrawing) {
					this.isDrawing = false;
					this.fireDrawFinalyzeEvent();
				}
				
				this.isSelectingSeason = !this.isSelectingSeason;
								
				if (this.isSelectingSeason) {
					this.hookOnSeason = this.currentSeason;
					this.hook = new Vec2(x, y);
					this.fireSeasonStartSelectionEvent();
				} else {
					//Dispara o evento de troca de estação indicando que não houve troca.
					this.fireSeasonChangedEvent(this.currentSeason, this.currentSeason);
				}
			}
		}
	}

	@Override
	public void mouseReleased(int button, int x, int y) {
		this.mousePosition.set(x, y);
		
		if (!this.isPaused) {
			if (this.currentMouseButton == 0) { //Botão esquerdo.		
				if (this.isDrawing) { //Caso esteja em desenho, encerra-o.
					this.isDrawing = false;
					this.fireDrawFinalyzeEvent();
				} else if (this.isSelectingSeason) {
					//Seleciona a estação na qual o mouse estiver sobre.					
					this.lastSeason = this.currentSeason;
					this.currentSeason = this.hookOnSeason;
					
					//Caso a estação seja outono, dispara a destruição dos objetos.
					if (this.currentSeason == Seasons.Autumn) this.fireDrawingsDestroyAllEvent();
					//Dispara o evento de troca de estação.
					this.fireSeasonChangedEvent(this.lastSeason, this.currentSeason);
					
					this.isSelectingSeason = false;
				}
			}
		}
	}

	@Override
	public void mouseWheelMoved(int arg0) {
	}
}
