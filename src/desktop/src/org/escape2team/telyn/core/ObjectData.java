package org.escape2team.telyn.core;

import org.escape2team.telyn.objects.LevelObject;
import org.jbox2d.common.Vec2;

/**
 * Dados de um objeto no universo do jogo.
 */
public class ObjectData {
	/** Tipo do objeto. */
	public ObjectType type;
	/** Posição do objeto, em unidades do mundo. */
	public Vec2 position;
	/** Posição na qual o jogador deve passar para acordar o objeto. */
	public Vec2 triggerPosition;
	/** Referência ao objeto no mundo. */
	@SuppressWarnings("rawtypes")
	public LevelObject object;
	
	/**
	 * Construtor da classe.
	 * @param type				Tipo do objeto.
	 * @param position			Posição do objeto, em unidades do mundo.
	 * @param triggerPosition	Posição na qual o jogador deve passar para acordar o objeto.
	 */
	public ObjectData(ObjectType type, Vec2 position, Vec2 triggerPosition) {
		this.type = type;
		this.position = position;
		this.triggerPosition = triggerPosition;
	}
}
