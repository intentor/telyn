package org.escape2team.telyn.core;

import java.util.LinkedList;
import java.util.List;

import org.escape2team.telyn.states.LevelState;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.ContactFilter;
/**
 * Handler de filtragem de colisões.
 */
public class PlayerContactFilterHandler implements ContactFilter {

	/** Listeners de eventos de input. */
	protected List<PlayerContactFilterListener> listeners;
	
	/** 
	 * Construtor da classe.
	 */
	public PlayerContactFilterHandler() {
		this.listeners = new LinkedList<PlayerContactFilterListener>();
	}
	
	/**
     * Adiciona listener de eventos.
     * @param l Listener do evento.
     */
    public void addListener(PlayerContactFilterListener l) {
        this.listeners.add(l);
    }
    
    /**
     * Remove listener de eventos.
     * @param l Listener do evento.
     */
    public void removeListener(PlayerContactFilterListener l) {
        this.listeners.remove(l);
    }
	
	@Override
	public boolean shouldCollide(Shape shape1, Shape shape2) {
		boolean collide = true;
		
		if (shape1.getUserData() == "orb") {
			if (shape2.getUserData() == "sensorright" ||
				shape2.getUserData() == "sensorleft" ||
				shape2.getUserData() == "groundsensor") {			
				this.fireOnOrbCollisionEvent(shape1.getBody());
			}
		
			collide = false;
		} else if (shape2.getUserData() == "orb") {
			if (shape1.getUserData() == "sensorright" ||
				shape1.getUserData() == "sensorleft" ||
				shape1.getUserData() == "groundsensor") {			
				this.fireOnOrbCollisionEvent(shape2.getBody());
			}
			
			collide = false;
		} else if (shape1.getUserData() == "checkpoint" &&
			shape2.m_body.getUserData() instanceof Player) { //Sensor do checkpoint no shape1.
			((LevelState)shape1.m_body.m_userData).checkpointActivated(shape1.m_body.getWorldCenter());
			collide = false;
		} else if (shape2.getUserData() == "checkpoint" &&
			shape1.m_body.getUserData() instanceof Player) { //Sensor do checkpoint no shape2.
			((LevelState)shape2.m_body.m_userData).checkpointActivated(shape2.m_body.getWorldCenter());
			collide = false;
		}
		
		return collide;
	}

	@Override
	public boolean rayCollide(Object userData, Shape shape) {
		return true;
	}
	
	//DISPARADORES DE EVENTOS=====================================================
	
	/**
	 * Indica que houve colisão com uma orb.
	 * @params orb Corpo da orb no qual o jogador colidiu.
	 */
	public void fireOnOrbCollisionEvent(Body orb) {
		for (PlayerContactFilterListener l: this.listeners) l.onOrbCollision(orb);
	}
}
