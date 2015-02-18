package org.escape2team.telyn.objects;

import org.escape2team.telyn.configuration.LevelConfiguration;
import org.escape2team.telyn.core.MassData;
import org.escape2team.telyn.core.Utils;
import org.escape2team.telyn.core.ViewportTransform;
import org.jbox2d.collision.shapes.PolygonDef;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Objeto de nível Rocha.
 */
public class RockObject extends LevelObject<Image, Body> {	
	/**
	 * Cria um novo objeto de nível.
	 * @param world		Referência ao mundo do jogo.
	 * @param transform	Objeto para conversões entre unidades de tela e mundo.
	 * @param sprite	Sprite do objeto.
	 * @param position	Posição do objeto no mundo.
	 */
	public RockObject(World world, ViewportTransform transform, Image sprite, Vec2 position) {
		super(world, transform, sprite, position);
	}
	
	//LEVEL OBJECT====================================================================
	
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		BodyDef def = new BodyDef();
		def.position.set(this.position);
		this.objBody = this.world.createBody(def);

		PolygonDef sd = new PolygonDef();
		sd.setAsBox(0.9f, 0.9f);
		MassData data = new MassData(1.5f, LevelConfiguration.GROUND_FRICTION, 0);
		data.setObjectMass(sd);
		sd.userData = data;
		
		this.objBody.createShape(sd);
		this.objBody.setMassFromShapes();
		this.objBody.setUserData(this);
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		
	}

	@Override
	public void leave(GameContainer container, StateBasedGame game) throws SlickException {
		
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g, Color filter) throws SlickException {
		Utils.drawImage(this.sprite, this.objBody.getWorldCenter(), this.objBody.getAngle(), this.transform, g, filter);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		
	}
	
	@Override
	public boolean causesPlayerToDye() {
		return false;
	}

	@Override
	public void destroy() {
		this.world.destroyBody(this.objBody);
	}

	@Override
	public void sleep() {
		this.sleepBody(this.objBody);
	}

	@Override
	public void wakeUp() {
		this.wakeUpBody(this.objBody);
	}
}
