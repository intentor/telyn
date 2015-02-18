package org.escape2team.telyn.configuration;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.escape2team.telyn.core.TouchInputProcessor;
import org.escape2team.telyn.core.GameInputProcessor;
import org.escape2team.telyn.states.LevelState;
import org.newdawn.slick.util.ResourceLoader;

/**
 * Representa as configurações do jogo.
 */
public class GameConfiguration extends ConfigurationData  {	
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
			}
			
			//Desabilita os editores.
			config.debugEnable = config.editorEnable = false;
			config.alwaysDrawHud = false;
			
			//Define a largura do raio de desenho.
			config.radiusHudOrb = 100;
			
			//Indica que o jogo está sendo executado em ambiente mobile.
			config.isMobile = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return config;
	}
	
	public GameInputProcessor getInputProcessor(LevelState state) {
		return new TouchInputProcessor(state);
	}
}
