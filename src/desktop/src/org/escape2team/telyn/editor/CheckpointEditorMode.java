package org.escape2team.telyn.editor;

import java.util.List;

import org.escape2team.telyn.core.Utils;
import org.escape2team.telyn.core.ViewportTransform;
import org.escape2team.telyn.states.LevelState;
import org.jbox2d.collision.AABB;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Modo de edição de colisões com o cenário.
 */
public class CheckpointEditorMode extends EditorModeHandler {
	/** Estado do nível do jogo. */
	private LevelState state;
	/** Checkpoints do jogo. */
	private List<Vec2> checkpoints;
	/** Posição do mouse na tela. */
	private Vec2 mousePositionScreen;
	
	/**
	 * Construtor da classe.
	 * @param environment	Bounding box do mundo.
	 * @param world			Objeto que representa o mundo do jogo.
	 * @param transform		Objeto para conversões entre unidades de tela e mundo.
	 * @param state			Estado do nível do jogo.
	 * @param checkpoints	Checkpoints do jogo.
	 */
	public CheckpointEditorMode(AABB environment, World world, ViewportTransform transform, LevelState state, List<Vec2> checkpoints) {
		super(environment, world, transform);
		this.state = state;
		this.checkpoints = checkpoints;
	}

	//HERDADOS=========================================================================
	
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		this.mousePositionScreen = new Vec2(container.getInput().getMouseX(), container.getInput().getMouseY());
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) {
	
	}

	@Override
	public void leave(GameContainer container, StateBasedGame game) throws SlickException {
		
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g, Color filter) throws SlickException {
		g.drawString("CHECKPOINT", this.mousePositionScreen.x, this.mousePositionScreen.y);
		Vec2 size = new Vec2(Utils.CHECKPOINT_SIZE);
		size.x = this.transform.convertWorldScaleInPixels(size.x);
		size.y = this.transform.convertWorldScaleInPixels(size.y);
		g.drawRect(this.mousePositionScreen.x - size.x, this.mousePositionScreen.y - size.y, size.x * 2, size.y * 2);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		
	}

	@Override
	public void mouseMoved(Vec2 screenPos, Vec2 worldPos, boolean isOffLimits) {
		this.mousePositionScreen = screenPos;
		
	}

	@Override
	public void mousePressed(int button, Vec2 screenPos, Vec2 worldPos, boolean isOffLimits) {
		this.mousePositionScreen = screenPos;
		
		if (button == 0) { //Botão esquerdo.
			this.checkpoints.add(new Vec2(worldPos));
			Utils.createCheckpoint(this.world, worldPos, this.state);
		} else if (button == 1) { //Botão direito.
			//Apaga o corpo na posição do mouse, se houver.
			Body b = Utils.getBodyAtPosition(this.world, worldPos);
			
			//Verifica se o corpo é um checkpoint.
			if (b != null) {
				if (b.getShapeList().getUserData() == "checkpoint") {
					for (int i = 0; i < this.checkpoints.size(); i++) {
						if (this.checkpoints.get(i).equals(b.getWorldCenter())) {
							this.world.destroyBody(b);
							this.checkpoints.remove(i);
							break;
						}
					}
				}
			}
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
		g.drawString("Clique esquerdo: cria um checkpoint", startPosition.x, startPosition.y); startPosition.y += EditorModeHandler.HELP_LINE_HEIGHT;
		g.drawString("Clique direito: exclui um checkpointo", startPosition.x, startPosition.y); startPosition.y += EditorModeHandler.HELP_LINE_HEIGHT;
	}

	//APOIO============================================================================
	
}
