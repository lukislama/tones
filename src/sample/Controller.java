package sample;

import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Controller
{
    @FXML
    private Slider fqcSlider, durSlider;
    @FXML
    private Label fqcLabel, fqcLabel2, durLabel, durLabel2;
    @FXML
    private TextField fqcField, durField;

    private DoubleProperty frequency = new SimpleDoubleProperty();
    private DoubleProperty duration = new SimpleDoubleProperty();


    @FXML
    public void initialize()
    {
        bootup();
    }

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
    }
}
