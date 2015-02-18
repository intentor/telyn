package org.escape2team.telyn.core;

import java.util.LinkedList;
import java.util.List;

import org.jbox2d.common.Vec2;

/**
 * Representa uma posi��o de tiles no mapa de tiles.
 */
public class TileMapItem {
	/** Tiles j� renderizados no ciclo de renderiza��o. � reiniciado a cada ciclo. */
	public static List<TileData> rendered;
	/** Posi��o no mapa de tiles. */
	public Vec2 position;
	/** Informa��es dos tiles presentes na posi��o. */
	public List<TileData> tiles;
	
	/**
	 * Cria uma posi��o de tiles no mapa de tiles.
	 */
	public TileMapItem(int x, int y) {
		this.position = new Vec2(x, y);
		this.tiles = new LinkedList<TileData>();
	}
	
	/**
	 * Adiciona um tile � lista de tiles da posi��o.
	 * @param data Informa��es do tile.
	 */
	public void addTile(TileData data) {
		this.tiles.add(data);
	}
}
