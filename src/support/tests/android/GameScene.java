package org.escape2team.telyn;

import java.io.IOException;
import java.io.InputStream;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.particles.ConfigurableEmitter;
import org.newdawn.slick.particles.ParticleIO;
import org.newdawn.slick.particles.ParticleSystem;
import org.newdawn.slick.util.ResourceLoader;


import android.content.Context;
import android.os.Vibrator;
public class GameScene extends BasicGame implements AccelerometerListener {

	private ParticleSystem system;
	private ConfigurableEmitter emitter;
	private Vibrator vibration;
	private float factorY;
	private float factorX;
	private double amplitude;
	String value = "[VAZIO]";
	
	public GameScene() {
		super("Android Tech Demo");
	}

	@Override
	public void init(GameContainer container) throws SlickException {
		try {
			//Verifica se o acelerômetro pode ser usado.
	        if (AccelerometerManager.isSupported()) {
	            AccelerometerManager.startListening(this);
	        }
	        
	        this.vibration = (Vibrator)Loader.ANDROID_CONTEXT.getSystemService(Context.VIBRATOR_SERVICE);
			this.vibration.vibrate(2000);
			
			Image image = new Image("particles/0.png", false);
			this.system = new ParticleSystem(image, 1000); 
			
			InputStream file = ResourceLoader.getResourceAsStream("particles/flame.xml");
			this.emitter = ParticleIO.loadEmitter(file);
			this.emitter.setPosition(400,300);
			
			this.system.addEmitter(this.emitter);	 
			this.system.setBlendingMode(ParticleSystem.BLEND_ADDITIVE);	
			this.system.setUsePoints(false);
		} catch (IOException e) {
			this.value = e.getMessage();
		} catch (Exception e) {
			this.value = e.getMessage();
		}
	}

	@Override
	public void render(GameContainer container, Graphics g) throws SlickException {
		system.render();
		g.setColor(Color.white);
		g.drawString(this.value, 20, 40);
		g.drawString("MIC(" + String.valueOf(this.amplitude) + ")", 20, 60);
		g.drawString("AC(" + String.valueOf(this.factorX) + "," + String.valueOf(this.factorY) + ")", 20, 80);		
	}

	@Override
	public void update(GameContainer container, int delta) throws SlickException {
		this.amplitude = Loader.AMP;
		this.emitter.gravityFactor.setValue(this.factorX * 5);
		this.emitter.windFactor.setValue(this.factorY * 5);
		this.emitter.growthFactor.setValue((float)this.amplitude * -10);
		system.update(delta);
	}
	
	/**
	 * Entry point into our game. Simple bootstrap for the container and fire
	 * the game off.
	 * 
	 * @param argv The arguments passed into the game
	 */
	public static void main(String[] argv) {
		try {
			AppGameContainer container = new AppGameContainer(new GameScene());
			container.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onAccelerationChanged(float x, float y, float z) {
		this.factorX = x;
		this.factorY = y;
	}

	@Override
	public void onShake(float force) {
	}
}