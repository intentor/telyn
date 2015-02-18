package org.escape2team.telyn.objects;

import org.escape2team.telyn.core.GameObject;
import org.escape2team.telyn.core.MassData;
import org.escape2team.telyn.core.ViewportTransform;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;

/**
 * Representa um objeto de nível de jogo.
 * @param <S> Tipo do sprite.
 * @param <B> Tipo que representa o corpo do objeto no mundo.
 */
public abstract class LevelObject<S, B> implements GameObject {
	/** Referência ao mundo do jogo. */
	protected World world;
	/** Objeto para conversões entre unidades de tela e mundo. */
	protected ViewportTransform transform;
	/** Sprite do objeto. */
	protected S sprite;
	/** Objeto no mundo. */
	protected B objBody;
	/** Posição do objeto no mundo. */
	protected Vec2 position;
	/** Indica se o disparador de evento do objeto já foi ativado. */
	protected boolean triggered;
	
	/**
	 * Cria um novo objeto de nível.
	 * @param world		Referência ao mundo do jogo.
	 * @param transform	Objeto para conversões entre unidades de tela e mundo.
	 * @param world		Referência ao mundo do jogo.
	 * @param position	Posição do objeto no mundo.
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
	 * @param position 	Posição do trigger.
	 */
	public void createTrigger(Vec2 position) { }
	
	/**
	 * Quando implementado na classe, indica que o um disparador de evento (trigger) do objeto foi ativado.
	 * @param position 	Posição do trigger.
	 * @param contacted	Corpo o qual está em contato com o trigger.
	 */
	public void triggerActivated(Vec2 position, Body contacted) { }
	
	/**
	 * Obtém o tamanho do retângulo do disparador de evento (trigger).
	 * @return Tamanho do trigger.
	 */
	public Vec2 getTriggerSize() {
		return new Vec2(0, 0);
	}
	
	/**
	 * Indica se o disparador de evento (trigger) já foi ativado.
	 * @return Valor booleano indicando se o trigger já foi ativado.
	 */
	public boolean isTriggered() {
		return this.triggered;
	}
	
	/**
	 * Indica se a colisão com o objeto causa a morte do jogador.
	 * @return Valor booleano indicando se a colisão do objeto causa a morte do jogador.
	 */
	public abstract boolean causesPlayerToDye();
	
	/**
	 * Indica se a força mínima necessária para causar morte do jogador.
	 * @return Força mínima necessária para causar morte do jogador.
	 */
	public float minForceToCauseDeath() {
		/* Por padrão, qualquer força pode causar a morte do 
		 * jogador se o objeto assim estiver configurado.*/
		return 0;
	}
	
	/**
	 * Destrói o objeto.
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
