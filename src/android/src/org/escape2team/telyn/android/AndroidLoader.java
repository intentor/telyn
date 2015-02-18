package org.escape2team.telyn.android;


import org.escape2team.telyn.states.Loader;
import org.newdawn.slick.SlickActivity;
import android.content.Context;
import android.os.Bundle;

public class AndroidLoader extends SlickActivity {
	/** Contexto de execução da aplicação ANDROID. */
	public static Context ANDROID_CONTEXT;	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ANDROID_CONTEXT = this;
        start(new Loader(), 800, 480);
    }
}