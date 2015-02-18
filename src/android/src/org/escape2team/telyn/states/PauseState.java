package org.escape2team.telyn.states;

import org.escape2team.telyn.core.BaseGameState;
import org.escape2team.telyn.core.Utils;
import org.jbox2d.common.Vec2;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.StateBasedGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;

/**
 * Estado de pausa do jogo.
 */
public class PauseState extends BaseGameState implements InputProcessor {
	/** Identifica o modo de tela. */
	private enum ScreenMode {
		  HowToPlay
		, Credits
		, Exit
	}
	
	/** Tempo de espera para análise de pressionamento da tecla ESC. */
	private static final int ESC_DELTA = 500;
	/** Cor do botão no estado normal. */
	private static final Color COLOR_NORMAL = new Color(Color.white);
	/** Cor do botão com mouse sobre. */
	private static final Color COLOR_HOVER = new Color(Color.gray);
	/** Cor do botão selecionado. */
	private static final Color COLOR_SELECTED = new Color(Color.orange);
	/** Velocidade do movimento dos créditos. */
	private static final float CREDITS_ROLL_SPEED = 0.7f;
	/** Altura da linha com a fonte atual. */
	private static final int LINE_HEIGHT = 30;
	/** Modo atual da tela. */
	private ScreenMode currentScreenMode;
	/** Retângulos dos botões, na ordem btnHowToPlay, btnCredits, btnResume e btnExit. */
	private Rectangle[] buttons;
	/** Labels dos botões, na ordem "howplay", "credits", "resume" e "exit". */
	private String[] labels;
	/** Filtros de cor dos botões, na ordem btnHowToPlay, btnCredits, btnResume, btnExit. */
	private Color[] filters;
	/** Créditos do jogo. */
	private String[] credits;
	/** Posição atual do mouse. */
	private Vec2 position;
	/** Espaço de tempo para análise de pressionamento do ESC. */
	private int currentEscDelta;
	/** Contador para movimentação dos créditos. */
	private float counter;
	/** Fonte principal do jogo. */
	Font fontMain;
	/** Imagem de como jogar. */
	Image howPlay;
	
	/**
	 * Cria um novo estado de pausa de jogo.
	 * @param id ID do estado.
	 */
	public PauseState(int id) {
		super(id);
	}
	
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		this.setInput(null);
		this.labels = null;
		this.buttons = null;
		this.credits = null;
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		Gdx.input.setInputProcessor(this);
		
		if (this.labels == null) {
			//Obtém os textos dos labels.
			this.labels = new String[] { 
				  Loader.LOCALIZATION.getString("howplay")
				, Loader.LOCALIZATION.getString("credits")
				, Loader.LOCALIZATION.getString("resume")
			};
		}
		
		if (this.credits == null) {
			this.credits = Loader.LOCALIZATION.getString("creditsroll").split("\\\\n");
		}
		
		if (this.buttons == null) {		
			//Cria os retângulos dos botões.
			this.buttons = new Rectangle[] { 
				  new Rectangle(20, 20, this.fontMain.getWidth(this.labels[0]), this.fontMain.getHeight(this.labels[0]))
				, new Rectangle(20, 60, this.fontMain.getWidth(this.labels[1]), this.fontMain.getHeight(this.labels[1]))
				, new Rectangle(20, 100, this.fontMain.getWidth(this.labels[2]), this.fontMain.getHeight(this.labels[2]))
			};
		}
		
		this.filters = new Color[] { COLOR_NORMAL, COLOR_NORMAL, COLOR_NORMAL };
		this.position = null;
		this.currentEscDelta = 0;
		this.currentScreenMode = ScreenMode.HowToPlay;
	}

	@Override
	public void leave(GameContainer container, StateBasedGame game) throws SlickException {
		Gdx.input.setInputProcessor(null);
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		//Renderiza objetos específicos dos modos.
		switch (this.currentScreenMode) {
			case HowToPlay:
				this.howPlay.draw(200, 0);
			break;
			case Credits:
				int start = 100;
				this.counter += CREDITS_ROLL_SPEED;
				if (this.counter > this.credits.length * (LINE_HEIGHT + 3)) {
					this.counter = -(container.getHeight() - start);
				}
				for (int i = 0; i < this.credits.length; i++) {
					Utils.drawStringRight(this.fontMain, this.credits[i], (start + i * LINE_HEIGHT) - this.counter , 30);
				}
			break;
		}
		
		//Renderiza os botões.
		for (int i = 0; i < this.buttons.length; i++) {
			this.fontMain.drawString(
					  this.buttons[i].getX()
					, this.buttons[i].getY()
					, this.labels[i]
					, this.filters[i]);
		}
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		//Avaliação de pressionamento da tecla ESC.
		if (this.currentEscDelta <= ESC_DELTA) this.currentEscDelta += delta;		
		if (container.getInput().isKeyDown(Input.KEY_ESCAPE)) {
			if (this.currentEscDelta > ESC_DELTA) {
				Loader.GAME.enterState(Loader.GAMEPLAYSTATE);
			}
		}
		
		//Define as cores dos botões. Somente os 2 primeiros podem ser efetivamente selecionados.
		this.filters[0] = (this.currentScreenMode == ScreenMode.HowToPlay ? COLOR_SELECTED : COLOR_NORMAL);
		this.filters[1] = (this.currentScreenMode == ScreenMode.Credits ? COLOR_SELECTED : COLOR_NORMAL);;
		this.filters[2] = COLOR_NORMAL;
		
		//Avaliação de hover.
		if (this.position != null){			
			for (int i = 0; i < this.buttons.length; i++) {
				if (this.buttons[i].contains(this.position.x, this.position.y)) {
					this.filters[i] = COLOR_HOVER;
					break;
				}
			}
		}
	}
	
	//MÉTODOS DE INPUT=================================================================

	@Override
	public boolean keyDown(int arg0) {
		return true;
	}

	@Override
	public boolean keyTyped(char arg0) {
		return true;
	}

	@Override
	public boolean keyUp(int arg0) {
		return true;
	}

	@Override
	public boolean scrolled(int arg0) {
		return true;
	}

	@Override
	public boolean touchDown(int x, int y, int pointer, int button) {
		if (pointer == 0) this.position = new Vec2(x, y);
		return true;
	}

	@Override
	public boolean touchDragged(int x, int y, int pointer) {
		if (pointer == 0 && this.position != null) this.position.set(x, y);
		return true;
	}

	@Override
	public boolean touchMoved(int arg0, int arg1) {
		return true;
	}

	@Override
	public boolean touchUp(int x, int y, int pointer, int button) {
		if (pointer == 0) {
			//Verifica se a posição é de algum dos botões.			
			if (this.buttons[0].contains(x, y)) this.currentScreenMode = ScreenMode.HowToPlay;
			else if (this.buttons[1].contains(x, y)) { this.currentScreenMode = ScreenMode.Credits; this.counter = 0; }
			else if (this.buttons[2].contains(x, y)) Loader.GAME.enterState(Loader.GAMEPLAYSTATE);

			this.position = null;
		}
		return true;
	}
}

