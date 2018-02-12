package com.komodo.InterviewBot;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class QuestionManager {

	private int counter;
	private Properties properties;

	public QuestionManager() {
		counter = 1;
		loadQuestions();
	}

	public void loadQuestions() {
		InputStream input = null;
		try {
			input = new FileInputStream("questions.properties");
			properties = new Properties();
			properties.load(input);
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public String getNextQuestion() {
		String question = properties.getProperty("app.question"+counter);	
		counter++;
		return question;
	}
}
