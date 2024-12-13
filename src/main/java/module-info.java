module org.example.sistemagestionempleados {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml.bind;
    requires com.fasterxml.jackson.databind;
    requires java.activation;
    requires com.google.gson;

    opens org.example.sistemagestionempleados to javafx.fxml;
    exports org.example.sistemagestionempleados;
}