package org.escape2team.telyn.core;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import org.escape2team.telyn.configuration.LevelConfiguration;
import org.escape2team.telyn.states.LevelState;
import org.escape2team.telyn.states.Loader;
import org.jbox2d.collision.AABB;
import org.jbox2d.collision.shapes.PolygonDef;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.particles.ParticleIO;
import org.newdawn.slick.particles.ParticleSystem;

/**
 * Procedimentos de apoio na manipula��o de assets.
 */
public final class Utils {	
	/** Tamanho do sensor do checkpoint. */
	public static final Vec2 CHECKPOINT_SIZE = new Vec2(0.1f, 5.0f);
	/** Gerador de n�meros aleat�rios. */
    private static Random rnd;
   
    /**
     * Obt�m o objeto <code>Random</code>.
     * @return Objeto <code>Random</code>.
     */
    private static Random getRandomObject()
    {
        if (rnd == null)
        {
            Calendar cal = Calendar.getInstance();
            int seed = cal.get(Calendar.SECOND) + cal.get(Calendar.MINUTE) + cal.get(Calendar.HOUR);
            rnd = new Random(seed);
        }

        return rnd;
    }
    
    /**
     * Gera um valor inteiro aleat�rio.
     * @param maximum Valor m�ximo a ser gerado.
     * @return Valor inteiro gerado.
     */
    public static int getRandomNumber(int maximum)
    {
        return getRandomObject().nextInt(maximum + 1);
    }
	
    /**
     * Carrega sprites de um pack.
     * @param name	Nome-base das imagens no sprite a serem carregadas.
     * @param pack	Pacote de sprites. 
     * @param count	Quantidade dos sprites.
     * @return Array contendo as imagens carregadas.
     */
    public static Image[] loadSprites(String name, PackedSpriteSheet pack, int count) {
    	Image[] img = new Image[count];
    	for (int i = 1; i <= count; i++) 
			img[i - 1] = pack.getSprite(name + "_" + String.valueOf(i));      
    	
    	return img;
    }

	/**
	 * Carrega uma anima��o a partir das informa��es do pacote de sprites.
	 * @param path		Caminho do arquivo de defina��o do pacote de sprites.			
	 * @param itensName	Nome dos itens a serem carregados.
	 * @param count		Quantidade de sprites.
	 * @param duration	Dura��o dos quadros da anima��o.
	 * @return Objeto representando a anima��o solicitada.
	 * @throws SlickException
	 */
    public static Animation loadAnimation(String path, String itensName, int count, int duration) throws SlickException {
    	int[] dur = { duration }; 
    	return loadAnimation(path, itensName, count, dur);
    }
	
	/**
	 * Carrega uma anima��o a partir das informa��es do pacote de sprites.
	 * @param path		Caminho do arquivo de defina��o do pacote de sprites.			
	 * @param itensName	Nome dos itens a serem carregados.
	 * @param count		Quantidade de sprites.
	 * @param duration	Dura��o para cada quadro da anima��o.
	 * @return Objeto representando a anima��o solicitada.
	 * @throws SlickException
	 */
	public static Animation loadAnimation(String path, String itensName, int count, int[] duration) throws SlickException {
    	PackedSpriteSheet pack = new PackedSpriteSheet(path, Image.FILTER_NEAREST);
    	return loadAnimation(pack, itensName, count, duration);
    }
	
	/**
	 * Carrega uma anima��o a partir das informa��es do pacote de sprites.
	 * @param pack		Pacote de sprites.			
	 * @param itensName	Nome dos itens a serem carregados.
	 * @param count		Quantidade de sprites.
	 * @param duration	Dura��o dos quadros da anima��o.
	 * @return Objeto representando a anima��o solicitada.
	 * @throws SlickException
	 */
	public static Animation loadAnimation(PackedSpriteSheet pack, String itensName, int count, int duration) throws SlickException {
		int[] dur = { duration }; 
    	return loadAnimation(pack, itensName, count, dur);
    }
	
