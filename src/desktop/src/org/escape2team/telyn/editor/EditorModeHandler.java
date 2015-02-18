package org.escape2team.telyn.editor;

import org.escape2team.telyn.core.GameObject;
import org.escape2team.telyn.core.ViewportTransform;
import org.jbox2d.collision.AABB;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Representa um modo do editor de níveis.
 */
public abstract class EditorModeHandler implements GameObject {
	/** Tamanho da linha do help. */
	public static final int HELP_LINE_HEIGHT = 20;
	/** Bounding box do mundo (limites do mundo). O que estiver dentro é regido pelo Box2D. */
	protected AABB environment;
	/** Objeto que representa o mundo do jogo. */
	protected World world;
	/** Objeto para conversões entre unidades de tela e mundo. */
	protected ViewportTransform transform;
	
	/**
	 * Construtor da classe.
	 * @param environment	Bounding box do mundo.
	 * @param world			Objeto que representa o mundo do jogo.
	 * @param transform		Objeto para conversões entre unidades de tela e mundo.
	 */
	public EditorModeHandler(AABB environment, World world, ViewportTransform transform) {
		this.environment = environment;
		this.world = world;
		this.transform = transform;
	}
	
	public abstract void mouseMoved(Vec2 screenPos, Vec2 worldPos, boolean isOffLimits);
	
	public abstract void mousePressed(int button, Vec2 screenPos, Vec2 worldPos, boolean isOffLimits);
	
	public abstract void mouseReleased(int button, Vec2 screenPos, Vec2 worldPos, boolean isOffLimits);

	public abstract void mouseWheelMoved(int size);
	
	public abstract void renderHelp(GameContainer container, StateBasedGame game, Graphics g, Vec2 startPosition);
}
