package org.escape2team.telyn.core;

import org.escape2team.telyn.objects.LevelObject;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.common.XForm;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class GameRenderer {	
	/** Objeto que representa o mundo do jogo. */
	private World world;
	/** Objeto para conversões entre unidades de tela e mundo. */
	private ViewportTransform transform;
	/** Container do jogo. */
	GameContainer container;
	/** Imagem de desenho. */
	private Image drawingImage;
	
	/**
	 * Construtor da classe.
	 * @param container Container do jogo.
	 * @param world 		Mundo do jogo.
	 * @param transform		Objeto para conversões entre unidades de tela e mundo.
	 * @param drawingImage	Imagem de desenho.
	 */
	public GameRenderer(GameContainer container, World world, ViewportTransform transform, Image drawingImage) {
		this.container = container;
		this.world = world;
		this.transform = transform;
		this.drawingImage = drawingImage;
	}
	
	/**
	 * Renderizador de objetos do jogo.
	 * @param container	Container do jogo.
	 * @param game		Informações do estado atual do jogo.
	 * @param g			Objeto gráfico para desenho.
	 */
	@SuppressWarnings("rawtypes")
	public void render(GameContainer container, StateBasedGame game, Graphics g, Color filter) throws SlickException {
		for (Body b = this.world.getBodyList(); b != null; b = b.m_next) {
			if (b.m_userData instanceof Image) { //Objetos do jogo que contenham imagens.
				Image img = (Image)b.m_userData;				
				Utils.drawImage(img, b.getWorldCenter(), b.getAngle(), this.transform, g, new Color(Color.white));
			} else if (b.m_userData instanceof LevelObject) { //Objetos de nível.
				((LevelObject)b.m_userData).render(container, game, g, filter);
			} else if (b.m_userData instanceof Animation) { //Objetos do jogo que contenham animações.
				Animation anim = (Animation)b.m_userData;				
				Utils.drawImage(anim.getCurrentFrame(), b.getWorldCenter(), b.getAngle(), this.transform, g, filter);
			} else if (b.m_userData instanceof Player) { //Personagem do jogo.
				Image frame = ((Player)b.m_userData).getCurrentFrame();
				Utils.drawImage(frame, b.getWorldCenter(), b.getAngle(), this.transform, g, filter);
			} else if (b.m_userData instanceof String) { //Desenhos do jogo.
				if (b.m_userData.equals("draw")) {
					for (Shape s = b.getShapeList(); s != null; s = s.getNext()) {
						final XForm xf = b.getMemberXForm();
						
						//Obtém os vertices do polígono.
						final PolygonShape poly = (PolygonShape)s;
						final Vec2[] vertex = poly.getVertices();				

						//Obtém os vetores de posição dos vértices.
						Vec2 v0 = XForm.mul(xf, vertex[0]); //Bottom left
						Vec2 v1 = XForm.mul(xf, vertex[1]); //Bottom left
						Vec2 v2 = XForm.mul(xf, vertex[3]); //Upper right
						//Obtém o ângulo a partir da base do retângulo.
						float angle = (float) Math.atan2(v1.y - v0.y, v1.x - v0.x);
						//Obtém a posição do centro do retângulo.
						Vec2 pos = new Vec2(v1.x + (v2.x - v1.x) / 2, v1.y + (v2.y - v1.y) / 2);
						
						//Desenha a imagem.
						Utils.drawImage(this.drawingImage, pos , angle, this.transform, g, filter);
					}
				}	
			}
		}
	}
}
