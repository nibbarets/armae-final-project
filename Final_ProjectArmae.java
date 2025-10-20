import java.util.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

// class object
class VehicleData {
    String plateNo, kind, slotCode;
    LocalDate entryDate;
    LocalTime timeIn, timeOut;
    long parkedHrs;
    double totalFee;

    // declare object variable
    VehicleData(String plateNo, String kind, String slotCode, LocalDate entryDate, LocalTime timeIn) {
        this.plateNo = plateNo;
        this.kind = kind;
        this.slotCode = slotCode;
        this.entryDate = entryDate;
        this.timeIn = timeIn;
    }

    // method for duration measurement of timeIn and timeOut
    void exit(LocalTime timeOut) {
        this.timeOut = timeOut;
        long minutes = Duration.between(timeIn, timeOut).toMinutes();
        parkedHrs = minutes / 60;

        // condition to append time to whole number
        if (minutes % 60 != 0) parkedHrs++;

        // fee computation
        totalFee = switch (kind.toLowerCase()) {
            case "car", "van" -> parkedHrs * 20;
            case "motorcycle" -> parkedHrs * 10;
            default -> 0;
        };
    }
}

// main class
public class ParkingManager {
    static Scanner in = new Scanner(System.in);
    static ArrayList<VehicleData> vehicles = new ArrayList<>(); // created array list with variable vehicles

    public static void main(String[] args) {
        int opt;
        do {
            showMenu(); // pulls showMenu Method
            opt = readInt();

            // choice validation
            switch (opt) {
                case 1 -> listVehicles();
                case 2 -> addVehicle();
                case 3 -> checkoutVehicle();
                case 4 -> showSummary();
                case 5 -> System.out.println("Goodbye!");
                default -> System.out.println("Invalid choice.");
            }
        } while (opt != 5); // loop stops when 5 is picked
    }

    // method for showing the menu
    static void showMenu() {
        System.out.println("\n--- PARKING MANAGER ---");
        System.out.println("1. View Vehicles");
        System.out.println("2. Park Vehicle");
        System.out.println("3. Remove Vehicle");
        System.out.println("4. Generate Report");
        System.out.println("5. Exit");
        System.out.print("Option: ");
    }

    // Modified: Only display vehicles that have timeOut (already removed)
    static void listVehicles() {
        List<VehicleData> completed = new ArrayList<>(); // created new array list for removed vehicles

        // loops inside the vehicle data variable and reads it
        for (VehicleData v : vehicles)
            if (v.timeOut != null) completed.add(v); // checks if the timeout side is empty then it adds it to the database

            // checks if completed variable is empty
        if (completed.isEmpty()) {
            System.out.println("No removed vehicles to display.");
            return;
        }

        // print table format
        System.out.printf("%-4s %-12s %-8s %-8s %-10s %-12s %-8s\n", "#", "Date", "In", "Out", "Plate", "Type", "Slot");
        int n = 1;
        for (VehicleData v : completed)
            System.out.printf("%-4d %-12s %-8s %-8s %-10s %-12s %-8s\n",
                    n++, v.entryDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")), // format for date
                    v.timeIn.format(DateTimeFormatter.ofPattern("hh:mm a")), // format for timein time
                    v.timeOut.format(DateTimeFormatter.ofPattern("hh:mm a")), // format for timeout time
                    v.plateNo, v.kind, v.slotCode);
    }


    // method for adding vehicle
    static void addVehicle() {
        try {

            // asks user for plate number
            System.out.print("Plate No: ");
            String plate = in.nextLine();

            // condition to check if input is empty
            if (plate.isEmpty()) {
                System.out.println("Plate cannot be empty!");
                return;
            }

            // asks user for vehicle type
            System.out.print("Type (Car/Van/Motorcycle): ");
            String type = in.nextLine();

            if (type.isEmpty()) {
                System.out.println("Vehicle type cannot be empty!");
                return;
            }
            
            // asks user for slot code
            System.out.print("Slot Code: ");
            String slot = in.nextLine();

            if (slot.isEmpty()) {
                System.out.println("Slot code cannot be empty!");
                return;
            }

            // asks user for date
            System.out.print("Date (MM/dd/yyyy): ");
            LocalDate date = LocalDate.parse(in.nextLine(), DateTimeFormatter.ofPattern("MM/dd/yyyy"));

            // asks user for time in
            System.out.print("Time In (hh:mm a): ");
            LocalTime tin = LocalTime.parse(in.nextLine(), DateTimeFormatter.ofPattern("hh:mm a")); // stricts pattern to this type
            vehicles.add(new VehicleData(plate, type, slot, date, tin)); // adds the given data values to the database
            System.out.println("Vehicle added.");
        } catch (Exception e) {
            System.out.println("Invalid input, try again.");
        }
    }

