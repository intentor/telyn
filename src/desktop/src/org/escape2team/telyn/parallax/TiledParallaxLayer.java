package org.escape2team.telyn.parallax;

import java.util.LinkedList;
import java.util.List;

import org.escape2team.telyn.configuration.LayerConfiguration;
import org.escape2team.telyn.core.TileData;
import org.escape2team.telyn.core.TileMapItem;
import org.escape2team.telyn.core.Utils;
import org.escape2team.telyn.core.ViewportTransform;
import org.escape2team.telyn.states.Loader;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class TiledParallaxLayer extends ParallaxLayer {
	/** Tamanho dos tiles de tela, em unidades de tela. */
	private static final int TILE_SIZE = 64;
	/** Configurações do nível. */
	private LayerConfiguration config;
	/** Objeto para conversões entre unidades de tela e mundo. */
	private ViewportTransform transform;
	/** Metade da largura do mundo, em unidades de tela. */
	private float halfWorldWidth;
	/** Metade da altura do mundo, em unidades de tela. */
	private float halfWorldHeight;
	/** Largura da câmera, em tiles. */
	private float camWidth;
	/** Altura da câmera, em tiles. */
	private float camHeight;
	/** Mapa de tiles. */
	private TileMapItem[][] map;
	
	/**
	 * Cria uma nova camada de paralaxe.
	 * @param id				Identificador da camada. Será positivo se à frente da tela e negativo se atrás.
	 * @param config			Configurações do nível.
	 * @param world				Objeto que representa o mundo do jogo.
	 * @param transform			Objeto para conversões entre unidades de tela e mundo.
	 * @param layerSize			Tamanho da camada.
	 * @param layerPosition		Posição da camada na tela.
	 * @param objectPosition	Posição do objeto na camada.
	 * @param speed				Velocidade da camada em função do movimento do jogador (1 = exato movimento do jogador).
	 */
	public TiledParallaxLayer(int id, LayerConfiguration config, World world, ViewportTransform transform, Vec2 layerSize, Vec2 layerPosition, Vec2 speed) {
		super(id, layerSize, layerPosition, new Vec2(0, 0), speed);
		this.config = config;
		this.transform = transform;
	}
	
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		this.createTileMap(this.config.tiles);
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) {
	
	}

	@Override
	public void leave(GameContainer container, StateBasedGame game) throws SlickException {
		
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g, Color filter) throws SlickException {
		this.renderTiles(g, filter);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		
	}
	
	//MÉTODOS DE CARREGAMENTO DE TILES================================================
	
	/**
	 * Cria o mapa de tiles para renderização do cenário.
	 * @param tiles Informações dos tiles a serem renderizados em tela.
	 */
	private void createTileMap(List<TileData> tiles) {
		//Define as dimensões da câmera em tiles.
		this.camWidth = (float) (Math.ceil((float)Loader.CONFIGURATIONS.screenWidth) / TILE_SIZE);
		this.camHeight = (float) (Math.ceil((float)Loader.CONFIGURATIONS.screenHeight) / TILE_SIZE);
		
		//Define as dimensões do mundo do jogo em unidades de tela.
		this.halfWorldWidth = this.transform.convertWorldScaleInPixels(
				Math.abs(this.config.level.layerUpperBound.x) + 
				Math.abs(this.config.level.layerLowerBound.x)
			) / 2;
		this.halfWorldHeight = this.transform.convertWorldScaleInPixels(
				Math.abs(this.config.level.layerUpperBound.y) + 
				Math.abs(this.config.level.layerLowerBound.y)
			) / 2;
		
		//Cria o objeto que representa o mapa de tiles.
		int tilesX = (int) Math.ceil((this.halfWorldWidth * 2) / TILE_SIZE);
		int tilesY = (int) Math.ceil((this.halfWorldHeight * 2) / TILE_SIZE);
		this.map = new TileMapItem[tilesX][tilesY];
		
		//Cria todas as posições do mapa de tiles.
		for (int x = 0; x < tilesX; x++) {
			for (int y = 0; y < tilesY; y++) {
				this.map[x][y] = new TileMapItem(x, y);
			}
		}
		
		//Para cada tile configurado para o nível, ajusta sua posição no mapa.
		for (TileData data : this.config.tiles) {
			//Posição do tile em unidades de tela.
			data.location.set(this.convertPositionFromWorld(data.position));
			
			//Tamanho do tile em pixels.
			Image sprite = this.config.level.packs.get(data.definitionFileId).getSprite(data.spriteName);	
			Vec2 size = new Vec2(sprite.getWidth(), sprite.getHeight());
			
			//Retira as metades do tamanho do tile em sua posição, a qual é baseada no centro.
			data.location.x -= size.x / 2;
			data.location.y -= size.y / 2;
			
			//Verifica a primeira posição no mapa de tiles no qual o tile deve aparecer.
			int tileX = (int) Math.floor(data.location.x / TILE_SIZE);
			int tileY = (int) Math.floor(data.location.y / TILE_SIZE);
			
			//Normaliza os valores.
			if (tileX < 0) tileX = 0;
			if (tileY < 0) tileY = 0;
						
			//Avalia as posições do tile no mapa de acordo com seu tamanho.
			for (int x = 0; x < (int) Math.ceil(size.x / TILE_SIZE); x++) {
				for (int y = 0; y < (int) Math.ceil(size.y / TILE_SIZE); y++) {
					//Evita que seja incluída uma posição além dos limites do mapa.
					if ((tileX + x) >= this.map.length || (tileY + y) >= this.map[0].length) continue;
					this.map[tileX + x][tileY + y].addTile(data);
				}
			}		
		}
		
		//Instancia a lista de armazenamento de tiles já renderizados.
		TileMapItem.rendered = new LinkedList<TileData>();
	}
	
	//MÉTODOS DE RENDERIZAÇÃO DE TILES================================================
	
	/**
	 * Renderiza os tiles armazenados.
	 * @param g 		Objeto de desenho.
	 * @param filter	Filtro de cores dos tiles.
	 */
	private void renderTiles(Graphics g, Color filter) {
		//Limpa a lista de tiles já renderizados.
		TileMapItem.rendered.clear();
		
		//Obtém a posição atual da câmera, em unidades de tela.
		Vec2 cam = this.transform.getCenterScreen();
		//Ajusta a posição da câmera considerando o ponto (0,0) do mundo.
		cam.x = -cam.x;		
		cam.x = (float) Math.floor(this.halfWorldWidth + cam.x);
		cam.y = (float) Math.floor(this.halfWorldHeight - cam.y);
		
		//Obtém o tile que representa a posição da câmera.
		int tileX = (int) Math.floor(cam.x / TILE_SIZE);
		int tileY = (int) Math.floor(cam.y / TILE_SIZE);
		
		//Obtém as extensões da câmera.
		int maxTileX = (int) (tileX + this.camWidth) + 1;
		if (maxTileX >= this.map.length) maxTileX = this.map.length - 1;
		int maxTileY = (int) (tileY + this.camHeight) + 1;
		if (maxTileY >= this.map[0].length) maxTileY = this.map[0].length - 1;
		
		//Renderiza os tiles.		
		for (int x = (tileX > 0 ? tileX - 1 : 0); x <= maxTileX; x++) {
			for (int y = (tileY > 0 ? tileY - 1 : 0); y <= maxTileY; y++) {
				//Renderiza todos os tiles na posição.
				for (TileData data : this.map[x][y].tiles) {
					if (!this.isRendered(data)) {
						//Obtém a imagem do tile.
						Image sprite = this.config.level.packs.get(data.definitionFileId).getSprite(data.spriteName).getFlippedCopy(data.flip, false);
						//Ajusta a posição do tile de acordo com a posição da câmera e do tile no mundo em unidades de tela.
						Vec2 position = new Vec2(data.location.x - cam.x, data.location.y - cam.y);									
						//Desenha o tile.
						Utils.drawImage(sprite, position, data.rotation, this.transform.getLocalScale(), g, filter);
					}
				}
			}
		}
	}
	
	/**
	 * Verifica se um tile já foi renderizado.
	 * @param data Dados do tile.
	 * @return Valor booleano indicando se o tile já foi renderizado.
	 */
	private boolean isRendered(TileData data) {
		boolean rendered = false;
		
		//Verifica se o tile já foi renderizado.
		for (TileData tile : TileMapItem.rendered) {
			if (tile.tileId == data.tileId) {
				rendered = true;
				break;
			}
		}
		
		//Caso o tile não tenha sido renderizado, adiciona-o à lista de tiles renderizados.
		if (!rendered) TileMapItem.rendered.add(data);
		
		return rendered;
	}
	
	//MÉTODOS DE APOIO================================================================
	
	/**
	 * Converte uma posição em unidades do mundo em unidades de tela,
	 * porém considerando o canto superior esquerdo do mundo como 
	 * ponto inicial.
	 * @param value Vetor a ser convertido, em unidades do mundo.
	 * @return Vetor convertido.
	 */
	private Vec2 convertPositionFromWorld(Vec2 value) {
		Vec2 res = new Vec2(); 
		
		//Eixo X.
		/* Caso o valor seja maior ou igual a 0, deve-se somar metade da largura.
		 * Caso contrário, deve-se subtrair o valor da metade da largura.
		 */
		res.x = (float) Math.floor(this.halfWorldWidth + this.transform.convertWorldScaleInPixels(value.x));
		
		//Eixo Y.
		/* Caso o valor seja maior ou igual a 0, deve-se subtrair a metade da altura pelo valor.
		 * Caso contrário, deve-se somar o valor da metade da largura.
		 */
		res.y = (float) Math.floor(this.halfWorldHeight - this.transform.convertWorldScaleInPixels(value.y));
		
		return res;
	}
}