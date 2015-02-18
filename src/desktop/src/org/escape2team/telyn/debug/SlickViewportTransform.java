package org.escape2team.telyn.debug;
import org.jbox2d.common.IViewportTransform;
import org.jbox2d.common.Vec2;
import org.newdawn.slick.GameContainer;

/**
 * Operações de transformações do ViewPort do JBox2D.
 */
public class SlickViewportTransform implements IViewportTransform {
	/**
	 * Offset from world 0,0 to the center of the screen.
	 */
	public Vec2 center;
	/**
	 * World scale.
	 */
	public float scaleFactor = 20.0f;
	/**
	 * Flip Y coordinate.
	 */
	public float yFlipFactor = 1.0f;
	/** 
	 * If true, draw from top left.
	 */
	private boolean isFlip = false;
	/**
	 * Half-width and half-height of the screen.
	 */
	protected Vec2 extendsVector;
	/**
	 * Slick Game Container.
	 */
	protected GameContainer container;

	public SlickViewportTransform(GameContainer container) {
		this.container = container;
		this.center = new Vec2(0.5f * this.container.getWidth(), 0.5f * this.container.getHeight());
		this.extendsVector = new Vec2(this.container.getScreenWidth() / 2, this.container.getScreenWidth() / 2);
	}	
	
	@Override
	public boolean isYFlip() {
		return this.isFlip;
	}

	@Override
	public void setYFlip(boolean yFlip) {
		this.isFlip = yFlip;
		if (yFlip) this.yFlipFactor = -1.0f;
		else this.yFlipFactor = 1.0f;
	}

	@Override
	public Vec2 getExtents() {
		return this.extendsVector;
	}

	@Override
	public void setExtents(Vec2 argExtents) {
		this.extendsVector.set(argExtents);
	}

	@Override
	public void setExtents(float argHalfWidth, float argHalfHeight) {
		this.extendsVector.set(argHalfWidth, argHalfHeight);
	}

	@Override
	public Vec2 getCenter() {
		return this.center;
	}

	@Override
	public void setCenter(Vec2 argPos) {
		this.center.set(argPos);
	}

	@Override
	public void setCenter(float x, float y) {
		this.center.set(x, y);
	}

	@Override
	public void setCamera(float x, float y, float scale) {
		// x and y are world coordinates, as in original JBox2D code;
		// scale is the ratio of screen (pixel) to world units;
		// offset, used internally, is in screen coordinates;
		float xOffset = this.container.getWidth() / 2;
		float yOffset = this.container.getHeight() / 2;
		center.set(x * scale + xOffset, y * scale + yOffset);
		scaleFactor = scale;
	}
	
	public Vec2 screenToWorld(Vec2 screenV) {
		return new Vec2((screenV.x - this.center.x) / this.scaleFactor, this.yFlipFactor
				* (screenV.y - this.center.y) / this.scaleFactor);
	}

	public Vec2 worldToScreen(Vec2 worldV) {
		return new Vec2(worldV.x * this.scaleFactor + this.center.x, this.yFlipFactor * worldV.y
				* this.scaleFactor + this.center.y);
	}

	@Override
	public void vectorTransform(Vec2 argWorld, Vec2 argScreen) {
		argScreen.set(this.worldToScreen(argWorld));
	}

	@Override
	public void vectorInverseTransform(Vec2 argScreen, Vec2 argWorld) {
		argWorld.set(this.screenToWorld(argScreen));
	}

	@Override
	public void getWorldToScreen(Vec2 argWorld, Vec2 argScreen) {
		argScreen.set(this.worldToScreen(argWorld));
	}

	@Override
	public void getScreenToWorld(Vec2 argScreen, Vec2 argWorld) {
		argWorld.set(this.screenToWorld(argScreen));
	}
}
