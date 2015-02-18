package org.escape2team.telyn.objects;

import org.escape2team.telyn.configuration.LevelConfiguration;
import org.escape2team.telyn.core.MassData;
import org.escape2team.telyn.core.Utils;
import org.escape2team.telyn.core.ViewportTransform;
import org.jbox2d.collision.shapes.CircleDef;
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
 * Objeto de nível Grande Rocha.
 */
public class CheckpointObject extends LevelObject<Image, Body> {
	/** Tamanho do trigger. */
	private static final Vec2 TRIGGER_SIZE = new Vec2(0.5f, 8.0f);
	/** Sensor do trigger. */
	private Body sensor;
	
	/**
	 * Cria um novo objeto de nível.
	 * @param world		Referência ao mundo do jogo.
	 * @param transform	Objeto para conversões entre unidades de tela e mundo.
	 * @param sprite	Sprite do objeto.
	 * @param position	Posição do objeto no mundo.
	 */
	public CheckpointObject(World world, ViewportTransform transform, Image sprite, Vec2 position) {
		super(world, transform, sprite, position);
	}
	
	//LEVEL OBJECT====================================================================
	
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		BodyDef def = new BodyDef();
		def.position.set(this.position);
		this.objBody = this.world.createBody(def);
		
		CircleDef cd = new CircleDef();
		cd.radius = 1.7f;
		MassData data = new MassData(20.0f, LevelConfiguration.GROUND_FRICTION, 0.05f);
		data.setObjectMass(cd);
		cd.userData = data;
		
		this.objBody.createShape(cd);
		this.objBody.setMassFromShapes();
		this.objBody.setUserData(this);
		
		//Coloca o objeto para dormir até que seu trigger seja ativado.
		this.sleepBody(this.objBody);
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
		if (this.triggered && this.sensor != null) {
			//Destrói a referência ao sensor.
			this.sensor = null;
			
			//Acorda o objeto.
			this.wakeUp();
		}
	}
	
	@Override
	public void createTrigger(Vec2 position) {
		if (this.sensor == null) {
			BodyDef def = new BodyDef();
			def.position.set(position);
			this.sensor = this.world.createBody(def);
			
			PolygonDef pd = new PolygonDef();
			pd.isSensor = true; //Todo trigger é um sensor.
			pd.userData = "trigger"; //Todo trigger contém a string "trigger" como userdata de sua definição.
			pd.setAsBox(TRIGGER_SIZE.x, TRIGGER_SIZE.y);
			
			this.sensor.createShape(pd);
			this.sensor.setMassFromShapes();
			this.sensor.setUserData(this); //Todo trigger contém seu objeto de nível como userdata de seu corpo.
		}
	}
	
	@Override
	public void triggerActivated(Vec2 position, Body contacted) {
		if (!this.triggered) this.triggered = true;
	}
	
	@Override
	public Vec2 getTriggerSize() {
		return new Vec2(TRIGGER_SIZE.x * 2, TRIGGER_SIZE.y * 2);
	}
	
	@Override
	public boolean causesPlayerToDye() {
		return true;
	}
	
	public float minForceToCauseDeath() {
		return 10;
	}

	@Override
	public void destroy() {
		this.world.destroyBody(this.objBody);
		if (this.sensor != null) this.world.destroyBody(this.sensor);
	}

	@Override
	public void sleep() {
		//Somente permite a desativação do objeto se estiver disparado e poder causar morte.
		if (this.triggered) this.sleepBody(this.objBody);
	}

	@Override
	public void wakeUp() {
		//Somente permite a ativação do objeto se estiver disparado e poder causar morte.
		if (this.triggered) this.wakeUpBody(this.objBody);
	}	
}
