package com.komodo.InterviewBot.util;

import com.darkprograms.speech.synthesiser.SynthesiserV2;

import javazoom.jl.player.Player;

public class SpeakerUtil {

	private static SynthesiserV2 synth = new SynthesiserV2(PropertiesUtil.get("google.key"));
	
	public static void speak(String text){
		Thread thread = new Thread(() -> {
			try{
				Player player = new Player(synth.getMP3Data(text));
				player.play();
			}catch(Exception e){
				e.printStackTrace();
			}
		});
		thread.setDaemon(false);
		thread.start();
	}
}
