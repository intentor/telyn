package org.escape2team.telyn.states;

import org.escape2team.telyn.configuration.GameConfiguration;
import org.escape2team.telyn.configuration.LocalizationData;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.GameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Carregador do jogo.
 */
public class Loader extends StateBasedGame {
	/** Estado de seleção de idiomas. */
	public static final int LANGUAGESTATE = 0;
	/** Estado de loading do jogo.*/
	public static final int LOADINGSTATE = 1;
	/** Estado de jogo. */
	public static final int GAMEPLAYSTATE = 2;	
	/** Estado de pauso do jogo. */
	public static final int PAUSESTATE = 3;
	/** Configurações do jogo. */
	public static GameConfiguration CONFIGURATIONS;	
	/**  Dados de localização do jogo. */
	public static LocalizationData LOCALIZATION;
	/** Estado principal do jogo. */
	public static StateBasedGame GAME;
	/** Container de jogo. */
	public static GameContainer CONTAINER; 
	
	/** Cria um novo objeto de carregamento do jogo. */
	public Loader() {	
		super("Telyn");
		//Carrega as configurações e dados de idioma do jogo.
		CONFIGURATIONS = GameConfiguration.load();
		GAME = this;
	}
	
	@Override
	public void initStatesList(GameContainer container) throws SlickException {
		container.setAlwaysRender(true);
		container.setVSync(false);
		container.setSmoothDeltas(false);
		container.setShowFPS(false);
		container.setTargetFrameRate(CONFIGURATIONS.fps);
		container.setMinimumLogicUpdateInterval(CONFIGURATIONS.minLogicUpdateInterval);
		container.setFullscreen(false);
		
		//Instancia os estados.
		LanguageState lang = new LanguageState(LANGUAGESTATE);
		LevelState level = new LevelState(GAMEPLAYSTATE);
		PauseState pause = (CONFIGURATIONS.isMobile ? null : new PauseState(PAUSESTATE));
		LoadingState loading = new LoadingState(LOADINGSTATE, level, pause);
		
		//Acresce os estados de jogo (o primeiro acrescido será o primeiro a ser executado).
		this.addState(lang);
		this.addState(loading);
		this.addState(level);	
		if (pause != null) this.addState(pause);	
	}
	
	/**
	 * Finaliza a execução do jogo.
	 */
	public static void leaveGame() {
		try {
			for (int i = 0; i < GAME.getStateCount(); i++) {
				GameState state = GAME.getState(i);
				if (state instanceof LevelState) {
					((LevelState)state).currentScreenMode = LevelState.ScreenMode.Exit;			
				}
				GAME.getState(i).leave(GAME.getContainer(), GAME);			
			}
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws SlickException {		
		AppGameContainer app = new AppGameContainer(new Loader());
		app.setIcon("data/sprites/icon-large.png");
		app.setDisplayMode(CONFIGURATIONS.screenWidth, CONFIGURATIONS.screenHeight, false);
		app.start();
	}
}
