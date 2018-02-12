package com.komodo.InterviewBot.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class TextFileUtil {

	private static File logFile = null;
	
	private static void checkDirectory(String path){
		File f = new File(path);
		if(!f.exists()){
			f.mkdir();
			System.out.println(path + " subdir created...");
		}
	}
	
	public static BufferedWriter createWriteFile(String path) {
		BufferedWriter writer = null;
		checkDirectory(path);
		try {
			String timeLog = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
			logFile = new File(path+"InterviewTranscript-"+timeLog + ".txt");
			System.out.println("saved in:" + logFile.getCanonicalPath());
			writer = new BufferedWriter(new FileWriter(logFile));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return writer;
	}

	public static void writeFile(BufferedWriter writer, String text) {
		try {
			if (!text.contains("(")) {
				writer.write(text);
				writer.write(System.getProperty("line.separator"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void closeWriter(BufferedWriter writer) {
		try {
			writer.close();
			cleanLog();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

//	public static void cleanTextLogged() {
//		BufferedReader br = null;
//		FileReader fr = null;
//		try {
//			fr = new FileReader(logFile);
//			br = new BufferedReader(fr);
//			String sCurrentLine;
//			while ((sCurrentLine = br.readLine()) != null) {
//				System.out.println("-->OS:"+sCurrentLine);
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				if (br != null)
//					br.close();
//				if (fr != null)
//					fr.close();
//			} catch (IOException ex) {
//				ex.printStackTrace();
//			}
//		}
		
//		try{
//			List<String> fileContent = new ArrayList<>(Files.readAllLines(logFile.toPath(), StandardCharsets.UTF_8));
//			for (int i = 0; i < fileContent.size(); i++) {
//			    System.out.println("++++B:"+fileContent.get(i));			
//			}
//		}catch(Exception e){
//			e.printStackTrace();
//		}
		//Files.write(FILE_PATH, fileContent, StandardCharsets.UTF_8);
//	}
	
	private static boolean wordcontained(String previous, String current, int limit){
		System.out.println("start");
		boolean areWordsContained = false;
		List<String> previousWordList = Arrays.asList(previous.trim().split(" "));
		List<String> currentWordList = Arrays.asList(current.trim().split(" "));
		int counterCoincidences = 0;
		System.out.println("current:"+currentWordList);
		for(String previousWord : previousWordList){
			System.out.println("prev word:"+previousWord);
			if(currentWordList.contains(previousWord)){
				System.out.println("contained");
				counterCoincidences++;
			}
		}
		System.out.println("counter:"+counterCoincidences);
		if(counterCoincidences>=limit){
			areWordsContained = true;
		}
		return areWordsContained;
	}
	
	public static void cleanLog(){
		try {
			
			List<String> fileContent = new ArrayList<>(Files.readAllLines(logFile.toPath(), StandardCharsets.UTF_8));
			
			List<String> toRemoveList = new ArrayList<String>();
			for (int i = 1; i < fileContent.size(); i++) {
				String current = fileContent.get(i);
				String previous = fileContent.get(i-1);
				if(current.contains(previous)){
					toRemoveList.add(previous);
				}
			}
			fileContent.removeAll(toRemoveList);
			
			toRemoveList.clear();
			for (int i = 1; i < fileContent.size(); i++) {
				String current = fileContent.get(i);
				String previous = fileContent.get(i-1);
				if(wordcontained(previous, current, 1)){
					toRemoveList.add(previous);
				}
			}
			fileContent.removeAll(toRemoveList);
			Files.write(logFile.toPath(), fileContent, StandardCharsets.UTF_8);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
