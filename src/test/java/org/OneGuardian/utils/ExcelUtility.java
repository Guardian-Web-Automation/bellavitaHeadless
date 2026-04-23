package org.OneGuardian.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;

public class ExcelUtility {

    private static final Logger log = LogManager.getLogger(ExcelUtility.class);

    // Returns a 2D Object array — each row is one test case, each column is one parameter
    // TestNG @DataProvider expects exactly this format: Object[][]
    public static Object[][] getTestData(String filePath, String sheetName) {

        Object[][] data = null;

        try {
            // FileInputStream opens the Excel file from disk
            FileInputStream fis = new FileInputStream(filePath);

            // XSSFWorkbook handles .xlsx format (HSSF handles old .xls)
            Workbook workbook = new XSSFWorkbook(fis);

            // Get the sheet by name
            Sheet sheet = workbook.getSheet(sheetName);

            // getLastRowNum() → index of last row (0-based), so total rows = lastRowNum
            // We subtract 1 because row 0 is the header row — we skip it
            int totalRows = sheet.getLastRowNum();        // e.g. 3 data rows = index 3
            int totalCols = sheet.getRow(0).getLastCellNum(); // number of columns

            log.info("Excel file loaded → Rows: " + totalRows + ", Cols: " + totalCols);

            // Initialize the 2D array with data rows only (skip header row 0)
            data = new Object[totalRows][totalCols];

            // Start from row 1 — row 0 is the header (SearchKeyword, ExpectedResult)
            for (int i = 1; i <= totalRows; i++) {
                Row row = sheet.getRow(i);

                for (int j = 0; j < totalCols; j++) {
                    Cell cell = row.getCell(j);

                    // getStringCellValue() reads the cell as text
                    // For mixed types use DataFormatter for safer reading
                    data[i - 1][j] = cell.getStringCellValue();

                    log.debug("Cell[" + i + "][" + j + "] = " + data[i - 1][j]);
                }
            }

            workbook.close();
            fis.close();

        } catch (IOException e) {
            log.error("Failed to read Excel file: " + e.getMessage());
        }

        return data;
    }
}