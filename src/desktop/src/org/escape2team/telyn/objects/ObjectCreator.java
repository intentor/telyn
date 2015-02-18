package org.escape2team.telyn.objects;

import java.util.LinkedList;
import java.util.List;

import org.escape2team.telyn.core.ObjectData;
import org.escape2team.telyn.core.ObjectType;
import org.escape2team.telyn.core.ViewportTransform;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Cria objetos no mundo do jogo.
 */
public class ObjectCreator {
	/** Objeto que representa o mundo do jogo. */
	private World world;
	/** Objeto para conversões entre unidades de tela e mundo. */
	protected ViewportTransform transform;
	/** Sprites dos objetos. */
	private Object[] sprites;
	/** Objetos de cenário. */
	@SuppressWarnings("rawtypes")
	public List<LevelObject> objects;
	
	/**
	 * Cria uma nova instância do criador de objetos.
	 * @param world		Objeto que representa o mundo do jogo.
	 * @param transform	Objeto para conversões entre unidades de tela e mundo.
	 * @param sprites	Sprites dos objetos.
	 */
	@SuppressWarnings("rawtypes")
	public ObjectCreator(World world, ViewportTransform transform, Object[] sprites) {
		this.world = world;
		this.transform = transform;
		this.sprites = sprites;
		this.objects = new LinkedList<LevelObject>();
	}
	
	/**
	 * Inicializa todos os objetos criados.
	 * @param container	Container de jogo.
	 * @param game		Estado atual do jogo.
	 */
	public void initAll(GameContainer container, StateBasedGame game) throws SlickException {
		for (@SuppressWarnings("rawtypes") LevelObject obj : this.objects) obj.init(container, game);
	}
	
	/**
	 * Cria objetos a partir de uma lista.
	 * @param objects Lista de objetos a serem criados.
	 */
	public void create(List<ObjectData> objects) {
		for (ObjectData obj : objects) {
			this.create(obj.type, obj.position, obj.triggerPosition);
		}
	}
	
	/**
	 * Cria um objeto a partir de seu tipo.
	 * @param type 		Tipo do objeto a ser criado.
	 * @param pos		Posição na qual o objeto será criado.
	 * @param trigger	Posição do disparador de eventos do objeto.
	 * @return Objeto representando o objeto criado.
	 */
	@SuppressWarnings("rawtypes")
	public LevelObject create(ObjectType type, Vec2 pos, Vec2 trigger) {
		LevelObject b = null;
		
		switch(type) {
			case Orb:
				b = this.createOrb(pos, trigger);
				break;
			case Rock:
				b = this.createRock(pos, trigger);
				break;
			case BigRock:
				b = this.createBigRock(pos, trigger);
				break;
			case StemTree:
				b = this.createStemTree(pos, trigger);
				break;
			case Pendulum:
				b = this.createPendulum(pos, trigger);
				break;
			case Rotator:
				b = this.createRotator(pos, trigger);
				break;
		}
		
		return b;
	}
	
	/**
	 * Configura um objeto.
	 * @param obj 		Objeto a ser configurado.
	 * @param trigger	Posição do disparador de eventos do objeto.
	 * @return Objeto representando o objeto criado.
	 */
	@SuppressWarnings("rawtypes")
	private LevelObject configure(LevelObject obj, Vec2 trigger) {
		if (trigger != null) obj.createTrigger(trigger);
		this.objects.add(obj);
		return obj;
	}
	
	/**
	 * Cria um objeto do tipo "Orb".
	 * @param pos 		Posição na qual o objeto será criado.
	 * @param trigger	Posição do disparador de eventos do objeto.
	 * @return Objeto representando o objeto criado.
	 */
	@SuppressWarnings("rawtypes")
	private LevelObject createOrb(Vec2 pos, Vec2 trigger) {
		return this.configure(new OrbObject(this.world, this.transform, (Image[]) this.sprites[ObjectType.Orb.getId()], pos), trigger);
	}
	
	/**
	 * Cria um objeto do tipo "Rock".
	 * @param pos 		Posição na qual o objeto será criado.
	 * @param trigger	Posição do disparador de eventos do objeto.
	 * @return Objeto representando o objeto criado.
	 */
	@SuppressWarnings("rawtypes")
	private LevelObject createRock(Vec2 pos, Vec2 trigger) {
		return this.configure(new RockObject(this.world, this.transform, (Image) this.sprites[ObjectType.Rock.getId()], pos), trigger);
	}
	
	/**
	 * Cria um objeto do tipo "BigRock".
	 * @param pos 		Posição na qual o objeto será criado.
	 * @param trigger	Posição do disparador de eventos do objeto.
	 * @return Objeto representando o objeto criado.
	 */
	@SuppressWarnings("rawtypes")
	private LevelObject createBigRock(Vec2 pos, Vec2 trigger) {
		return this.configure(new BigRockObject(this.world, this.transform, (Image) this.sprites[ObjectType.BigRock.getId()], pos), trigger);
	}
	
	/**
	 * Cria um objeto do tipo "StemTree".
	 * @param pos 		Posição na qual o objeto será criado.
	 * @param trigger	Posição do disparador de eventos do objeto.
	 * @return Objeto representando o objeto criado.
	 */
	@SuppressWarnings("rawtypes")
	private LevelObject createStemTree(Vec2 pos, Vec2 trigger) {
		return this.configure(new StemTreeObject(this.world, this.transform, (Image) this.sprites[ObjectType.StemTree.getId()], pos), trigger);
	}
	
	/**
	 * Cria um objeto do tipo "Pendulum".
	 * @param pos 		Posição na qual o objeto será criado.
	 * @param trigger	Posição do disparador de eventos do objeto.
	 * @return Objeto representando o objeto criado.
	 */
	@SuppressWarnings("rawtypes")
	private LevelObject createPendulum(Vec2 pos, Vec2 trigger) {
		return this.configure(new PendulumObject(this.world, this.transform, (Image) this.sprites[ObjectType.Pendulum.getId()], pos), trigger);
	}
	
	/**
	 * Cria um objeto do tipo "Rotator".
	 * @param pos 		Posição na qual o objeto será criado.
	 * @param trigger	Posição do disparador de eventos do objeto.
	 * @return Objeto representando o objeto criado.
	 */
	@SuppressWarnings("rawtypes")
	private LevelObject createRotator(Vec2 pos, Vec2 trigger) {
		return this.configure(new RotatorObject(this.world, this.transform, (Image) this.sprites[ObjectType.Rotator.getId()], pos), trigger);
	}
}
