package org.escape2team.telyn.parallax;

import java.util.LinkedList;
import java.util.List;

import org.escape2team.telyn.configuration.LayerConfiguration;
import org.escape2team.telyn.core.GameObject;
import org.escape2team.telyn.core.ViewportTransform;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Controlador de execução de paralaxe.
 */
public class ParallaxController implements GameObject {
	/** Camadas da paralaxe. */
	private List<ParallaxLayer> layers;
	/** Camadas de background da paralaxe. */
	private List<ParallaxLayer> background;
	/** Camada principal da paralaxe. */
	private ParallaxLayer main;
	/** Camadas de foreground da paralaxe. */
	private List<ParallaxLayer> foreground;

	/**
	 * Cria um novo gerenciador de camadas de paralaxe.
	 */
	public ParallaxController() {
		this.layers = new LinkedList<ParallaxLayer>();
		this.background = new LinkedList<ParallaxLayer>();
		this.foreground = new LinkedList<ParallaxLayer>();
	}

	/**
	 * Adiciona uma nova camada de paralaxe
	 * @param layer Camada a ser adicionada.
	 */
	public void addLayer(ParallaxLayer layer) {
		this.layers.add(layer);
		
		//Adiciona a camada ao campo correto.
		if (layer.getLayerID() > 0) {
			this.foreground.add(layer);
		} else if (layer.getLayerID() < 0) {
			this.background.add(layer);
		} else {
			this.main = layer;
		}
	}
	
	/**
	 * Cria uma camada de paralaxe estática.
	 * @param id				Identificador da camada. Será positivo se à frente da tela e negativo se atrás.
	 * @param img				Imagem da camada.
	 * @param layerPosition		Posição da camada na tela.
	 * @param objectPosition	Posição do objeto na camada.
	 * @param speed				Velocidade da camada em função do movimento do jogador (1 = exato movimento do jogador).
	 */
	public void addLayer(int id, Image img, Vec2 layerPosition, Vec2 objectPosition, Vec2 speed) {
		Vec2 layerSize = new Vec2(img.getWidth(), img.getHeight());
		this.addLayer(id, img, layerSize, layerPosition, objectPosition, speed);
	}
	
	/**
	 * Cria uma camada de paralaxe estática.
	 * @param id				Identificador da camada. Será positivo se à frente da tela e negativo se atrás.
	 * @param img				Imagem da camada.
	 * @param layerSize			Tamanho da camada.
	 * @param layerPosition		Posição da camada na tela.
	 * @param objectPosition	Posição do objeto na camada.
	 * @param speed				Velocidade da camada em função do movimento do jogador (1 = exato movimento do jogador).
	 */
	public void addLayer(int id, Image img, Vec2 layerSize, Vec2 layerPosition, Vec2 objectPosition, Vec2 speed) {
		ParallaxLayer layer = new StaticParallaxLayer(id, img, layerSize, layerPosition, objectPosition, speed);
		this.addLayer(layer);
	}
	
	/**
	 * Cria uma camada de paralaxe animada.
	 * @param id				Identificador da camada. Será positivo se à frente da tela e negativo se atrás.
	 * @param anim				Animação da camada.
	 * @param layerPosition		Posição da camada na tela.
	 * @param objectPosition	Posição do objeto na camada.
	 * @param speed				Velocidade da camada em função do movimento do jogador (1 = exato movimento do jogador).
	 */
	public void addLayer(int id, Animation anim, Vec2 layerPosition, Vec2 objectPosition, Vec2 speed) {
		Vec2 layerSize = new Vec2(anim.getWidth(), anim.getHeight());
		this.addLayer(id, anim, layerSize, layerPosition, objectPosition, speed);
	}
	
	/**
	 * Cria uma camada de paralaxe animada.
	 * @param id				Identificador da camada. Será positivo se à frente da tela e negativo se atrás.
	 * @param anim				Animação da camada.
	 * @param layerSize			Tamanho da camada.
	 * @param layerPosition		Posição da camada na tela.
	 * @param objectPosition	Posição do objeto na camada.
	 * @param speed				Velocidade da camada em função do movimento do jogador (1 = exato movimento do jogador).
	 */
	public void addLayer(int id, Animation anim, Vec2 layerSize, Vec2 layerPosition, Vec2 objectPosition, Vec2 speed) {
		ParallaxLayer layer = new AnimatedParallaxLayer(id, anim, layerSize, layerPosition, objectPosition, speed);
		this.addLayer(layer);
	}
	
