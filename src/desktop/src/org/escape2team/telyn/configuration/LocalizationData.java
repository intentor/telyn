package org.escape2team.telyn.configuration;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.newdawn.slick.util.ResourceLoader;

/**
 * Representa dados de localização.
 */
public final class LocalizationData {
	/** Dados de localização. */
	private Map<String, String> data;
	
	/**
	 * Cria um novo conjunto de dados de localização.
	 * @param culture Idioma a ser carregado.
	 */
	public LocalizationData(String language) {
		this.data = new HashMap<String, String>();
		String path = String.format("data/lang/%s.lang", language);
		this.loadData(path);
	}
	
	/**
	 * Carrega os dado de idioma do arquivo indicado.
	 * @param path Arquivo do qual os dados serão carregados.
	 */
	private void loadData(String path) {
		try {
			InputStream is = ResourceLoader.getResourceAsStream(path);
			BufferedReader input = new BufferedReader(new InputStreamReader(is));   
	        String line = null;   
			while ((line = input.readLine()) != null){
				String map[] = line.split("=");
				this.data.put(map[0], map[1]);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Obtém um texto no idioma configurado.
	 * @param key Chave do texto a ser obtido.
	 * @return Texto no idioma configurado.
	 */
	public String getString(String key) {
		return this.data.get(key);
	}
	
	/**
	 * Realiza parse de um texto em busca de entradas "lang:key".
	 * @param text Texto a ser analisado.
	 * @return Texto com todas as entradas "lang:key" trocadas pela respectiva chave de idioma.
	 */
	public String parse(String text) {
		Pattern regex = Pattern.compile("lang:(\\w+)");
		Matcher match = regex.matcher(text);
		while (match.find()) {
			text = text.replace(match.group(), this.getString(match.group(1)));
		}
		
		return text;
	}
}
