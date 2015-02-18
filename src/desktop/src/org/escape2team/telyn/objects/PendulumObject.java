package org.escape2team.telyn.objects;

import org.escape2team.telyn.core.MassData;
import org.escape2team.telyn.core.SoundPlayer;
import org.escape2team.telyn.core.Utils;
import org.escape2team.telyn.core.ViewportTransform;
import org.jbox2d.collision.shapes.CircleDef;
import org.jbox2d.collision.shapes.PolygonDef;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.RevoluteJoint;
import org.jbox2d.dynamics.joints.RevoluteJointDef;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Objeto de n�vel P�ndulo.
 */
public class PendulumObject extends LevelObject<Image, RevoluteJoint> {
	/** Tamanho da �rea de detec��o de som, em unidades do mundo. */
	private static final Vec2 SOUND_DETECTION_AREA = new Vec2(10.0f, 6.0f);
	/** Velocidade do motor da jun��o. */
	private static final float MOTOR_SPEED = 60.0f;
	/** Torque do motor da jun��o. */
	private static final float MOTOR_TORQUE = 120.0f;
	/** M�ximo �ngulo no qual o p�ndulo deve variar. */
	private static final int MAX_ANGLE = 40;
	/** Efeito sonoro do objeto. */
	private Sound sfx;
	/** Indica se o som j� foi tocado. */
	private boolean soundPlayed;
	
	/**
	 * Cria um novo objeto de n�vel.
	 * @param world		Refer�ncia ao mundo do jogo.
	 * @param transform	Objeto para convers�es entre unidades de tela e mundo.
	 * @param sprite	Sprite do objeto.
	 * @param position	Posi��o do objeto no mundo.
	 */
	public PendulumObject(World world, ViewportTransform transform, Image sprite, Vec2 position) {
		super(world, transform, sprite, position);
	}
	
	//LEVEL OBJECT====================================================================
	
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		float boxWidth = 0.4f;
		float boxHeight = 6.0f;
		Vec2 jointPos = new Vec2(this.position.x, this.position.y + boxHeight);
		
		//==Pivot============
		BodyDef pivotDef = new BodyDef();
		pivotDef.position.set(jointPos);
		Body pivot = this.world.createBody(pivotDef);
		
		CircleDef cd = new CircleDef();
		cd.radius = boxWidth * 0.5f;
		cd.density = 0;	

		pivot.createShape(cd);
		pivot.setMassFromShapes();
		
		//==P�ndulo==========
		BodyDef boxDef = new BodyDef();
		boxDef.position.set(this.position);
		Body box = this.world.createBody(boxDef);
		
		PolygonDef sd = new PolygonDef();
		sd.setAsBox(boxWidth, boxHeight);
		MassData data = new MassData(0.1f, 1.0f, 0.1f);
		data.setObjectMass(sd);
		sd.userData = data;

		box.createShape(sd);
		box.setMassFromShapes();
		box.setUserData(this);
		
		//==Revolute Joint===
		RevoluteJointDef rd = new RevoluteJointDef();
		rd.initialize(pivot, box, jointPos);
		rd.enableMotor = true;
		rd.motorSpeed = -MOTOR_SPEED;
		rd.maxMotorTorque = MOTOR_TORQUE;
		rd.enableLimit = true;
		rd.upperAngle = (float) (MAX_ANGLE * (Math.PI / 180));
		rd.lowerAngle = (float) (-MAX_ANGLE * (Math.PI / 180));
		this.objBody = (RevoluteJoint) this.world.createJoint(rd);
		this.objBody.m_body1.setAngularDamping(-0.3f);
		
		//Obt�m o objeto de som.
		this.sfx = SoundPlayer.playObjPendulumImpulse();
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		
	}

	@Override
	public void leave(GameContainer container, StateBasedGame game) throws SlickException {
		
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g, Color filter) throws SlickException {
		Utils.drawImage(this.sprite, this.objBody.m_body2.getWorldCenter(), this.objBody.m_body2.getAngle(), this.transform, g, filter);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		//Obt�m a fator que representa a porcentagem do objeto vis�vel na c�mera.
		float factor = this.transform.isInViewport(this.objBody.m_body2.getWorldCenter(), SOUND_DETECTION_AREA.x, SOUND_DETECTION_AREA.y, new Vec2(1.0f, 1.0f));
		
		if (this.objBody.m_enableMotor && factor > 0) {
			if (this.objBody.getJointAngle() >= 30 * (Math.PI / 180)) {
				this.objBody.setMotorSpeed(-MOTOR_SPEED);
				this.soundPlayed = false;
			} else if (this.objBody.getJointAngle() <= 0) {
				this.objBody.setMotorSpeed(MOTOR_SPEED);
				if (!this.sfx.playing() && !this.soundPlayed) {
					this.sfx.play(1.0f, factor);
					this.soundPlayed = true;
				}
			}
		}
	}
	
	@Override
	public boolean causesPlayerToDye() {
		return true;
	}

	@Override
	public void destroy() {
		this.world.destroyBody(this.objBody.m_body1);
		this.world.destroyBody(this.objBody.m_body2);
	}

	@Override
	public void sleep() {
		if (this.sfx.playing()) this.sfx.stop();
		this.objBody.m_enableMotor = false;
		this.sleepBody(this.objBody.m_body1);
		this.sleepBody(this.objBody.m_body2);
	}

	@Override
	public void wakeUp() {
		this.objBody.m_enableMotor = true;
		this.wakeUpBody(this.objBody.m_body1);
		this.wakeUpBody(this.objBody.m_body2);
	}
}
