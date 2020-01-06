package daily;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class DataExport {

	public void export(String fileName) throws IllegalArgumentException, IllegalAccessException, IOException {
		Workbook wb = new HSSFWorkbook();
		FileOutputStream fs = new FileOutputStream(fileName);
		Sheet sheet = wb.createSheet("");
		Row rowTitle = sheet.createRow(0);//表头
//		for (int i = 0; i < titls.length; i++) {
//			//创建单元格
//			Cell cell = rowTitle.createCell(i);
//			//填充值
//			cell.setCellValue(titls[i]);
//		}
//		for (int i = 0; i < ls.size(); i++) {
//			//创建row
//			Row rowData = sheet.createRow(i + 1);//行数据
//			Field[] f = clz.getDeclaredFields();
//			for (int j = 0; j < titls.length; j++) {
//				//创建单元格
//				Cell cell = rowData.createCell(j);
//				//获取属性的名称
//				f[j].setAccessible(true);
//				cell.setCellValue(f[j].get(ls.get(i)).toString());
//				f[j].setAccessible(false);
//			}
//		}

		wb.write(fs);
		fs.close();
		wb.close();
	}
}
