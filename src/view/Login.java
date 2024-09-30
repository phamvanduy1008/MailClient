package view;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

public class Login extends Application {

    private TextField usernameField;
    private PasswordField passwordField;

    public static void main(String[] args) {
        launch(args);
    }
 
    @Override
    public void start(Stage primaryStage) {
        usernameField = new TextField();
        usernameField.setPromptText("Tên đăng nhập");
        passwordField = new PasswordField();
        passwordField.setPromptText("Mật khẩu");

        Button loginBtn = new Button("Đăng nhập");
        Button registerBtn = new Button("Đăng ký");

        loginBtn.setOnAction(e -> loginUser(primaryStage));
        registerBtn.setOnAction(e -> openRegisterPage());

        VBox layout = new VBox(10, usernameField, passwordField, loginBtn, registerBtn);
        Scene scene = new Scene(layout, 300, 200);

        primaryStage.setTitle("Đăng nhập");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loginUser(Stage primaryStage) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try (Socket socket = new Socket("localhost", 12345);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println("LOGIN " + username + " " + password);
            String response = in.readLine();
            if (response.startsWith("SUCCESS")) {
                log("Đăng nhập thành công!");
                
                // Khởi động MailClient và truyền username
                MailClient mailClient = new MailClient();
                mailClient.setUsername(username); // Thiết lập username
                Stage mailStage = new Stage();
                mailClient.start(mailStage); // Mở cửa sổ mail

                primaryStage.close(); // Đóng cửa sổ đăng nhập
            } else {
                log("Đăng nhập thất bại: " + response);
            }
        } catch (IOException e) {
            log("Lỗi: " + e.getMessage());
        }
    }

    private void openRegisterPage() {
        Register register = new Register();
        Stage registerStage = new Stage();
        try {
            register.start(registerStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void log(String text) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Log");
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
    }
}