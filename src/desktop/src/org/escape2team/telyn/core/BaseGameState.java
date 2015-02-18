package org.escape2team.telyn.core;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Classe base para definição de cenas.
 */
public abstract class BaseGameState extends BasicGameState {
	/** ID do estado atual. */
	private int stateID;
	
    /**
     * Inicia um estado de jogo.
     * @param stateID ID do estado de jogo.
     */
	public BaseGameState(int stateID) {
		this.stateID = stateID;		
	}
	
	@Override
	public int getID() {
		return this.stateID;
	}
	
	/**
	 * Initialise the state. It should load any resources it needs at this stage
	 * 
	 * @param container The container holding the game
	 * @param game The game holding this state
	 * @throws SlickException Indicates a failure to initialise a resource for this state
	 */
	public abstract void init(GameContainer container, StateBasedGame game) throws SlickException;

	/**
	 * Update the state's logic based on the amount of time thats passed
	 * 
	 * @param container The container holding the game
	 * @param game The game holding this state
	 * @param delta The amount of time thats passed in millisecond since last update
	 * @throws SlickException Indicates an internal error that will be reported through the
	 * standard framework mechanism
	 */
	public abstract void update(GameContainer container, StateBasedGame game, int delta) throws SlickException;
		
	/**
	 * Render this state to the game's graphics context
	 * 
	 * @param container The container holding the game
	 * @param game The game holding this state
	 * @param g The graphics context to render to
	 * @throws SlickException Indicates a failure to render an artifact
	 */
	public abstract void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException;

	/**
	 * Notification that we've entered this game state
	 * 
	 * @param container The container holding the game
	 * @param game The game holding this state
	 * @throws SlickException Indicates an internal error that will be reported through the
	 * standard framework mechanism
	 */
	public abstract void enter(GameContainer container, StateBasedGame game) throws SlickException;

	/**
	 * Notification that we're leaving this game state
	 * 
	 * @param container The container holding the game
	 * @param game The game holding this state
	 * @throws SlickException Indicates an internal error that will be reported through the
	 * standard framework mechanism
	 */
	public abstract void leave(GameContainer container, StateBasedGame game) throws SlickException;	
}
