package org.escape2team.telyn.core;

import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.collision.shapes.ShapeDef;

/**
 * Dados de massa de um objeto. 
 * É utilizado para assegurar que, ao retorno do movimento dos objetos, estes tenham as mesmas propriedades.
 */
public class MassData {
	/** Densidade do objeto. */
	public float density;
	/** Atrito do objeto. */
	public float friction;
	/** Elasticidade do objeto. */
	public float restitution;
	
	/**
	 * Construtor da classe.
	 * @param density		Densidade do objeto.
	 * @param friction		Atrito do objeto.
	 * @param restitution	Elasticidade do objeto.
	 */
	public MassData(float density, float friction, float restitution) {
		this.density = density;
		this.friction = friction;
		this.restitution = restitution;
	}
	
	/**
	 * Define as propriedades de massa de um objeto.
	 * @param def Definições do objeto.
	 */
	public void setObjectMass(ShapeDef def) {
		def.density = this.density;
		def.friction = this.friction;
		def.restitution = this.restitution;		
	}
	
	/**
	 * Define as propriedades de massa de um objeto em um objeto shape.
	 * @param shape Objeto shape.
	 */
	public void setShapeMass(Shape shape) {
		shape.m_density = this.density;
		shape.m_friction = this.friction;
		shape.m_restitution = this.restitution;	
	}
	
	/**
	 * Reseta as propriedades de massa de um shape.
	 * @param shape Objeto shape.
	 */
	public static void resetShapeMass(Shape shape) {
		shape.m_density = 0.0f;
		shape.m_restitution = 0.0f;
	}
	
	/**
	 * Verifica se as propriedades de massa de um shape estão resetadas.
	 * @param shape Objeto shape.
	 */
	public static boolean isReseted(Shape shape) {
		return (shape.m_density == 0.0f && shape.m_restitution == 0.0f);
	}
}
