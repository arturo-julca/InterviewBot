package com.komodo.InterviewBot;

import java.io.BufferedWriter;

import com.darkprograms.speech.recognizer.GSpeechResponseListener;
import com.darkprograms.speech.recognizer.GoogleResponse;
import com.komodo.InterviewBot.util.TextFileUtil;

public class GSpeechManager {

	public static GSpeechResponseListener createGSpeechRecognizerListener(BufferedWriter writer) {
		GSpeechResponseListener googleSpeechResponseListener = new GSpeechResponseListener() {
			String old_text = "";
			String responseTemp = "";

			public void onResponse(GoogleResponse gr) {
				String output = "";
				output = gr.getResponse();
				if (gr.getResponse() == null) {
					this.old_text = responseTemp;
					if (this.old_text.contains("(")) {
						this.old_text = this.old_text.substring(0, this.old_text.indexOf('('));
					}
					System.out.println("Paragraph Line Added");
					this.old_text = (responseTemp + "\n");
					this.old_text = this.old_text.replace(")", "").replace("( ", "");
					responseTemp = this.old_text;
					return;
				}
				if (output.contains("(")) {
					output = output.substring(0, output.indexOf('('));
				}
				if (!gr.getOtherPossibleResponses().isEmpty()) {
					output = output + " (" + (String) gr.getOtherPossibleResponses().get(0) + ")";
				}
				TextFileUtil.writeFile(writer, this.old_text + output);
				System.out.println("L3:" + output);
				responseTemp = "";
				responseTemp = this.old_text + output;
			}
		};
		return googleSpeechResponseListener;
	}
}
