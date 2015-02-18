package org.escape2team.telyn.core;

import org.jbox2d.dynamics.Body;

/**
 * Listener de eventos de filtragem de contato de objetos.
 */
public interface PlayerContactFilterListener {
	/**
	 * Indica que houve colisão com uma orb.
	 * @params orb Corpo da orb no qual o jogador colidiu.
	 */
	void onOrbCollision(Body orb);
}
