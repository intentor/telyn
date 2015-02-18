package org.escape2team.telyn.debug;

/* This work is derivative of, and has the same provisos, permissions, and license
 * (<a href="http://sourceforge.net/softwaremap/trove_list.php?form_cat=195)>zlib/libpng </a>)
 * as
 * JBox2D - A Java Port of Erin Catto's Box2D
 * 
 * JBox2D homepage: http://jbox2d.sourceforge.net/ 
 * Box2D homepage: http://www.box2d.org
 * 
 * This software is provided 'as-is', without any express or implied
 * warranty.  In no event will the authors be held liable for any damages
 * arising from the use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 * 
 * 1. The origin of this software must not be misrepresented; you must not
 * claim that you wrote the original software. If you use this software
 * in a product, an acknowledgment in the product documentation would be
 * appreciated but is not required.
 * 2. Altered source versions must be plainly marked as such, and must not be
 * misrepresented as being the original software.
 * 3. This notice may not be removed or altered from any source distribution.
 */

import org.jbox2d.common.Color3f;
import org.jbox2d.common.Vec2;
import org.jbox2d.common.XForm;
import org.jbox2d.dynamics.DebugDraw;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

/**
 * Renderizador de debug do JBox2D.
 */
public class SlickDebugDraw extends DebugDraw {
	/**
	 * Transform object.
	 */
	private final SlickViewportTransform transform;
	/**
	 * Slick Game Container.
	 */
	protected GameContainer container;

	public SlickDebugDraw(GameContainer container) {
		super(new SlickViewportTransform(container));
		this.transform = (SlickViewportTransform) super.getViewportTranform();
		this.container = container;

		this.viewportTransform.setCamera(0.0f, 10.0f, this.transform.scaleFactor);
		this.viewportTransform.setYFlip(true);
	}
	
	public void drawImage(Image image
			, Vec2 position
			, float rotation
			, float localScale
			, Vec2 localOffset
			, float halfImageWidth
			, float halfImageHeight) {
		float s = localScale * this.transform.scaleFactor;
		Vec2 p = this.transform.worldToScreen(position);
		float angle = (float) Math.toDegrees(rotation);
		Graphics g = container.getGraphics();
		g.rotate(p.x, p.y, -angle);
		image.draw(p.x - s * halfImageWidth, p.y - s * halfImageHeight, s);
		g.rotate(p.x, p.y, angle);
	}
	
	@Override
	public void drawCircle(Vec2 center, float radius, Color3f color) {
		Graphics g = container.getGraphics();
		Color c = g.getColor();
		g.setColor(new Color((int) color.x, (int) color.y, (int) color.z));
		Vec2 screenCenter = this.transform.worldToScreen(center);
		float screenRadius = this.transform.scaleFactor * radius;
		// x1, y1 are upper left corner
		float x1 = screenCenter.x - screenRadius;
		float y1 = screenCenter.y - screenRadius;
		g.drawOval(x1, y1, 2 * screenRadius, 2 * screenRadius);
		g.setColor(c);
	}

	@Override
	public void drawPoint(Vec2 position, float f, Color3f color) {
		Graphics g = container.getGraphics();
		Color c = g.getColor();
		g.setColor(new Color((int) color.x, (int) color.y, (int) color.z));
		Vec2 screenCenter = this.transform.worldToScreen(position);
		float screenRadius = 3;
		// x1, y1 are upper left corner
		float x1 = screenCenter.x - screenRadius;
		float y1 = screenCenter.y - screenRadius;
		g.fillOval(x1, y1, 2 * screenRadius, 2 * screenRadius);
		g.setColor(c);
	}

	@Override
	public void drawPolygon(Vec2[] vertices, int vertexCount, Color3f color) {
		Graphics g = container.getGraphics();
		Color c = g.getColor();
		g.setColor(new Color((int) color.x, (int) color.y, (int) color.z));
		Vec2 last = this.transform.worldToScreen(vertices[vertexCount - 1]);
		for (int i = 0; i < vertexCount; i++) {
			Vec2 current = this.transform.worldToScreen(vertices[i]);
			g.drawLine((int) (0.5f + current.x), (int) (0.5f + current.y),
					(int) (0.5f + last.x), (int) (0.5f + last.y));
			last = current;
		}
		g.setColor(c);
	}

