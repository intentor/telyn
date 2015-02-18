package org.escape2team.telyn.core;

import org.newdawn.slick.Color;

/** 
 * Modo de jogo, de acordo com as estações do ano.
 */
public enum Seasons {
	  Spring(0)
	, Summer(1)
	, Autumn(2)
	, Winter(3);
	  
	/** Cores das estações, na ordem: 0: Spring; 1: Summer; 2: Autumn; 3: Winter. */
	private static final Color[] SEASON_COLOR_FILTERS = { new Color(0.6f, 1.0f, 0.6f, 1.0f)
														, new Color(1.0f, 1.0f, 0.0f, 1.0f)
														, new Color(1.0f, 0.7f, 0.3f, 1.0f)
														, new Color(0.5f, 0.7f, 1.0f, 1.0f) };
	/** ID da estação do ano. */
	private int id;
	  
	/**
	 * Construtor da enumeração.
	 * @param id ID da estação.
	 */
	Seasons(int id) {
		this.id = id;
	}
	
	/**
	 * Obtém o ID da estação.
	 * @return ID da estação.
	 */
	public int getId() {
		return this.id;
	}
	
	/**
	 * Obtém a cor da estação. Sempre retorna uma nova instância.
	 * @return Cor da estação.
	 */
	public Color getColor() {
		return new Color(SEASON_COLOR_FILTERS[this.id]);
	}
	
	/**
	 * Obtém uma estação através de seu ID.
	 * @param id ID da estação.
	 * @return Estação.
	 */
	public static Seasons getFromId(int id) {
		Seasons mode;
		
		switch(id) {
			case 1:
			default:
				mode = Seasons.Spring;
				break;
			case 2:
				mode = Seasons.Summer;
				break;
			case 3:
				mode = Seasons.Autumn;
				break;
			case 4:
				mode = Seasons.Winter;
				break;
		}
		
		return mode;
	}
}	