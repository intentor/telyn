package org.escape2team.telyn.core;

/** 
 * Tipos de objetos móveis do mundo do jogo.
 */
public enum ObjectType {
	  Orb(0)
	, Rock(1)
	, BigRock(2)
	, StemTree(3)
	, Pendulum(4)
	, Rotator(5);		
	  
	/** ID do objeto. */
	private int id;
	  
	/**
	 * Construtor da enumeração.
	 * @param id ID do objeto.
	 */
	ObjectType(int id) {
		this.id = id;
	}
	
	/**
	 * Obtém o ID do objeto.
	 * @return ID do objeto.
	 */
	public int getId() {
		return this.id;
	}
	
	/**
	 * Obtém um tipo de objeto através de seu ID.
	 * @param id ID do tipo de objeto.
	 * @return Tipo de objeto.
	 */
	public static ObjectType getFromId(int id) {
		ObjectType mode;
		
		switch(id) {
			case 0:
			default:
				mode = ObjectType.Orb;
				break;
			case 1:
				mode = ObjectType.Rock;
				break;
			case 2:
				mode = ObjectType.BigRock;
				break;
			case 3:
				mode = ObjectType.StemTree;
				break;
			case 4:
				mode = ObjectType.Pendulum;
				break;
			case 5:
				mode = ObjectType.Rotator;
				break;
		}
		
		return mode;
	}
}	