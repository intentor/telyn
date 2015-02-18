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
 * Objeto de nível Galho de Árvore.
 */
public class StemTreeObject extends LevelObject<Image, Body> {
	/** Última posição do centro do mundo. */
	private Vec2 lastWorldCenter;
	
	/**
	 * Cria um novo objeto de nível.
	 * @param world		Referência ao mundo do jogo.
	 * @param transform	Objeto para conversões entre unidades de tela e mundo.
	 * @param sprite	Sprite do objeto.
	 * @param position	Posição do objeto no mundo.
	 */
	public StemTreeObject(World world, ViewportTransform transform, Image sprite, Vec2 position) {
		super(world, transform, sprite, position);
	}
	
	//LEVEL OBJECT====================================================================
	
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		//Vértices do objeto.
		Vec2[] vertex = new Vec2[] {
			  new Vec2(-8.891340f, 1.263923f)
			, new Vec2(-8.477758f, 0.914517f)
			, new Vec2(-8.270967f, -0.212137f)
			, new Vec2(-7.315451f, -0.732680f)
			, new Vec2(-5.072156f, -0.755771f)
			, new Vec2(-3.916979f, -0.955431f)
			, new Vec2(-3.225299f, -0.705856f)
			, new Vec2(-2.207641f, -1.069523f)
			, new Vec2(1.928693f, -1.048130f)
			, new Vec2(5.013412f, -0.762901f)
			, new Vec2(7.842275f, 0.492106f)
			, new Vec2(8.113242f, 0.506367f)
			, new Vec2(4.989985f, -0.905516f)
			, new Vec2(1.830561f, -1.205006f)
			, new Vec2(0.519693f, -1.319098f)
			, new Vec2(-2.403903f, -1.283444f)
			, new Vec2(-3.131237f, -0.934039f)
			, new Vec2(-3.856536f, -1.290575f)
			, new Vec2(-7.107810f, -1.197876f)
			, new Vec2(-8.084719f, -0.919777f)
			, new Vec2(-7.849405f, -0.677333f)
			, new Vec2(-8.262987f, -0.228097f) };
					
		BodyDef def = new BodyDef();
		def.position.set(this.position);
		this.objBody = this.world.createBody(def);
		MassData data = new MassData(1.5f, LevelConfiguration.GROUND_FRICTION, 0);
		
		for (int i = 0; i < vertex.length - 1; ++i) {
			PolygonDef sd = new PolygonDef();
			data.setObjectMass(sd);
			sd.userData = data;
			
			Utils.createStrokeRect(vertex[i], vertex[i + 1], 0.03f, this.objBody, sd);
		}
			
		this.objBody.setMassFromShapes();
		this.objBody.setUserData(this);
		
		this.lastWorldCenter = new Vec2();
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		
	}

	@Override
	public void leave(GameContainer container, StateBasedGame game) throws SlickException {
		
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g, Color filter) throws SlickException {
		Vec2 pos = this.objBody.getWorldCenter();
		
		/* Para poder corrigir problemas de posionamento com a imagem que compõem o 
		 * objeto causados pelo JBox2D, a imagem que representa o objeto é reposicionada.
		 * Os valores foram obtidos de forma empírica.*/
		if (this.objBody.isStatic()) {
			//Sendo estático, reposiciona o objeto com o valor da última posição.
			pos.set(this.lastWorldCenter);
		} else {
			pos.x += 0.58f;
			pos.y += 0.85f;
			
			//Não sendo estático, armazena a última posição do objeto no mundo.
			this.lastWorldCenter.set(pos);
		}
		
		Utils.drawImage(this.sprite, pos, this.objBody.getAngle(), this.transform, g, filter);
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