    // method for vehicle checkout
    static void checkoutVehicle() {

        // condition to check if vehicles array is empty
        if (vehicles.isEmpty()) {
            System.out.println("No vehicles to remove.");
            return;
        }

        // asks user for the plate number that will be removes
        System.out.print("Enter Plate No: ");
        String plate = in.nextLine();
        VehicleData match = null;
        
        // loops and looks inside the VehicleData array
        for (VehicleData v : vehicles)

        // if input has the same plate that is inside the arrray then it removes the vehicle
            if (v.plateNo.equalsIgnoreCase(plate)) { match = v; break; }

        if (match == null) {
            System.out.println("Not found.");
            return;
        }

        // prints out the receipt
        try {
            System.out.print("Time Out (hh:mm a): ");
            LocalTime tout = LocalTime.parse(in.nextLine(), DateTimeFormatter.ofPattern("hh:mm a"));
            match.exit(tout);
            System.out.println("\nChecked out: " + match.plateNo + " (" + match.kind + ")");
            System.out.println("In: " + match.timeIn + "  Out: " + match.timeOut);
            System.out.println("Hours: " + match.parkedHrs + "  Fee: PHP " + match.totalFee);
            vehicles.remove(match);
        } catch (Exception e) {
            System.out.println("Invalid time input.");
        }
    }

    // Modified: Only include completed (removed) vehicles
    static void showSummary() {
        List<VehicleData> completed = new ArrayList<>();
        for (VehicleData v : vehicles)
            if (v.timeOut != null) completed.add(v);

        if (completed.isEmpty()) {
            System.out.println("No removed vehicles to summarize.");
            return;
        }

        // prints table format
        System.out.printf("%-4s %-12s %-10s %-10s %-8s %-8s\n", "#", "Date", "Plate", "Type", "Hours", "Fee");
        int n = 1; double total = 0;
        for (VehicleData v : completed) {
            System.out.printf("%-4d %-12s %-10s %-10s %-8d PHP %.2f\n",
                    n++, v.entryDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")),
                    v.plateNo, v.kind, v.parkedHrs, v.totalFee);
            total += v.totalFee;
        }
        System.out.println("Vehicles: " + (n - 1));
        System.out.printf("Total Fees: PHP %.2f\n", total);
    }

    static int readInt() {
        try { return Integer.parseInt(in.nextLine()); }
        catch (Exception e) { return -1; }
    }


    // method for generating report
    static void generateReport() {
        List<VehicleData> completed = new ArrayList<>(); // created new completed array list

        // looks inside the VehicleData array
        for (VehicleData record : vehicles) {

            // check if vehicle timeout varibale is not empty
            if (record.timeOut != null) {
                completed.add(record); // if not then add it to completed.add
            }
        }

        // condition to check if no vehicles have left the parking space
        if (completed.isEmpty()) {
            System.out.println("\nNo completed parking records to report.");
            return;
        }

        double totalFees = 0;
        int totalVehicles = 0;

        // prints out table format
        System.out.println("\n--- PARKING REPORT ---");
        System.out.printf("%-3s %-12s %-10s %-12s %-12s %-7s %-10s%n",
                "#", "Date", "Time-in", "Plate No.", "Type", "Hours", "Fee");

        int count = 1; // handles # iteration
        for (VehicleData record : completed) {
            String date = record.entryDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
            String timeIn = record.timeIn.format(DateTimeFormatter.ofPattern("hh:mm a"));
            System.out.printf("%-3d %-12s %-10s %-12s %-12s %-7d %-10.2f%n",
                    count++, date, timeIn, record.plateNo, record.kind, record.parkedHrs, record.totalFee);
            totalVehicles++;
            totalFees += record.totalFee;
        }

        System.out.println("-----------------------------------------------------------");
        System.out.println("Total Vehicles: " + totalVehicles);
        System.out.println("Total Fees Collected: ₱" + totalFees);
    }
}
