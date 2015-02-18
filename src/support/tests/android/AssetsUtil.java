package org.escape2team.telyn;

import java.util.Calendar;
import java.util.Random;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.PackedSpriteSheet;
import org.newdawn.slick.SlickException;

/**
 * Procedimentos de apoio na manipula��o de assets.
 */
public final class AssetsUtil {
	
	/**
	 * Gerador de n�meros aleat�rios.
	 */
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
     * Realiza carregamento de sprites de um pack.
     * @param name	Nome-base das imagens no sprite a serem carregadas.
     * @param pack	Pacote de sprites. 
     * @param flip	Indica se se deve inverter o sprite horizontalmente.
     * @return Array contendo as imagens carregadas.
     */
    public static Image[] loadSprites(String name, PackedSpriteSheet pack) {
    	Image[] img = new Image[20];
    	for (int i = 1; i <= 20; i++) 
			img[i - 1] = pack.getSprite(name + "_" + String.valueOf(i));      
    	
    	return img;
    }

	/**
	 * Realiza carregamento de uma anima��o a partir das informa��es do pacote de sprites.
	 * @param path		Caminho do arquivo de defina��o do pacote de sprites.			
	 * @param itensName	Nome dos itens a serem carregados.
	 * @param duration	Dura��o dos quadros da anima��o.
	 * @return Objeto representando a anima��o solicitada.
	 * @throws SlickException
	 */
    public static Animation loadAnimation(String path, String itensName, int duration) throws SlickException {
    	int[] dur = { duration }; 
    	return loadAnimation(path, itensName, dur);
    }
	
	/**
	 * Realiza carregamento de uma anima��o a partir das informa��es do pacote de sprites.
	 * @param path		Caminho do arquivo de defina��o do pacote de sprites.			
	 * @param itensName	Nome dos itens a serem carregados.
	 * @param duration	Dura��o para cada quadro da anima��o.
	 * @return Objeto representando a anima��o solicitada.
	 * @throws SlickException
	 */
	public static Animation loadAnimation(String path, String itensName, int[] duration) throws SlickException {
    	Animation anim;
    	PackedSpriteSheet pack = new PackedSpriteSheet(path, Image.FILTER_NEAREST);
    	
    	if (duration.length == 1) {
    		anim = new Animation(AssetsUtil.loadSprites(itensName, pack), duration[0]);
    	} else {
        	anim = new Animation(AssetsUtil.loadSprites(itensName, pack), duration);
    	}
    	
    	return anim;
    }
}
