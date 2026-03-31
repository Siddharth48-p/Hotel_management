package hotel.model;

import java.io.Serializable;

/**
 * Room model class representing a hotel room.
 * Implements Serializable for file-based persistence (Week 6 concept).
 */
public class Room implements Serializable {
    private static final long serialVersionUID = 1L;

    private int roomNumber;
    private String roomType;   // Single, Double, Deluxe, Suite
    private double pricePerNight;
    private boolean booked;
    private String guestName;

    public Room(int roomNumber, String roomType, double pricePerNight) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.pricePerNight = pricePerNight;
        this.booked = false;
        this.guestName = "";
    }

    // --- Getters ---
    public int getRoomNumber()      { return roomNumber; }
    public String getRoomType()     { return roomType; }
    public double getPricePerNight(){ return pricePerNight; }
    public boolean isBooked()       { return booked; }
    public String getGuestName()    { return guestName; }

    /** Computed property for TableView display */
    public String getStatus() {
        return booked ? "Occupied" : "Available";
    }

    // --- Setters ---
    public void setBooked(boolean booked)       { this.booked = booked; }
    public void setGuestName(String guestName)  { this.guestName = guestName; }

    @Override
    public String toString() {
        return "Room " + roomNumber + " (" + roomType + ")  -  Rs." +
               String.format("%.0f", pricePerNight) + "/night";
    }
}