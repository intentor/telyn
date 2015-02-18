package org.escape2team.telyn.editor;

import java.util.List;

import org.escape2team.telyn.core.PackedSpriteSheet;
import org.escape2team.telyn.core.TileData;
import org.escape2team.telyn.core.ViewportTransform;
import org.escape2team.telyn.states.Loader;
import org.jbox2d.collision.AABB;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Modo de edição de tiles.
 * NOTA: deve haver pelo menos um pacote de sprites com pelo menos um sprite.
 */
public class TilesEditorMode extends EditorModeHandler {
	/** Máximo tamanho do grid. */
	private static final int GRID_SIZE_MAX = 64;
	/** Mínimo dtamanho do grid. */
	private static final int GRID_SIZE_MIN = 8;
	/** Fator de alteração do tamanho do grid. */
	private static final int GRID_SIZE_FACTOR = 8;
	/** Fator de rotação, em radianos. */
	private static final float ROTATION_FACTOR = 0.1f;
	/** Tempo de espera para análise de pressionamento de teclas. */
	private static final int KEY_DELTA = 100;
	/** Espaço de tempo para análise de pressionamento de teclas. */
	private int keyDelta;
	/** Pacotes de tiles do jogo. */
	private List<PackedSpriteSheet> packs;
	/** Lista de tiles do jogo. */
	private List<TileData> tiles;
	/** Índice do pacote de sprites atual. */
	private int currentSpritePack;
	/** Índice do sprite atual. */
	private int currentSprite;
	/** Rotação atual da imagem, em radianos. */
	private float currentRotation;
	/** Indica se a imagem deve ser flipada. */
	private boolean flip;
	/** Indica se o grid deve ser exibido. */
	private boolean showGrid;
	/** Tamanho do grid. */
	private int currentGridSize;
	/** Posição atual do tile, em unidades de tela. */
	private Vec2 currentTilePosition;
	/** Posição do mouse na tela. */
	private Vec2 mousePositionScreen;
	
	/**
	 * Construtor da classe.
	 * @param environment	Bounding box do mundo.
	 * @param world			Objeto que representa o mundo do jogo.
	 * @param transform		Objeto para conversões entre unidades de tela e mundo.
	 * @param packs			Pacotes de tiles do jogo.
	 * @param tiles			Lista de tiles do jogo.
	 */
	public TilesEditorMode(AABB environment, World world, ViewportTransform transform, List<PackedSpriteSheet> packs, List<TileData> tiles) {
		super(environment, world, transform);
		this.packs = packs;
		this.tiles = tiles;
	}

	//HERDADOS=========================================================================
	
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		this.currentSpritePack = 0;
		this.currentSprite = 0;
		
		this.mousePositionScreen = new Vec2(container.getInput().getMouseX(), container.getInput().getMouseY());
		this.currentTilePosition = new Vec2(this.mousePositionScreen);
		
