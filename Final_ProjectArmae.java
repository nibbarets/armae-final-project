import java.util.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

class VehicleData {
    String plateNo, kind, slotCode;
    LocalDate entryDate;
    LocalTime timeIn, timeOut;
    long parkedHrs;
    double totalFee;

    VehicleData(String plateNo, String kind, String slotCode, LocalDate entryDate, LocalTime timeIn) {
        this.plateNo = plateNo;
        this.kind = kind;
        this.slotCode = slotCode;
        this.entryDate = entryDate;
        this.timeIn = timeIn;
    }

    void exit(LocalTime timeOut) {
        this.timeOut = timeOut;
        long minutes = Duration.between(timeIn, timeOut).toMinutes();
        parkedHrs = minutes / 60;
        if (minutes % 60 != 0) parkedHrs++;
        totalFee = switch (kind.toLowerCase()) {
            case "car", "van" -> parkedHrs * 20;
            case "motorcycle" -> parkedHrs * 10;
            default -> 0;
        };
    }
}

public class ParkingManager {
    static Scanner in = new Scanner(System.in);
    static ArrayList<VehicleData> vehicles = new ArrayList<>();

    public static void main(String[] args) {
        int opt;
        do {
            showMenu();
            opt = readInt();
            switch (opt) {
                case 1 -> listVehicles();
                case 2 -> addVehicle();
                case 3 -> checkoutVehicle();
                case 4 -> showSummary();
                case 5 -> System.out.println("Goodbye!");
                default -> System.out.println("Invalid choice.");
            }
        } while (opt != 5);
    }

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
        List<VehicleData> completed = new ArrayList<>();
        for (VehicleData v : vehicles)
            if (v.timeOut != null) completed.add(v);

        if (completed.isEmpty()) {
            System.out.println("No removed vehicles to display.");
            return;
        }

        System.out.printf("%-4s %-12s %-8s %-8s %-10s %-12s %-8s\n", "#", "Date", "In", "Out", "Plate", "Type", "Slot");
        int n = 1;
        for (VehicleData v : completed)
            System.out.printf("%-4d %-12s %-8s %-8s %-10s %-12s %-8s\n",
                    n++, v.entryDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")),
                    v.timeIn.format(DateTimeFormatter.ofPattern("hh:mm a")),
                    v.timeOut.format(DateTimeFormatter.ofPattern("hh:mm a")),
                    v.plateNo, v.kind, v.slotCode);
    }

    static void addVehicle() {
        try {
            System.out.print("Plate No: ");
            String plate = in.nextLine();
            System.out.print("Type (Car/Van/Motorcycle): ");
            String type = in.nextLine();
            System.out.print("Slot Code: ");
            String slot = in.nextLine();
            System.out.print("Date (MM/dd/yyyy): ");
            LocalDate date = LocalDate.parse(in.nextLine(), DateTimeFormatter.ofPattern("MM/dd/yyyy"));
            System.out.print("Time In (hh:mm a): ");
            LocalTime tin = LocalTime.parse(in.nextLine(), DateTimeFormatter.ofPattern("hh:mm a"));
            vehicles.add(new VehicleData(plate, type, slot, date, tin));
            System.out.println("Vehicle added.");
        } catch (Exception e) {
            System.out.println("Invalid input, try again.");
        }
    }

    static void checkoutVehicle() {
        if (vehicles.isEmpty()) {
            System.out.println("No vehicles to remove.");
            return;
        }
        System.out.print("Enter Plate No: ");
        String plate = in.nextLine();
        VehicleData match = null;
        for (VehicleData v : vehicles)
            if (v.plateNo.equalsIgnoreCase(plate)) { match = v; break; }

        if (match == null) {
            System.out.println("Not found.");
            return;
        }

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

    static void generateReport() {
        List<VehicleData> completed = new ArrayList<>();

        for (VehicleData record : vehicles) {
            if (record.timeOut != null) {
                completed.add(record);
            }
        }

        if (completed.isEmpty()) {
            System.out.println("\nNo completed parking records to report.");
            return;
        }

        double totalFees = 0;
        int totalVehicles = 0;

        System.out.println("\n--- PARKING REPORT ---");
        System.out.printf("%-3s %-12s %-10s %-12s %-12s %-7s %-10s%n",
                "#", "Date", "Time-in", "Plate No.", "Type", "Hours", "Fee");

        int count = 1;
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
