package org.escape2team.telyn.editor;

import java.util.List;

import org.escape2team.telyn.core.ObjectData;
import org.escape2team.telyn.core.ObjectType;
import org.escape2team.telyn.core.Utils;
import org.escape2team.telyn.core.ViewportTransform;
import org.escape2team.telyn.objects.ObjectCreator;
import org.jbox2d.collision.AABB;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Modo de edição de criação e posicionamento de objetos.
 */
public class ObjectPositionEditorMode extends EditorModeHandler {
	/** Tempo de espera para análise de pressionamento de teclas. */
	private static final int KEY_DELTA = 100;
	/** Espaço de tempo para análise de pressionamento de teclas. */
	private int keyDelta;
	/** Objetos do jogo. */
	public List<ObjectData> objects;
	/** Sprites dos objetos. */
	private Object[] sprites;
	/** Criador de objetos do jogo. */
	private ObjectCreator creator;
	/** Objeto atual. */
	private ObjectData currentObject;
	/** Indica se se está posicionando o trigger do objeto. */
	private boolean isPositioningTrigger;
	/** Posição do mouse na tela. */
	private Vec2 mousePositionScreen;
	
	/**
	 * Construtor da classe.
	 * @param environment		Bounding box do mundo.
	 * @param world				Objeto que representa o mundo do jogo.
	 * @param transform			Objeto para conversões entre unidades de tela e mundo.
	 * @param objects			Objetos do jogo.
	 * @param creator			Criador de objetos do jogo.
	 * @param sprites			Sprites do jogo.
	 * @throws SlickException 
	 */
	public ObjectPositionEditorMode(AABB environment, World world, ViewportTransform transform, List<ObjectData> objects, ObjectCreator creator, Object[] sprites) throws SlickException {
		super(environment, world, transform);
		this.objects = objects;
		this.creator = creator;
		this.sprites = sprites;
		
		this.isPositioningTrigger = false;
	}

	//HERDADOS=========================================================================
	
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		this.mousePositionScreen = new Vec2(container.getInput().getMouseX(), container.getInput().getMouseY());
		
