module com.example.renatojava.javasemester {
    requires javafx.controls;
    requires javafx.fxml;
            
                            
    opens com.example.renatojava.javasemester to javafx.fxml;
    exports com.example.renatojava.javasemester;
}