	/**
	 * Carrega uma anima��o a partir das informa��es do pacote de sprites.
	 * @param pack		Pacote de sprites.			
	 * @param itensName	Nome dos itens a serem carregados.
	 * @param count		Quantidade de sprites.
	 * @param duration	Dura��o para cada quadro da anima��o.
	 * @return Objeto representando a anima��o solicitada.
	 * @throws SlickException
	 */
	public static Animation loadAnimation(PackedSpriteSheet pack, String itensName, int count, int[] duration) throws SlickException {
    	Animation anim;		
		
		if (duration.length == 1) {
    		anim = new Animation(Utils.loadSprites(itensName, pack, count), duration[0]);
    	} else {
        	anim = new Animation(Utils.loadSprites(itensName, pack, count), duration);
    	}
    	
    	return anim;
    }
	
	/**
	 * Carrega um sistema de part�culas.
	 * @param path Caminho do arquivo XML do sistema de part�culas.
	 * @return Sistema de part�culas carregado.
	 * @throws IOException 
	 */
	public static ParticleSystem loadParticleSystem(String path) throws IOException {
		ParticleSystem system = ParticleIO.loadConfiguredSystem(path);
		system.setDefaultImageName("data/particles/particle.tga");
		return system;
	}
	
	/**
	 * Verifica se houve interse��o entre dois ret�ngulos.
	 * @param r1 Ret�ngulo 1.
	 * @param r2 Ret�ngulo 2.
	 * @return Valor booleano indicando se houve interse��o.
	 */
	public static boolean intersect(Rectangle r1, Rectangle r2) {
		/* Ocorre colis�o se:
		 * !(left > other.right || right < other.left ||
		 *	top > other.bottom || bottom < other.top)
		 */		
		return !((r1.getX() > (r2.getX() + r2.getWidth()) || (r1.getX() + r1.getWidth()) < r2.getX()) ||
				(r1.getY() > (r2.getY() + r2.getHeight()) || (r1.getY() + r1.getHeight()) < r2.getY()));		
	}
	
	/**
	 * Obt�m a �rea de interse��o de 2 ret�ngulos.
	 * @param r1 Ret�ngulo 1.
	 * @param r2 Ret�ngulo 2.
	 * @return Ret�ngulo representado a �rea de interse��o.
	 */
	public static Rectangle getOverlapArea(Rectangle r1, Rectangle r2) {
		/* O overlap rectangle �:
		 * x = max(left, other.left)
		 * y = max(top, other.top),
		 * w = min(right, other.right) - x
		 * h = min(bottom, other.bottom) - y;
		 */
		int x = (int) Math.max(r1.getX(), r2.getX())
			, y = (int) Math.max(r1.getY(), r2.getY())
			, w = (int) Math.min((r1.getX() + r1.getWidth()), (r2.getX() + r2.getWidth())) - x
			, h = (int) Math.min((r1.getY() + r1.getHeight()), (r2.getY() + r2.getHeight())) - y;
		
		return new Rectangle(x, y, w, h);
	}
	
	/**
	 * Carrega uma imagem.
	 * @param path Caminho da imagem.
	 * @return Imagem carregada.
	 * @throws SlickException
	 */
	public static Image loadImage(String path) throws SlickException {
		return new Image(path);
	}
	
	/**
	 * Escreve uma string centralizada.
	 * @param font		Fonte a ser utilizada para escrita.
	 * @param s			Texto a ser escrito.
	 * @param y			Posi��o no eixo Y a ser utilizada para escrita.
	 */
	public static void drawStringCenter(Font font, String s, float y) {
		drawStringCenter(font, s, y, Color.white);
	}
	
	/**
	 * Escreve uma string centralizada.
	 * @param font		Fonte a ser utilizada para escrita.
	 * @param s			Texto a ser escrito.
	 * @param y			Posi��o no eixo Y a ser utilizada para escrita.
	 * @param filter	Cor para filtragem da fonte.
	 */
	public static void drawStringCenter(Font font, String s, float y, Color filter) {
		int size = font.getWidth(s);
		font.drawString((Loader.CONFIGURATIONS.screenWidth - size) / 2, y, s, filter);
	}
	
