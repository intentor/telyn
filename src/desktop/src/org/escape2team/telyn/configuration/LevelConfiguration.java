package org.escape2team.telyn.configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import org.escape2team.telyn.core.ObjectData;
import org.escape2team.telyn.core.ObjectType;
import org.escape2team.telyn.core.PackedSpriteSheet;
import org.jbox2d.common.Vec2;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.ResourceLoader;

/** 
 * Configurações de nível de jogo.
 */
public class LevelConfiguration {
	/** Atrito dos pontos de colisão a serem criados. */
	public static final float GROUND_FRICTION = 0.7f;
	/** Raio dos desenhos do cenário. */
	public static final float STROKE_RADIUS = 0.02f;
	/** Caminho do arquivo de configurações. */
	public String path;
	/** Conjuntos de pontos das áreas de colisão. */
	public List<List<Vec2>> collisions;
	/** Objetos do jogo. */
	public List<ObjectData> objects;
	/** Checkpoints do jogo. */
	public List<Vec2> checkpoints;
	/** Pacotes de tiles do jogo. */
	public List<PackedSpriteSheet> packs;
	/** Posição do canto superior direito da camada em relação ao centro do mundo, em unidades do mundo. */
	public Vec2 layerUpperBound;
	/** Posição do canto inferior esquerdo da camada em relação ao centro do mundo, em unidades do mundo. */
	public Vec2 layerLowerBound;
	/** Posição inicial do jogador, caso haja. */
	public Vec2 characterPosition;
	/** Área do arquivo (0: bounds; 1: character; 2: packs; 3: collisions; 4: objects; 5: checkpoints). */
	private int currentArea;
	
	/**
	 * Construtor da classe.
	 * @param path Caminho do arquivo de configuração.
	 * @throws SlickException 
	 */
	public LevelConfiguration(String path) throws SlickException {
		this.path = path;
		this.collisions = new LinkedList<List<Vec2>>();
		this.objects = new LinkedList<ObjectData>();
		this.packs = new LinkedList<PackedSpriteSheet>();
		this.checkpoints = new LinkedList<Vec2>();
		
		this.load();
	}
	
	/**
	 * Carrega os dados do arquivo.
	 */
	private void load() throws SlickException {
		InputStream is = ResourceLoader.getResourceAsStream(this.path);
		BufferedReader input = new BufferedReader(new InputStreamReader(is));
		
		String line = null;			
		try {
			while ((line = input.readLine()) != null){
				if (line.equals("bounds [")) { 
					currentArea = 0;
					continue;
				} else if (line.equals("character [")) { 
					currentArea = 1;
					continue;
				} else if (line.trim().equals("packs [")) { 					
					currentArea = 2;
					continue;
				} else if (line.trim().equals("collisions [")) { 
					currentArea = 3;
					continue;
				} else if (line.trim().equals("objects [")) { 
					currentArea = 4;
					continue;
				} else if (line.trim().equals("checkpoints [")) { 
					currentArea = 5;
					continue;
				} else if (line.trim().equals("]")) {
					continue;
				}
				
				switch (currentArea) {
					case 0: //bounds
						this.parseBounds(line);
						break;
					case 1: //character
						this.parseCharacter(line);
						break;
					case 2: //packs
						this.parsePack(line);
						break;
					case 3: //collisions
						this.parseCollisionVectors(line);
						break;
					case 4: //objects
						this.parseObjects(line);
						break;
					case 5: //checkpoints
						this.parseCheckpoints(line);
						break;
				}
			}
			
			input.close();
		} catch (IOException e) { 
			e.printStackTrace();
		}
	}
	
	/**
	 * Realiza análise de linha contendo dados de limites de cenário.
	 * @param line Linha a ser analisada.
	 */
	private void parseBounds(String line) throws SlickException {
		String[] data = line.split(";");
		
		String[] upper = data[0].split(",");		
		this.layerUpperBound = new Vec2(Float.valueOf(upper[0]), Float.valueOf(upper[1]));
		String[] lower = data[1].split(",");
		this.layerLowerBound = new Vec2(Float.valueOf(lower[0]), Float.valueOf(lower[1]));
	}
	
	/**
	 * Realiza análise de linha contendo dados de limites de personagem.
	 * @param line Linha a ser analisada.
	 */
	private void parseCharacter(String line) throws SlickException {
		String[] data = line.split(",");		
		this.characterPosition = new Vec2(Float.valueOf(data[0]), Float.valueOf(data[1]));
	}
	
	/**
	 * Realiza análise de linha contendo dados de pacote de sprites.
	 * @param line Linha a ser analisada.
	 */
	private void parsePack(String line) throws SlickException {
		String[] data = line.split(":");
		this.packs.add(new PackedSpriteSheet(data[1]));
	}
	
	/**
	 * Realiza análise de linha contendo dados de colisão.
	 * @param line Linha a ser analisada.
	 */
	private void parseCollisionVectors(String line) {
		String[] data = line.split(";");
		List<Vec2> points = new LinkedList<Vec2>();
		
		for (int i = 0; i < data.length; i++) {
			String[] vec = data[i].split(",");
			points.add(new Vec2(Float.valueOf(vec[0]), Float.valueOf(vec[1])));
		}
		
		this.collisions.add(points);
	}
	
	/**
	 * Realiza análise de linha contendo dados de objetos no mundo do jogo.
	 * @param line Linha a ser analisada.
	 */
	private void parseObjects(String line) {
		String[] data = line.split(";");
		String[] vec1 = data[1].split(",");
		Vec2 pos = new Vec2(Float.valueOf(vec1[0]), Float.valueOf(vec1[1]));
		
		String[] vec2 = data[2].split(",");
		Vec2 trigger = new Vec2(Float.valueOf(vec2[0]), Float.valueOf(vec2[1]));
		if (trigger.x == 0 && trigger.y == 0) trigger = null;
		
		this.objects.add(new ObjectData(ObjectType.getFromId(Integer.valueOf(data[0])), pos, trigger));
	}
	
	/**
	 * Realiza análise de linha contendo dados de checkpoints do jogo.
	 * @param line Linha a ser analisada.
	 */
	private void parseCheckpoints(String line) {
		String[] data = line.split(";");
		
		for (int i = 0; i < data.length; i++) {
			String[] vec = data[i].split(",");
			this.checkpoints.add(new Vec2(Float.valueOf(vec[0]), Float.valueOf(vec[1])));
		}
	}
}