		this.showGrid = true;
		this.currentGridSize = GRID_SIZE_MIN;
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) {
	
	}

	@Override
	public void leave(GameContainer container, StateBasedGame game) throws SlickException {
		
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g, Color filter) throws SlickException {
		this.renderGrid(g);
		this.renderSpriteInMouse(g);
		this.renderSpriteInfo(g);		
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		this.keyDelta += delta;
		Input kb = container.getInput();
		
		if (this.keyDelta > KEY_DELTA) {	
			this.keyDelta = 0;
			
			//Troca de pacotes de sprites.
			if (kb.isKeyDown(Input.KEY_0)) this.setCurrentSpritePack(0);
			if (kb.isKeyDown(Input.KEY_1)) this.setCurrentSpritePack(1);
			if (kb.isKeyDown(Input.KEY_2)) this.setCurrentSpritePack(2);
			if (kb.isKeyDown(Input.KEY_3)) this.setCurrentSpritePack(3);
			if (kb.isKeyDown(Input.KEY_4)) this.setCurrentSpritePack(4);
			if (kb.isKeyDown(Input.KEY_5)) this.setCurrentSpritePack(5);
			if (kb.isKeyDown(Input.KEY_6)) this.setCurrentSpritePack(6);
			if (kb.isKeyDown(Input.KEY_7)) this.setCurrentSpritePack(7);
			if (kb.isKeyDown(Input.KEY_8)) this.setCurrentSpritePack(8);
			if (kb.isKeyDown(Input.KEY_9)) this.setCurrentSpritePack(9);
	
			//Troca de sprites.
			if (kb.isKeyDown(Input.KEY_NEXT)) this.setCurrentSprite(this.currentSprite + 1);
			if (kb.isKeyDown(Input.KEY_PRIOR)) this.setCurrentSprite(this.currentSprite - 1);
			//Flip do sprite.
			if (kb.isKeyDown(Input.KEY_F)) this.flip = !this.flip;
			//Exibição do grid.
			if (kb.isKeyDown(Input.KEY_G)) this.showGrid = !this.showGrid;
			//Redimensionamento do grid.
			if (kb.isKeyDown(Input.KEY_H)) this.expandGrid(-GRID_SIZE_FACTOR);
			if (kb.isKeyDown(Input.KEY_J)) this.expandGrid(GRID_SIZE_FACTOR);		
		}
		
		//Rotação de sprites (não requerem delay de tecla).
		if (kb.isKeyDown(Input.KEY_A)) this.addRotation(false);
		if (kb.isKeyDown(Input.KEY_D)) this.addRotation(true);
		if (kb.isKeyDown(Input.KEY_S)) this.currentRotation = 0;
		
		this.updateTilePosition();
	}

	@Override
	public void mouseMoved(Vec2 screenPos, Vec2 worldPos, boolean isOffLimits) {
		this.mousePositionScreen = screenPos;
	}

	@Override
	public void mousePressed(int button, Vec2 screenPos, Vec2 worldPos, boolean isOffLimits) {
		this.mousePositionScreen = screenPos;
		
		if (!isOffLimits) {
			if (button == 0) this.saveTile();
			else if (button == 1) this.deleteTile();
		}
	}

	@Override
	public void mouseReleased(int button, Vec2 screenPos, Vec2 worldPos, boolean isOffLimits) {
		this.mousePositionScreen = screenPos;
	}
	
	@Override
	public void mouseWheelMoved(int size) {
		if (size >= 0) this.setCurrentSprite(this.currentSprite + 1);
		else this.setCurrentSprite(this.currentSprite - 1);
	}	
	
	@Override
	public void renderHelp(GameContainer container, StateBasedGame game, Graphics g, Vec2 startPosition) {
		g.drawString("Clique esquerdo: posiciona tiles", startPosition.x, startPosition.y); startPosition.y += EditorModeHandler.HELP_LINE_HEIGHT;
		g.drawString("Clique direito: exclui tiles", startPosition.x, startPosition.y); startPosition.y += EditorModeHandler.HELP_LINE_HEIGHT;
		g.drawString("0...9: seleção de pacote de sprite", startPosition.x, startPosition.y); startPosition.y += EditorModeHandler.HELP_LINE_HEIGHT;
		g.drawString("Wheel: seleção de sprite do pacote", startPosition.x, startPosition.y); startPosition.y += EditorModeHandler.HELP_LINE_HEIGHT;
		g.drawString("A/D: rotação do sprite", startPosition.x, startPosition.y); startPosition.y += EditorModeHandler.HELP_LINE_HEIGHT;
		g.drawString("S: reseta a rotação do sprite", startPosition.x, startPosition.y); startPosition.y += EditorModeHandler.HELP_LINE_HEIGHT;
		g.drawString("F: flip do sprite", startPosition.x, startPosition.y); startPosition.y += EditorModeHandler.HELP_LINE_HEIGHT;
		g.drawString("G: exibição do grid", startPosition.x, startPosition.y); startPosition.y += EditorModeHandler.HELP_LINE_HEIGHT;
		g.drawString("H/J: ajuste de tamanho do grid", startPosition.x, startPosition.y);
	}
	
	//APOIO============================================================================
	
	/**
	 * Atualiza a posição do objeto de desenho.
	 */
	private void updateTilePosition() {
		if (this.showGrid) {
			//Configura a posição como se fosse borda inferior esquerda.
			Image sprite = this.packs.get(this.currentSpritePack).getSprite(this.currentSprite);
			this.currentTilePosition.x -= sprite.getWidth() / 2;
			this.currentTilePosition.y += sprite.getHeight() / 2;			
			//Posiciona de acordo com a posição do grid mais próxima.
			this.currentTilePosition.x = this.mousePositionScreen.x - this.mousePositionScreen.x % this.currentGridSize;
			this.currentTilePosition.y = this.mousePositionScreen.y - this.mousePositionScreen.y % this.currentGridSize;
			//Volta à posição central.
			this.currentTilePosition.x += sprite.getWidth() / 2;
			this.currentTilePosition.y -= sprite.getHeight() / 2;	
		} else {
			//Posiciona de acordo com o centro do mouse.
			this.currentTilePosition.set(this.mousePositionScreen);
		}		
	}
	
	/**
	 * Define um pacote de sprites atual.
	 * @param index Índice do pacote.
	 */
	private void setCurrentSpritePack(int index) {
		int packsLastPosition = this.packs.size() - 1;
		if (index > packsLastPosition) index = packsLastPosition;
		this.currentSpritePack = index;
		this.currentSprite = 0;		
		this.currentRotation = 0;
	}
	
	/**
	 * Define um sprite atual.
	 * @param index Índice do sprite no pacote atual.
	 */
	private void setCurrentSprite(int index) {
		int spritesLastPosition = this.packs.get(this.currentSpritePack).getCount() - 1;
		if (index < 0) index = 0;
		else if (index > spritesLastPosition) index = spritesLastPosition;
		this.currentSprite = index;		
		this.currentRotation = 0;
	}
	
	/**
	 * Adiciona rotação.
	 * @param clockwise Indica se a rotação deve se dar no sentido horário.
	 */
	private void addRotation(boolean clockwise) {
		if (clockwise) this.currentRotation -= ROTATION_FACTOR;
		else this.currentRotation += ROTATION_FACTOR;
		
		if (this.currentRotation > 2 * Math.PI || this.currentRotation < -2 * Math.PI) this.currentRotation = 0;
	}
	
	/**
	 * Realiza expansão do grid.
	 * @param size Tamanho do grid.
	 */
	private void expandGrid(int size) {
		if (this.showGrid) {
			this.currentGridSize += size;
			
			if (this.currentGridSize < GRID_SIZE_MIN) this.currentGridSize = GRID_SIZE_MIN;
			else if (this.currentGridSize > GRID_SIZE_MAX) this.currentGridSize = GRID_SIZE_MAX;
		}
	}
	
	/**
	 * Salva o tile na posição atual do mundo.
	 */
	private void saveTile() {
		TileData data = new TileData(-1
			, this.currentSpritePack
			, this.packs.get(this.currentSpritePack).getSpriteName(this.currentSprite)
			, this.transform.screenToWorld(this.currentTilePosition)
			, this.currentRotation
			, this.flip);
		this.tiles.add(data);
	}
	
	/**
	 * Delete o tile na posição atual do mundo.
	 */
	private void deleteTile() {
		//Verifica se há algum sprite na posição atual.
		
		for (int i = this.tiles.size() - 1; i >= 0; i--) {
			TileData data = this.tiles.get(i);
			Image sprite = this.packs.get(data.definitionFileId).getSprite(data.spriteName);			
			
			//Cria um retângulo representando a imagem.
			float width = this.transform.convertPixelsInWorldScale(sprite.getWidth());
			float height = this.transform.convertPixelsInWorldScale(sprite.getHeight());
			float x = data.position.x - width / 2;
			float y = data.position.y - height / 2;
			Rectangle rec = new Rectangle(x, y, width, height);
			
			//Verifica se o ponto está no retângulo.
			Vec2 worldPosition = this.transform.screenToWorld(this.currentTilePosition);
			if (rec.contains(worldPosition.x, worldPosition.y)) {
				this.tiles.remove(i);
				break;
			}
		}
	}
	
	/**
	 * Renderiza informações sobre o sprite atual.
	 * @param g Objeto de desenho.
	 */
	public void renderSpriteInfo(Graphics g) throws SlickException {	
		String currentPack = "PACK: " +
			String.valueOf(this.packs.get(this.currentSpritePack).getPackName() +
			" (" + String.valueOf(this.currentSpritePack) +")");
		String currentSprite = "SPRITE: " +
			String.valueOf(this.packs.get(this.currentSpritePack).getSpriteName(this.currentSprite));
		String angle = "ANGLE: " + String.valueOf(this.currentRotation);
		String grid = "GRID: " + String.valueOf(this.currentGridSize);

		g.drawString(currentPack, 792 - g.getFont().getWidth(currentPack), 45);
		g.drawString(currentSprite, 792 - g.getFont().getWidth(currentSprite), 65);
		g.drawString(angle, 792 - g.getFont().getWidth(angle), 85);
		if (this.showGrid) g.drawString(grid, 792 - g.getFont().getWidth(grid), 105);
	}
	
	/**
	 * Renderiza sprite na posição atual do mouse.
	 * @param g Objeto de desenho.
	 */	
	private void renderSpriteInMouse(Graphics g) {
		//Coloca o sprite atual no centro do mouse.
		Image sprite = this.packs.get(this.currentSpritePack).getSprite(this.currentSprite).getFlippedCopy(this.flip, false);
		//Desenha o sprite no centro do mouse.
		Vec2 worldPosition = this.transform.screenToWorld(this.currentTilePosition);
		this.drawImage(sprite, worldPosition, this.currentRotation, g, Color.white);
	}
	
	/**
	 * Desenha o grid.
	 * @param g Objeto de desenho.
	 */
	private void renderGrid(Graphics g) {
		if (this.showGrid) {
			int width = Loader.CONFIGURATIONS.screenWidth;
			int height = Loader.CONFIGURATIONS.screenHeight;
			int qtdX = (height / this.currentGridSize) + 1; //Linhas.
			int qtdY = (width / this.currentGridSize) + 1; //Colunas.
			
			//Desenha as linhas.
			for (int i = 0; i < qtdX; i++) {
				g.drawLine(0, i * this.currentGridSize, width, i * this.currentGridSize);
			}
			//Desenha as colunas.
			for (int i = 0; i < qtdY; i++) {
				g.drawLine(i * this.currentGridSize, 0, i * this.currentGridSize, height);
			}
		}
	}
	
	/**
	 * Desenha uma imagem.
	 * @param image		Imagem a ser desenhada.
	 * @param position	Posição da imagem em unidades do mundo.
	 * @param rotation	Rotação da imagem em radianos.
	 * @param g			Objeto gráfico.
	 * @param filter	Filtro de cor da imagem.
	 */
	private void drawImage(Image img, Vec2 position, float rotation, Graphics g, Color filter) {
		float localScale = this.transform.getScale() / ViewportTransform.BASE_WORLD_SCALE;
		float halfImageWidth = img.getWidth() / 2;
		float halfImageHeight = img.getHeight() / 2;
		Vec2 p = this.transform.worldToScreen(position);
		float angle = (float) Math.toDegrees(rotation);
		
		g.rotate(p.x, p.y, -angle);
		img.draw(p.x - localScale * halfImageWidth, p.y - localScale * halfImageHeight, localScale, filter);
		g.rotate(p.x, p.y, angle);
	}
}
