package localendar;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class Benchmark {
    private static Benchmark single_instance = null;

    private int pass;
    private Sheet sheet;

    private Workbook workbook;
    private Benchmark(){
        pass = 5;
        try {
            FileInputStream fis = new FileInputStream("data.xlsx");
            workbook = new XSSFWorkbook(fis);
            // 0 = 10, 1 = 100, 2 = 1000
            sheet = workbook.getSheetAt(3);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static Benchmark getInstance() {
        if (single_instance == null) {
            single_instance = new Benchmark();
        }
        return single_instance;
    }

    /**
     * Mode: Determines which cells to write
     * 1: Loading
     * 2: Searching
     * 3: Creating
     * 4: Updating
     * 5: Deleting
     * 6: Filter and sort
     *
     */
    public void getTime(long startTime, long endTime, int mode) {
        double nanoSeconds = endTime - startTime;
        double milliSeconds = nanoSeconds/1000000;
        if(mode == 7){
            try {
                FileWriter writer = new FileWriter("time.txt",true);
                writer.write(milliSeconds + "\n");
                writer.close();
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        } else{
            Row row = sheet.getRow(1+mode);
            // 1 because 0 indexed, and real daat writing starts at the 3rd column
            if (row == null) row = sheet.createRow(1+mode);
            Cell cell = row.getCell(pass);
            if (cell == null) cell = row.createCell(pass);
            cell.setCellValue(milliSeconds);
            try (FileOutputStream fos = new FileOutputStream("data.xlsx")) {
                workbook.write(fos);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void getSpace(int mode){
        Runtime rt = Runtime.getRuntime();
        long total_mem = rt.totalMemory();
        long free_mem = rt.freeMemory();
        long used_mem = total_mem - free_mem;
        if(mode == 7){
            try {
                FileWriter writer = new FileWriter("space.txt",true);
                writer.write(used_mem+ "\n");
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Row row = sheet.getRow(13 + mode);
            // 10 because 0 indexed, and real daat writing starts at the 12th column
            if (row == null) row = sheet.createRow(13 + mode);
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
}
