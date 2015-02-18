package org.escape2team.telyn.objects;

import org.escape2team.telyn.core.GameObject;
import org.escape2team.telyn.core.MassData;
import org.escape2team.telyn.core.ViewportTransform;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;

/**
 * Representa um objeto de n�vel de jogo.
 * @param <S> Tipo do sprite.
 * @param <B> Tipo que representa o corpo do objeto no mundo.
 */
public abstract class LevelObject<S, B> implements GameObject {
	/** Refer�ncia ao mundo do jogo. */
	protected World world;
	/** Objeto para convers�es entre unidades de tela e mundo. */
	protected ViewportTransform transform;
	/** Sprite do objeto. */
	protected S sprite;
	/** Objeto no mundo. */
	protected B objBody;
	/** Posi��o do objeto no mundo. */
	protected Vec2 position;
	/** Indica se o disparador de evento do objeto j� foi ativado. */
	protected boolean triggered;
	
	/**
	 * Cria um novo objeto de n�vel.
	 * @param world		Refer�ncia ao mundo do jogo.
	 * @param transform	Objeto para convers�es entre unidades de tela e mundo.
	 * @param world		Refer�ncia ao mundo do jogo.
	 * @param position	Posi��o do objeto no mundo.
	 */
	public LevelObject(World world, ViewportTransform transform, S sprite, Vec2 position) {
		this.world = world;
		this.transform = transform;
		this.sprite = sprite;
		this.position = position;
		this.triggered = false;
	}
	
	/**
	 * Colocar um corpo para dormir.
	 * @param b Corpo a ser colocado para dormir.
	 */
	protected void sleepBody(Body b) {
		for (Shape s = b.getShapeList(); s != null; s = s.m_next) {
			if (s.m_userData instanceof MassData) {
				MassData.resetShapeMass(s);
			}
		}
		b.setMassFromShapes();
		b.putToSleep();
	}
	
	/**
	 * Acorda um corpo.
	 * @param b Corpo a ser acordado.
	 */
	protected void wakeUpBody(Body b) {
		for (Shape s = b.getShapeList(); s != null; s = s.m_next) {
			if (s.m_userData instanceof MassData) {
				((MassData)s.m_userData).setShapeMass(s);
			}
		}
		b.setMassFromShapes();
		b.wakeUp();
	}
	
	/**
	 * Quando implementado na classe, cria um disparador de evento (trigger).
	 * @param position 	Posi��o do trigger.
	 */
	public void createTrigger(Vec2 position) { }
	
	/**
	 * Quando implementado na classe, indica que o um disparador de evento (trigger) do objeto foi ativado.
	 * @param position 	Posi��o do trigger.
	 * @param contacted	Corpo o qual est� em contato com o trigger.
	 */
	public void triggerActivated(Vec2 position, Body contacted) { }
	
	/**
	 * Obt�m o tamanho do ret�ngulo do disparador de evento (trigger).
	 * @return Tamanho do trigger.
	 */
	public Vec2 getTriggerSize() {
		return new Vec2(0, 0);
	}
	
	/**
	 * Indica se o disparador de evento (trigger) j� foi ativado.
	 * @return Valor booleano indicando se o trigger j� foi ativado.
	 */
	public boolean isTriggered() {
		return this.triggered;
	}
	
	/**
	 * Indica se a colis�o com o objeto causa a morte do jogador.
	 * @return Valor booleano indicando se a colis�o do objeto causa a morte do jogador.
	 */
	public abstract boolean causesPlayerToDye();
	
	/**
	 * Indica se a for�a m�nima necess�ria para causar morte do jogador.
	 * @return For�a m�nima necess�ria para causar morte do jogador.
	 */
	public float minForceToCauseDeath() {
		/* Por padr�o, qualquer for�a pode causar a morte do 
		 * jogador se o objeto assim estiver configurado.*/
		return 0;
	}
	
	/**
	 * Destr�i o objeto.
	 */
	public abstract void destroy();
	
	/**
	 * Coloca o objeto para dormir.
	 */
	public abstract void sleep();
	
	/**
	 * Acorda o objeto.
	 */
	public abstract void wakeUp();
}
