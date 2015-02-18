package org.escape2team.telyn.core;

import org.newdawn.slick.Color;

/** 
 * Modo de jogo, de acordo com as esta��es do ano.
 */
public enum Seasons {
	  Spring(0)
	, Summer(1)
	, Autumn(2)
	, Winter(3);
	  
	/** Cores das esta��es, na ordem: 0: Spring; 1: Summer; 2: Autumn; 3: Winter. */
	private static final Color[] SEASON_COLOR_FILTERS = { new Color(0.6f, 1.0f, 0.6f, 1.0f)
														, new Color(1.0f, 1.0f, 0.0f, 1.0f)
														, new Color(1.0f, 0.7f, 0.3f, 1.0f)
														, new Color(0.5f, 0.7f, 1.0f, 1.0f) };
	/** ID da esta��o do ano. */
	private int id;
	  
	/**
	 * Construtor da enumera��o.
	 * @param id ID da esta��o.
	 */
	Seasons(int id) {
		this.id = id;
	}
	
	/**
	 * Obt�m o ID da esta��o.
	 * @return ID da esta��o.
	 */
	public int getId() {
		return this.id;
	}
	
	/**
	 * Obt�m a cor da esta��o. Sempre retorna uma nova inst�ncia.
	 * @return Cor da esta��o.
	 */
	public Color getColor() {
		return new Color(SEASON_COLOR_FILTERS[this.id]);
	}
	
	/**
	 * Obt�m uma esta��o atrav�s de seu ID.
	 * @param id ID da esta��o.
	 * @return Esta��o.
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