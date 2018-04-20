package sample;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import com.vividsolutions.jts.simplify.DouglasPeuckerSimplifier;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import org.apache.commons.lang3.StringUtils;

import javax.sound.sampled.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;



public class Controller
{
    @FXML
    private Slider fqcSlider, durSlider, offsetSlider;
    @FXML
    private Label fqcLabel, fqcLabel2, durLabel, durLabel2, offsetLabel, offsetLabel2, scoreLabel, peakLabel, exactFrequencyLabel;
    @FXML
    private TextField fqcField, durField, offsetField, exactFrequencyField;
    @FXML
    private Button playButton, randomButton, exactButton;
    @FXML
    private LineChart resultsChart;
    @FXML
    private NumberAxis xAxis, yAxis;

    private DoubleProperty frequency = new SimpleDoubleProperty();
    private DoubleProperty exactFrequency = new SimpleDoubleProperty();
    private DoubleProperty duration = new SimpleDoubleProperty();
    private DoubleProperty offset = new SimpleDoubleProperty();

    private IntegerProperty correct = new SimpleIntegerProperty(0);

    private IntegerProperty total = new SimpleIntegerProperty(0);

    private AtomicBoolean tonePlaying = new AtomicBoolean(false);
    private AtomicBoolean isListening = new AtomicBoolean(false);

    private boolean isExact = false;

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

