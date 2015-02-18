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
public class OrbsHudDrawer implements GameObject {

	/** Máximo tamanho dos vetores de desenho. */
	public static final int MAX_DRAWING_LENGTH = 20;
	/** Tamanho dos pontos das orbs. */
	private static final int SIZE_POINTS = 6;
	/** Quantidade total de orbs disponíveis. */
	public int totalOrbs;
	/** Quantidade de orbs utilizadas. */
	public int usedOrbs;
	/** Centro da circunferência do HUD. */
	public Vec2 center;
	/** Tamanho do raio da circunferência. */
	private int radius;
	/** Background do HUD. */
	private Image background;
	/** ORB vazia. */
	private Image emptyOrb;
	/** ORB cheia. */
	private Image filledOrb;
	
	/**
	 * Construtor da classe.
	 * @param totalOrbs Total de orbs inicialmente disponíveis.
	 * @param center	Centro da circunferência do HUD.
	 * @param radius	Tamanho do raio da circunferência do HUD.
	 * 
	 */
	public OrbsHudDrawer(int totalOrbs, Vec2 center, int radius) {
		this.totalOrbs = totalOrbs;
		this.center = center;
		this.radius = radius;
	}
	
	/**
	 * Adiciona uma orb ao total de orbs.
	 */
	public void addOrb() {
		if (this.totalOrbs < MAX_DRAWING_LENGTH) this.totalOrbs++;
	}
	
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		PackedSpriteSheet pack = new PackedSpriteSheet("data/sprites/orbhud.def");
		this.background = pack.getSprite("background");
		this.emptyOrb = pack.getSprite("emptyOrb");
		this.filledOrb = pack.getSprite("filledOrb");
		
		this.usedOrbs = 0;
	}

	@Override
	public void leave(GameContainer container, StateBasedGame game) throws SlickException {
		
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g, Color filter) throws SlickException {
		/*g.setColor(Color.gray);
		
		int sizeInternalCircle =(int) (this.radius * 1.40f);
		g.drawOval(this.center.x - sizeInternalCircle / 2, this.center.y - sizeInternalCircle / 2, sizeInternalCircle, sizeInternalCircle);
				
		Vec2[] points = Utils.getPointsInCircunference(this.center, this.radius, this.totalOrbs, 180, true, true);
		for (int i = 0; i < this.totalOrbs; i++) {
			Vec2 p = points[i];
			
			if (i >= this.usedOrbs) g.setColor(Color.orange);
			else g.setColor(Color.gray);
			
			g.fillOval(p.x - SIZE_POINTS / 2, p.y - SIZE_POINTS / 2, SIZE_POINTS, SIZE_POINTS, 10);
			
		}
		
		g.setColor(Color.white);*/
		
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
