/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package daily.model;

import daily.common.RawDay;
import daily.common.RawPerson;
import daily.common.RawTick;
import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javafx.application.Platform;
import org.apache.poi.poifs.filesystem.NotOLE2FileException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class XlsToRawData {

    public interface CallBack {

	void showText(String text);

	void afterHandle(String content);

	void finish(List<RawPerson> data);
    }

    private CallBack callback;

    public XlsToRawData(CallBack callback) {
	this.callback = callback;
    }

    private void showText(String text) {
	callback.showText(text);
    }

    private void afterHandle(String content) {
	callback.afterHandle(content);
    }

    public void countXlsFile(File dataXlsFile, File peopleXlsFile) {
	new Thread(() -> {
	    try {
		Workbook dataWorkbook = WorkbookFactory.create(dataXlsFile);
		Workbook peopleWorkbook = WorkbookFactory.create(peopleXlsFile);
		int numberOfSheets = dataWorkbook.getNumberOfSheets();
		if (numberOfSheets <= 0) {
		    throw new Exception("文件内容不符合要求(1001)");
		}
		Sheet sheet = dataWorkbook.getSheetAt(0);
		int rowNumbers = sheet.getLastRowNum() + 1;
		if (rowNumbers < 1) {
		    throw new Exception("文件内容不符合要求(1002)");
		}
		List<RawTick> results = new ArrayList<>();
		for (int index = 1; index < rowNumbers; index++) {
		    RawTick tick = new RawTick();
		    Row row = sheet.getRow(index);
		    Cell cell0 = row.getCell(0);
		    if (cell0 != null) {
			try {
			    switch (cell0.getCellType()) {
				case STRING:
				    tick.time = LocalDateTime.parse(cell0.toString(),
					    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
				    break;
				case NUMERIC:
				    tick.time = cell0.getDateCellValue().toInstant()
					    .atZone(ZoneId.systemDefault()).toLocalDateTime();
				    break;
			    }
			} catch (Exception e) {
			    log("line " + index + " date in raw data error " + e.getMessage());
			}
		    } else {
			log("line " + index + " date in raw data is null");
		    }
		    Cell cell9 = row.getCell(9);
		    if (cell9 != null && cell9.getCellType() == CellType.STRING) {
			tick.name = cell9.toString();
		    } else {
			log("line " + index + " name in raw data is null or error");
		    }
		    Cell cell1 = row.getCell(1);
		    if (cell1 != null && cell1.getCellType() == CellType.STRING) {
			String inOrOut = cell1.toString().toUpperCase();
			if (inOrOut.contains("IN")) {
			    tick.inOrOut = 0;
			} else if (inOrOut.contains("OUT")) {
			    tick.inOrOut = 1;
			} else {
			    tick.inOrOut = -1;
			}
		    }
		    results.add(tick);
		}
		createResult(results);
		dataWorkbook.close();
		peopleWorkbook.close();
		Platform.runLater(() -> afterHandle("处理完成"));
	    } catch (NotOLE2FileException e) {
		Platform.runLater(() -> afterHandle("文件不是Excel文件(1003)"));
	    } catch (FileNotFoundException e) {
		e.printStackTrace();
		Platform.runLater(() -> afterHandle("出错了，文件无法打开，可能被其它软件打开了(1004)"));
	    } catch (Exception e) {
		e.printStackTrace();
		Platform.runLater(() -> afterHandle("出错了，" + e.getMessage() + "(1005)"));
	    }
	}).start();
    }

    private void createResult(List<RawTick> results) {
	List<RawPerson> finalResult = new ArrayList<>();
	log("数据总条数" + results.size());
	Map<String, List<RawTick>> dataOfEveryOne = results.stream()
		.collect(Collectors.groupingBy(item -> item.name));
	dataOfEveryOne.forEach((key, value)
		-> finalResult.add(new RawPerson(key, countEveryOne(value))));
	finalResult.sort((i, j) -> i.name.compareTo(j.name));
	callback.finish(finalResult);
    }

    private List<RawDay> countEveryOne(List<RawTick> data) {
	List<RawDay> result = new ArrayList<>();
	Map<Integer, List<RawTick>> dataOfEveryDay = data.stream()
		.collect(Collectors.groupingBy(item -> item.time.getDayOfYear()));
	dataOfEveryDay.forEach((k, v) -> {
	    RawDay item = new RawDay(k, countEveryDay(v));
	    result.add(item);
	});
	result.sort((i, j) -> Integer.compare(i.day, j.day));
	return result;
    }

    private List<RawTick> countEveryDay(List<RawTick> data) {
	data.sort((item1, item2) -> {
	    if (item1.time.isBefore(item2.time)) {
		return -1;
	    } else if (item1.time.isAfter(item2.time)) {
		return 1;
	    } else {
		return 0;
	    }
	});
	return data;
    }

    private void log(Object content) {
	System.out.println(content);
    }
}