        exactFrequencyField.textProperty().addListener((observable, oldValue, newValue) ->
        {
            try
            {
                exactFrequency.setValue(Double.parseDouble(newValue));
                exactFrequencyLabel.setVisible(false);
            }
            catch (NumberFormatException e3)
            {
                if(e3.getLocalizedMessage().indexOf(',') >= 0)
                {
                    exactFrequencyLabel.setText("Jako desetinné znaménko použijte tečku.");
                    exactFrequencyLabel.setVisible(true);
                }
                else if(e3.getLocalizedMessage().contains("empty"))
                {
                    exactFrequencyLabel.setVisible(false);
                }
                else
                {
                    exactFrequencyLabel.setText("Zadejte číslo.");
                    exactFrequencyLabel.setVisible(true);
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

        randomButton.setOnAction(e ->
        {
            resultsChart.getData().clear();

            startListenThread();

            randomButton.setDisable(true);
            randomButton.setText("Listening...");
            randomButton.setStyle("-fx-font: 20 italic; -fx-font-style: italic");

            exactButton.setDisable(true);
            exactButton.setText("Listening...");
            exactButton.setStyle("-fx-font: 20 italic; -fx-font-style: italic");

            exactFrequencyField.setDisable(true);
        });

        exactButton.setOnAction(e ->
        {
            if(isListening.compareAndSet(false, true))
            {
                if(StringUtils.isNumeric(exactFrequencyField.getCharacters()))
                {
                    resultsChart.getData().clear();

                    isExact = true;

                    startListenThread();

                    randomButton.setDisable(true);
                    randomButton.setText("Listening...");
                    randomButton.setStyle("-fx-font: 20 italic; -fx-font-style: italic");

                    exactButton.setDisable(true);
                    exactButton.setText("Listening...");
                    exactButton.setStyle("-fx-font: 20 italic; -fx-font-style: italic");

                    exactFrequencyField.setDisable(true);
                }
                else
                {
                    WarningDialog w1 = new WarningDialog("Musíte zadat číslo.",
                            "Zadejte prosím číslo.");
                    w1.show();
                }
            }
        });

    }

    public void startListenThread()
    {
        int frequency;

        if(isExact)
            frequency = exactFrequency.intValue();
        else
            frequency = ThreadLocalRandom.current().nextInt(100, 500);

        int duration = 1500;

        InformationDialog i1 = new InformationDialog("Zazní tón."
                , "Nyní uslyšíte tón o náhodné frekvenci, snažte se ho napodobit hlasem, jak nejlépe umíte.");
        i1.show();

        Tone t1 = new Tone((float) frequency, (float) duration);
        try
        {
            t1.play();

        } catch (LineUnavailableException e)
        {
            ErrorDialog e1 = new ErrorDialog("Tón nelze přehrát"
                    , "Zkontrolujte si prosím nastavení vašeho přehrávače a zkuste to znovu.");
            e1.show();
            return;
        }

        Thread t2 = new Thread(() -> getTone(frequency));

        t2.setDaemon(true);
        t2.start();
    }

    public void getTone(int frequency)
    {

        final int DEF_BUFFER_SAMPLE_SZ = 48000;

        AudioFormat format = new AudioFormat(48000,16,2,true, true);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

        if(!AudioSystem.isLineSupported(info))
        {
            WarningDialog w = new WarningDialog("Line not supported."
            , "Zkontrolujte si prosím nastavení Vašeho mikrofonu." +
                    "\nPoužijte: \n\t48000Hz\n\t16bits\n\t2 channels");
            Platform.runLater(() -> w.show());
        }
        else
        {
            findHz finding = new findHz(DEF_BUFFER_SAMPLE_SZ, format, info);

            int result = 0;

            do
            {
                try
                {
                    finding.capture();
                }
                catch (LineUnavailableException e)
                {
                    e.printStackTrace();
                }

                result = finding.calculateFFT();

                final int finalResult = result;
                Platform.runLater(() ->
                {
                    peakLabel.setStyle("-fx-text-fill: rgba(255,0,0,0.45); -fx-font-style: italic; -fx-font-size: 24");
                    peakLabel.setText(finalResult + " Hz");
                });

            }
            while((result > 15000 || result < 60));

            final int finalResult = result;
            final int discrepancy = result - frequency;

            Platform.runLater(() ->
            {

                if(Math.abs(discrepancy) < 5)
                {
                    peakLabel.setStyle("-fx-text-fill: #069200; -fx-font-style: normal; -fx-font-size: 24");
                    peakLabel.setText("Dobrá práce!\nVáš tón: " + finalResult + " Hz"
                            + "\nTón, který byl přehráván: " + frequency + " Hz");
                }
                else
                {
                    peakLabel.setStyle("-fx-text-fill: #92000a; -fx-font-family: normal; -fx-font-size: 24");
                    peakLabel.setText("Váš tón: " + finalResult + " Hz"
                    + "\nTón, který byl přehráván: " + frequency + " Hz"
                    + "\nNevadí! Trénujte a příště se zadaří!");
                }

                buildRDPeucker(finding.MagX);

            });
        }

        Platform.runLater(() ->
        {
            randomButton.setText("Náhodně");
            randomButton.setStyle("-fx-font: 24 italic; -fx-font-style: normal; -fx-font-size: 24");
            randomButton.setDisable(false);

            exactButton.setText("Přesně");
            exactButton.setStyle("-fx-font: 24 italic; -fx-font-style: normal; -fx-font-size: 24");
            exactButton.setDisable(false);

            exactFrequencyField.setDisable(false);
        });

        isExact = false;

        isListening.compareAndSet(true, false);
    }

    public void buildRDPeucker(double[] MagX)
    {
        resultsChart.setAnimated(true);

        XYChart.Series<Number, Number> dataSeries = new XYChart.Series<>();

        GeometryFactory gf = new GeometryFactory();

        long t0 = System.nanoTime();
        Coordinate[] coordinates = new Coordinate[MagX.length];
        for(int i = 0; i < coordinates.length; i++)
        {
            coordinates[i] = new Coordinate(i, MagX[i]);
        }

        Geometry geom = new LineString(new CoordinateArraySequence(coordinates), gf);
        Geometry simplified = DouglasPeuckerSimplifier.simplify(geom, 2);

        List<XYChart.Data<Number, Number>> update = new ArrayList<>();
        for(Coordinate each : simplified.getCoordinates())
            update.add(new XYChart.Data<>(each.x, each.y));
        long t1 = System.nanoTime();

        System.out.println(String.format("Reduces points from %d to %d in %.1f ms", coordinates.length, update.size(),
                (t1 - t0) / 1e6));

        dataSeries.getData().addAll(update);
        resultsChart.getData().addAll(dataSeries);
    }

    /*
    This populates the chart with 96000 points. It is not reccomended to run this.

    public void buildChart(double[] MagX)
    {
        XYChart.Series<Integer, Double> dataSeries = new XYChart.Series<>();
        List<XYChart.Data<Integer, Double>> coordinates = new LinkedList<>();

        for(int i = 0; i < MagX.length; i++)
        {
            XYChart.Data<Integer, Double> data = new XYChart.Data<>(i, MagX[i]);
            coordinates.add(data);
        }

        dataSeries.getData().addAll(coordinates);

        System.out.println("Done adding data");
        System.out.println("Started populating chart.");
        resultsChart.getData().addAll(dataSeries);
        System.out.println("Chart is done.");


    }*/
}
