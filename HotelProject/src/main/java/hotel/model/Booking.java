package hotel.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class Booking implements Serializable {
    private static final long serialVersionUID = 1L;

    private int bookingId;
    private String customerName;
    private String contactNumber;
    private int roomNumber;
    private String roomType;
    private double pricePerNight;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private long numberOfNights;
    private boolean checkedOut;
    
    // Detailed Service Tracking
    private List<String> serviceNames = new ArrayList<>();
    private List<Double> serviceCosts = new ArrayList<>();

    public Booking(int bookingId, String customerName, String contactNumber,
                   int roomNumber, String roomType, double pricePerNight,
                   LocalDate checkInDate, LocalDate checkOutDate) {
        this.bookingId = bookingId;
        this.customerName = customerName;
        this.contactNumber = contactNumber;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.pricePerNight = pricePerNight;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.numberOfNights = Math.max(1, ChronoUnit.DAYS.between(checkInDate, checkOutDate));
        this.checkedOut = false;
    }

    public void addService(String type, double cost) {
        serviceNames.add(type);
        serviceCosts.add(cost);
    }

    public double getTotalServiceCharges() {
        return serviceCosts.stream().mapToDouble(Double::doubleValue).sum();
    }

    public String getServiceBreakdown() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < serviceNames.size(); i++) {
            sb.append(String.format("  %-22s : Rs. %.2f\n", serviceNames.get(i), serviceCosts.get(i)));
        }
        return sb.toString();
    }

    public double getTotalAmount() { 
        return (numberOfNights * pricePerNight) + getTotalServiceCharges(); 
    }

    // Standard Getters
    public int getBookingId() { return bookingId; }
    public String getCustomerName() { return customerName; }
    public int getRoomNumber() { return roomNumber; }
    public String getRoomType() { return roomType; }
    public double getPricePerNight() { return pricePerNight; }
    public LocalDate getCheckInDate() { return checkInDate; }
    public LocalDate getCheckOutDate() { return checkOutDate; }
    public long getNumberOfNights() { return numberOfNights; }
    public boolean isCheckedOut() { return checkedOut; }
    public String getCheckoutStatus() { return checkedOut ? "Checked Out" : "Active"; }
    public void setCheckedOut(boolean checkedOut) { this.checkedOut = checkedOut; }
}