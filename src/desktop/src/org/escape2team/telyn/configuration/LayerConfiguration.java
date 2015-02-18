package org.escape2team.telyn.configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import org.escape2team.telyn.core.TileData;
import org.jbox2d.common.Vec2;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.ResourceLoader;

/** 
 * Configurações de camada do jogo.
 */
public class LayerConfiguration {
	/** Caminho do arquivo de confiuração da camada. */
	public String path;
	/** Configurações do nível o qual a camada pertence. */
	public LevelConfiguration level;
	/** Lista de tiles do jogo. */
	public List<TileData> tiles;
	/** Área do arquivo (0: tiles). */
	private int currentArea;
	
	/**
	 * Construtor da classe.
	 * @param path 		Caminho do arquivo de configuração.
	 * @param config 	Configurações do nível o qual a camada pertence.
	 * @throws SlickException 
	 */
	public LayerConfiguration(String path, LevelConfiguration config) throws SlickException {
		this.path = path;
		this.level = config; 
		this.tiles = new LinkedList<TileData>();
		
		this.load();
	}
	
	/**
	 * Carrega os dados do arquivo.
	 */
	private void load() throws SlickException {
		InputStream is = ResourceLoader.getResourceAsStream(this.path);
		BufferedReader input = new BufferedReader(new InputStreamReader(is));
		
		String line = null;	
		int i = 0;
		try {
			while ((line = input.readLine()) != null){
				if (line.trim().equals("tiles [")) { 					
					currentArea = 0;
					continue;
				} else if (line.trim().equals("]")) {
					continue;
				}
				
				switch (currentArea) {
					case 0: //tiles
						this.parseTileData(line, i++);
						break;
				}
			}
			
			input.close();
		} catch (IOException e) { 
			e.printStackTrace();
		}
	}
	
	/**
	 * Realiza análise de linha contendo dados de tiles.
	 * @param line 	Linha a ser analisada.
	 * @param id	Tile ID.
	 */
	private void parseTileData(String line, int id) {
		String[] data = line.split(";");
		
		String[] vec = data[2].split(",");
		Vec2 pos = new Vec2(Float.valueOf(vec[0]), Float.valueOf(vec[1]));
		
		this.tiles.add(new TileData(id, Integer.valueOf(data[0]), data[1], pos, Float.valueOf(data[3]), data[4].equals("1")));
	}
}
