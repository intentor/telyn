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
 * Obtenção de sprites a partir de arquivo de definição conforme aplicativo
 * Image Packer (http://homepage.ntlworld.com/config/imagepacker/).
 * Baseado em org.newdawn.slick.PackedSpriteSheet
 */
public class PackedSpriteSheet {
	/** Imagem contendo os sprites */
	private Image image;
	/** Caminho do arquivo de definição. */
	private String path;
	/** Caminho aonde a imagem está, baseado */
	private String basePath;
	/** Definição das seções do sprite sheet. */
	private List<PackedSpriteSection> sections = new LinkedList<PackedSpriteSection>();
	/** Filtro utilizado para carregamento da imagem. */
	private int filter = Image.FILTER_NEAREST;
	
	/**
	 * Cria um novo packed sprite sheet baseado no arquivo de definição do ImagePacker.
	 * @param def Caminho do arquivo de definição.
	 */
	public PackedSpriteSheet(String def) throws SlickException {
		this(def, null);
	}
	
	/**
	 * Cria um novo packed sprite sheet baseado no arquivo de definição do ImagePacker.
	 * @param def 	Caminho do arquivo de definição.
	 * @param trans Cor a ser tratada como transparência.
	 */
	public PackedSpriteSheet(String def, Color trans) throws SlickException {
		this.path = def.replace('\\', '/');
		this.basePath = this.path.substring(0,def.lastIndexOf("/") + 1);
		
		loadDefinition(def, trans);
	}

	/**
	 * Cria um novo packed sprite sheet baseado no arquivo de definição do ImagePacker.
	 * @param def 		Caminho do arquivo de definição.
	 * @param filter 	Filtro de imagem a ser utilizado quando da carga do sprite sheet.
	 */
	public PackedSpriteSheet(String def, int filter) throws SlickException {
		this(def, filter, null);
	}
	
	/**

	 * Cria um novo packed sprite sheet baseado no arquivo de definição do ImagePacker.
	 * @param def 		Caminho do arquivo de definição.
	 * @param filter 	Filtro de imagem a ser utilizado quando da carga do sprite sheet.
	 * @param trans 	Cor a ser tratada como transparência.
	 */
	public PackedSpriteSheet(String def, int filter, Color trans) throws SlickException {
		this.path = def.replace('\\', '/');
		this.basePath = this.path.substring(0,def.lastIndexOf("/") + 1);
		this.filter = filter;
		
		loadDefinition(def, trans);
	}
	
	/**
	 * Obtém a imagem contendo todas as seções.
	 * @return Imagem contendo todas as seções.
	 */
	public Image getFullImage() {
		return image;
	}
	
	/** 
	 * Obtém a quantidade de sprites do arquivo.
	 * @return Quantidade de sprites do arquivo.
	 */
	public int getCount() {
		return this.sections.size();
	}
	
	/**
	 * Obtém um sprite a partir do pacote de sprites.
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
	 * Obtém um sprite a partir do pacote de sprites.
	 * @param index Posição do sprite a ser obtido.
	 * @return Sprite requisitado.
	 */
	public Image getSprite(int index) {
		return this.sections.get(index).getSectionImage();
	}
	
	/**
	 * Obtém o nome de um sprite a partir de seu índice.
	 * @param index Posição do sprite a ser obtido.
	 * @return Nome do sprite.
	 */
	public String getSpriteName(int index) {
		return this.sections.get(index).name;
	}
	
	/**
	 * Obtém o nome do pacote de sprites.
	 * @return Nome do pacote de sprites.
	 */
	public String getPackName() {
		return this.path.substring(this.path.lastIndexOf("/") + 1, this.path.lastIndexOf("."));
	}
	
	/**
	 * Obtém o caminho do pacote de sprites.
	 * @return Caminho do pacote de sprites.
	 */
	public String getPath() {
		return this.path;
	}
	
	/**
	 * Carrega o arquivo de definição e cria as seções.
	 * @param def 	Caminho do arquivo de definição.
	 * @param trans Cor a ser tratada como transparência.
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
			throw new SlickException("Falha no processamento do arquivo de definição. Talvez o formato seja inválido?", e);
		}
	}
}
