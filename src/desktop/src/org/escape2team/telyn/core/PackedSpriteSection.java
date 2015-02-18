package org.escape2team.telyn.core;

import java.io.BufferedReader;
import java.io.IOException;

import org.newdawn.slick.Image;

/**
 * Representa uma se��o do sprite sheet.
 */
 public class PackedSpriteSection {
	/** Nome da se��o. */
	public String name;
	/** Imagem representada pela se��o. */
	public Image sectionImage;
	/** Posi��o no eixo X da se��o. */
	public int x;
	/** Posi��o no eixo Y da se��o */
	public int y;
	/** Largura da se��o */
	public int width;
	/** Altura da se��o. */
	public int height;
	/** Quantidade de sprite ao longo da se��o. */
	public int tilesx;
	/** Quantidade de sprites abaixo da se��o. */
	public int tilesy;
	
	/**
	 * Cria uma nova se��o a partir da leitura da Stream informada.
	 * @param reader 			Leitor do arquivo de defini��o.
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
	 * Obt�m a imagem que representa a se��o.
	 * @return Imagem que representa a se��o.
	 */
	public Image getSectionImage() {
		return this.sectionImage;
	}
}
