package view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MailClient extends Application {
    private String username;
    private ListView<String> emailListView;
    private int clientPort = 12346; // Port riêng cho mỗi client

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();

        // Các nút điều khiển
        Button inboxButton = new Button("Hộp thư đến");
        Button composeButton = new Button("Soạn thư");
        Button logoutButton = new Button("Đăng xuất");

        // Danh sách email và khu vực xem nội dung
        emailListView = new ListView<>();
        TextArea emailDetailArea = new TextArea();
        emailDetailArea.setEditable(false);

        // Bố cục các nút
        HBox topButtons = new HBox(10, inboxButton, composeButton, logoutButton);
        root.setTop(topButtons);
        root.setCenter(emailListView);
        root.setRight(emailDetailArea);

        // Hành động cho các nút
        inboxButton.setOnAction(e -> requestInbox());
        composeButton.setOnAction(e -> openComposeDialog());
        logoutButton.setOnAction(e -> Platform.exit());

        // Cài đặt giao diện
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setTitle("Mail Client - " + username);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Bắt đầu nhận email qua UDP
        new Thread(this::receiveEmails).start();
    }

    private void requestInbox() {
        // Gửi yêu cầu hộp thư đến tới server qua UDP
        try (DatagramSocket socket = new DatagramSocket()) {
            String request = "INBOX_REQUEST|" + username;
            byte[] buffer = request.getBytes();
            InetAddress address = InetAddress.getByName("10.60.226.31");
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, 12345); // UDP port của server
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openComposeDialog() {
        // Tạo cửa sổ soạn thư
        Stage composeStage = new Stage();
        composeStage.initModality(Modality.APPLICATION_MODAL);

        VBox composeLayout = new VBox(10);
        TextField recipientField = new TextField();
        recipientField.setPromptText("Người nhận");
        TextField subjectField = new TextField();
        subjectField.setPromptText("Tiêu đề");
        TextArea contentArea = new TextArea();
        contentArea.setPromptText("Nội dung email");

        Button sendButton = new Button("Gửi");
        sendButton.setOnAction(e -> sendEmail(recipientField.getText(), subjectField.getText(), contentArea.getText()));

        composeLayout.getChildren().addAll(recipientField, subjectField, contentArea, sendButton);
        Scene scene = new Scene(composeLayout, 300, 200);
        composeStage.setTitle("Soạn Thư");
        composeStage.setScene(scene);
        composeStage.show();
    }

    private void sendEmail(String recipient, String subject, String content) {
        // Gửi email tới server qua UDP
        try (DatagramSocket socket = new DatagramSocket()) {
            String emailData = username + "|" + recipient + "|" + subject + "|" + content;
            byte[] buffer = emailData.getBytes();
            InetAddress address = InetAddress.getByName("localhost");
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, 12345); // UDP port của server
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void receiveEmails() {
       
    }

    public static void main(String[] args) {
        launch(args);
    }
}