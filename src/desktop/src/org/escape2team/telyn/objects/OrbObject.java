package org.escape2team.telyn.objects;

import org.escape2team.telyn.core.SoundPlayer;
import org.escape2team.telyn.core.Utils;
import org.escape2team.telyn.core.ViewportTransform;
import org.jbox2d.collision.shapes.PolygonDef;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
/**
 * Objeto de nível Orb do Poder.
 */
public class OrbObject extends LevelObject<Image[], Body> {
	/** Animação da orb. */
	private Animation anim;
	
	/**
	 * Cria um novo objeto de nível.
	 * @param world		Referência ao mundo do jogo.
	 * @param transform	Objeto para conversões entre unidades de tela e mundo.
	 * @param sprite	Sprite do objeto.
	 * @param position	Posição do objeto no mundo.
	 */
	public OrbObject(World world, ViewportTransform transform, Image[] sprite, Vec2 position) {
		super(world, transform, sprite, position);
	}
	
	//LEVEL OBJECT====================================================================
	
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		this.anim = new Animation(this.sprite, 75);
		this.anim.setCurrentFrame(Utils.getRandomNumber(13));
		this.anim.start();
		
		BodyDef def = new BodyDef();
		def.position.set(this.position);
		this.objBody = this.world.createBody(def);

		PolygonDef sd = new PolygonDef();
		sd.setAsBox(0.3f, 0.3f);
		sd.userData = "orb"; //Indica que o objeto é uma orb.
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
		Utils.drawImage(this.anim.getCurrentFrame(), this.objBody.getWorldCenter(), this.objBody.getAngle(), this.transform, g, filter);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		this.anim.update(delta);
	}
	
	@Override
	public boolean causesPlayerToDye() {
		return false;
	}

	@Override
	public void destroy() {
		//Toca o som de pegando a orb.
		SoundPlayer.playActCatchingOrb();
		this.world.destroyBody(this.objBody);
	}

	@Override
	public void sleep() {
		//A animação da orb não pára, independente da estação na qual está.
	}

	@Override
	public void wakeUp() {
		//A animação da orb não pára, independente da estação na qual está.
	}
}
