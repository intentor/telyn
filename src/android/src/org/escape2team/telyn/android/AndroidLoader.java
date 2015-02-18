package org.escape2team.telyn.android;


import org.escape2team.telyn.states.Loader;
import org.newdawn.slick.SlickActivity;
import android.content.Context;
import android.os.Bundle;

public class AndroidLoader extends SlickActivity {
	/** Contexto de execu��o da aplica��o ANDROID. */
	public static Context ANDROID_CONTEXT;	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ANDROID_CONTEXT = this;
        start(new Loader(), 800, 480);
    }
}