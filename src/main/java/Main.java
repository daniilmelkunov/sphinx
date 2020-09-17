import controllers.SphinxSpeechRecognizerController;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        SphinxSpeechRecognizerController.getInstance().startRecording();
    }

}