		//Indica a inclusão de objeto do tipo 0.
		this.changeObjectType(0);
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) {
	
	}

	@Override
	public void leave(GameContainer container, StateBasedGame game) throws SlickException {
		
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g, Color filter) throws SlickException {
		this.renderObjectInMouse(g);
		this.renderObjectInfo(g);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		this.keyDelta += delta;
		Input kb = container.getInput();
		
		if (this.keyDelta > KEY_DELTA) {	
			this.keyDelta = 0;
			
			//Troca de objetos.
			if (kb.isKeyDown(Input.KEY_0)) this.changeObjectType(0);
			if (kb.isKeyDown(Input.KEY_1)) this.changeObjectType(1);
			if (kb.isKeyDown(Input.KEY_2)) this.changeObjectType(2);
			if (kb.isKeyDown(Input.KEY_3)) this.changeObjectType(3);
			if (kb.isKeyDown(Input.KEY_4)) this.changeObjectType(4);
			if (kb.isKeyDown(Input.KEY_5)) this.changeObjectType(5);
			if (kb.isKeyDown(Input.KEY_6)) this.changeObjectType(6);
			if (kb.isKeyDown(Input.KEY_7)) this.changeObjectType(7);
			if (kb.isKeyDown(Input.KEY_8)) this.changeObjectType(8);
			if (kb.isKeyDown(Input.KEY_9)) this.changeObjectType(9);
			if (kb.isKeyDown(Input.KEY_R)) this.reloadObjects();			
		}
	}

	@Override
	public void mouseMoved(Vec2 screenPos, Vec2 worldPos, boolean isOffLimits) {
		this.mousePositionScreen = screenPos;
	}

	@Override
	public void mousePressed(int button, Vec2 screenPos, Vec2 worldPos, boolean isOffLimits) {
		this.mousePositionScreen = screenPos;
		
		if (!isOffLimits) {
			if (button == 0) {
				if (this.isPositioningTrigger) this.saveTriggerPosition();
				else this.saveObjectPosition();
			}
			else if (button == 1) this.deleteObjectOrTrigger();
		}
	}

	@Override
	public void mouseReleased(int button, Vec2 screenPos, Vec2 worldPos, boolean isOffLimits) {
		this.mousePositionScreen = screenPos;
	}
	
	@Override
	public void mouseWheelMoved(int size) {
		
	}

	@Override
	public void renderHelp(GameContainer container, StateBasedGame game, Graphics g, Vec2 startPosition) {
		g.drawString("Clique esquerdo: posiciona objeto/trigger", startPosition.x, startPosition.y); startPosition.y += EditorModeHandler.HELP_LINE_HEIGHT;
		g.drawString("Clique direito: exclui objeto e seu trigger", startPosition.x, startPosition.y); startPosition.y += EditorModeHandler.HELP_LINE_HEIGHT;
		g.drawString("0...9: seleciona o objeto", startPosition.x, startPosition.y); startPosition.y += EditorModeHandler.HELP_LINE_HEIGHT;
		g.drawString("T: cria trigger do objeto", startPosition.x, startPosition.y); startPosition.y += EditorModeHandler.HELP_LINE_HEIGHT;
		g.drawString("R: recarrega os objetos criados", startPosition.x, startPosition.y); startPosition.y += EditorModeHandler.HELP_LINE_HEIGHT;
	}
	
	//APOIO============================================================================

	/**
	 * Troca o tipo do objeto.
	 * @param id ID do objeto, de acordo com os tipos em ObjectType.
	 */
	private void changeObjectType(int id) {
		Vec2 pos = this.transform.screenToWorld(this.mousePositionScreen);
		this.currentObject = new ObjectData(ObjectType.getFromId(id), pos, new Vec2(0, 0));		
		this.isPositioningTrigger = false;
	}
	
	/**
	 * Salva a posição atual do objeto.
	 */
	private void saveObjectPosition() {
		this.currentObject.position.set(this.transform.screenToWorld(this.mousePositionScreen));
		this.currentObject.object = this.creator.create(this.currentObject.type, this.currentObject.position, null);
		try {
			this.currentObject.object.init(null, null);
		} catch (SlickException e) {
			e.printStackTrace();
		}
		this.objects.add(this.currentObject);		
		
		//Verifica se o objeto possui trigger.
		if (this.currentObject.type == ObjectType.BigRock) {
			this.isPositioningTrigger = true;
		} else {
			//Força a troca do tipo de objeto para criação de novo objeto.
			this.changeObjectType(this.currentObject.type.getId());
		}
	}
	
	/**
	 * Salva a posição da trigger.
	 */
	private void saveTriggerPosition() {
		this.currentObject.triggerPosition.set(this.transform.screenToWorld(this.mousePositionScreen));
		this.currentObject.object.createTrigger(this.currentObject.triggerPosition);
		this.isPositioningTrigger = false;		

		//Força a troca do tipo de objeto para criação de novo objeto.
		this.changeObjectType(this.currentObject.type.getId());
	}
	
	/**
	 * Exclui um objeto ou trigger na posição atual do mouse.
	 */
	private void deleteObjectOrTrigger() {
		//Verifica se há um corpo na posição definida.
		Body b = Utils.getBodyAtPosition(this.world, this.transform.screenToWorld(this.mousePositionScreen));
		
		//Havendo um corpo, verifica se é algum dos corpos de objetos do jogo.
		if (b != null) {
			for (int i = this.objects.size() - 1; i >= 0; i--) {
				ObjectData data = this.objects.get(i);
				if (data.object == b.m_userData) {
					data.object.destroy();
					this.objects.remove(i);
					
					if (data == this.currentObject) {
						this.isPositioningTrigger = false;
						//Força a troca do tipo de objeto para criação de novo objeto.
						this.changeObjectType(this.currentObject.type.getId());
					}
				}
			}
		}
		
	}
	
	/**
	 * Renderiza informações sobre o objeto atual.
	 * @param g Objeto de desenho.
	 */
	private void renderObjectInfo(Graphics g) throws SlickException {	
		String currentObject = "OBJECT: " + this.currentObject.type.toString();
		g.drawString(currentObject, 792 - g.getFont().getWidth(currentObject), 45);		
	}
	
	/**
	 * Renderiza sprite do objeto na posição atual do mouse.
	 * @param g Objeto de desenho.
	 */	
	private void renderObjectInMouse(Graphics g) {
		Image sprite = this.getSprite(this.currentObject.type);
		float localScale = this.transform.getScale() / ViewportTransform.BASE_WORLD_SCALE;
		sprite.draw(this.mousePositionScreen.x - (localScale * sprite.getWidth()) / 2, this.mousePositionScreen.y - (localScale * sprite.getHeight()) / 2, localScale);
		
		if (this.isPositioningTrigger) {
			g.drawString("TRIGGER", this.mousePositionScreen.x, this.mousePositionScreen.y);
			Vec2 size = this.currentObject.object.getTriggerSize();
			size.x = this.transform.convertWorldScaleInPixels(size.x);
			size.y = this.transform.convertWorldScaleInPixels(size.y);
			g.drawRect(this.mousePositionScreen.x - size.x / 2, this.mousePositionScreen.y - size.y / 2, size.x, size.y);
		}
	}
	
	/** 
	 * Recarrega todos os objetos de volta às suas posições iniciais.
	 */
	private void reloadObjects() {
		for (ObjectData data : this.objects) {
			data.object.destroy();
			data.object = this.creator.create(data.type, data.position, data.triggerPosition);
		}
	}
	
	/**
	 * Obtpem o sprite de um determinado tipo de objeto.
	 * @param type Tipo do objeto.
	 * @return Sprite do objeto.
	 */
	private Image getSprite(ObjectType type) {
		int id = type.getId();
		Image sprite = (this.sprites[id] instanceof Image[] ? ((Image[])this.sprites[id])[0] : (Image)this.sprites[id]);
		return sprite;
	}
}
