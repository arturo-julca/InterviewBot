package com.komodo.InterviewBot.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.darkprograms.speech.microphone.Microphone;
import com.darkprograms.speech.recognizer.GSpeechDuplex;
import com.darkprograms.speech.recognizer.GSpeechResponseListener;
import com.darkprograms.speech.recognizer.GoogleResponse;
import com.komodo.InterviewBot.GSpeechManager;
import com.komodo.InterviewBot.QuestionManager;
import com.komodo.InterviewBot.util.PropertiesUtil;
import com.komodo.InterviewBot.util.SpeakerUtil;
import com.komodo.InterviewBot.util.TextFileUtil;
import com.komodo.recorder.RecorderManager;

import net.sourceforge.javaflacencoder.FLACFileWriter;

public class DesktopClient implements GSpeechResponseListener {

	JButton startInterviewButton;
	JButton nextQuestionButton;
	BufferedWriter writer;
	JTextArea messagePanel;
	QuestionManager questionManager;
	Microphone mic;
	GSpeechDuplex duplex;
	boolean recorderSuccess;

	public DesktopClient() {
		startInterviewButton = new JButton("Start Interview");
		nextQuestionButton = new JButton("Next Question");
		messagePanel = new JTextArea();
		writer = TextFileUtil.createWriteFile(PropertiesUtil.get("app.path.log"));
		questionManager = new QuestionManager();
		mic = new Microphone(FLACFileWriter.FLAC);
		duplex = new GSpeechDuplex(PropertiesUtil.get("google.key"));
		duplex.setLanguage(PropertiesUtil.get("default.lang"));
		TextFileUtil.writeFile(writer, "[Starting interview]");
		recorderSuccess = true;
		//RecorderManager.setPath(PropertiesUtil.get("app.path.video"));
	}

	public void launch() {
		JFrame frame = new JFrame(PropertiesUtil.get("app.title"));
		frame.setDefaultCloseOperation(3);
		messagePanel.setEditable(false);
		messagePanel.setWrapStyleWord(true);
		messagePanel.setLineWrap(true);
		messagePanel.setText(PropertiesUtil.get("app.initialText"));
		nextQuestionButton.setEnabled(false);

		JScrollPane scroll = new JScrollPane(messagePanel);
		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), 1));
		frame.getContentPane().add(scroll);
		JPanel recordBar = new JPanel();
		frame.getContentPane().add(recordBar);
		recordBar.setLayout(new BoxLayout(recordBar, 0));
		recordBar.add(startInterviewButton);
		recordBar.add(nextQuestionButton);
		frame.setVisible(true);
		frame.pack();
		frame.setSize(500, 200);
		frame.setLocationRelativeTo(null);

		duplex.addResponseListener(GSpeechManager.createGSpeechRecognizerListener(writer));
		nextQuestionButton.addActionListener(createNextListener());
		startInterviewButton.addActionListener(createRecordListener());
		
		SpeakerUtil.speak(PropertiesUtil.get("app.initialText"));
	}

	@Override
	public void onResponse(GoogleResponse paramGoogleResponse) {
		// TODO Auto-generated method stub
	}

	private ActionListener createRecordListener() {
		ActionListener recordListener = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				new Thread(() -> {
					try {
						duplex.recognize(mic.getTargetDataLine(), mic.getAudioFormat());
					} catch (Exception ex) {
						ex.printStackTrace();
					}

				}).start();
				try {
					RecorderManager.startActionNoUI();
				}catch(Exception e) {
					recorderSuccess = false;
					e.printStackTrace();
				}				
				startInterviewButton.setEnabled(false);
				nextQuestionButton.setEnabled(true);
				goToNextQuestion();
			}
		};
		return recordListener;
	}

	private void goToNextQuestion() {
		String nextQuestion = questionManager.getNextQuestion();
		if (nextQuestion != null) {
			TextFileUtil.writeFile(writer, "[Question: "+nextQuestion+"]");
			SpeakerUtil.speak(nextQuestion);
			messagePanel.setText(nextQuestion);
		} else {
			messagePanel.setText(" "+PropertiesUtil.get("app.finalText.wait"));
			nextQuestionButton.setEnabled(false);
			finishInterview();
		}
	}

	private ActionListener createNextListener() {
		ActionListener nextListener = new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				goToNextQuestion();
			}
		};
		return nextListener;
	}

	private void finishInterview() {
		try{
			System.out.println("finishing interview...");
			//messagePanel.setText(PropertiesUtil.get("app.finalText.wait"));
			TextFileUtil.writeFile(writer, "[Ending interview]");
			mic.close();
			duplex.stopSpeechRecognition();
			Thread.sleep(5000);
			TextFileUtil.closeWriter(writer);
			if(recorderSuccess) {
				RecorderManager.stopActionNoUI();
			}			
			messagePanel.setText(PropertiesUtil.get("app.finalText.close"));
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
