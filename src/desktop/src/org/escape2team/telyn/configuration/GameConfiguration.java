package org.escape2team.telyn.configuration;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.escape2team.telyn.core.GameInputProcessor;
import org.escape2team.telyn.states.LevelState;
import org.newdawn.slick.util.ResourceLoader;

/**
 * Representa as configurações do jogo.
 */
public class GameConfiguration extends ConfigurationData {
	
	/**
	 * Cria um novo objeto de configurações do jogo.
	 */
	public GameConfiguration() {
		super();
	}
	
	/**
	 * Carrega as configurações do jogo.
	 * @return Configurações do jogo.
	 */
	public static GameConfiguration load() {
		GameConfiguration config = null;
		
		try {
			InputStream is = ResourceLoader.getResourceAsStream("data/game.config");
			BufferedReader input = new BufferedReader(new InputStreamReader(is));
			config = new GameConfiguration();  
			
			String line = null;			
			while ((line = input.readLine()) != null){
				String map[] = line.split(":");				
				if (map[0].equals("screen-width")) config.screenWidth = Integer.valueOf(map[1]);
				else if (map[0].equals("screen-height")) config.screenHeight = Integer.valueOf(map[1]);
				else if (map[0].equals("screen-fps")) config.fps = Integer.valueOf(map[1]);
				else if (map[0].equals("minLogicUpdateInterval")) config.minLogicUpdateInterval = Integer.valueOf(map[1]);
				else if (map[0].equals("defaultLanguage")) config.language = map[1];
				else if (map[0].equals("debug")) config.debugEnable = Boolean.valueOf(map[1]);
				else if (map[0].equals("editor")) config.editorEnable = Boolean.valueOf(map[1]);
				else if (map[0].equals("exitButtonOnPause")) config.showExitButtonOnPause = Boolean.valueOf(map[1]);				
			}
			
			//Caso o editor esteja habilitado, o debug estará igualmente habilitado.
			if (config.editorEnable) config.debugEnable = true;		
			
			input.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return config;
	}
	
	public GameInputProcessor getInputProcessor(LevelState state) {
		if (this.editorEnable) return new org.escape2team.telyn.editor.EditorInputProcessor(state);
		else return new org.escape2team.telyn.core.DesktopInputProcessor(state);
	}
}
