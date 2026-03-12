import java.util.*;

/*
   CampusTixDSA
   Covers CO1–CO6 using your Seat Booking System
*/

class Event {
    String name;
    String date;
    String venue;

    Event(String n, String d, String v) {
        name = n;
        date = d;
        venue = v;
    }
}

class Booking {
    String bookingId;
    String eventName;
    List<String> seats;

    Booking(String id, String event, List<String> seats) {
        this.bookingId = id;
        this.eventName = event;
        this.seats = seats;
    }
}

public class CampusTixDSA {

    static Scanner sc = new Scanner(System.in);

    // CO2 → Array ADT (Event storage)
    static ArrayList<Event> events = new ArrayList<>();

    // CO2 → 2D Array for Seat Map
    static char[][] seats = new char[6][8];

    // CO4 → HashMap for Bookings
    static HashMap<String, Booking> bookingMap = new HashMap<>();

    // CO3 → Stack for Undo Feature
    static Stack<String> undoStack = new Stack<>();

    // CO3 → Queue for Waiting List
    static Queue<String> waitingQueue = new LinkedList<>();

    static final int PRICE = 150;

    public static void main(String[] args) {

        initializeSeats();
        initializeEvents();

        while (true) {
            System.out.println("\n===== CAMPUS TIX SYSTEM =====");
            System.out.println("1. View Events");
            System.out.println("2. Book Seat");
            System.out.println("3. Search Booking (Hashing)");
            System.out.println("4. Undo Last Booking");
            System.out.println("5. Exit");
            System.out.print("Choose: ");

            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    displayEvents();
                    break;
                case 2:
                    bookSeat();
                    break;
                case 3:
                    searchBooking();
                    break;
                case 4:
                    undoBooking();
                    break;
                case 5:
                    System.exit(0);
            }
        }
    }

    // -------------------------------
    // INITIALIZATION
    // -------------------------------

    static void initializeSeats() {
        for (int i = 0; i < 6; i++)
            for (int j = 0; j < 8; j++)
                seats[i][j] = 'O'; // O = Available
    }

    static void initializeEvents() {
        events.add(new Event("Cultural Fest", "12 May", "Auditorium"));
        events.add(new Event("Tech Symposium", "19 May", "CS Hall"));
        events.add(new Event("Dance Show", "25 May", "Sports Complex"));
        events.add(new Event("Coding Contest", "2 June", "Innovation Hub"));

        // CO1 → Sorting Events Alphabetically (O(n log n))
        events.sort(Comparator.comparing(e -> e.name));
    }

    // -------------------------------
    // CO1 → Searching & Sorting
    // -------------------------------

    static void displayEvents() {
        System.out.println("\n--- Available Events ---");
        for (int i = 0; i < events.size(); i++) {
            System.out.println((i + 1) + ". " + events.get(i).name +
                    " | " + events.get(i).date +
                    " | " + events.get(i).venue);
        }
    }

    // -------------------------------
    // CO5 → Practical Booking System
    // -------------------------------

    static void bookSeat() {

        displayEvents();
        System.out.print("Select Event Number: ");
        int eventChoice = sc.nextInt();

        displaySeats();

        System.out.print("Number of seats (max 4): ");
        int count = sc.nextInt();

        if (count > 4) {
            System.out.println("Max 4 seats allowed!");
            return;
        }

        List<String> selectedSeats = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            System.out.print("Row (A-F): ");
            char rowChar = sc.next().toUpperCase().charAt(0);
            System.out.print("Column (1-8): ");
            int col = sc.nextInt();

            int row = rowChar - 'A';

            if (row >= 0 && row < 6 && col >= 1 && col <= 8) {
                if (seats[row][col - 1] == 'O') {
                    seats[row][col - 1] = 'X';
                    selectedSeats.add("" + rowChar + col);
                } else {
                    System.out.println("Seat already booked!");
                    i--;
                }
            }
        }

        String bookingId = "TIX" + (int) (Math.random() * 9000 + 1000);

        Booking booking = new Booking(bookingId,
                events.get(eventChoice - 1).name,
                selectedSeats);

        bookingMap.put(bookingId, booking); // O(1) average
        undoStack.push(bookingId); // CO3 Stack

        int total = count * PRICE;

        System.out.println("\nBooking Confirmed!");
        System.out.println("Booking ID: " + bookingId);
        System.out.println("Seats: " + selectedSeats);
        System.out.println("Total: ₹" + total);
    }

    static void displaySeats() {
        System.out.println("\nSeat Map (O=Available, X=Booked)");
        for (int i = 0; i < 6; i++) {
            char row = (char) ('A' + i);
            System.out.print(row + " ");
            for (int j = 0; j < 8; j++) {
                System.out.print(seats[i][j] + " ");
            }
            System.out.println();
        }
    }

    // -------------------------------
    // CO4 → Hash Search
    // -------------------------------

    static void searchBooking() {
        System.out.print("Enter Booking ID: ");
        String id = sc.next();

        Booking booking = bookingMap.get(id); // O(1)

        if (booking != null) {
            System.out.println("Event: " + booking.eventName);
            System.out.println("Seats: " + booking.seats);
        } else {
            System.out.println("Booking not found.");
        }
    }

    // -------------------------------
    // CO3 → Stack Undo
    // -------------------------------

    static void undoBooking() {
        if (undoStack.isEmpty()) {
            System.out.println("No booking to undo.");
            return;
        }

        String lastId = undoStack.pop();
        Booking booking = bookingMap.remove(lastId);

        for (String seat : booking.seats) {
            int row = seat.charAt(0) - 'A';
            int col = Integer.parseInt(seat.substring(1)) - 1;
            seats[row][col] = 'O';
        }

        System.out.println("Last booking undone: " + lastId);
    }
}