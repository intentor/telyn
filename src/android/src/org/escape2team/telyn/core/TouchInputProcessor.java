package org.escape2team.telyn.core;

import java.util.LinkedList;
import java.util.List;

import org.escape2team.telyn.android.AndroidLoader;
import org.escape2team.telyn.states.LevelState;
import org.jbox2d.common.Vec2;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.PackedSpriteSheet;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.StateBasedGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;

import android.content.Context;
import android.os.Vibrator;

public class TouchInputProcessor extends GameInputProcessor implements InputProcessor {
	/** Controles de movimento. */
	private Image[] controls;
	/** Posição do botão de movimento para cima.*/
	private Rectangle up;	
	/** Posição do botão de movimento para esquerda. */
	private Rectangle left;	
	/** Posição do botão de movimento para direita. */
	private Rectangle right;
	/** Círculo ao redor do pivô utilizado para definir a precisão do pressionamento de espera. */
	protected Circle hookCircle;
	/** Lista de posições de tela pressionadas. */
	private List<TouchPosition> pressed;
	/** ID do ponteiro do touch do movimento de drag das estações. */
	private int dragPointerId = -1;
	/** Ponto anterior do drag, para evitar que o evento seja chamado muitas vezes no mesmo ponto. */
	private Vec2 lastDragPoint;
	/** Objeto para vibração. */
	private Vibrator vibration;
	
	/**
	 * Construtor da classe.
	 * @param state Estado de jogo dos níveis.
	 */
	public TouchInputProcessor(LevelState state) {
		super(state);
	}
	
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {		
		CHANGE_SEASON_SIZE = 150;
		CHANGE_SEASON_PADDING = 150;

		this.initProcessor(container, game);
				
		this.pressed = new LinkedList<TouchPosition>();
		this.vibration = (Vibrator)AndroidLoader.ANDROID_CONTEXT.getSystemService(Context.VIBRATOR_SERVICE);
		
		//Obtém as imagens dos controles.
		PackedSpriteSheet pack = new PackedSpriteSheet("data/sprites/control.def", Image.FILTER_NEAREST);
		this.controls = new Image[3];
		this.controls[0] = pack.getSprite("control_left");
		this.controls[1] = pack.getSprite("control_right");
		this.controls[2] = pack.getSprite("control_up");
		
		//Posições dos botões do controle.
		this.left = new Rectangle(5, 347, 80, 128);
		this.right = new Rectangle(85, 347, 80, 128);
		this.up = new Rectangle(667, 347, 128, 128);
		
		this.lastDragPoint = new Vec2(0,0);
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) {
		Gdx.input.setInputProcessor(this);
	}

