package org.escape2team.telyn.core;

import java.util.LinkedList;
import java.util.List;

import org.jbox2d.common.Vec2;

/**
 * Representa uma posição de tiles no mapa de tiles.
 */
public class TileMapItem {
	/** Tiles já renderizados no ciclo de renderização. É reiniciado a cada ciclo. */
	public static List<TileData> rendered;
	/** Posição no mapa de tiles. */
	public Vec2 position;
	/** Informações dos tiles presentes na posição. */
	public List<TileData> tiles;
	
	/**
	 * Cria uma posição de tiles no mapa de tiles.
	 */
	public TileMapItem(int x, int y) {
		this.position = new Vec2(x, y);
		this.tiles = new LinkedList<TileData>();
	}
	
	/**
	 * Adiciona um tile à lista de tiles da posição.
	 * @param data Informações do tile.
	 */
	public void addTile(TileData data) {
		this.tiles.add(data);
	}
}