	/**
	 * Escreve uma string � direita.
	 * @param font		Fonte a ser utilizada para escrita.
	 * @param s			Texto a ser escrito.
	 * @param y			Posi��o no eixo Y a ser utilizada para escrita.
	 * @param padding	Margem direita.
	 */
	public static void drawStringRight(Font font, String s, float y, int padding) {
		drawStringRight(font, s, y, padding, Color.white);
	}

	/**
	 * Escreve uma string � direita.
	 * @param font		Fonte a ser utilizada para escrita.
	 * @param s			Texto a ser escrito.
	 * @param y			Posi��o no eixo Y a ser utilizada para escrita.
	 * @param padding	Margem direita.
	 * @param filter	Cor para filtragem da fonte.
	 */
	public static void drawStringRight(Font font, String s, float y, int padding, Color filter) {
		int size = font.getWidth(s);
		font.drawString(Loader.CONFIGURATIONS.screenWidth - size - padding, y, s, filter);
	}
	
	/**
	 * Desenha uma imagem.
	 * @param image		Imagem a ser desenhada.
	 * @param position	Posi��o da imagem em unidades de tela.
	 * @param rotation	Rota��o da imagem em radianos.
	 * @param scale		Escala de renderiza��o.
	 * @param g			Objeto gr�fico.
	 * @param filter	Filtro de cor da imagem.
	 */
	public static void drawImage(Image img, Vec2 position, float rotation, float scale, Graphics g, Color filter) {
		float angle = (float) Math.toDegrees(rotation);
		
		g.rotate(position.x, position.y, -angle);
		img.draw(position.x, position.y, scale, filter);
		g.rotate(position.x, position.y, angle);
	}
	
	/**
	 * Desenha uma imagem.
	 * @param image			Imagem a ser desenhada.
	 * @param position		Posi��o da imagem em unidades do mundo.
	 * @param rotation		Rota��o da imagem em radianos.
	 * @param transform		Objeto para convers�es entre unidades de tela e mundo.
	 * @param g				Objeto gr�fico.
	 * @param filter		Filtro de cor da imagem.
	 */
	public static void drawImage(Image img, Vec2 position, float rotation, ViewportTransform transform, Graphics g, Color filter) {
		float localScale = transform.getLocalScale();
		float halfImageWidth = img.getWidth() / 2;
		float halfImageHeight = img.getHeight() / 2;
		Vec2 p = transform.worldToScreen(position);
		float angle = (float) Math.toDegrees(rotation);
		
		g.rotate(p.x, p.y, -angle);
		img.draw(p.x - localScale * halfImageWidth, p.y - localScale * halfImageHeight, localScale, filter);
		g.rotate(p.x, p.y, angle);
	}
	
	/**
	 * Cria um checkpoint do jogo.
	 * @param world 	Objeto que representa o mundo do jogo.
	 * @param position	Posi��o do checkpoint, em unidades do mundo.
	 * @param position  Objeto de estado do jogo.
	 * @return Corpo do checkpoint.
	 */
	public static Body createCheckpoint(World world, Vec2 position, LevelState state) {
		BodyDef def = new BodyDef();
		def.position.set(position);
		Body checkpoint = world.createBody(def);
		
		PolygonDef pd = new PolygonDef();
		pd.isSensor = true; //Todo checkpoint � um sensor.
		pd.userData = "checkpoint";
		pd.setAsBox(CHECKPOINT_SIZE.x, CHECKPOINT_SIZE.y);
		
		checkpoint.createShape(pd);
		checkpoint.setMassFromShapes();
		checkpoint.setUserData(state);
		
		return checkpoint;
	}
	
	/**
	 * Cria as colis�es.
	 * @param world 		Objeto que representa o mundo do jogo.
	 * @param collisions	V�rtices dos pol�gonos de colis�o.
	 */
	public static void createCollisions(World world, List<List<Vec2>> collisions) {
		for (List<Vec2> vertex : collisions) {			
			BodyDef bd = new BodyDef();
			Body b = world.createBody(bd);
			
			for (int i = 0; i < vertex.size() - 1; ++i) {
				PolygonDef sd = new PolygonDef();
				sd.friction = LevelConfiguration.GROUND_FRICTION;
				createStrokeRect(vertex.get(i), vertex.get(i + 1), LevelConfiguration.STROKE_RADIUS, b, sd);
			}
			
			b.setMassFromShapes();
		}
	}
	
