package commonTechs;

import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class ExportSWTTableToExcel {

	
	public static synchronized XSSFWorkbook createWorkbookFromTable(Table table) {
	    // create a workbook
	    XSSFWorkbook wb = new XSSFWorkbook();

	    // add a worksheet
	    XSSFSheet sheet = wb.createSheet("My Table Data");

	    // shade the background of the header row
	    XSSFCellStyle headerStyle = wb.createCellStyle();
	    headerStyle.setAlignment(HorizontalAlignment.CENTER);

	    // add header row
	    TableColumn[] columns = table.getColumns();
	    int rowIndex = 0;
	    int cellIndex = 0;
	    XSSFRow header = sheet.createRow((short) rowIndex++);
	    for (TableColumn column : columns) {
	        String columnName = column.getText();
	        XSSFCell cell = header.createCell(cellIndex++);
	        cell.setCellValue(column.getText());
	        cell.setCellStyle(headerStyle);
	    }

	    // add data rows
	    TableItem[] items = table.getItems();

	    System.out.println("about to create data rowcount is " + items.length);
	    
	    for (TableItem item : items) {
	        // create a new row
	        XSSFRow row = sheet.createRow((short) rowIndex++);
	        cellIndex = 0;

		    System.out.println("have read rowIndex " + rowIndex);

	        for (int colNum = 0; colNum < columns.length; colNum++) {
	            // create a new cell
	            String columnName = columns[colNum].getText();
	            XSSFCell cell = row.createCell(cellIndex++);

	            // set the horizontal alignment (default to RIGHT)
	            XSSFCellStyle cellStyle = wb.createCellStyle();
	            //ha = HorizontalAlignment.RIGHT;
	            //cellStyle.setAlignment(ha);
	            cell.setCellStyle(cellStyle);

			    System.out.println("about to apply value at rowIndex colNum " + rowIndex + " " + colNum);
	            
	            // set the cell's value
	            String text = item.getText(colNum);
	            cell.setCellValue(text);
	            
			    System.out.println("about to apply value text " + text);
	            
	        }
	    }

	    // autofit the columns
	    for (int i = 0; i < columns.length; i++) {
	        sheet.autoSizeColumn((short) i);
	    }

	    sheet.createFreezePane(1, 1);
	    
	    return wb;
	}	
	
}
