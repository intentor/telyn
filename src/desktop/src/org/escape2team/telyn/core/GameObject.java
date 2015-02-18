package org.escape2team.telyn.core;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Representa um objeto de jogo.
 */
public interface GameObject {
	void init(GameContainer container, StateBasedGame game) throws SlickException;
	
	void enter(GameContainer container, StateBasedGame game) throws SlickException;

	void leave(GameContainer container, StateBasedGame game) throws SlickException;

	void render(GameContainer container, StateBasedGame game, Graphics g, Color filter) throws SlickException;

	void update(GameContainer container, StateBasedGame game, int delta) throws SlickException;
}