	/**
	 * Cria um ret�ngulo de desenho.
	 * @param start		In�cio do ret�ngulo.
	 * @param end		Fim do ret�ngulo.
	 * @param height	Altura do ret�ngulo.
	 * @param body		Corpo do ret�ngulo.
	 * @param sd		Defini��es do pol�gono do ret�ngulo.
	 */
	public static void createStrokeRect(Vec2 start, Vec2 end, float height, Body body, PolygonDef sd) {
		Vec2 tangent = end.sub(start);
		Vec2 perp = new Vec2(tangent.y, -tangent.x);
		perp.normalize();
		perp.mulLocal(height);
		sd.vertices.add(start.add(perp));
		sd.vertices.add(end.add(perp));
		sd.vertices.add(end.sub(perp));
		sd.vertices.add(start.sub(perp));
		body.createShape(sd);
	}
	
	/**
	 * Distribui pontos ao redor de um circunfer�ncia.
	 * @param center			Centro da circunfer�ncia.
	 * @param radius			Raio da circunfer�ncia.
	 * @param totalPoints		Total de pontos a serem distribu�dos.
	 * @param startAngle		�ngulo do primeiro ponto (em graus).
	 * @param evenDistribution	Distruibui os n�mero a partir de fatores pares.
	 * @param isClockwise		Indica se a distribui��o deve ser em sentido hor�rio.
	 * @return
	 */
	public static Vec2[] getPointsInCircunference(Vec2 center, float radius, int totalPoints, int startAngle, boolean evenDistribution, boolean isClockwise) {
		Vec2[] pts = new Vec2[totalPoints];
		
		int arc = 360;
		float mpi = (float) (Math.PI / 180);
		float startRadians = startAngle * mpi;
		
		float incrementAngle = arc / totalPoints;
		float incrementRadians = incrementAngle * mpi;
		
		if (arc < 360) {
			if (evenDistribution) {
				incrementAngle = arc / (totalPoints - 1);
				incrementRadians = incrementAngle * mpi;
			} else {
				incrementAngle = arc / totalPoints;
				incrementRadians = incrementAngle * mpi;
			}
		}
		
		for (int i = 0; i < totalPoints; i++) {
			float xp = (float) (center.x + Math.sin(startRadians) * radius);
			float yp = (float) (center.y + Math.cos(startRadians) * radius);

			pts[i] = new Vec2(xp, yp);

			if (isClockwise) startRadians -= incrementRadians;
			else startRadians += incrementRadians;
		}
		
		return pts;
	}
	
	/**
	 * Obt�m o corpo presente em uma determinada posi��o do mouse.
	 * @param world Mundo f�sico.
	 * @param p 	Posi��o do mundo a ser analisada.
	 * @return Corpo na posi��o ou null caso nenhum corpo esteja no ponto informado.
	 */
	public static Body getBodyAtPosition(World world, Vec2 p) {
		return getBodyAtPosition(world, p, new Vec2(0.001f, 0.001f));
	}
	
	/**
	 * Obt�m o corpo presente em uma determinada posi��o do mouse.
	 * @param world Mundo f�sico.
	 * @param p 	Posi��o do mundo a ser analisada.
	 * @param d 	Dimens�es da box de an�lise.
	 * @return Corpo na posi��o ou null caso nenhum corpo esteja no ponto informado.
	 */
	public static Body getBodyAtPosition(World world, Vec2 p, Vec2 d) {
		//Cria uma pequena caixa que representa a posi��o informada.		
		AABB aabb = new AABB(p.sub(d), p.add(d));
		
		//Realiza pesquisa no mundo.
		int k_maxCount = 10;
		Shape shapes[] = world.query(aabb, k_maxCount);

		Body body = null;
        for (int i = 0; i < shapes.length; i++) {
            Body shapeBody = shapes[i].getBody();
            boolean inside = shapes[i].testPoint(shapeBody.getMemberXForm(),p);
            if (inside) {
                body = shapes[i].m_body;
                break;
            }
        }
        
        return body;
	}
}
