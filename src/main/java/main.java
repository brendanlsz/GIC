import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
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
        for (Flight i : flights) {
            System.out.println(i.arrivalTime);
        }
    }
}
