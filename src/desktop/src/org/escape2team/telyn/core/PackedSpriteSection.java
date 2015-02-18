package org.escape2team.telyn.core;

import java.io.BufferedReader;
import java.io.IOException;

import org.newdawn.slick.Image;

/**
 * Representa uma seção do sprite sheet.
 */
 public class PackedSpriteSection {
	/** Nome da seção. */
	public String name;
	/** Imagem representada pela seção. */
	public Image sectionImage;
	/** Posição no eixo X da seção. */
	public int x;
	/** Posição no eixo Y da seção */
	public int y;
	/** Largura da seção */
	public int width;
	/** Altura da seção. */
	public int height;
	/** Quantidade de sprite ao longo da seção. */
	public int tilesx;
	/** Quantidade de sprites abaixo da seção. */
	public int tilesy;
	
	/**
	 * Cria uma nova seção a partir da leitura da Stream informada.
	 * @param reader 			Leitor do arquivo de definição.
	 * @param packedSpriteImage	Imagem do packed sprite sheet.
	 */
	public PackedSpriteSection(BufferedReader reader, Image packedSpriteImage) throws IOException {
		this.name = reader.readLine().trim();
		
		this.x = Integer.parseInt(reader.readLine().trim());
		this.y = Integer.parseInt(reader.readLine().trim());
		this.width = Integer.parseInt(reader.readLine().trim());
		this.height = Integer.parseInt(reader.readLine().trim());
		this.tilesx = Integer.parseInt(reader.readLine().trim());
		this.tilesy = Integer.parseInt(reader.readLine().trim());
		
		reader.readLine().trim();
		reader.readLine().trim();
		
		this.tilesx = Math.max(1,tilesx);
		this.tilesy = Math.max(1,tilesy);
		
		this.sectionImage = packedSpriteImage.getSubImage(x, y, width, height);
	}
	
	/**
	 * Obtém a imagem que representa a seção.
	 * @return Imagem que representa a seção.
	 */
	public Image getSectionImage() {
		return this.sectionImage;
	}
}
