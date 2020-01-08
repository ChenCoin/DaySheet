package daily.model;

import daily.common.RawPerson;
import daily.common.RawTick;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.application.Platform;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class DataExport {

    public interface Callback {

	void finish(String content);
    }

    private Callback callback;

    String[] titles = {"姓名", "日期", "数据"};

    public DataExport(Callback callback) {
	this.callback = callback;
    }

    private int rowIndex = 0;

    public void export(List<RawPerson> data, String fileName) {
	new Thread(() -> {
	    try {
		File file = new File(fileName);
		if (file.exists()) {
		    file.delete();
		}
		Workbook wb = new HSSFWorkbook();
		FileOutputStream fs = new FileOutputStream(file);
		Sheet sheet = wb.createSheet("0");

		Row rowTitle = sheet.createRow(0);
		IntStream.range(0, titles.length)
			.forEach(i -> rowTitle.createCell(i).setCellValue(titles[i]));

		rowIndex = 0;
		data.forEach(person -> {
		    person.data.forEach(day -> {
			List<RawTick> noNullDay = day.data.stream()
				.filter(item -> (item.time != null))
				.collect(Collectors.toList());
			if (noNullDay.size() > 0) {
			    Row row = sheet.createRow(++rowIndex);
			    row.createCell(0).setCellValue(person.name);
			    String today = noNullDay.get(0).time
				    .format(DateTimeFormatter.ofPattern("MM-dd"));
			    row.createCell(1).setCellValue(today);
			    IntStream.range(0, noNullDay.size()).forEach(index -> {
				try {
				    RawTick item = noNullDay.get(index);
				    String tick = item.time.format(
					    DateTimeFormatter.ofPattern("HH:mm:ss"));
				    Cell cell = row.createCell(index + 2);
				    cell.setCellValue(tick);
				} catch (Exception e) {
				    System.err.println(e.getMessage());
				}
			    });
			}
		    });
		});
		wb.write(fs);
		fs.close();
		wb.close();
		finish("导出成功");
	    } catch (FileNotFoundException e) {
		finish("导出失败，" + e.getMessage());
	    } catch (IOException e) {
		finish("导出失败，" + e.getMessage());
	    }
	}).start();
    }

    private void finish(String content) {
	Platform.runLater(() -> callback.finish(content));
    }
}
