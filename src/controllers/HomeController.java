package controllers;

import enums.RepositoryType;
import factory.RepositoryFactory;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import model.Table;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import java.util.Optional;
import javafx.scene.image.Image;
import javafx.application.Platform;




public class HomeController implements Initializable {
    public ImageView imageView1;
    public Label nameimg1;
    public Label nameimg2;
    public Label nameimg3;
    public Label nameimg4;
    public Label nameimg5;
    public Label nameimg6;
    public Label nameimg7;
    public Label nameimg8;
    public Label nameimg9;
    public ImageView imageView2;
    public ImageView imageView3;
    public ImageView imageView4;
    public ImageView imageView5;
    public ImageView imageView6;
    public ImageView imageView7;
    public ImageView imageView8;
    public ImageView imageView9;
    public Button btnJuices;
    public Button btnFruits;
    public Button btnMilktea;
    public Button btnYogurt;
    public Button btnPastry;
    @FXML
    private TableView<Table> tbv;
    @FXML
    private TableColumn<Table, String> colName;
    @FXML
    private TableColumn<Table, Double> colPrice;
    @FXML
    private TableColumn<Table, Integer> colQty;
    @FXML
    private TableColumn<Table, Double> colTotalPrice;
    @FXML
    private Label qty1;
    @FXML
    private Label qty2;
    @FXML
    private Label qty3;
    @FXML
    private Label qty4;
    @FXML
    private Label qty5;
    @FXML
    private Label qty6;
    @FXML
    private Label qty7;
    @FXML
    private Label qty8;
    @FXML
    private Label qty9;
    @FXML
    private Label total;
    @FXML
    private Label totalTextField;


    @FXML
    private TableColumn<Table, String> colDelete;
    private static Table selectedTable;


    private ObservableList<Table> list = FXCollections.observableArrayList();
    private static Table btnDelete;

    public void addTable(String name, Double price, int qty) {
        // Check if the product is already in the table
        for (Table table : list) {
            if (table.getName().equals(name)) {
                // Update the quantity and total price of the existing product
                int currentQty = Integer.parseInt(table.getQty());
                int newQty = currentQty + qty;
                table.setQty(newQty);
                table.setTotalPrice(price * newQty);
                tbv.refresh();
                updateTotalProduct();
                calculateTotalPrice();

                return;
            }
        }

        // Add the new product to the table
        Table tb = new Table(name, price, qty);
        list.add(tb);
        tbv.setItems(list);
        updateTotalProduct();

        calculateTotalPrice(); // Cập nhật giá trị tổng sau khi thêm sản phẩm mới
    }


