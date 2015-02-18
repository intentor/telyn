package org.escape2team.telyn.core;

import org.escape2team.telyn.objects.LevelObject;
import org.escape2team.telyn.states.LevelState;
import org.jbox2d.dynamics.ContactListener;
import org.jbox2d.dynamics.contacts.ContactPoint;
import org.jbox2d.dynamics.contacts.ContactResult;

/**
 * Handler de colis�es.
 */
public class PlayerContactHandler implements ContactListener {
	/** M�nima for�a para causar a tela tremer. */
	private static final float MIN_FORCE_TO_SHAKE_VIEWPORT = 14.0f;
	/** Indica se o personagem pode pular. */
	private boolean canJump = false;
	/** Indica se o personagem morreu por algum tipo de colis�o. */
	private boolean isDead = false;
	/** Indica se o personagem est� bloqueado na direita. */
	private boolean isBlockedLeft = false;
	/** Indica se o personagem est� bloqueado na esquerda. */
	private boolean isBlockedRight = false;
	/** Indica se o personagem est� empurrando um objeto. */
	private boolean isPushable = false;
	/** For�a da �ltima colis�o ocorrida. */
	private float force = 0;
	/** Refer�ncia ao estado do n�vel do jogo. */
	private LevelState state;
	
	/**
	 * Cria um novo avaliador de contato.
	 * @param state Refer�ncia ao estado do jogo.
	 */
	public PlayerContactHandler(LevelState state) {
		this.state = state;
	}
	
	/**
	 * Indica se o personagem est� bloqueado na direita.
	 * @return Valor booleano indicando se o personagem est� bloqueado.
	 */
	public boolean isBlockedLeft() {
		return this.isBlockedLeft;
	}
	
	/**
	 * Indica se o personagem est� bloqueado na esquerda.
	 * @return Valor booleano indicando se o personagem est� bloqueado.
	 */
	public boolean isBlockedRight() {
		return this.isBlockedRight;
	}
	
	/**
	 * Indica se o personagem est� empurrando um objeto.
	 * @return Valor booleano indicando se o personagem est� empurrando um objeto.
	 */
	public boolean isPushable() {
		return this.isPushable;
	}
	
	/**
	 * Indica se o personagem pode pular.
	 * @return Valor booleano indicando se o persongem pode pular.
	 */
	public boolean canJump() {
		return this.canJump;
	}
	
	/**
	 * Indica se o personagem morreu por algum tipo de colis�o.
	 * @return Valor booleano indicando se o personagem morreu por algum tipo de colis�o.
	 */
	public boolean isDead() {
		return this.isDead;
	}
	
	/**
	 * Indica que o personagem reviveu.
	 */
	public void revive() {
		this.isDead = false;
	}
	
