package org.escape2team.telyn.editor;

import java.util.Locale;

import org.escape2team.telyn.core.Player;
import org.escape2team.telyn.core.ViewportTransform;
import org.jbox2d.collision.AABB;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Modo de edição de posicionamento do personagem.
 */
public class CharacterPositionEditorMode extends EditorModeHandler {
	/** Objeto que representa o jogador. */
	private Player player;
	/** Indica se o jogador já foi liberado da movimentação. */
	private boolean isReleased;
	/** Posição inicial do jogador. */
	private Vec2 characterPosition;
	
	/**
	 * Construtor da classe.
	 * @param environment		Bounding box do mundo.
	 * @param world				Objeto que representa o mundo do jogo.
	 * @param transform			Objeto para conversões entre unidades de tela e mundo.
	 * @param player			Objeto que representa o jogador.
	 * @param characterPosition	Posição inicial do jogador.
	 */
	public CharacterPositionEditorMode(AABB environment, World world, ViewportTransform transform, Player player, Vec2 characterPosition) {
		super(environment, world, transform);
		this.player = player;
		this.characterPosition = characterPosition;
	}

	//HERDADOS=========================================================================
	
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		this.isReleased = false;
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) {
	
	}

	@Override
	public void leave(GameContainer container, StateBasedGame game) throws SlickException {
		
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g, Color filter) throws SlickException {
		String currentPosition = String.format(Locale.US, "POSITION: (%.2f,%.2f)", this.characterPosition.x, this.characterPosition.y);
		g.drawString(currentPosition, 792 - g.getFont().getWidth(currentPosition), 45);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		Input kb = container.getInput();
		if (kb.isKeyDown(Input.KEY_P)) this.characterPosition.set(this.player.getCurrentWorldCenterPosition());
	}

	@Override
	public void mouseMoved(Vec2 screenPos, Vec2 worldPos, boolean isOffLimits) {
		if (!this.isReleased) {
			this.player.putToSleep();		
			if (!isOffLimits) {
				Vec2 pos = new Vec2(worldPos);
				this.player.jumpStartY = worldPos.y;
				this.player.setPlayerPosition(pos);
			}
		}
	}

	@Override
	public void mousePressed(int button, Vec2 screenPos, Vec2 worldPos, boolean isOffLimits) {
		this.isReleased = true;
		this.player.jumpStartY = worldPos.y;
		this.player.wakeUp();
	}

	@Override
	public void mouseReleased(int button, Vec2 screenPos, Vec2 worldPos, boolean isOffLimits) {
		
	}
	
	@Override
	public void mouseWheelMoved(int size) {
		
	}

	@Override
	public void renderHelp(GameContainer container, StateBasedGame game, Graphics g, Vec2 startPosition) {
		g.drawString("P: define a posição inicial do jogador", startPosition.x, startPosition.y);		
	}
	
	//APOIO============================================================================

	
}
