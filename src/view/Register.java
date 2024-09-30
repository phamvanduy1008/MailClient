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

public class Register extends Application {

    private TextField tenDangNhapField;
    private PasswordField matKhauField;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        tenDangNhapField = new TextField();
        tenDangNhapField.setPromptText("Tên đăng nhập");
        matKhauField = new PasswordField();
        matKhauField.setPromptText("Mật khẩu");

        Button dangKyBtn = new Button("Đăng ký");
        Button dangNhapBtn = new Button("Đăng nhập");

        dangKyBtn.setOnAction(e -> dangKyNguoiDung());
        dangNhapBtn.setOnAction(e -> moTrangDangNhap());

        VBox layout = new VBox(10, tenDangNhapField, matKhauField, dangKyBtn, dangNhapBtn);
        Scene scene = new Scene(layout, 300, 200);

        primaryStage.setTitle("Đăng ký");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void dangKyNguoiDung() {
        String tenDangNhap = tenDangNhapField.getText();
        String matKhau = matKhauField.getText();

        try (Socket socket = new Socket("localhost", 12345);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println("REGISTER " + tenDangNhap + " " + matKhau);
            String phanHoi = in.readLine();
            if (phanHoi.startsWith("SUCCESS")) {
                System.out.println("Đăng ký thành công!");
            } else {
            	log("Đăng ký thất bại: " + phanHoi);
            }
        } catch (IOException e) {
        	log("Lỗi: " + e.getMessage());
        }
    }

    private void moTrangDangNhap() {
        Login dangNhap = new Login();
        Stage dangNhapStage = new Stage();
        try {
            dangNhap.start(dangNhapStage);
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