	/**
	 * Cria uma camada de paralaxe com tiles.
	 * @param id				Identificador da camada. Será positivo se à frente da tela e negativo se atrás.
	 * @param config			Configurações do nível.
	 * @param world				Objeto que representa o mundo do jogo.
	 * @param transform			Objeto para conversões entre unidades de tela e mundo.
	 * @param layerSize			Tamanho da camada.
	 * @param layerPosition		Posição da camada na tela.
	 * @param objectPosition	Posição do objeto na camada.
	 * @param speed				Velocidade da camada em função do movimento do jogador (1 = exato movimento do jogador).
	 */
	public void addLayer(int id, LayerConfiguration config, World world, ViewportTransform transform, Vec2 layerSize, Vec2 layerPosition, Vec2 speed) {
		ParallaxLayer layer = new TiledParallaxLayer(id, config, world, transform,  layerSize, layerPosition, speed);
		this.addLayer(layer);
	}
		
	/**
	 * Obtém a posição da camada principal.
	 * @return Posição atual da camada principal.
	 */
	public Vec2 getMainLayerPosition() {
		return this.main.getPosition();
	}
	
	/**
	 * Obtém todas as camadas.
	 */
	public List<ParallaxLayer> getLayersAll() {
		return this.layers;
	}
	
	/**
	 * Obtém as camadas de background.
	 */
	public List<ParallaxLayer> getLayersBackground() {
		return this.background;
	}
	
	/**
	 * Obtém a camada principal do jogo.
	 */
	public ParallaxLayer getLayerMain() {
		return this.main;
	}
	
	/**
	 * Obtém as camadas de foreground.
	 */
	public List<ParallaxLayer> getLayersForeground() {
		return this.foreground;
	}
	
	/**
	 * Movimenta a camada.
	 * @param size Quantidade de movimento, em pixels.
	 */
	public void move(Vec2 size) {
		for (ParallaxLayer layer : this.layers) layer.move(size);
	}
	
	/**
	 * Inicia as animações das camadas.
	 */
	public void animationStart() {
		for (ParallaxLayer layer : this.layers) {
			if (layer instanceof AnimatedParallaxLayer) ((AnimatedParallaxLayer)layer).animationStart();
		}
	}
	
	/**
	 * Pára as animações da camadas.
	 */
	public void animationStop() {
		for (ParallaxLayer layer : this.layers) {
			if (layer instanceof AnimatedParallaxLayer) ((AnimatedParallaxLayer)layer).animationStop();
		}
	}

	/**
	 * Renderiza as camadas de background.
	 * @param container
	 * @param game
	 * @param g
	 */
	public void renderBackground(GameContainer container, StateBasedGame game, Graphics g, Color filter) throws SlickException {
		for (ParallaxLayer layer : this.background) layer.render(container, game, g, filter);
	}
	
	/**
	 * Renderiza a camada principal do jogo.
	 * @param container
	 * @param game
	 * @param g
	 */
	public void renderMain(GameContainer container, StateBasedGame game, Graphics g, Color filter) throws SlickException {
		this.main.render(container, game, g, filter);
	}
	
	/**
	 * Renderiza as camadas de foreground.
	 * @param container
	 * @param game
	 * @param g
	 */
	public void renderForeground(GameContainer container, StateBasedGame game, Graphics g, Color filter) throws SlickException {
		for (ParallaxLayer layer : this.foreground) layer.render(container, game, g, filter);
	}
	
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		for (ParallaxLayer layer : this.layers) layer.init(container, game);
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		for (ParallaxLayer layer : this.layers) layer.enter(container, game);
	}

	@Override
	public void leave(GameContainer container, StateBasedGame game) throws SlickException {
		for (ParallaxLayer layer : this.layers) layer.leave(container, game);
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g, Color filter) throws SlickException {
		for (ParallaxLayer layer : this.layers) layer.render(container, game, g, filter);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		for (ParallaxLayer layer : this.layers) layer.update(container, game, delta);
	}
}
