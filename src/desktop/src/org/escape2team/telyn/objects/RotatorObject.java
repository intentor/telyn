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
 * Objeto de nível Rotacionador.
 */
public class RotatorObject extends LevelObject<Image, RevoluteJoint> {
	/** Espaços adicionais para detecção de som, em porcentagem do tamanho original. */
	private static final Vec2 SOUND_DETECTION_OFFSET = new Vec2(1.5f, 1.2f);
	/** Máxima velocidade do motor da junção. */
	private static final int MAX_MOTOR_SPEED = 12;
	/** Mínima velocidade do motor da junção. */
	private static final int MIN_MOTOR_SPEED = 6;
	/** Torque do motor da junção. */
	private static final float MOTOR_TORQUE = 20.0f;
	/** Raio do objeto. */
	private static final float RADIUS = 2.3f;
	/** Velocidade do motor. */
	private float motorSpeed;
	/** Efeito sonoro do objeto. */
	private Sound sfx;
	/** Tonalidade do som, baseada na rotação. */
	private float pitch;
	
	/**
	 * Cria um novo objeto de nível.
	 * @param world		Referência ao mundo do jogo.
	 * @param transform	Objeto para conversões entre unidades de tela e mundo.
	 * @param sprite	Sprite do objeto.
	 * @param position	Posição do objeto no mundo.
	 */
	public RotatorObject(World world, ViewportTransform transform, Image sprite, Vec2 position) {
		super(world, transform, sprite, position);
	}
	
	//LEVEL OBJECT====================================================================
	
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		this.motorSpeed = Utils.getRandomNumber(MAX_MOTOR_SPEED);
		if (this.motorSpeed < MIN_MOTOR_SPEED) this.motorSpeed = MIN_MOTOR_SPEED;		
		
		//==Pivot============
		BodyDef pivotDef = new BodyDef();
		pivotDef.position.set(this.position);
		Body pivot = this.world.createBody(pivotDef);
		
		CircleDef cd = new CircleDef();
		cd.radius = RADIUS * 0.1f;
		cd.density = 0;	

		pivot.createShape(cd);
		pivot.setMassFromShapes();
		
		//==Rotator==========
		
		BodyDef rotDef = new BodyDef();
		rotDef.position.set(this.position);
		Body rotator = this.world.createBody(rotDef);
		
		//Massa do objeto.
		MassData data = new MassData(0.1f, 1.0f, 0.1f);
		
		//Círculo da área que causa morte.
		CircleDef crd = new CircleDef();
		crd.radius = RADIUS;
		data.setObjectMass(crd);
		crd.userData = data;
		rotator.createShape(crd);
		
		//Retângulo da área que se pode parar sem morrer.
		PolygonDef sd = new PolygonDef();
		sd.setAsBox(1.0f, 0.15f, new Vec2(0, 2.37f), 0);
		sd.userData = false; //Indica que não causa morte.
		rotator.createShape(sd);
		
		rotator.setMassFromShapes();
		rotator.setUserData(this);
		
		//==Revolute Joint===
		RevoluteJointDef rd = new RevoluteJointDef();
		rd.initialize(pivot, rotator, this.position);
		rd.enableMotor = true;
		rd.motorSpeed = (Utils.getRandomNumber(2) == 1 ? 1 : -1) * this.motorSpeed;
		rd.maxMotorTorque = MOTOR_TORQUE;
		this.objBody = (RevoluteJoint) this.world.createJoint(rd);
		
		//Define a tonalidade do som.
		this.pitch = rd.motorSpeed / MAX_MOTOR_SPEED;

		//Obtém o objeto de som.
		this.sfx = SoundPlayer.playObjRotatorMotor();
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
		//Obtém a fator que representa a porcentagem do objeto visível na câmera.
		float factor = this.transform.isInViewport(this.objBody.m_body2.getWorldCenter(), RADIUS, RADIUS, SOUND_DETECTION_OFFSET);

		if (factor > 0) {
			//Caso não esteja tocando, inicia-a. Se o motor parou, pára o som.
			if (!this.sfx.playing() && this.objBody.m_enableMotor) this.sfx.play(this.pitch, factor);
			else if (this.sfx.playing() && !this.objBody.m_enableMotor) this.sfx.stop();
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
