package controllers;

import sphinx_model.SphinxSpeechRecognizer;

import java.io.IOException;

public class SphinxSpeechRecognizerController {

    // Our controller has the model hardwired in
    private SphinxSpeechRecognizer sphinxSpeechRecognizer;

    private static SphinxSpeechRecognizerController instance;

    public static SphinxSpeechRecognizerController getInstance() throws IOException {
        if (instance == null) {
            instance = new SphinxSpeechRecognizerController();
        }
        return instance;
    }

    private SphinxSpeechRecognizerController() throws IOException {
        this.sphinxSpeechRecognizer = new SphinxSpeechRecognizer();
    }

    public void startRecording() {
        sphinxSpeechRecognizer.beginRecording();
    }
}
