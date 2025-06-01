package localendar;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Benchmark {
    private static Benchmark single_instance = null;

    private int pass;
    private Sheet sheet;

    private Workbook workbook;
    private Benchmark(){
        pass = 1;
        try {
            FileInputStream fis = new FileInputStream("data.xlsx"); // Opens the excel sheet
            workbook = new XSSFWorkbook(fis);
            // 0 = 10, 1 = 100, 2 = 1000
            sheet = workbook.getSheetAt(0); // The index of the tabs for the data, 0 is 10 tasks, etc
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static Benchmark getInstance() {
        if (single_instance == null) {
            single_instance = new Benchmark();
        }
        return single_instance; // Singleton design pattern, no need to keep initializing
    }

    /**
     * Mode: Determines which cells to write
     * 1: Loading
     * 2: Searching
     * 3: Creating
     * 4: Updating
     * 5: Deleting
     * 6: Filter and sort
     */
    public void getTime(long startTime, long endTime, int mode) {
        double nanoSeconds = endTime - startTime;
        double milliSeconds = nanoSeconds/1000000;
        Row row = sheet.getRow(1+mode);
        // 1 because 0 indexed, and real daat writing starts at the 3rd column
        if (row == null) row = sheet.createRow(1+mode);
        Cell cell = row.getCell(pass);
        if (cell == null) cell = row.createCell(pass);
        cell.setCellValue(milliSeconds);
        try (FileOutputStream fos = new FileOutputStream("data.xlsx")) {
            workbook.write(fos);
            System.out.println("Benchmark data saved to data.xlsx.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void getSpace(int mode){
        Runtime rt = Runtime.getRuntime();
        long total_mem = rt.totalMemory();
        long free_mem = rt.freeMemory();
        long used_mem = total_mem - free_mem;
        Row row = sheet.getRow(10+mode);
        // 10 because 0 indexed, and real daat writing starts at the 12th column
        if (row == null) row = sheet.createRow(10+mode);
        Cell cell = row.getCell(pass);
        if (cell == null) cell = row.createCell(pass);
        cell.setCellValue(used_mem);
        try (FileOutputStream fos = new FileOutputStream("data.xlsx")) {
            workbook.write(fos);
            System.out.println("Benchmark data saved to data.xlsx.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}