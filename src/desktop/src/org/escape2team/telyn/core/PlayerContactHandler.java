package org.escape2team.telyn.core;

import org.escape2team.telyn.objects.LevelObject;
import org.escape2team.telyn.states.LevelState;
import org.jbox2d.dynamics.ContactListener;
import org.jbox2d.dynamics.contacts.ContactPoint;
import org.jbox2d.dynamics.contacts.ContactResult;

/**
 * Handler de colisões.
 */
public class PlayerContactHandler implements ContactListener {
	/** Mínima força para causar a tela tremer. */
	private static final float MIN_FORCE_TO_SHAKE_VIEWPORT = 14.0f;
	/** Indica se o personagem pode pular. */
	private boolean canJump = false;
	/** Indica se o personagem morreu por algum tipo de colisão. */
	private boolean isDead = false;
	/** Indica se o personagem está bloqueado na direita. */
	private boolean isBlockedLeft = false;
	/** Indica se o personagem está bloqueado na esquerda. */
	private boolean isBlockedRight = false;
	/** Indica se o personagem está empurrando um objeto. */
	private boolean isPushable = false;
	/** Força da última colisão ocorrida. */
	private float force = 0;
	/** Referência ao estado do nível do jogo. */
	private LevelState state;
	
	/**
	 * Cria um novo avaliador de contato.
	 * @param state Referência ao estado do jogo.
	 */
	public PlayerContactHandler(LevelState state) {
		this.state = state;
	}
	
	/**
	 * Indica se o personagem está bloqueado na direita.
	 * @return Valor booleano indicando se o personagem está bloqueado.
	 */
	public boolean isBlockedLeft() {
		return this.isBlockedLeft;
	}
	
	/**
	 * Indica se o personagem está bloqueado na esquerda.
	 * @return Valor booleano indicando se o personagem está bloqueado.
	 */
	public boolean isBlockedRight() {
		return this.isBlockedRight;
	}
	
	/**
	 * Indica se o personagem está empurrando um objeto.
	 * @return Valor booleano indicando se o personagem está empurrando um objeto.
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
	 * Indica se o personagem morreu por algum tipo de colisão.
	 * @return Valor booleano indicando se o personagem morreu por algum tipo de colisão.
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
		//Força da colisão.
		this.force = Math.abs(point.velocity.x) + Math.abs(point.velocity.y);
		
		//Se a força for 0, verifica se algum dos corpos é um LevelObject e obtém sua força.
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
		
		/* Somente treme a tela se a força aplicada não tiver a ver com o jogador.
		 * (Tem de ser sempre algum elemento do mundo interagindo com o mundo. */
		if (this.force > MIN_FORCE_TO_SHAKE_VIEWPORT &&
			!(point.shape2.m_body.getUserData() instanceof Player) &&
			!(point.shape2.m_body.getUserData() instanceof Player)) {
			this.state.shakeViewport();
		}
		
		/* Primeiramente checa se houve colisão de morte.
		 * A morte do personagem é causada se ele colidir-se com algum
		 * objeto que cause morte (causesPlayerToDye() == true) e a força
		 * do impacto ser maior que a mínima força necessário para causar morte
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
			//Não havendo colisão de morte, checa os diversos tipos de colisão possíveis.
			if (point.shape1.getUserData() == "groundsensor" || point.shape2.getUserData() == "groundsensor") { //Sensor de pulo.
				this.canJump = true;
			} else if (point.shape1.getUserData() == "trigger" &&
				point.shape2.m_body.getUserData() instanceof Player) { //Trigger no shape1.
				((LevelObject)point.shape1.m_body.m_userData).triggerActivated(point.position, point.shape2.m_body);
			} else if (point.shape2.getUserData() == "trigger" &&
				point.shape1.m_body.getUserData() instanceof Player) { //Trigger no shape2.
				((LevelObject)point.shape2.m_body.m_userData).triggerActivated(point.position, point.shape1.m_body);
			} else if (point.shape1.getUserData() == "sensorleft") { //Colisão do jogador à esquerda no shape1.
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
			} else if (point.shape2.getUserData() == "sensorleft") { //Colisão do jogador à esquerda no shape2.
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
			} else if (point.shape1.getUserData() == "sensorright") { //Colisão do jogador à direita no shape1.
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
			} else if (point.shape2.getUserData() == "sensorright") { //Colisão do jogador à direita no shape2.
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
