package org.escape2team.telyn.states;

import org.escape2team.telyn.configuration.LocalizationData;
import org.escape2team.telyn.core.BaseGameState;
import org.jbox2d.common.Vec2;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.StateBasedGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;

/**
 * Estado de seleção de idiomas.
 */
public class LanguageState extends BaseGameState implements InputProcessor {
	/** Bandeira do Brasil. */
	private Image flagBrazil;
	/** Banderia dos EUA. */
	private Image flagUSA;
	/** Retângulo representando a posição do Brasil. */
	private Rectangle btnBrazil;
	/** Retângulo representando a posição dos EUA. */
	private Rectangle btnUSA;
	/** Filtro de cor para a imagem do Brasil. */
	private Color filterBrazil;
	/** Filtro de cor para a imagem dos EUA. */
	private Color filterUSA;
	/** Posição atual do mouse. */
	private Vec2 position;
	
	/**
	 * Cria um novo estado de seleção de idiomas.
	 * @param id
	 */
	public LanguageState(int id) {
		super(id);
	}
	
	//MÉTODOS DE JOGO=================================================================

	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		this.setInput(null);
		
		this.flagBrazil = new Image("data/sprites/flag-brazil.png");
		this.flagUSA = new Image("data/sprites/flag-usa.png");
		this.btnBrazil = new Rectangle(94, 112, 256, 256);
		this.btnUSA = new Rectangle(450, 112, 256, 256);
		this.filterBrazil = new Color(Color.white);
		this.filterUSA = new Color(Color.white);
		this.filterBrazil.a = this.filterUSA.a = 0.6f;
		this.position = null;
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {		
		this.filterBrazil.a = this.filterUSA.a = 0.6f;
		
		if (this.position != null) {
			//Aplica canal alpha às imagens caso o cursor esteja sobre elas.
			if (this.btnBrazil.contains(this.position.x, this.position.y)) {
				this.filterBrazil.a = 1.0f;
			} else if (this.btnUSA.contains(this.position.x, this.position.y)) {
				this.filterUSA.a = 1.0f;
			}
		}
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		this.flagBrazil.draw(94, 112, this.filterBrazil);
		this.flagUSA.draw(450, 112, this.filterUSA);
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		Gdx.input.setInputProcessor(this);	
	}

	@Override
	public void leave(GameContainer container, StateBasedGame game) throws SlickException {
		Gdx.input.setInputProcessor(null);
		this.flagBrazil.destroy();
		this.flagUSA.destroy();
	}
		
	//MÉTODOS DE APOIO================================================================
	
	/**
	 * Carrega conteúdo para um determinado idioma.
	 * @param lang Localização a ser utilizada.
	 */
	private void loadLanguage(String lang) {
		Loader.LOCALIZATION = new LocalizationData(lang);
		Loader.CONFIGURATIONS.language = lang;
		Loader.GAME.enterState(Loader.LOADINGSTATE);
	}	

	//MÉTODOS DE INPUT=================================================================

	@Override
	public boolean keyDown(int arg0) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean keyTyped(char arg0) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean keyUp(int arg0) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean scrolled(int arg0) {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean touchUp(int x, int y, int pointer, int button) {
		if (pointer == 0) {
			//Verifica se a posição é de algum dos idiomas.			
			if (this.btnBrazil.contains(x, y)) this.loadLanguage("pt-br");
			else if (this.btnUSA.contains(x, y)) this.loadLanguage("en-us");
			else this.position = null;
		}
		return true;
	}
}
