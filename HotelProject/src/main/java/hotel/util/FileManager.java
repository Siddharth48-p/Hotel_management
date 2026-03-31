package hotel.util;

import hotel.model.Booking;
import hotel.model.Room;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * FileManager handles permanent storage of Room and Booking data
 * using Java Serialization (Week 6 concept).
 */
public class FileManager {

    private static final String ROOMS_FILE    = "hotel_rooms.dat";
    private static final String BOOKINGS_FILE = "hotel_bookings.dat";

    // ===================== ROOMS =====================

    /** Serialize and save the room list to file */
    public static void saveRooms(List<Room> rooms) {
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(ROOMS_FILE))) {
            oos.writeObject(rooms);
        } catch (IOException e) {
            System.err.println("Error saving rooms: " + e.getMessage());
        }
    }

    /** Deserialize and load rooms from file; returns empty list if file not found */
    @SuppressWarnings("unchecked")
    public static List<Room> loadRooms() {
        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(ROOMS_FILE))) {
            return (List<Room>) ois.readObject();
        } catch (Exception e) {
            return new ArrayList<>();   // First run - no file yet
        }
    }

    // ===================== BOOKINGS =====================

    /** Serialize and save the booking list to file */
    public static void saveBookings(List<Booking> bookings) {
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(BOOKINGS_FILE))) {
            oos.writeObject(bookings);
        } catch (IOException e) {
            System.err.println("Error saving bookings: " + e.getMessage());
        }
    }

    /** Deserialize and load bookings from file; returns empty list if not found */
    @SuppressWarnings("unchecked")
    public static List<Booking> loadBookings() {
        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(BOOKINGS_FILE))) {
            return (List<Booking>) ois.readObject();
        } catch (Exception e) {
            return new ArrayList<>();   // First run - no file yet
        }
    }
}