package sphinx_model;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Port;
import java.io.*;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SphinxSpeechRecognizer {
    private Logger logger = Logger.getLogger(getClass().getName());
    private String result;
    private StreamSpeechRecognizer streamSpeechRecognizer;
    private LiveSpeechRecognizer liveSpeechRecognizer;
    Thread speechThread;
    Thread resourcesThread;

    private boolean liveRec = false;

    private static final String ACOUSTIC_MODEL_PATH = "resource:/edu/cmu/sphinx/models/en-us/en-us";
    private static final String ACOUSTIC_MODEL_PATHru = "rus/ru"; //
    private static final String DICTIONARY_PATH = "resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict";
    private static final String DICTIONARY_PATHru = "rus/ru.dict";
    private static final String LANGMODEL_PATH = "resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin";
    private static final String LANGMODEL_PATHru = "rus/ru.dict";
    private static final String GRAMMAR_PATH = "resource/grammars";
    private static final String GRAMMAR = "grammar";
    private static final String GRAMMARru = "grammar_ru";

    public SphinxSpeechRecognizer() throws IOException {
        configureSphinxSpeechRecognizer();
    }

    private void configureSphinxSpeechRecognizer() throws IOException {
        logger.log(Level.INFO, "Loading..\n");

        Configuration configuration = new Configuration();

        // Load model from the jar
        configuration.setAcousticModelPath(ACOUSTIC_MODEL_PATH);
        configuration.setDictionaryPath(DICTIONARY_PATH);
        //configuration.setLanguageModelPath(LANGMODEL_PATH);

        // Grammar
        configuration.setGrammarPath(GRAMMAR_PATH);
        configuration.setGrammarName(GRAMMAR);
        configuration.setUseGrammar(true);


        if(liveRec){
            try {
                liveSpeechRecognizer = new LiveSpeechRecognizer(configuration);
            } catch (IOException ex) {
                logger.log(Level.SEVERE, null, ex);
            }

            liveSpeechRecognizer.startRecognition(true);
        }else{
            try {
                streamSpeechRecognizer = new StreamSpeechRecognizer(configuration);
            } catch (IOException ex) {
                logger.log(Level.SEVERE, null, ex);
            }

            InputStream file = new FileInputStream(new File("test.wav"));
            file.skip(44);


            streamSpeechRecognizer.startRecognition(file);
            SpeechResult result;
            String resultText = "";
            System.out.println("Date 1: "+new Date());
            while ((result = streamSpeechRecognizer.getResult()) != null) {
                resultText += result.getHypothesis();
            }
            System.out.println("Date 2: "+new Date());
            streamSpeechRecognizer.stopRecognition();
            System.out.println(resultText);
        }
    }

    public void beginRecording() {
        if(liveRec){
            startSpeechThread();
            startResourcesThread();
        }

    }

    /**
     * Starting the main Thread of speech recognition
     */
    protected void startSpeechThread() {
        // alive?
        if (speechThread != null && speechThread.isAlive())
            return;

        speechThread = new Thread(() -> {
            logger.log(Level.INFO, "You can start to speak...\n");
            try {
                while (true) {
                    /*
                     * This method will return when the end of speech is
                     * reached. Note that the end pointer will determine the end
                     * of speech.
                     */
                    SpeechResult speechResult = liveSpeechRecognizer.getResult();
                    if (speechResult != null) {
                        result = speechResult.getHypothesis();
                        System.out.println("You said: [" + result + "]\n");
                        // logger.log(Level.INFO, "You said: " + result + "\n")
                    } else
                        logger.log(Level.INFO, "I can't understand what you said.\n");
                }
            } catch (Exception ex) {
                logger.log(Level.WARNING, null, ex);
            }
            logger.log(Level.INFO, "SpeechThread has exited...");
        });
        speechThread.start();
    }

    /**
     * Starting a Thread that checks if the resources needed to the
     * SpeechRecognition library are available
     */
    protected void startResourcesThread() {

        // alive?
        if (resourcesThread != null && resourcesThread.isAlive())
            return;

        resourcesThread = new Thread(() -> {
            try {
                // Detect if the microphone is available
                while (true) {
                    if (AudioSystem.isLineSupported(Port.Info.MICROPHONE)) {
                          // logger.log(Level.INFO, "Microphone is available.\n");
                    } else {
                          // logger.log(Level.INFO, "Microphone is not available.\n");
                    }
                    Thread.sleep(350);
                }
            } catch (InterruptedException ex) {
                logger.log(Level.WARNING, null, ex);
                resourcesThread.interrupt();
            }
        });
        resourcesThread.start();
    }
}
