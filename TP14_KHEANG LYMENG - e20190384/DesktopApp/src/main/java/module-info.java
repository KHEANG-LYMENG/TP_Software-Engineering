module itc.gic.i4a {
    requires javafx.controls;
    requires javafx.fxml;

    opens itc.gic.i4a to javafx.fxml;
    exports itc.gic.i4a;
}
