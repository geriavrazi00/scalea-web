package com.scalea.utils;

import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtil {
	public static void addHeaders(Workbook workbook, Sheet sheet, int rowNum, String[] headers) {
		Row header = sheet.createRow(rowNum);

		CellStyle headerStyle = workbook.createCellStyle();

		XSSFFont font = ((XSSFWorkbook) workbook).createFont();
		font.setFontName("Arial");
		font.setFontHeightInPoints((short) 10);
		font.setBold(true);
		headerStyle.setFont(font);
		
		for (int i = 0; i < headers.length; i++) {
			Cell headerCell = header.createCell(i);
			headerCell.setCellValue(headers[i]);
			headerCell.setCellStyle(headerStyle);
			
			sheet.autoSizeColumn(i);
		}
	}
	
	public static void addRows(Sheet sheet, int startRow, List<Object[]> data) {
		for (int i = 0; i < data.size(); i++) {
			Row row = sheet.createRow(startRow + i);
			Object[] currentRow = data.get(i);
			
			for (int j = 0; j < currentRow.length; j++) {
				Cell cell = row.createCell(j);
				
				if (currentRow[j] instanceof String) {
					cell.setCellValue((String) currentRow[j]);
				} else if (currentRow[j] instanceof Double) {
					cell.setCellValue((Double) currentRow[j]);
				} else if (currentRow[j] instanceof Integer) {
					cell.setCellValue((Integer) currentRow[j]);
				}else if (currentRow[j] instanceof Long) {
					cell.setCellValue((Long) currentRow[j]);
				} else if (currentRow[j] instanceof Date) {
					String formattedDate = Utils.albanianFullDateFormat((Date) currentRow[j]);
					cell.setCellValue(formattedDate);
				} else if (currentRow[j] == null) {
					cell.setCellValue("-");
				}
				
				sheet.autoSizeColumn(j);
			}
		}
	}
}
