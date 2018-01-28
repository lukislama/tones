package sample;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class Controller
{
    @FXML
    private Slider fqcSlider, durSlider, offsetSlider;
    @FXML
    private Label fqcLabel, fqcLabel2, durLabel, durLabel2, offsetLabel, offsetLabel2, scoreLabel;
    @FXML
    private TextField fqcField, durField, offsetField;
    @FXML
    private Button playButton, listenButton;

    private DoubleProperty frequency = new SimpleDoubleProperty();
    private DoubleProperty duration = new SimpleDoubleProperty();
    private DoubleProperty offset = new SimpleDoubleProperty();

    private IntegerProperty correct = new SimpleIntegerProperty(0);

    private IntegerProperty total = new SimpleIntegerProperty(0);

    private AtomicBoolean tonePlaying = new AtomicBoolean(false);

    @FXML
    public void initialize()
    {
        bootup();
    }

    public void playTones() throws LineUnavailableException
    {

        Random r1 = new Random();

        boolean order = r1.nextDouble() > 0.5;
        int higher;
        float frequency = this.frequency.floatValue();
        float duration = this.duration.floatValue()*1000;

        Tone t1 = new Tone(frequency, duration);

        if(r1.nextDouble() > 0.5)
        {
            frequency += offset.floatValue();
            higher = 2;
        }

        else
        {
            frequency -= offset.floatValue();
            higher = 1;
        }


        Tone t2 = new Tone(frequency, duration);

        if(order)
        {
            t1.play();
            t2.play();
        }
        else
        {
            t2.play();
            t1.play();
        }


        if(t1.hz != t2.hz)
        {
            if(order && higher == 1)
                checkScore(1);
            else if(order && higher == 2)
                checkScore(2);
            else if(!order && higher == 1)
                checkScore(2);
            else if(!order && higher == 2)
                checkScore(1);
        }
        else
        {
            Platform.runLater(() ->
            {
                total.setValue(total.getValue() - 1);
            });
        }

    }

    public void checkScore(int order)
    {
        Platform.runLater(() ->
        {
            ConfirmationDialog c1 = new ConfirmationDialog();
            if(c1.show() == order)
                correct.setValue(correct.getValue() + 1);
        });
    }

    public void startToneThread()
    {

        Thread t1 = new Thread(() ->
        {
            try
            {
                playTones();
            }
            catch (LineUnavailableException e)
            {
                e.printStackTrace();
            }

            tonePlaying.set(false);

            Platform.runLater(() ->
            {
                playButton.setText("Play!");
                playButton.setStyle("-fx-font: 24 italic; -fx-font-style: normal;");
                playButton.setDisable(false);
            });
        });

        t1.setDaemon(true);
        t1.start();
    }

    @SuppressWarnings("Duplicates")
    private void bootup()
    {
        fqcSlider.valueProperty().bindBidirectional(frequency);
        fqcSlider.valueProperty().addListener((observable, oldValue, newValue) ->
        {
            fqcLabel.setText("Frekvence: " + new BigDecimal(newValue.doubleValue()).setScale(2, RoundingMode.HALF_UP).doubleValue() + " Hz");
            fqcField.setPromptText(new BigDecimal(newValue.doubleValue()).setScale(2, RoundingMode.HALF_UP).toString());
        });

        fqcField.textProperty().addListener((observable, oldValue, newValue) ->
        {
            try
            {
                frequency.setValue(Double.parseDouble(newValue));
                fqcLabel2.setVisible(false);
            }
            catch (NumberFormatException e)
            {
                if(e.getLocalizedMessage().indexOf(',') >= 0)
                {
                    fqcLabel2.setText("Jako desetinné znamínko použijte tečku.");
                    fqcLabel2.setVisible(true);
                }

                else if(e.getLocalizedMessage().contains("empty"))
                {
                    fqcLabel2.setVisible(false);
                }
                else
                {
                    fqcLabel2.setText("Zadejte číslo.");
                    fqcLabel2.setVisible(true);
                }

            }
        });


        durSlider.valueProperty().bindBidirectional(duration);
        durSlider.valueProperty().addListener(((observable, oldValue, newValue) ->
        {
            durLabel.setText("Doba trvání: " + new BigDecimal(newValue.doubleValue()).setScale(2, RoundingMode.HALF_UP).doubleValue() + " s");
            durField.setPromptText(new BigDecimal(newValue.doubleValue()).setScale(2, RoundingMode.HALF_UP).toString());
        }));

        durField.textProperty().addListener((observable, oldValue, newValue) ->
        {
            try
            {
                duration.setValue(Double.parseDouble(newValue));
                durLabel2.setVisible(false);
            }
            catch (NumberFormatException e1)
            {
                if (e1.getLocalizedMessage().indexOf(',') >= 0)
                {
                    durLabel2.setText("Jako desetinné znaménko použijte tečku.");
                    durLabel2.setVisible(true);
                }
                else if(e1.getLocalizedMessage().contains("empty"))
                {
                    durLabel2.setVisible(false);
                }
                else
                {
                    durLabel2.setText("Zadejte číslo.");
                    durLabel2.setVisible(true);
                }
            }
        });

        offsetSlider.valueProperty().bindBidirectional(offset);
        offsetSlider.valueProperty().addListener(((observable, oldValue, newValue) ->
        {
            offsetLabel.setText("Offset: " + new BigDecimal(newValue.doubleValue()).setScale(2, RoundingMode.HALF_UP).doubleValue() + " Hz");
            offsetField.setPromptText(new BigDecimal(newValue.doubleValue()).setScale(2, RoundingMode.HALF_UP).toString());
        }));

        offsetField.textProperty().addListener((observable, oldValue, newValue) ->
        {
            try
            {
                offset.setValue(Double.parseDouble(newValue));
                offsetLabel2.setVisible(false);
            }
            catch (NumberFormatException e2)
            {
                if (e2.getLocalizedMessage().indexOf(',') >= 0)
                {
                    offsetLabel2.setText("Jako desetinné znaménko použijte tečku.");
                    offsetLabel2.setVisible(true);
                }
                else if(e2.getLocalizedMessage().contains("empty"))
                {
                    offsetLabel2.setVisible(false);
                }
                else
                {
                    offsetLabel2.setText("Zadejte číslo.");
                    offsetLabel2.setVisible(true);
                }
            }
        });

        playButton.setOnAction(e ->
        {
            if(tonePlaying.compareAndSet(false, true))
            {
                if(fqcSlider.getValue() > 0
                        && durSlider.getValue() > 0)
                {
                    playButton.setDisable(true);
                    playButton.setText("Playing...");
                    playButton.setStyle("-fx-font: 20 italic; -fx-font-style: italic");

                    total.setValue(total.getValue() + 1);
                    startToneThread();
                }
                else
                {
                    tonePlaying.compareAndSet(true, false);
                    Platform.runLater(() ->
                    {
                        WarningDialog w1 = new WarningDialog("Frekvence nebo doba trvání chybí."
                                , "Musíte zadat platnou hodnotu pro frekvenci a dobu trvání.");
                        w1.show();
                    });
                }

            }
            else
            {
                WarningDialog w1 = new WarningDialog("Tón už hraje.", "Tón už hraje. Vždy může hrát maximálně jeden tón.");
                w1.show();
            }
        });

        total.addListener((observable, oldValue, newValue) ->
        {
            scoreLabel.setText("Skóre: " + correct.getValue() + "/" + newValue);
        });

        correct.addListener((observable, oldValue, newValue) ->
        {
            scoreLabel.setText("Skóre: " + newValue + "/" + total.getValue());
        });

        //Second Pane starts here

        listenButton.setOnAction(e ->
        {
            startListenThread();
        });

    }

    public void startListenThread()
    {
        Thread t2 = new Thread(() ->
        {
            getTone();
        });

        t2.setDaemon(true);
        t2.start();
    }

    public void getTone()
    {

        final int DEF_BUFFER_SAMPLE_SZ = 8100;

        /*
        AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
        TargetDataLine line;
        AudioFormat format = new AudioFormat(48000,24,2,true, true);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

        if(!AudioSystem.isLineSupported(info))
        {
            WarningDialog w = new WarningDialog("Line not supported."
            , "Váš počítač nesplňuje požadavky pro tuto funkcionalitu.");
        }
        else
        {
            try
            {
                line = (TargetDataLine) AudioSystem.getLine(info);
                line.open(format);
                line.start();

                System.out.println("Started capturing");
            }
            catch (LineUnavailableException e)
            {
                e.printStackTrace();
            }

        }
        */

        File audioFile = new File(this.getClass().getResource("1000.wav").getPath());
        AudioInputStream in = null;

        try
        {
            final AudioFormat audioFormat = (AudioSystem.getAudioFileFormat(audioFile).getFormat());

            in = AudioSystem.getAudioInputStream(audioFile);

            final int normalBytes = Fft.normalBytesFromBits(audioFormat.getSampleSizeInBits());

            float[] samples = new float[DEF_BUFFER_SAMPLE_SZ * audioFormat.getChannels()];
            long[] transfer = new long[samples.length];
            byte[] bytes = new byte[samples.length * normalBytes];
            double[] MagXSorted = new double[DEF_BUFFER_SAMPLE_SZ];
            ArrayList<Double> peaks = new ArrayList<>();
            ArrayList<Integer> peakIndex = new ArrayList<>();

            int bread;
           /* while(true)
            {
                if((bread = in.read(bytes)) == -1)
                    break;

*/              bread = in.read(bytes);

                Object[] fft = Fft.fft(bytes, transfer, samples, bread, audioFormat);

                double[] real = (double[]) fft[0];
                double[] imag = (double[]) fft[1];
                
                Object[] polarCoord = Fft.convertToPolar(real, imag);

                double[] MagX = (double[]) polarCoord[0];
                double[] PhaseX = (double[]) polarCoord[1];

                double peak = -1.0;
                int index = -1;

                for(int i = 0; i < MagX.length; i++)
                {
                    if(MagX[i] > peak)
                    {
                        peak = MagX[i];
                        index = i;
                    }
                }

                System.out.println(index);

            // }

            in.close();

        }
        catch (UnsupportedAudioFileException | IOException e)
        {
            e.printStackTrace();
        }

    }

}
