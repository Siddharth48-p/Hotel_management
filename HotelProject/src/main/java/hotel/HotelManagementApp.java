package hotel;

import hotel.model.Booking;
import hotel.model.Room;
import hotel.util.FileManager;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * HotelManagementApp — Main JavaFX Application
 * Features: Dashboard, Room Management, Bookings, Analytics, and Itemized Billing.
 */
public class HotelManagementApp extends Application {

    private ObservableList<Room> roomList;
    private ObservableList<Booking> bookingList;
    private int bookingIdCounter = 1001;

    private TableView<Room> roomTable;
    private TableView<Booking> bookingTable;
    private ComboBox<Room> cmbRoom;

    @Override
    public void start(Stage primaryStage) {
        // Load data using Serialization
        roomList = FXCollections.observableArrayList(FileManager.loadRooms());
        bookingList = FXCollections.observableArrayList(FileManager.loadBookings());

        for (Booking b : bookingList) {
            if (b.getBookingId() >= bookingIdCounter) {
                bookingIdCounter = b.getBookingId() + 1;
            }
        }

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Define Tabs with catchy light themes
        Tab tabDash     = new Tab("  Dashboard  ", createDashboard());
        Tab tabRooms    = new Tab("  Rooms  ",     createRoomsTab());
        Tab tabBookings = new Tab("  Bookings  ",  createBookingsTab());
        Tab tabService  = new Tab("  Services & Analytics  ", createAnalyticsTab());
        Tab tabBilling  = new Tab("  Billing  ",   createBillingTab());

        tabPane.getTabs().addAll(tabDash, tabRooms, tabBookings, tabService, tabBilling);

        // Refresh views on tab selection
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab == tabDash) tabDash.setContent(createDashboard());
            if (newTab == tabService) tabService.setContent(createAnalyticsTab());
        });

        Scene scene = new Scene(tabPane, 1150, 800);
        applyStyles(scene);

        primaryStage.setTitle("Grand View Management System — OOSD Lab");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(e -> saveAll());
        primaryStage.show();
    }

    private void applyStyles(Scene scene) {
        scene.getRoot().setStyle("-fx-font-family: 'Segoe UI'; -fx-background-color: #f0f7ff;");
    }

    private void saveAll() {
        FileManager.saveRooms(roomList);
        FileManager.saveBookings(bookingList);
    }

    // ===================== TAB 1: DASHBOARD =====================
    private VBox createDashboard() {
        VBox root = new VBox(30);
        root.setPadding(new Insets(50));
        root.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Hotel Management Dashboard");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #2c5282;");

        HBox stats = new HBox(25);
        stats.setAlignment(Pos.CENTER);
        long occupied = roomList.stream().filter(Room::isBooked).count();
        double revenue = bookingList.stream().mapToDouble(Booking::getTotalAmount).sum() * 1.18;

        stats.getChildren().addAll(
            statCard("Available", (roomList.size() - occupied) + " Rooms", "#38a169", "#f0fff4"),
            statCard("Occupied", occupied + " Rooms", "#e53e3e", "#fff5f5"),
            statCard("Total Revenue", "Rs. " + String.format("%.0f", revenue), "#3182ce", "#ebf8ff")
        );

        root.getChildren().addAll(title, new Separator(), stats);
        return root;
    }

    // ===================== TAB: ANALYTICS & SERVICES =====================
    private VBox createAnalyticsTab() {
        VBox root = new VBox(25);
        root.setPadding(new Insets(30));

        HBox mainContent = new HBox(40);
        mainContent.setAlignment(Pos.CENTER);

        // Business Analytics Pie Chart
        long occupied = roomList.stream().filter(Room::isBooked).count();
        PieChart pie = new PieChart(FXCollections.observableArrayList(
            new PieChart.Data("Occupied", occupied),
            new PieChart.Data("Available", roomList.size() - occupied)
        ));
        pie.setTitle("Live Room Occupancy");

        // Service Entry Form
        VBox form = new VBox(15);
        form.setPadding(new Insets(25));
        form.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, #d1d1d1, 10, 0, 0, 5);");
        
        Label subT = new Label("Apply Room Service Charge");
        subT.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        ComboBox<Booking> activeBookings = new ComboBox<>();
        bookingList.stream().filter(b -> !b.isCheckedOut()).forEach(activeBookings.getItems()::add);
        activeBookings.setPromptText("Select Active Guest");
        activeBookings.setPrefWidth(250);

        ComboBox<String> serviceType = new ComboBox<>(FXCollections.observableArrayList("Dining", "Laundry", "Spa", "Transport"));
        serviceType.setPromptText("Service Type");
        serviceType.setPrefWidth(250);

        TextField costField = styledField("Cost (Rs.)");
        Button btnAdd = actionButton("Apply Charge", "#4299e1");

        btnAdd.setOnAction(e -> {
            Booking b = activeBookings.getValue();
            if (b != null && !costField.getText().isEmpty()) {
                b.addService(serviceType.getValue(), Double.parseDouble(costField.getText()));
                showAlert(Alert.AlertType.INFORMATION, "Success", "Added Rs. " + costField.getText() + " service to " + b.getCustomerName());
                costField.clear();
                saveAll();
            }
        });

        form.getChildren().addAll(subT, activeBookings, serviceType, costField, btnAdd);
        mainContent.getChildren().addAll(pie, form);
        root.getChildren().addAll(mainContent);
        return root;
    }

    // ===================== TAB: BILLING (Visibility Fixed) =====================
    private VBox createBillingTab() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(40));

        HBox search = new HBox(15);
        search.setAlignment(Pos.CENTER_LEFT);
        TextField tid = styledField("Enter Booking ID");
        Button btn = actionButton("Generate Detailed Bill", "#667eea");
        search.getChildren().addAll(new Label("Booking ID:"), tid, btn);

        TextArea area = new TextArea();
        area.setEditable(false);
        area.setFont(Font.font("Courier New", 14));
        // High visibility styling
        area.setStyle("-fx-control-inner-background: #ffffff; -fx-text-fill: #2d3748; -fx-border-color: #bee3f8; -fx-border-width: 2;");

        btn.setOnAction(e -> {
            try {
                int id = Integer.parseInt(tid.getText().trim());
                bookingList.stream().filter(b -> b.getBookingId() == id).findFirst()
                    .ifPresentOrElse(b -> area.setText(itemizedReceipt(b)), () -> area.setText("ID Not Found"));
            } catch (Exception ex) { area.setText("Please enter a numeric ID"); }
        });

        root.getChildren().addAll(sectionLabel("Billing Management"), search, area);
        return root;
    }

    private String itemizedReceipt(Booking b) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        double base = b.getNumberOfNights() * b.getPricePerNight();
        double services = b.getTotalServiceCharges();
        double total = (base + services) * 1.18;

        return "==================================================\n" +
               "             GRAND VIEW LUXURY HOTEL              \n" +
               "==================================================\n" +
               "Guest: " + b.getCustomerName() + " | ID: " + b.getBookingId() + "\n" +
               "Room: " + b.getRoomNumber() + " (" + b.getRoomType() + ")\n" +
               "Stay: " + b.getCheckInDate().format(dtf) + " to " + b.getCheckOutDate().format(dtf) + "\n" +
               "--------------------------------------------------\n" +
               String.format("Stay Charges (%d nights)      : Rs. %.2f\n", b.getNumberOfNights(), base) +
               "Additional Services:\n" +
               b.getServiceBreakdown() +
               "--------------------------------------------------\n" +
               String.format("Subtotal                     : Rs. %.2f\n", (base + services)) +
               String.format("GST (18%%)                    : Rs. %.2f\n", (base + services) * 0.18) +
               "--------------------------------------------------\n" +
               String.format("TOTAL PAYABLE                : Rs. %.2f\n", total) +
               "==================================================\n" +
               "          Thank you! Visit us again soon.         ";
    }

    // ===================== ROOMS & BOOKINGS TABS =====================
    private BorderPane createRoomsTab() {
        BorderPane root = new BorderPane();
        VBox form = new VBox(15);
        form.setPadding(new Insets(25));
        form.setPrefWidth(300);
        form.setStyle("-fx-background-color: white;");

        TextField tNum = styledField("Room Number");
        ComboBox<String> tType = new ComboBox<>(FXCollections.observableArrayList("Single", "Double", "Deluxe", "Suite"));
        tType.setPromptText("Room Type");
        tType.setMaxWidth(Double.MAX_VALUE);
        TextField tPrice = styledField("Price/Night");
        Button btnAdd = actionButton("Register Room", "#38b2ac");

        btnAdd.setOnAction(e -> {
            try {
                roomList.add(new Room(Integer.parseInt(tNum.getText()), tType.getValue(), Double.parseDouble(tPrice.getText())));
                tNum.clear(); tPrice.clear();
                saveAll();
                refreshRoomCombo();
            } catch (Exception ex) { showAlert(Alert.AlertType.ERROR, "Error", "Invalid input"); }
        });

        form.getChildren().addAll(sectionLabel("Room Registration"), tNum, tType, tPrice, btnAdd);
        
        roomTable = new TableView<>(roomList);
        roomTable.getColumns().addAll(tableCol("Room", "roomNumber", 80), tableCol("Type", "roomType", 100), tableCol("Status", "status", 100));

        root.setLeft(form);
        root.setCenter(roomTable);
        return root;
    }

    private BorderPane createBookingsTab() {
        BorderPane root = new BorderPane();
        VBox form = new VBox(15);
        form.setPadding(new Insets(25));
        form.setPrefWidth(300);
        form.setStyle("-fx-background-color: white;");

        TextField tName = styledField("Guest Name");
        TextField tCont = styledField("Contact Number");
        cmbRoom = new ComboBox<>();
        refreshRoomCombo();
        cmbRoom.setMaxWidth(Double.MAX_VALUE);
        DatePicker dIn = new DatePicker(LocalDate.now());
        DatePicker dOut = new DatePicker(LocalDate.now().plusDays(1));
        Button btnBk = actionButton("Check-In Guest", "#63b3ed");

        btnBk.setOnAction(e -> {
            Room r = cmbRoom.getValue();
            if (r != null && !tName.getText().isEmpty()) {
                r.setBooked(true);
                bookingList.add(new Booking(bookingIdCounter++, tName.getText(), tCont.getText(), r.getRoomNumber(), r.getRoomType(), r.getPricePerNight(), dIn.getValue(), dOut.getValue()));
                refreshRoomCombo(); roomTable.refresh(); saveAll();
                tName.clear(); tCont.clear();
            }
        });

        form.getChildren().addAll(sectionLabel("New Reservation"), tName, tCont, cmbRoom, dIn, dOut, btnBk);
        
        bookingTable = new TableView<>(bookingList);
        bookingTable.getColumns().addAll(tableCol("ID", "bookingId", 50), tableCol("Guest", "customerName", 120), tableCol("Status", "checkoutStatus", 100));

        root.setLeft(form);
        root.setCenter(bookingTable);
        return root;
    }

    // ===================== HELPER UI COMPONENTS =====================
    private VBox statCard(String l, String v, String c, String bg) {
        VBox cb = new VBox(5); cb.setPadding(new Insets(25)); cb.setPrefWidth(250);
        cb.setStyle("-fx-background-color: " + bg + "; -fx-background-radius: 20; -fx-border-color: " + c + "; -fx-border-width: 2;");
        Label lblV = new Label(v); lblV.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: " + c + ";");
        Label lblL = new Label(l); lblL.setStyle("-fx-text-fill: #4a5568; -fx-font-weight: bold;");
        cb.getChildren().addAll(lblV, lblL); return cb;
    }

    private TextField styledField(String p) {
        TextField f = new TextField(); f.setPromptText(p);
        f.setStyle("-fx-pref-height: 40; -fx-background-radius: 10; -fx-border-color: #cbd5e0; -fx-border-radius: 10;");
        return f;
    }

    private Button actionButton(String t, String c) {
        Button b = new Button(t);
        b.setStyle("-fx-background-color: " + c + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-pref-height: 40; -fx-cursor: hand;");
        b.setMaxWidth(Double.MAX_VALUE); return b;
    }

    private Label sectionLabel(String t) {
        Label l = new Label(t); l.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2d3748;");
        return l;
    }

    private <T> TableColumn<T, ?> tableCol(String h, String p, double w) {
        TableColumn<T, Object> col = new TableColumn<>(h);
        col.setCellValueFactory(new PropertyValueFactory<>(p));
        col.setPrefWidth(w); return col;
    }

    private void refreshRoomCombo() { if (cmbRoom != null) cmbRoom.setItems(roomList.filtered(r -> !r.isBooked())); }
    
    private void showAlert(Alert.AlertType t, String h, String m) {
        Alert a = new Alert(t); a.setTitle(h); a.setHeaderText(null); a.setContentText(m); a.showAndWait();
    }

    public static void main(String[] args) { launch(args); }
}