package org.escape2team.telyn.editor;

import java.util.LinkedList;
import java.util.List;

import org.escape2team.telyn.configuration.LevelConfiguration;
import org.escape2team.telyn.core.Utils;
import org.escape2team.telyn.core.ViewportTransform;
import org.jbox2d.collision.AABB;
import org.jbox2d.collision.shapes.PolygonDef;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Modo de edi��o de colis�es com o cen�rio.
 */
public class CollisionEditorMode extends EditorModeHandler {
	/** Pontos da �rea de colis�o atual. */
	private List<Vec2> currentCollision;
	/** Indica se se est� criando pol�gonos de colis�o. */
	private boolean isCreatingPolygon;
	/** Posi��o do mouse na mundo. */
	private Vec2 mousePositionWorld;
	/** Conjuntos de pontos das �reas de colis�o. */
	private List<List<Vec2>> collisions;
	
	/**
	 * Construtor da classe.
	 * @param environment	Bounding box do mundo.
	 * @param world			Objeto que representa o mundo do jogo.
	 * @param transform		Objeto para convers�es entre unidades de tela e mundo.
	 * @param collisions	Conjuntos de pontos das �reas de colis�o.
	 */
	public CollisionEditorMode(AABB environment, World world, ViewportTransform transform, List<List<Vec2>> collisions) {
		super(environment, world, transform);
		this.collisions = collisions;
	}

	//HERDADOS=========================================================================
	
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) {
	
	}

	@Override
	public void leave(GameContainer container, StateBasedGame game) throws SlickException {
		
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g, Color filter) throws SlickException {
		//Caso esteja em desenho, cria linha de desenho.
		if (this.isCreatingPolygon) {
			//Desenha os segmentos j� armazenados.
			for (int i = 0; i < this.currentCollision.size() - 1; i++) {
				this.editorDrawSegment(this.currentCollision.get(i), this.currentCollision.get(i + 1), g);
			}
			
			//Cria linha entre o �ltimo ponto e o atual.
			this.editorDrawSegment(this.currentCollision.get(this.currentCollision.size() - 1), this.mousePositionWorld, g);
		}
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		Input kb = container.getInput();
		
		//Verifica t�rmino de cria��o de pol�gono.
		if (this.isCreatingPolygon && kb.isKeyDown(Input.KEY_D)) {
			this.editorEndDrawing();
		}
	}

	@Override
	public void mouseMoved(Vec2 screenPos, Vec2 worldPos, boolean isOffLimits) {
		this.mousePositionWorld = worldPos;
		
	}

	@Override
	public void mousePressed(int button, Vec2 screenPos, Vec2 worldPos, boolean isOffLimits) {
		this.mousePositionWorld = worldPos;
		
		if (button == 0) { //Bot�o esquerdo.
			if (this.currentCollision == null) {
				this.currentCollision = new LinkedList<Vec2>();
				this.isCreatingPolygon = true;
			}
			this.currentCollision.add(new Vec2(this.mousePositionWorld));
		} else if (button == 1) { //Bot�o direito.
			if (this.isCreatingPolygon) {
				//Caso esteja em desenho, apaga o ponto anterior.
				if (this.currentCollision.size() > 0) {
					this.currentCollision.remove(this.currentCollision.size() - 1);
					
					if (this.currentCollision.size() == 0) {
						this.currentCollision = null;
						this.isCreatingPolygon = false;
					}
				}
			} else {						
				//Apaga o corpo na posi��o do mouse, se houver.
				Body b = Utils.getBodyAtPosition(this.world, this.mousePositionWorld);
				
				//Antes de apagar o corpo, remove os pontos da lista de colis�es.
				if (b != null) {
					if (b.getUserData() != null) this.collisions.remove(b.getUserData());
					this.world.destroyBody(b);
				}
			}
		}
	}

	@Override
	public void mouseReleased(int button, Vec2 screenPos, Vec2 worldPos, boolean isOffLimits) {
		this.mousePositionWorld = worldPos;
		
	}
	
	@Override
	public void mouseWheelMoved(int size) {
		
	}

	@Override
	public void renderHelp(GameContainer container, StateBasedGame game, Graphics g, Vec2 startPosition) {
		g.drawString("Clique esquerdo: edita pontos de colis�o", startPosition.x, startPosition.y); startPosition.y += EditorModeHandler.HELP_LINE_HEIGHT;
		g.drawString("Clique direito: exclui pontos de colis�o", startPosition.x, startPosition.y); startPosition.y += EditorModeHandler.HELP_LINE_HEIGHT;
		g.drawString("D: finaliza desenho do pol�gono de colis�o", startPosition.x, startPosition.y);
	}

	//APOIO============================================================================
	
	/**
	 * Desenha um segmento de reta.
	 * @param start In�cio do segmento, em unidades do mundo.
	 * @param end	Fim do segmento, em unidades do mundo.
	 * @param g		Objeto gr�fico para desenho.
	 */
	private void editorDrawSegment(Vec2 start, Vec2 end, Graphics g) {
		Vec2 screenP1 = this.transform.worldToScreen(start);
		Vec2 screenP2 = this.transform.worldToScreen(end);
		g.drawLine(screenP1.x, screenP1.y, screenP2.x, screenP2.y);
	}
	
	/**
	 * Finaliza o desenho.
	 */
	private void editorEndDrawing() {
		BodyDef bd = new BodyDef();
		Body b = this.world.createBody(bd);
		
		for (int i = 0; i< this.currentCollision.size() - 1; ++i) {
			PolygonDef sd = new PolygonDef();
			sd.friction = LevelConfiguration.GROUND_FRICTION;
			Utils.createStrokeRect(this.currentCollision.get(i), this.currentCollision.get(i + 1), LevelConfiguration.STROKE_RADIUS, b, sd);
		}
		
		b.setMassFromShapes();
		//Define os dados de usu�rio do pol�gono como os pontos de colis�o.
		b.setUserData(this.currentCollision);
		//Adiciona os dados do pol�gono � lista de colis�es.
		this.collisions.add(this.currentCollision);

		this.currentCollision = null;
		this.isCreatingPolygon = false;
	}
}
