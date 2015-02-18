package org.escape2team.telyn.core;

import org.jbox2d.common.Vec2;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.geom.Rectangle;

/**
 * Operações de transformações do ViewPort do Box2D.
 */
public class ViewportTransform {
    /** Escala do mundo. Por padrão, 1 metro = 32px (Pixels em 1 metro). */
	public static final float BASE_WORLD_SCALE = 32.0f;
    /** Escala atual do mundo. */
	private float worldScale;
	/** Offset da posição do mundo 0,0 em relação ao centro da tela. */
	private Vec2 center;
	/** Metade do tamanho da tela no eixo X. */
	private float xOffset;
	/** Metade do tamanho da tela no eixo Y. */
	private float yOffset;
	/** Inversão da coordenada Y. */
	private float yFlipFactor = -1.0f;
	/** Retângulo representando a câmera do jogo. */
	private Rectangle cam;
	
	/**
	 * Construtor da classe.
	 * @param container Container do jogo.
	 */
	public ViewportTransform(GameContainer container) {
		this.worldScale = BASE_WORLD_SCALE;
		this.center = new Vec2(0, 0);
		
		this.xOffset = container.getWidth() / 2;
		this.yOffset = container.getHeight() / 2;	
		
		this.cam = new Rectangle(0, 0, container.getWidth(), container.getHeight());
	}
	
	/**
	 * Obtém a escala atual do mundo.
	 * @return Escala do mundo
	 */
	public float getScale() {
		return this.worldScale;
	}
	
	/**
	 * Obtém a escala local do mundo.
	 * @return Escala local do mundo
	 */
	public float getLocalScale() {
		return this.getScale() / ViewportTransform.BASE_WORLD_SCALE;
	}

	/**
	 * Obtém o centro da câmera em unidades do mundo.
	 * @return Vetor representando o centro.
	 */
	public Vec2 getCenter() {
		Vec2 pos = new Vec2((this.center.x - this.xOffset) / this.worldScale, (this.center.y - this.yOffset) / this.worldScale);	

		return pos;
	}
	
	/**
	 * Obtém o centro da câmera em unidades de tela.
	 * @return Vetor representando o centro.
	 */
	public Vec2 getCenterScreen() {
		return new Vec2(this.center.x, this.center.y);
	}

	/**
	 * Obtém a metade do tamanho da tela no eixo X.
	 * @return Valor da metade do tamanho da tela, em unidades de tela.
	 */
	public float getXOffset() {
		return this.xOffset;
	}
	
	/**
	 * Obtém a metade do tamanho da tela no eixo Y.
	 * @return Valor da metade do tamanho da tela, em unidades de tela.
	 */
	public float getYOffset() {
		return this.yOffset;
	}

	/**
	 * Define a câmera do jogo.
	 * @param x Posição da câmera em X em unidades do mundo.
	 * @param y Posição da câmera em Y em unidades do mundo.
	 */
	public void setCamera(float x, float y) {
		this.setCamera(x, y, this.worldScale);
	}

	/**
	 * Define a câmera do jogo.
	 * @param x Posição da câmera em X em unidades do mundo.
	 * @param y Posição da câmera em Y em unidades do mundo.
	 * @param scale Escala da câmera, quantos pixels equivalem a 1 metro do mundo.
	 */
	public void setCamera(float x, float y, float scale) {
		this.worldScale = scale;
		this.center.set(x * this.worldScale + this.xOffset, y * this.worldScale + this.yOffset);
	}
	
	/**
	 * Verifica qual a porcentagem de um determinado objeto de um determinado tamanho no viewport.
	 * @param objCenter		Centro do corpo, em unidades do mundo.
	 * @param halfWidth		Metade da largura do corpo, em unidades do mundo.
	 * @param halfHeight	Metade da largura do corpo, em unidades do mundo.
	 * @param offset		Extensões da área de avaliação de som.
	 * @return				Porcentagem da área do objeto no viewport.
	 */
	public float isInViewport(Vec2 objCenter, float halfWidth, float halfHeight, Vec2 offset) {
		//Por padrão, o fatro de som é 0.
		float factor = 0;

		//O corpo tem suas medidas convertidas para unidades de tela e sua posição no canto superior esquerdo.
		Vec2 wObjCenter = this.worldToScreen(objCenter);
		Vec2 size = new Vec2(this.convertWorldScaleInPixels(halfWidth), this.convertWorldScaleInPixels(halfHeight));
		size.x *= offset.x;
		size.y *= offset.y;
		Rectangle body = new Rectangle(wObjCenter.x - size.x, wObjCenter.y - size.y, size.x * 2, size.y * 2);
		
		//Primeiramente, verifica se os retângulos interseccionaram.
		if (Utils.intersect(cam, body)) {
			/* Caso tenham interseccionado, verifica o fator de som
			 * de acordo com a área de r2 em r1.*/
			Rectangle overlap = Utils.getOverlapArea(cam, body);
			factor = (overlap.getWidth() * overlap.getHeight()) / (body.getWidth() * body.getHeight());
			
			//Normaliza o fator.
			if (factor > 1.0f) factor = 1.0f;
		}
		
		return factor;
	}
	
	/**
	 * Converte uma coordenada de tela para coordenada do mundo.
	 * @param screenV Coordenada de tela, em pixels.
	 * @return Coordenada em unidades do mundo.
	 */
	public Vec2 screenToWorld(Vec2 screenV) {
		return new Vec2((screenV.x - this.center.x) / this.worldScale
				, this.yFlipFactor * (screenV.y - this.center.y) / this.worldScale);
	}
	
	/**
	 * Converte uma coordenada do mundo para coordenada de tela.
	 * @param worldV Coordenada do mundo, em unidades do mundo.
	 * @return Coordenada em unidades do tela.
	 */
	public Vec2 worldToScreen(Vec2 worldV) {
		return new Vec2(worldV.x * this.worldScale + this.center.x
				, this.yFlipFactor * worldV.y * this.worldScale + this.center.y);
	}
	
	/** 
	 * Converte valores em unidades do mundo a partir de valores em pixels.
	 * @param value Valor em pixels a ser convertido.
	 * @return Valor em unidades do mundo.
	 */
	public float convertPixelsInWorldScale(float value) {
		return value / this.worldScale;
	}
	
	/** 
	 * Converte valores em pixels a partir de valores em unidades do mundo.
	 * @param value Valor em unidades do mundo a ser convertido.
	 * @return Valor em pixels.
	 */
	public float convertWorldScaleInPixels(float value) {
		return value * this.worldScale;
	}	
}
