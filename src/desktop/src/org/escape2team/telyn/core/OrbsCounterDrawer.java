package org.escape2team.telyn.core;

import org.jbox2d.common.Vec2;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Objeto de desenho do HUD de orbs.
 */
public class OrbsCounterDrawer implements GameObject {
	/** Número máximo de orbs a serem acrescidas.. */
	public int maxOrbs;
	/** Quantidade total de orbs disponíveis. */
	public int totalOrbs;
	/** Quantidade de orbs utilizadas. */
	public int usedOrbs;
	/** Centro da circunferência. */
	public Vec2 center;
	/** Tamanho do raio da circunferência. */
	private int radius;
	/** Pacote de sprites contendo as imagens. */
	PackedSpriteSheet pack;
	/** Background do HUD. */
	private Image background;
	/** ORB vazia. */
	private Image emptyOrb;
	/** ORB cheia. */
	private Image filledOrb;
	
	/**
	 * Construtor da classe.
	 * @param maxOrbs	Número máximo de orbs.
	 * @param totalOrbs Total de orbs inicialmente disponíveis.
	 * @param center	Centro da circunferência do HUD.
	 * @param radius	Tamanho do raio da circunferência do HUD.
	 * @param pack		Pacote de sprites contendo as imagens.
	 */
	public OrbsCounterDrawer(int maxOrbs, int totalOrbs, Vec2 center, int radius, PackedSpriteSheet pack) {
		this.maxOrbs = maxOrbs;
		this.totalOrbs = totalOrbs;
		this.center = center;
		this.radius = radius;
		this.pack = pack;
	}
	
	/**
	 * Adiciona uma orb ao total de orbs.
	 */
	public void addOrb() {
		if (this.totalOrbs < maxOrbs) this.totalOrbs++;
	}
	
	/**
	 * Reseta a quantidade de orbs utilizadas.
	 */
	public void resetOrbs() {
		this.usedOrbs = 0;
	}
	
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		this.background = pack.getSprite("background");
		this.emptyOrb = pack.getSprite("emptyOrb");
		this.filledOrb = pack.getSprite("filledOrb");
		
		this.usedOrbs = 0;
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) {
	
	}

	@Override
	public void leave(GameContainer container, StateBasedGame game) throws SlickException {
		this.background.destroy();
		this.emptyOrb.destroy();
		this.filledOrb.destroy();
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g, Color filter) throws SlickException {
		int size = this.background.getWidth() / 2;
		this.background.draw(this.center.x - size, this.center.y - size);
		
		Vec2[] points = Utils.getPointsInCircunference(this.center, this.radius, this.totalOrbs, 180, true, true);
		for (int i = 0; i < this.totalOrbs; i++) {
			Vec2 p = points[i];
			
			if (i >= this.usedOrbs) {
				size = this.filledOrb.getWidth() / 2;				
				this.filledOrb.draw(p.x - size, p.y - size);
			} else {
				size = this.emptyOrb.getWidth() / 2;	
				this.emptyOrb.draw(p.x - size, p.y - size);
			}
		}
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		
	}
}
