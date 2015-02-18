for (Shape s = b.getShapeList(); s != null; s = s.getNext()) {
	//DESENHO DE FORMAS DE UM CORPO
	final XForm xf = b.getMemberXForm();

	final PolygonShape poly = (PolygonShape)s;
	final int vertexCount = poly.getVertexCount();
	final Vec2[] localVertices = poly.getVertices();
	float[] points = new float[vertexCount * 2];

	assert(vertexCount <= Settings.maxPolygonVertices);

	for (int i = 0; i < vertexCount; i++) {						
		Vec2 screen = this.transform.worldToScreen(XForm.mul(xf, localVertices[i]));
		points[2 * i] = screen.x;
		points[2 * i + 1] = screen.y;
	}

	Color c = g.getColor();
	//Borda sólida.
	//g.setColor(new Color(88, 70, 59));
	//g.draw(polyToDraw);
	g.setColor(new Color(47, 67, 27));
	g.fill(new Polygon(points));
	g.setColor(c);
}