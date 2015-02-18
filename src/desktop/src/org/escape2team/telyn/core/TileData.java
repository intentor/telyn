package org.escape2team.telyn.core;

import org.jbox2d.common.Vec2;

/**
 * Dados de um tile do editor.
 */
public class TileData {
	/** ID atribuído ao tile para indexação. */
	public int tileId;
	/** ID do arquivo ao qual o tile pertence. */
	public int definitionFileId;
	/** Nome do tile. */
	public String spriteName;
	/** Posição do tile, em unidades do mundo. */
	public Vec2 position;
	/** Posição do tile, em unidades de tela. */
	public Vec2 location;
	/** Rotação da imagem, em radianos. */
	public float rotation;
	/** Indica se a imagem deve ser flipada. */
	public boolean flip;
	
	/**
	 * Construtor da classe.
	 * @param tileID			ID atribuído ao tile para indexação.
	 * @param definitionFileId	ID do arquivo ao qual o tile pertence.
	 * @param spriteName		Nome do tile.
	 * @param position			Posição do tile.
	 * @param rotation			Posição do tile.
	 * @param flip				Indica se a imagem deve ser flipada.
	 */
	public TileData(int tileId, int definitionFileId, String spriteName, Vec2 position, float rotation, boolean flip) {
		this.tileId = tileId;
		this.definitionFileId = definitionFileId;
		this.spriteName = spriteName;
		this.position = position;
		this.rotation = rotation;
		this.flip = flip;
		
		this.location = new Vec2();
	}
}