	@Override
	public void add(ContactPoint point) {
		
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void persist(ContactPoint point) {
		//For�a da colis�o.
		this.force = Math.abs(point.velocity.x) + Math.abs(point.velocity.y);
		
		//Se a for�a for 0, verifica se algum dos corpos � um LevelObject e obt�m sua for�a.
		if (this.force == 0) {
			if (point.shape1.m_body.getUserData() instanceof LevelObject &&
				((LevelObject)point.shape1.m_body.getUserData()).causesPlayerToDye()) {
				this.force = Math.abs(point.shape1.m_body.m_linearVelocity.x) + Math.abs(point.shape1.m_body.m_linearVelocity.y);
			}
			if (point.shape2.m_body.getUserData() instanceof LevelObject &&
					((LevelObject)point.shape2.m_body.getUserData()).causesPlayerToDye()) {
				this.force = Math.abs(point.shape2.m_body.m_linearVelocity.x) + Math.abs(point.shape2.m_body.m_linearVelocity.y);
			}
		}
		
		/* Somente treme a tela se a for�a aplicada n�o tiver a ver com o jogador.
		 * (Tem de ser sempre algum elemento do mundo interagindo com o mundo. */
		if (this.force > MIN_FORCE_TO_SHAKE_VIEWPORT &&
			!(point.shape2.m_body.getUserData() instanceof Player) &&
			!(point.shape2.m_body.getUserData() instanceof Player)) {
			this.state.shakeViewport();
		}
		
		/* Primeiramente checa se houve colis�o de morte.
		 * A morte do personagem � causada se ele colidir-se com algum
		 * objeto que cause morte (causesPlayerToDye() == true) e a for�a
		 * do impacto ser maior que a m�nima for�a necess�rio para causar morte
		 * (force > forceToCauseDeath()).*/
		if (
				(!(point.shape1.m_userData instanceof Boolean) &&
				point.shape1.m_userData != "trigger" &&
				point.shape2.m_body.getUserData() instanceof Player &&
				point.shape1.m_body.getUserData() instanceof LevelObject &&
				((LevelObject)point.shape1.m_body.getUserData()).causesPlayerToDye() &&
				force >= ((LevelObject)point.shape1.m_body.getUserData()).minForceToCauseDeath()) 
			||
				(!(point.shape2.m_userData instanceof Boolean) &&
				point.shape2.m_userData != "trigger" &&
				point.shape1.m_body.getUserData() instanceof Player &&
				point.shape2.m_body.getUserData() instanceof LevelObject &&
				((LevelObject)point.shape2.m_body.getUserData()).causesPlayerToDye() &&
				force >= ((LevelObject)point.shape2.m_body.getUserData()).minForceToCauseDeath()) 
			) {
			this.isDead = true;
		} else {
			//N�o havendo colis�o de morte, checa os diversos tipos de colis�o poss�veis.
			if (point.shape1.getUserData() == "groundsensor" || point.shape2.getUserData() == "groundsensor") { //Sensor de pulo.
				this.canJump = true;
			} else if (point.shape1.getUserData() == "trigger" &&
				point.shape2.m_body.getUserData() instanceof Player) { //Trigger no shape1.
				((LevelObject)point.shape1.m_body.m_userData).triggerActivated(point.position, point.shape2.m_body);
			} else if (point.shape2.getUserData() == "trigger" &&
				point.shape1.m_body.getUserData() instanceof Player) { //Trigger no shape2.
				((LevelObject)point.shape2.m_body.m_userData).triggerActivated(point.position, point.shape1.m_body);
			} else if (point.shape1.getUserData() == "sensorleft") { //Colis�o do jogador � esquerda no shape1.
				if (point.shape2.getUserData() instanceof MassData) {
					if (this.canJump) {
						this.isPushable = true;
						this.isBlockedLeft = false;
					} else {
						this.isPushable = false;
						this.isBlockedLeft = true;
					}
				} else {
					this.isPushable = false;
					this.isBlockedLeft = true;
				}
			} else if (point.shape2.getUserData() == "sensorleft") { //Colis�o do jogador � esquerda no shape2.
				if (point.shape1.getUserData() instanceof MassData) {
					if (this.canJump) {
						this.isPushable = true;
						this.isBlockedLeft = false;
					} else {
						this.isPushable = false;
						this.isBlockedLeft = true;
					}
				} else {
					this.isPushable = false;
					this.isBlockedLeft = true;
				}
			} else if (point.shape1.getUserData() == "sensorright") { //Colis�o do jogador � direita no shape1.
				if (point.shape2.getUserData() instanceof MassData) {
					if (this.canJump) {
						this.isPushable = true;
						this.isBlockedRight = false;
					} else {
						this.isPushable = false;
						this.isBlockedRight = true;
					}
				} else {
					this.isPushable = false;
					this.isBlockedRight = true;
				}
			} else if (point.shape2.getUserData() == "sensorright") { //Colis�o do jogador � direita no shape2.
				if (point.shape1.getUserData() instanceof MassData) {
					if (this.canJump) {
						this.isPushable = true;
						this.isBlockedRight = false;
					} else {
						this.isPushable = false;
						this.isBlockedRight = true;
					}
				} else {
					this.isPushable = false;
					this.isBlockedRight = true;
				}
			}
		}
	}

	@Override
	public void remove(ContactPoint point) {
		if (point.shape1.getUserData() == "groundsensor" || point.shape2.getUserData() == "groundsensor") {
			this.canJump = false;
		} else if (point.shape1.getUserData() == "sensorleft" || point.shape2.getUserData() == "sensorleft") {
			this.isBlockedLeft = false;
			this.isPushable = false;
		} else if (point.shape1.getUserData() == "sensorright" || point.shape2.getUserData() == "sensorright") {
			this.isBlockedRight = false;
			this.isPushable = false;
		}
	}

	@Override
	public void result(ContactResult point) {
		
	}
}
