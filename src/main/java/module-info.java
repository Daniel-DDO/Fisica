module br.com.fisica.basicas {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;


    opens br.com.fisica.basicas to javafx.fxml;
    exports br.com.fisica.basicas;
    opens br.com.fisica.gui to javafx.fxml;
    exports br.com.fisica.gui;
}