import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

class Flight {
    String flightNumber;
    String departure;
    String arrival;
    LocalTime departureTime;
    LocalTime arrivalTime;
    List<Integer> schedule;
    Integer price;
    Integer days;

    public Flight(String flightNumber, String departure, String arrival, LocalTime departureTime, LocalTime arrivalTime,
                  List<Integer> schedule, Integer price, Integer days) {
        this.flightNumber = flightNumber;
        this.departure = departure;
        this.arrival = arrival;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.schedule = schedule;
        this.price = price;
        this.days = days;
    }

    @Override
    public String toString() {
        return "Flight No: " + this.flightNumber + "\n          " +
                " From: " + this.departure + "\n          " +
                " To: " + this.arrival + "\n          " +
                " Departure time: " + this.departureTime.toString() + "\n          " +
                " Arrival time: " + this.arrivalTime.toString() + "\n          " +
                " Price: " + this.price;
    }

    public String toString2() {
        return "Flight No: " + this.flightNumber + "\n" +
                "From: " + this.departure + "\n" +
                "To: " + this.arrival + "\n" +
                "Departure time: " + this.departureTime.toString() + "\n" +
                "Arrival time: " + this.arrivalTime.toString() + "\n" +
                "Price: " + this.price;
    }

    public long getFlightTime() {
        if (days == 1) {
            LocalTime midnight = LocalTime.parse("00:00", DateTimeFormatter.ofPattern("H:m"));
            return departureTime.until(midnight, ChronoUnit.MINUTES) + midnight.until(arrivalTime, ChronoUnit.MINUTES);
        }
        return departureTime.until(arrivalTime, ChronoUnit.MINUTES);
    }

}

class FlightPlan {
    Flight going;
    Flight back;

    public void setGoing(Flight going) {
        this.going = going;
    }
    public void setBack(Flight back) {
        this.back = back;
    }

    public int getCost() {
        return going.price + back.price;
    }

    public String toString() {
        return "Departing:\n\n" + going.toString2() + "\n\nReturning:\n\n" + back.toString2() + "\n" +
                "\nTotal Trip Cost: $" + this.getCost();
    }
}

public class main {
    private static final String NAME = "src/main/data/flights.xlsx";
    private static final ArrayList<Flight> flights = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("hi");
        try {
            FileInputStream file = new FileInputStream(NAME);
            Workbook workbook = new XSSFWorkbook(file);
            DataFormatter dataFormatter = new DataFormatter();
            Iterator<Sheet> sheets = workbook.sheetIterator();
            Sheet sh = sheets.next();
            Iterator<Row> iterator = sh.iterator();
            iterator.next();
            while (iterator.hasNext()) {
                Row row = iterator.next();
                Iterator<Cell> cellIterator = row.iterator();
                int index = 0;
                String flightNumber = null;
                String departure = null;
                String arrival = null;
                LocalTime departureTime = null;
                LocalTime arrivalTime = null;
                List<Integer> schedule = null;
                Integer price = null;
                Integer days = null;
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    String cellValue = dataFormatter.formatCellValue(cell);
                    switch(index) {
                    case 0:
                        flightNumber = cellValue;
                        break;
                    case 1:
                        departure = cellValue;
                        break;
                    case 2:
                        arrival = cellValue;
                        break;
                    case 3:
                        departureTime = LocalTime.parse(cellValue, DateTimeFormatter.ofPattern("H:m"));
                        break;
                    case 4:
                        arrivalTime = LocalTime.parse(cellValue, DateTimeFormatter.ofPattern("H:m"));
                        break;
                    case 5:
                        int[] numbers = Arrays.asList(cellValue.split(",")).stream()
                                .map(String::trim)
                                .mapToInt(Integer::parseInt).toArray();
                        List<Integer> list = Arrays.stream(numbers).boxed().collect(Collectors.toList());
                        schedule = list;
                        break;
                    case 6:
                        price = Integer.parseInt(cellValue);
                        break;
                    case 7:
                        days = Integer.parseInt(cellValue);
                        break;
                    }
                    index++;
                }
                flights.add(new Flight(flightNumber, departure, arrival, departureTime, arrivalTime,
                        schedule, price, days));
            }
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Scanner scanner = new Scanner(System.in);
            HashSet<FlightPlan> fp = new HashSet<>();
            System.out.println("Finding flights to Korea (Incheon)!!\n");
            System.out.println("Select Priority: ");
            System.out.println("1 = cost");
            System.out.println("2 = time");
            System.out.println("Enter selection:");
            Integer priority = scanner.nextInt();
            System.out.println("Enter Departure Date (yyyy-mm-dd):");
            LocalDate departureDate = LocalDate.parse(scanner.next());
            Integer departureDay = departureDate.getDayOfWeek().getValue();
            FlightPlan plan = new FlightPlan();
            List<Flight> departingFlight  = flights.stream().filter(i -> i.schedule.contains(departureDay))
                    .filter(j -> j.departure.equals("Singapore"))
                    .filter(k -> k.arrival.equals("Incheon"))
                    .collect(Collectors.toList());
            if (priority == 1) {
                departingFlight.sort(Comparator.comparing(a -> a.price));
            } else {
                departingFlight.sort(Comparator.comparing(Flight::getFlightTime));
            }
            int index = 1;
            System.out.println("Option:    " + "Flight Details:");
            for (Flight f : departingFlight) {
                System.out.println(index + ".         " + f.toString());
                index++;
            }
            System.out.println("Enter departing option (eg. 1): ");
            Integer selectionDeparture = scanner.nextInt();
            plan.setGoing(departingFlight.get(selectionDeparture - 1));
            System.out.println("Selected departing flight:\n");
            System.out.println(plan.going.toString2());

            LocalDate earliestReturnDate = departureDate.plusDays(135);
            LocalDate latestReturnDate = LocalDate.parse("2023-01-07");
            System.out.println("Enter return date (after " + earliestReturnDate + " and before " + latestReturnDate + "):");
            LocalDate returnDate = LocalDate.parse(scanner.next());
            while (returnDate.isBefore(earliestReturnDate) || returnDate.isAfter(latestReturnDate)) {
                System.out.println("Enter return date (after " + earliestReturnDate + " and before " + latestReturnDate + "):");
                returnDate = LocalDate.parse(scanner.next());
            }
            Integer arrivalDay = returnDate.getDayOfWeek().getValue();
            System.out.println("Option:    " + "Flight Details:");
            List<Flight> returningFlight = flights.stream().filter(i -> i.schedule.contains(arrivalDay))
                    .filter(j -> j.arrival.equals("Singapore"))
                    .filter(k -> k.departure.equals("Incheon"))
                    .collect(Collectors.toList());
            if (priority == 1) {
                returningFlight.sort(Comparator.comparing(a -> a.price));
            } else {
                returningFlight.sort(Comparator.comparing(Flight::getFlightTime));
            }
            index = 1;
            for (Flight f : returningFlight) {
                System.out.println(index + ".         " + f.toString());
                index++;
            }
            System.out.println("Select return option (eg. 1): ");
            Integer selectionReturn = scanner.nextInt();
            plan.setBack(returningFlight.get(selectionReturn - 1));
            System.out.println("Selected return flight:\n");
            System.out.println(plan.going.toString2());


            for (int i = 0; i < 3; i++) {
                System.out.println("\nProcessing...\n");
            }
            System.out.println("Here is your itinerary:");
            System.out.println(plan.toString());
        } catch (Exception e) {
            System.out.println("Whoops! Something went wrong, please try again!");
        }

    }
}