	@Override
	public void drawSegment(Vec2 p1, Vec2 p2, Color3f color) {
		Graphics g = container.getGraphics();
		Color c = g.getColor();
		g.setColor(new Color((int) color.x, (int) color.y, (int) color.z));
		Vec2 screenP1 = this.transform.worldToScreen(p1);
		Vec2 screenP2 = this.transform.worldToScreen(p2);
		g.drawLine(screenP1.x, screenP1.y, screenP2.x, screenP2.y);
		g.setColor(c);
	}

	@Override
	public void drawSolidCircle(Vec2 center, float radius, Vec2 axis,
			Color3f color) {
		Graphics g = container.getGraphics();
		Color c = g.getColor();
		g.setColor(new Color((int) color.x, (int) color.y, (int) color.z));
		Vec2 screenCenter = this.transform.worldToScreen(center);
		float screenRadius = this.transform.scaleFactor * radius;
		// x1, y1 are upper left corner
		float x1 = screenCenter.x - screenRadius;
		float y1 = screenCenter.y - screenRadius;
		// solid outline and
		g.setColor(new Color((int) color.x, (int) color.y, (int) color.z));
		g.drawOval(x1, y1, 2 * screenRadius, 2 * screenRadius);
		g.drawLine(screenCenter.x, screenCenter.y, screenCenter.x
				+ screenRadius * axis.x, screenCenter.y + this.transform.yFlipFactor * screenRadius
				* axis.y);
		// semi-transparent fill
		g.setColor(new Color((int) color.x, (int) color.y, (int) color.z, 63));
		g.fillOval(x1, y1, 2 * screenRadius, 2 * screenRadius);
		g.setColor(c);
	}

	@Override
	public void drawSolidPolygon(Vec2[] vertices, int vertexCount, Color3f color) {
		Graphics g = container.getGraphics();
		Color c = g.getColor();
		float[] points = new float[vertexCount * 2];
		for (int i = 0; i < vertexCount; i++) {
			Vec2 screen = this.transform.worldToScreen(vertices[i]);
			points[2 * i] = screen.x;
			points[2 * i + 1] = screen.y;
		}
		org.newdawn.slick.geom.Polygon poly = new org.newdawn.slick.geom.Polygon(
				points);
		// solid outline
		g.setColor(new Color((int) color.x, (int) color.y, (int) color.z));
		g.draw(poly);
		// semi-transparent fill
		g.setColor(new Color((int) color.x, (int) color.y, (int) color.z, 63));
		g.fill(new org.newdawn.slick.geom.Polygon(points));
		g.setColor(c);
	}

	@Override
	public void drawString(float x, float y, String s, Color3f color) {
		// no world-to-screen transformation; x and y are screen coordinates
		Graphics g = container.getGraphics();
		Color c = g.getColor();
		g.setColor(new Color((int) color.x, (int) color.y, (int) color.z));
		g.drawString(s, x, y);
		g.setColor(c);
	}

	@Override
	public void drawXForm(XForm xf) {
		float r = 3;
		Graphics g = container.getGraphics();
		Vec2 p1 = xf.position.clone(), p2 = new Vec2();
		float k_axisScale = 0.4f;
		Vec2 p1world = this.transform.worldToScreen(p1);
		g.fillOval(p1world.x - r, p1world.y - r, 2 * r, 2 * r);
		p2.x = p1.x + k_axisScale * xf.R.col1.x;
		p2.y = p1.y + k_axisScale * xf.R.col1.y;
		Vec2 p2world = this.transform.worldToScreen(p2);
		p2.x = p1.x + k_axisScale * xf.R.col2.x;
		p2.y = p1.x + k_axisScale * xf.R.col2.y;
		p2world = this.transform.worldToScreen(p2);
		g.drawLine(p1world.x, p1world.y, p2world.x, p2world.y);
		g.fillOval(p2world.x - r, p2world.y - r, 2 * r, 2 * r);
	}
}
