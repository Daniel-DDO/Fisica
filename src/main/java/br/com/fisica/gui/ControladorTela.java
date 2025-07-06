package br.com.fisica.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import lombok.Getter;
import lombok.Setter;

import java.net.URL;
import java.util.ResourceBundle;

@Getter
@Setter
public class ControladorTela implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        inicializarCbox();
    }

    private static ControladorTela instancia;

    public static ControladorTela getInstancia() {
        if (instancia == null) {
            instancia = new ControladorTela();
        }
        return instancia;
    }

    Image circuitoCAlternada = new Image(getClass().getResourceAsStream("/br/com/fisica/images/C-Alternada.png"));

    Image circuitoCContinua = new Image(getClass().getResourceAsStream("/br/com/fisica/images/C-Continua.png"));

    @FXML
    private ImageView imageViewCircuito;

    @FXML
    private ComboBox<String> cboxSelecionarFonte;

    @FXML
    private Button confirmarFonteButton;

    @FXML
    private Label atualFonteLabel;

    @FXML
    private TextField capacitanciaField;

    @FXML
    private TextField indutanciaField;

    @FXML
    private TextField resistenciaField;

    public void inicializarCbox() {
        cboxSelecionarFonte.getItems().add("Corrente Contínua (CC)");
        cboxSelecionarFonte.getItems().add("Corrente Alternada (CA)");
    }

    public void selecionarCircuito() {
        if (cboxSelecionarFonte.getValue() != null) {
            if (cboxSelecionarFonte.getValue().equals("Corrente Contínua (CC)")) {
                imageViewCircuito.setImage(circuitoCContinua);
                atualFonteLabel.setText("Selecionado: Corrente Contínua (CC)");
                System.out.println("continua");
            } else if (cboxSelecionarFonte.getValue().equals("Corrente Alternada (CA)")) {
                imageViewCircuito.setImage(circuitoCAlternada);
                atualFonteLabel.setText("Selecionado: Corrente Alternada (CA)");
                System.out.println("alternada");
            }
        }
    }

    @FXML
    void buttonConfirmarFonte(ActionEvent event) {
        selecionarCircuito();
    }

}
