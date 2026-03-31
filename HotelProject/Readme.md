# Hotel Management System
### OOSD Lab Project — IV Semester, MIT MAHE

---

## Features Covered (Rubric Checklist)

| Feature | Status | Location |
|---|---|---|
| JavaFX GUI (tabs, buttons, forms) | ✅ | HotelManagementApp.java |
| TableView, ComboBox, DatePicker | ✅ | HotelManagementApp.java |
| Room add/view/filter | ✅ | Rooms Tab |
| Booking + Checkout | ✅ | Bookings Tab |
| File persistence (Serialization) | ✅ | FileManager.java |
| Billing with GST breakdown | ✅ | Billing Tab |
| Screen design with styles/layouts | ✅ | Styled with CSS colors |
| Maven build system | ✅ | pom.xml |
| Collections (ObservableList, filter) | ✅ | HotelManagementApp.java |

---

## Project Structure

```
hotel-management/
│
├── pom.xml                              ← Maven build file
└── src/
    └── main/
        └── java/
            └── hotel/
                ├── HotelManagementApp.java    ← Main JavaFX App
                ├── model/
                │   ├── Room.java              ← Room model
                │   └── Booking.java           ← Booking model
                └── util/
                    └── FileManager.java       ← File persistence
```

---

## How to Set Up (Maven)

### Prerequisites
- Java 17 or higher
- Maven installed (`mvn --version` to check)

### Step 1 — Create the folder structure
```
mkdir -p HotelProject/src/main/java/hotel/model
mkdir -p HotelProject/src/main/java/hotel/util
```

### Step 2 — Copy files
```
HotelProject/
├── pom.xml
└── src/main/java/hotel/
    ├── HotelManagementApp.java
    ├── model/Room.java
    ├── model/Booking.java
    └── util/FileManager.java
```

### Step 3 — Run
```bash
cd HotelProject
mvn javafx:run
```

---

## How to Set Up (IntelliJ IDEA — without Maven)

1. Open IntelliJ → New Project → JavaFX
2. Add JavaFX SDK to project libraries
3. Create packages: `hotel`, `hotel.model`, `hotel.util`
4. Copy each `.java` file into the correct package folder
5. Run `HotelManagementApp.java`

---

## How to Set Up (Eclipse)

1. File → New → Project → JavaFX Project
2. Add JavaFX jars to build path
3. Create the same package structure
4. Add each Java file to its package
5. Run as Java Application

---

## Data Persistence

Room and Booking data is saved automatically to:
- `hotel_rooms.dat`    ← Serialized room list
- `hotel_bookings.dat` ← Serialized booking list

These files are created in the project's **working directory** when the app first saves data (on close or after first booking).

---

## App Tabs

| Tab | What it does |
|---|---|
| Dashboard | Live stats — rooms, bookings, revenue |
| Rooms | Add rooms, view all/available/occupied |
| Bookings | Book a room, view records, checkout |
| Billing | Enter Booking ID → get full receipt with GST |

---

## Marking Rubric Coverage

**Basic System (5M):**
- JavaFX GUI with tabbed layout ✅
- Add/view rooms, book/checkout ✅

**Additional Features (5M):**
- Permanent file storage using Serialization ✅
- Screen design with colored cards, CSS styles, layouts ✅
- Maven build system (pom.xml) ✅
- Billing with GST, summary report ✅
- ComboBox, DatePicker, TableView components ✅