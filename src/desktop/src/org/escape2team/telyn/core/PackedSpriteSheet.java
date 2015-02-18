package org.escape2team.telyn.core;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.ResourceLoader;

/**
 * Obten��o de sprites a partir de arquivo de defini��o conforme aplicativo
 * Image Packer (http://homepage.ntlworld.com/config/imagepacker/).
 * Baseado em org.newdawn.slick.PackedSpriteSheet
 */
public class PackedSpriteSheet {
	/** Imagem contendo os sprites */
	private Image image;
	/** Caminho do arquivo de defini��o. */
	private String path;
	/** Caminho aonde a imagem est�, baseado */
	private String basePath;
	/** Defini��o das se��es do sprite sheet. */
	private List<PackedSpriteSection> sections = new LinkedList<PackedSpriteSection>();
	/** Filtro utilizado para carregamento da imagem. */
	private int filter = Image.FILTER_NEAREST;
	
	/**
	 * Cria um novo packed sprite sheet baseado no arquivo de defini��o do ImagePacker.
	 * @param def Caminho do arquivo de defini��o.
	 */
	public PackedSpriteSheet(String def) throws SlickException {
		this(def, null);
	}
	
	/**
	 * Cria um novo packed sprite sheet baseado no arquivo de defini��o do ImagePacker.
	 * @param def 	Caminho do arquivo de defini��o.
	 * @param trans Cor a ser tratada como transpar�ncia.
	 */
	public PackedSpriteSheet(String def, Color trans) throws SlickException {
		this.path = def.replace('\\', '/');
		this.basePath = this.path.substring(0,def.lastIndexOf("/") + 1);
		
		loadDefinition(def, trans);
	}

	/**
	 * Cria um novo packed sprite sheet baseado no arquivo de defini��o do ImagePacker.
	 * @param def 		Caminho do arquivo de defini��o.
	 * @param filter 	Filtro de imagem a ser utilizado quando da carga do sprite sheet.
	 */
	public PackedSpriteSheet(String def, int filter) throws SlickException {
		this(def, filter, null);
	}
	
	/**

	 * Cria um novo packed sprite sheet baseado no arquivo de defini��o do ImagePacker.
	 * @param def 		Caminho do arquivo de defini��o.
	 * @param filter 	Filtro de imagem a ser utilizado quando da carga do sprite sheet.
	 * @param trans 	Cor a ser tratada como transpar�ncia.
	 */
	public PackedSpriteSheet(String def, int filter, Color trans) throws SlickException {
		this.path = def.replace('\\', '/');
		this.basePath = this.path.substring(0,def.lastIndexOf("/") + 1);
		this.filter = filter;
		
		loadDefinition(def, trans);
	}
	
	/**
	 * Obt�m a imagem contendo todas as se��es.
	 * @return Imagem contendo todas as se��es.
	 */
	public Image getFullImage() {
		return image;
	}
	
	/** 
	 * Obt�m a quantidade de sprites do arquivo.
	 * @return Quantidade de sprites do arquivo.
	 */
	public int getCount() {
		return this.sections.size();
	}
	
	/**
	 * Obt�m um sprite a partir do pacote de sprites.
	 * @param name Nome do sprite a ser obtido.
	 * @return Sprite requisitado.
	 */
	public Image getSprite(String name) {
		PackedSpriteSection section = null;
		
		for (PackedSpriteSection s : this.sections) {
			if (s.name.equals(name)) {
				section = s;
				break;
			}
		}
		
		if (section == null) {
			throw new RuntimeException("Sprite desconhecido no pacote atual: " + name);
		}
		
		return section.getSectionImage();
	}
	
	/**
	 * Obt�m um sprite a partir do pacote de sprites.
	 * @param index Posi��o do sprite a ser obtido.
	 * @return Sprite requisitado.
	 */
	public Image getSprite(int index) {
		return this.sections.get(index).getSectionImage();
	}
	
	/**
	 * Obt�m o nome de um sprite a partir de seu �ndice.
	 * @param index Posi��o do sprite a ser obtido.
	 * @return Nome do sprite.
	 */
	public String getSpriteName(int index) {
		return this.sections.get(index).name;
	}
	
	/**
	 * Obt�m o nome do pacote de sprites.
	 * @return Nome do pacote de sprites.
	 */
	public String getPackName() {
		return this.path.substring(this.path.lastIndexOf("/") + 1, this.path.lastIndexOf("."));
	}
	
	/**
	 * Obt�m o caminho do pacote de sprites.
	 * @return Caminho do pacote de sprites.
	 */
	public String getPath() {
		return this.path;
	}
	
	/**
	 * Carrega o arquivo de defini��o e cria as se��es.
	 * @param def 	Caminho do arquivo de defini��o.
	 * @param trans Cor a ser tratada como transpar�ncia.
	 */
	private void loadDefinition(String def, Color trans) throws SlickException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(ResourceLoader.getResourceAsStream(def)));
	
		try {
			image = new Image(basePath + reader.readLine(), false, filter, trans);
			while (reader.ready()) {
				if (reader.readLine() == null) break;
				this.sections.add(new PackedSpriteSection(reader, this.image));				
				if (reader.readLine() == null) break;
			}
		} catch (Exception e) {
			throw new SlickException("Falha no processamento do arquivo de defini��o. Talvez o formato seja inv�lido?", e);
		}
	}
}
