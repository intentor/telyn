package org.escape2team.telyn.configuration;

import org.escape2team.telyn.core.GameInputProcessor;
import org.escape2team.telyn.states.LevelState;

/**
 * Dados de configuração do jogo.
 */
public abstract class ConfigurationData {
	/** Tamanho do raio da circunferência do HUD. */
	public int radiusHudOrb;
	/** Largura da tela. */
	public int screenWidth;
	/** Altura da tela. */
	public int screenHeight;
	/** Quantidade de frames por segundo do jogo. */
	public int fps;
	/** Mínimo intervalo de atualização de lógica de jogo, em milissegundos. */
	public int minLogicUpdateInterval;
	/** Idioma do jogo. Deve existir um arquivo de mesmo nome no diretório "lang". */
	public String language;
	/** Indica se o modo de debug está habilitado. */
	public boolean debugEnable;
	/** Indica o editor deve ser carregado. */
	public boolean editorEnable;
	/** Indica se se deve desenhar sempre o HUD na tela. */
	public boolean alwaysDrawHud;
	/** Indica se o jogo está sendo executado em plataforma mobile. */
	public boolean isMobile;
	/** Indica se se deve exibir o botão de sair do jogo no estado de pause. */
	public boolean showExitButtonOnPause;
	
	/**
	 * Cria um novo objeto de configurações do jogo.
	 */
	public ConfigurationData() {
		//Define configurações padrão.
		this.radiusHudOrb = 25;
		this.screenWidth = 800;
		this.screenHeight = 480;
		this.fps = 30;
		this.minLogicUpdateInterval = 10;
		this.language = "en-us";
		this.debugEnable = false;
		this.editorEnable = false;
		this.alwaysDrawHud = true;
		this.isMobile = false;
		this.showExitButtonOnPause = true;
	}
	
	/**
	 * Obtém o processador de input do jogo.
	 * @param state Estado de jogo do nível.
	 * @return InputProcessar do jogo.
	 */
	public abstract GameInputProcessor getInputProcessor(LevelState state);
}