    public void addToTable(MouseEvent mouseEvent, String productName, Label quantityLabel, int quantityMultiplier) {
        try {
            String text = quantityLabel.getText().replace("$", "");
            Double price = Double.parseDouble(text);
            boolean productExists = false;

            // Check if the product is already in the table
            for (Table table : list) {
                if (table.getName().equals(productName)) {
                    // Update the quantity and total price of the existing product
                    int currentQty = Integer.parseInt(table.getQty());
                    int newQty = currentQty + quantityMultiplier;
                    table.setQty(newQty);
                    table.setTotalPrice(price * newQty);
                    tbv.refresh();
                    updateTotalProduct();
                    calculateTotalPrice();
                    productExists = true;
                    break;
                }
            }

            if (!productExists) {
                addTable(productName, price, quantityMultiplier);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    public void updateTotalProduct() {
        int totalProduct = 0;
        double totalPrice = 0;
        for (Table table : list) {
            totalProduct += Integer.parseInt(table.getQty());
            totalPrice += table.getTotalPrice();
        }


        totalTextField.setText(String.format("%.2f", totalPrice));
        calculateTotalPrice();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("qty"));
        colTotalPrice.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        tbv.setOnMouseClicked(this::editTableRow);
        totalTextField.setText("0.00");


        colPrice.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrice()).asObject());
        colPrice.setCellFactory(column -> new TableCell<Table, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    calculateTotalPrice();
                } else {
                    setText(String.format("%.2f$", item));
                }
                calculateTotalPrice();
            }
        });

        colTotalPrice.setCellFactory(column -> new TableCell<Table, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(String.format("%.2f$", item));
                }
                calculateTotalPrice();
            }
        });

        // Thêm chức năng xóa vào cột

        colDelete.setCellFactory(column -> {
            TableCell<Table, String> cell = new TableCell<Table, String>() {
                private final Button deleteButton = new Button("Xóa");

                {
                    deleteButton.setOnAction(event -> {
                        Table data = getTableView().getItems().get(getIndex());
                        list.remove(data);
                        updateTotalProduct();
                        calculateTotalPrice();

                    });
                }

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(deleteButton);
                    }
                }
            };
            return cell;
        });


        try {
            list.addAll(RepositoryFactory.createRepositoryInstance(RepositoryType.TABLE).getAll());
            updateTotalProduct();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    @FXML
    public void addToTable1(MouseEvent mouseEvent) {
        addToTable(mouseEvent, nameimg1.getText(), qty1, 1);

    }

    @FXML
    public void addToTable2(MouseEvent mouseEvent) {
        addToTable(mouseEvent, nameimg2.getText(), qty2, 1);

    }

    public void addToTable3(MouseEvent mouseEvent) {
        addToTable(mouseEvent, nameimg3.getText(), qty3, 1);

    }

    public void addToTable4(MouseEvent mouseEvent) {
        addToTable(mouseEvent, nameimg4.getText(), qty4, 1);


    }

    public void addToTable5(MouseEvent mouseEvent) {
        addToTable(mouseEvent, nameimg5.getText(), qty5, 1);

    }

    public void addToTable6(MouseEvent mouseEvent) {
        addToTable(mouseEvent, nameimg6.getText(), qty6, 1);
    }

    public void addToTable7(MouseEvent mouseEvent) {
        addToTable(mouseEvent, nameimg7.getText(), qty7, 1);

    }

    public void addToTable8(MouseEvent mouseEvent) {
        addToTable(mouseEvent, nameimg8.getText(), qty8, 1);

    }

    public void addToTable9(MouseEvent mouseEvent) {
        addToTable(mouseEvent, nameimg9.getText(), qty9, 1);

    }

    private void calculateTotalPrice() {
        double totalPrice = 0;
        for (Table table : list) {
            totalPrice += table.getTotalPrice();
        }
        totalTextField.setText(String.format("%.2f$", totalPrice));
    }

    @FXML
    public void editTableRow(MouseEvent event) {
        if (event.getClickCount() == 2) { // Kiểm tra xem đã nhấp đúp chuột vào hàng sản phẩm chưa
            selectedTable = tbv.getSelectionModel().getSelectedItem(); // Lấy hàng sản phẩm đang được chọn
            if (selectedTable != null) {
                // Hiển thị hộp thoại để sửa thông tin
                TextInputDialog dialog = new TextInputDialog(selectedTable.getQty());
                dialog.setTitle("Sửa hàng sản phẩm");
                dialog.setHeaderText(null);
                dialog.setContentText("Số lượng hàng sản phẩm:");
                Optional<String> result = dialog.showAndWait();

                result.ifPresent(qty -> {
                    try {
                        int newQty = Integer.parseInt(qty);
                        selectedTable.setQty(newQty);
                        selectedTable.setTotalPrice(selectedTable.getPrice() * newQty);
                        tbv.refresh();

                    } catch (NumberFormatException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    calculateTotalPrice();
                });
            }
        }
    }

    @FXML
    public void Payment(ActionEvent actionEvent) {
        String total = totalTextField.getText();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/pay.fxml"));
            Parent root = loader.load();
            PayContronller pc = loader.getController(); // Lấy tham chiếu đến PayController đã tạo từ FXML
            pc.setTotal(total);


            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();


            // Đóng cửa sổ hiện tại (nếu cần)
            Stage currentStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            currentStage.close();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    public void Exit(ActionEvent actionEvent) {
        // Đóng cửa sổ hiện tại
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.close();

        // Mở trang login.fxml
        try {
            Parent root = FXMLLoader.load(getClass().getResource("../views/login.fxml"));
            Stage loginStage = new Stage();
            loginStage.setScene(new Scene(root));
            loginStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Cancelorder(ActionEvent actionEvent) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Xác nhận hủy đơn hàng");
        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText("Bạn có chắc chắn muốn hủy đơn hàng và xóa tất cả các sản phẩm đã thêm?");

        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            list.clear();
            tbv.refresh();
            updateTotalProduct();
            calculateTotalPrice();
        }
    }


    public void juices(ActionEvent actionEvent) {
        Image image1 = new Image("img/l33.jpeg");
             imageView1.setImage(image1);
             nameimg1.setText("Strawberry Whip Juice");
             qty1.setText("3$");
        Image image2 = new Image("img/l34.jpeg");
             imageView2.setImage(image2);
             nameimg2.setText("Grapefruit Juice");
             qty2.setText("4$");
        Image image3 = new Image("img/l35.jpeg");
             imageView3.setImage(image3);
             nameimg3.setText("Whip Juice");
             qty3.setText("2$");
        Image image4 = new Image("img/l36.jpeg");
             imageView4.setImage(image4);
             nameimg4.setText("Pineapple Orange Juice");
             qty4.setText("4$");
        Image image5 = new Image("img/l37.jpeg");
             imageView5.setImage(image5);
             nameimg5.setText("Watermelon juice");
             qty5.setText("5$");
        Image image6 = new Image("img/l38.jpeg");
             imageView6.setImage(image6);
             nameimg6.setText("Orange juice");
             qty6.setText("2$");
        Image image7 = new Image("img/l39.jpeg");
             imageView7.setImage(image7);
             nameimg7.setText("Toad Guava Juice");
             qty7.setText("4$");
        Image image8 = new Image("img/l40.jpeg");
             imageView8.setImage(image8);
             nameimg8.setText("Pineapple Juice");
             qty8.setText("6$");
        Image image9 = new Image("img/l41.jpeg");
             imageView9.setImage(image9);
             nameimg9.setText("Toad juice");
             qty9.setText("3$");

        btnJuices.setStyle("-fx-background-color: #4e2a84; -fx-text-fill: white;-fx-background-radius:10;");
        btnFruits.setStyle("-fx-background-color: white; -fx-text-fill: black;-fx-background-radius:10;");
        btnMilktea.setStyle("-fx-background-color: white; -fx-text-fill: black;-fx-background-radius:10;");
        btnYogurt.setStyle("-fx-background-color: white; -fx-text-fill: black;-fx-background-radius:10;");
        btnPastry.setStyle("-fx-background-color: white; -fx-text-fill: black;-fx-background-radius:10;");

    }

    public void fruits(ActionEvent actionEvent) {

        btnJuices.setStyle("-fx-background-color: white; -fx-text-fill: black;-fx-background-radius:10;");
        btnFruits.setStyle("-fx-background-color:  #4e2a84; -fx-text-fill: white;-fx-background-radius:10;");
        btnMilktea.setStyle("-fx-background-color: white; -fx-text-fill: black;-fx-background-radius:10;");
        btnYogurt.setStyle("-fx-background-color: white; -fx-text-fill: black;-fx-background-radius:10;");
        btnPastry.setStyle("-fx-background-color: white; -fx-text-fill: black;-fx-background-radius:10;");

        Image image1 = new Image("img/l25.jpeg");

             imageView1.setImage(image1);
             nameimg1.setText("Four Quarter Mangoes");
             qty1.setText("2$");

        Image image2 = new Image("img/l26.jpeg");

             imageView2.setImage(image2);
             nameimg2.setText("Green grapefruit");
             qty2.setText("1$");

        Image image3 = new Image("img/l27.jpeg");

             imageView3.setImage(image3);
             nameimg3.setText("Cucumber");
             qty3.setText("2$");

        Image image4 = new Image("img/l28.jpeg");

             imageView4.setImage(image4);
             nameimg4.setText("Stomach Mango");
             qty4.setText("3$");

        Image image5 = new Image("img/l29.jpeg");

             imageView5.setImage(image5);
             nameimg5.setText("Toad Stomach");
             qty5.setText("1$");

        Image image6 = new Image("img/l30.jpeg");

             imageView6.setImage(image6);
             nameimg6.setText("Rod");
             qty6.setText("2$");

        Image image7 = new Image("img/l31.jpeg");

             imageView7.setImage(image7);
             nameimg7.setText("Pickled plums");
             qty7.setText("1$");

        Image image8 = new Image("img/a1.jfif");

        imageView8.setImage(image8);
        nameimg8.setText("");
        qty8.setText("");

        Image image9 = new Image("img/a1.jfif");
        imageView9.setImage(image9);
        nameimg9.setText("");
        qty9.setText("");

    }

    public void Milktea(ActionEvent actionEvent) {

        btnJuices.setStyle("-fx-background-color: white; -fx-text-fill: black;-fx-background-radius:10;");
        btnFruits.setStyle("-fx-background-color: white; -fx-text-fill: black;-fx-background-radius:10;");
        btnMilktea.setStyle("-fx-background-color:#4e2a84; -fx-text-fill: white;-fx-background-radius:10;");
        btnYogurt.setStyle("-fx-background-color: white; -fx-text-fill: black;-fx-background-radius:10;");
        btnPastry.setStyle("-fx-background-color: white; -fx-text-fill: black;-fx-background-radius:10;");

        Image image1 = new Image("img/l17.jpg");

        imageView1.setImage(image1);
        nameimg1.setText("Black Pearl Milk Tea");
        qty1.setText("3$");

        Image image2 = new Image("img/l18.jpg");

        imageView2.setImage(image2);
        nameimg2.setText("Oolong Milk Tea");
        qty2.setText("1$");

        Image image3 = new Image("img/l19.jpg");

        imageView3.setImage(image3);
        nameimg3.setText("Green Tea Milk Tea");
        qty3.setText("1$");

        Image image4 = new Image("img/l20.jpg");

        imageView4.setImage(image4);
        nameimg4.setText("Hokkaido Milk Tea");
        qty4.setText("4$");

        Image image5 = new Image("img/l21.jpg");

        imageView5.setImage(image5);
        nameimg5.setText("Okinawa Milk Tea");
        qty5.setText("2$");

        Image image6 = new Image("img/l22.jpg");

        imageView6.setImage(image6);
        nameimg6.setText("Black Tea Milk Tea");
        qty6.setText("4$");

        Image image7 = new Image("img/l23.jpg");

        imageView7.setImage(image7);
        nameimg7.setText("Chocolate Milk Tea");
        qty7.setText("1$");

        Image image8 = new Image("img/l24.jpg");

        imageView8.setImage(image8);
        nameimg8.setText("Taro Milk Tea");
        qty8.setText("4$");

        Image image9 = new Image("img/a1.jfif");
        imageView9.setImage(image9);
        nameimg9.setText("");
        qty9.setText("");

    }

    public void YOGURT(ActionEvent actionEvent) {
        btnJuices.setStyle("-fx-background-color: white; -fx-text-fill: black;-fx-background-radius:10;");
        btnFruits.setStyle("-fx-background-color: white; -fx-text-fill: black;-fx-background-radius:10;");
        btnMilktea.setStyle("-fx-background-color:white; -fx-text-fill: black;-fx-background-radius:10;");
        btnYogurt.setStyle("-fx-background-color: #4e2a84; -fx-text-fill: white;-fx-background-radius:10;");
        btnPastry.setStyle("-fx-background-color: white; -fx-text-fill: black;-fx-background-radius:10;");

        Image image1 = new Image("img/l8.jpg");

        imageView1.setImage(image1);
        nameimg1.setText("Sticky Jackfruit Yogurt Tea");
        qty1.setText("2$");

        Image image2 = new Image("img/l9.jpg");

        imageView2.setImage(image2);
        nameimg2.setText("Jackfruit Yogurt Tea");
        qty2.setText("1$");

        Image image3 = new Image("img/l10.jpg");

        imageView3.setImage(image3);
        nameimg3.setText("Mixed Yogurt Tea");
        qty3.setText("3$");

        Image image4 = new Image("img/l11.jpg");

        imageView4.setImage(image4);
        nameimg4.setText("Sticky Cam Yogurt Tea");
        qty4.setText("4$");

        Image image5 = new Image("img/l12.jpg");

        imageView5.setImage(image5);
        nameimg5.setText("Leo Lemon Yogurt");
        qty5.setText("2$");

        Image image6 = new Image("img/l13.jpg");

        imageView6.setImage(image6);
        nameimg6.setText("Pearl Coconut Milk");
        qty6.setText("3$");

        Image image7 = new Image("img/l14.jpg");

        imageView7.setImage(image7);
        nameimg7.setText("Thai Yogurt Tea");
        qty7.setText("1$");

        Image image8 = new Image("img/l15.jpg");

        imageView8.setImage(image8);
        nameimg8.setText("Coffee Yogurt");
        qty8.setText("5$");

        Image image9 = new Image("img/l16.jpg");
        imageView9.setImage(image9);
        nameimg9.setText("Strawberry Yogurt");
        qty9.setText("4$");
    }

    public void pastry(ActionEvent actionEvent) {
        btnJuices.setStyle("-fx-background-color: white; -fx-text-fill: black;-fx-background-radius:10;");
        btnFruits.setStyle("-fx-background-color: white; -fx-text-fill: black;-fx-background-radius:10;");
        btnMilktea.setStyle("-fx-background-color:white; -fx-text-fill: black;-fx-background-radius:10;");
        btnYogurt.setStyle("-fx-background-color: white; -fx-text-fill: black;-fx-background-radius:10;");
        btnPastry.setStyle("-fx-background-color: #4e2a84; -fx-text-fill: white;-fx-background-radius:10;");

        Image image1 = new Image("img/l1.jpeg");

        imageView1.setImage(image1);
        nameimg1.setText("Almond Tile Cake");
        qty1.setText("2$");

        Image image2 = new Image("img/l2.jpeg");

        imageView2.setImage(image2);
        nameimg2.setText("Chocolate biscuits");
        qty2.setText("4$");

        Image image3 = new Image("img/l3.jpeg");

        imageView3.setImage(image3);
        nameimg3.setText("Tiramisu Cacao");
        qty3.setText("3$");

        Image image4 = new Image("img/l4.jpeg");

        imageView4.setImage(image4);
        nameimg4.setText("Leo Lemon Mousse");
        qty4.setText("1$");

        Image image5 = new Image("img/l5.jpeg");

        imageView5.setImage(image5);
        nameimg5.setText("Oreo cake");
        qty5.setText("3$");

        Image image6 = new Image("img/l6.jpeg");

        imageView6.setImage(image6);
        nameimg6.setText("Milk Tea Cake");
        qty6.setText("2$");

        Image image7 = new Image("img/l7.jpeg");

        imageView7.setImage(image7);
        nameimg7.setText("Tiramisu Matcha");
        qty7.setText("1$");

        Image image8 = new Image("img/a1.jfif");

        imageView8.setImage(image8);
        nameimg8.setText("");
        qty8.setText("");

        Image image9 = new Image("img/a1.jfif");
        imageView9.setImage(image9);
        nameimg9.setText("");
        qty9.setText("");
    }
}