	@Override
	public void leave(GameContainer container, StateBasedGame game) throws SlickException {
		Gdx.input.setInputProcessor(null);
		for (Image control : this.controls) control.destroy();
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g, Color filter) throws SlickException {
		this.controls[0].draw(this.left.getX(), this.left.getY());
		this.controls[1].draw(this.right.getX(), this.right.getY());
		this.controls[2].draw(this.up.getX(), this.up.getY());

		if (this.isSelectingSeason) {
			int seasonId = (this.hookOnSeason == null ? this.currentSeason.getId() : this.hookOnSeason.getId());
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
		boolean foundDragPoint = false;
		for (TouchPosition p : this.pressed) {
			if (!this.isPaused) {			
				//Primeiramente, verifica se o ponto é um ponto de drag.
				if (p.id == this.dragPointerId) {
					/* Somente permite a execução do evento de drag se o 
					 * ponto atual for diferente do ponto anterior. */
					if (!(this.lastDragPoint.x == p.x && this.lastDragPoint.y == p.y)) {
						this.checkDrag(p.x, p.y);
						this.lastDragPoint = new Vec2(p.x, p.y);
					}
					
					//Indica que o DragPoint existe.
					foundDragPoint = true;
				} else if (!this.isDrawing && !this.isSelectingSeason){ //Somente permite input se não estiver em desenho ou seleção de estação.			
					if (this.left.contains(p.x, p.y)) this.fireMoveLeftEvent();
					else if (this.right.contains(p.x, p.y)) this.fireMoveRightEvent();
					else if (this.up.contains(p.x, p.y)) this.fireJumpEvent();
				}
			}
		}

		//Encerra a seleção de estações caso não haja botões ou o DragPoint não foi encontrado.
		if (this.isSelectingSeason && (!foundDragPoint || this.pressed.size() == 0)) {
			this.checkEndDrag();
		}
		
		this.checkStartDrawing(delta);
	}
	
	@Override
	public void vibrate(long milliseconds) {
		this.vibration.vibrate(milliseconds);
	}
	
	@Override
	public void vibrate(long[] pattern, int repeat) {
		this.vibration.vibrate(pattern, repeat);
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
	
	/**
	 * Cria um círculo ao redor do ponto pivô.
	 * @param point
	 */
	protected void createHookCircle(Vec2 point) {
		this.hookCircle = new Circle(point.x + HOOK_CIRCLE_RADIUS, point.y + HOOK_CIRCLE_RADIUS, HOOK_CIRCLE_RADIUS);
		this.waitTime = 0;
	}
	
	/**
	 * Verifica se se pode iniciar um desenho.
	 * @param delta Tempo passado desde a última chamada ao método update.
	 */
	protected void checkStartDrawing(int delta) {
		/* Caso haja um círculo pivô e não haja desenho, começa a contagem de tempo para início de desenho. */
		if (this.hookCircle != null && !this.isDrawing) {
			this.waitTime += delta;
			
			//Caso o tempo de espera tenha passado, permite o início de desenho.
			if (this.waitTime > GameInputProcessor.DRAWING_WAITING_TIME) {				
				//Indica que não se está mais na troca de estação.
				this.isSelectingSeason = false;
				//Dispara o evento de troca de estação indicando que não houve troca.
				this.fireSeasonChangedEvent(this.currentSeason, this.currentSeason);
				
				//Indica que se está em desenho.
				this.isDrawing = true;
				//Dispara o evento de início de desenho.
				this.fireDrawBeginEvent(this.hook.x, this.hook.y);
			}
		}
	}
	
	/**
	 * Verifica ações de drag.
	 * @param x Posição de drag atual no eixo X.
	 * @param y Posição de drag atual no eixo Y. 
	 */
	protected void checkDrag(float x, float y) {
		if (this.hook != null) {
			/* Caso a estação seja Primavera, permite a realização de desenho.
			 * Para tal, a posição do mouse deve manter-se dentro do círculo pivô
			 * pelo tempo determinado pela constante GameInputProcessor.DRAWING_WAITING_TIME. */
			
			/* Primeiramente, verifica se há um hook circle e se o ponto atual
			 * saiu de seu interior.
			 * Caso tenha saído, zera o hook circle. */
			if (this.hookCircle != null && !this.hookCircle.contains(x, y)) {				
				this.hookCircle = null;
			}
			
			//Verifica se está em desenho.
			if (this.isDrawing) {
				//Reporta evento de desenho.
				this.fireDrawAddSegmentEvent(x, y);
			} else if (this.hookCircle == null) { //Caso não haja um hook circle, permite a troca de estação.	
				/* Verifica para qual estação o jogador está movendo o mouse. 
				 * As posições das estações são baseadas em quatro quadrantes ao redor
				 * do ponto de hook, de acordo com o tamanho das imagens utilizadas. */
				if (x > this.hook.x) { //Quadrantes direitos.
					if (y > this.hook.y) { //Quadrante inferior direito (AUTUMN).
						this.hookOnSeason = Seasons.Autumn;
					} else { //Quadrante superior direito (SUMMER).
						this.hookOnSeason = Seasons.Summer;
					}
				} else { //Quadrantes esquerdos
					if (y > this.hook.y) { //Quadrante inferior esquerdo (WINTER).
						this.hookOnSeason = Seasons.Winter;
					} else { //Quadrante superior esquerdo (SPRING).
						this.hookOnSeason = Seasons.Spring;
					}
				}
				
				if (this.hookOnSeason == null) this.hookOnSeason = this.currentSeason;
				this.hookSize = (float) Math.sqrt(Math.pow((this.hook.x - x), 2) + Math.pow((this.hook.y - y), 2));		
				this.fireSeasonChangingEvent(this.getChangingSeasonFactor(), this.currentSeason, this.hookOnSeason);
			}
		}
	}
	
	/**
	 * Faz verificações do encerramento do movimento de drag.
	 */
	protected void checkEndDrag() {
		if (this.isDrawing) { //Caso esteja em desenho, encerra-o.
			this.isDrawing = false;
			this.fireDrawFinalyzeEvent();
		} else if (this.hookSize > CHANGE_SEASON_SIZE) { //Caso seja troca de estação, avalia a ação.
			this.lastSeason = this.currentSeason;
			this.currentSeason = this.hookOnSeason;
			
			//Caso a estação seja outono, dispara a destruição dos objetos.
			if (this.currentSeason == Seasons.Autumn) this.fireDrawingsDestroyAllEvent();
			//Dispara o evento de troca de estação.
			this.fireSeasonChangedEvent(this.lastSeason, this.currentSeason);
		} else {
			//Dispara o evento de troca de estação indicando que não houve troca.
			this.fireSeasonChangedEvent(this.currentSeason, this.currentSeason);
		}		

		this.isSelectingSeason = false;
		
		this.hook = null;
		this.hookSize = 0;
		this.hookOnSeason = this.currentSeason;
		this.hookCircle = null;
		
		this.dragPointerId = -1;
		this.lastDragPoint = new Vec2(0,0);
	}
	
	//MÉTODOS DE INPUT================================================================

	@Override
	public boolean keyDown(int key) {		
		return true;
	}

	@Override
	public boolean keyTyped(char key) {
		return true;
	}

	@Override
	public boolean keyUp(int key) {
		//TODO: averiguar problemas com carregamento do menu de ajuda.
		/*
		if (!this.isPaused && key == 82) { //4 = BACK; 82 = MENU
			this.firePauseGameEvent();
		}
		*/
		
		return true;
	}

	@Override
	public boolean scrolled(int key) {
		return true;
	}

	@Override
	public boolean touchDown(int x, int y, int pointer, int button) {
		pressed.add(new TouchPosition(x, y, pointer));
		
		if (!this.isPaused) {		
			/* Caso não haja ponteiro e drag de tela e o ponto não seja
			 * algum dos botões, indica o ponto atual como ponto de drag. */
			if (this.dragPointerId == -1 &&
				!this.left.contains(x, y) &&
				!this.right.contains(x, y) &&
				!this.up.contains(x, y)) {			
				this.hook = new Vec2(x, y);
				this.hookSize = 0;
				this.hookOnSeason = null;
				this.hookCircle = null;
				this.dragPointerId = pointer;
				
				this.isSelectingSeason = true;
				this.fireSeasonStartSelectionEvent();
				
				//Somente cria o círculo de hook caso seja primavera.
				if (this.currentSeason == Seasons.Spring) this.createHookCircle(this.hook);
			}
		}
		
		return true;
	}

	@Override
	public boolean touchDragged(int x, int y, int pointer) {
		for (int i = 0; i < pressed.size(); i++) {
			if (pressed.get(i).id == pointer) {
				pressed.get(i).set(x, y);
			}
		}
		
		return true;
	}

	@Override
	public boolean touchMoved(int x, int y) {
		return true;
	}

	@Override
	public boolean touchUp(int x, int y, int pointer, int button) {
		for (int i = 0; i < pressed.size(); i++) {
			if (pressed.get(i).id == pointer) {
				pressed.remove(i);
				
				if (!this.isPaused) {
					//Verifica se o ponteiro era o mesmo do movimento de drag.
					if (i == this.dragPointerId) {
						this.checkEndDrag();
					}
				}
			}
		}
		
		return true;
	}
}
