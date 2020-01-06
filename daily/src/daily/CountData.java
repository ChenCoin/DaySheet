/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package daily;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.application.Platform;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.poifs.filesystem.NotOLE2FileException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public abstract class CountData {
	
	protected abstract void showText(String text);
	
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
				List<DailyResult> results = new ArrayList<>();
				for (int index = 1; index < rowNumbers; index++) {
					DailyResult result = new DailyResult();
					Row row = sheet.getRow(index);
					Cell cell0 = row.getCell(0);
					if (cell0 != null) {
						try {
							switch (cell0.getCellType()) {
								case STRING:
									result.time = LocalDate.parse(cell0.toString(),
											DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
									break;
								case NUMERIC:
									result.time = cell0.getDateCellValue().toInstant()
											.atZone(ZoneId.systemDefault()).toLocalDate();
									break;
							}
						} catch (Exception e) {
							log(e.getMessage());
						}
					}
					Cell cell9 = row.getCell(9);
					if (cell9 != null && cell9.getCellType() == CellType.STRING) {
						result.name = cell9.toString();
					}
					Cell cell1 = row.getCell(1);
					if (cell1 != null && cell1.getCellType() == CellType.STRING) {
						String inOrOut = cell1.toString().toUpperCase();
						if (inOrOut.contains("IN")) {
							result.inOrOut = 0;
						} else if (inOrOut.contains("OUT")) {
							result.inOrOut = 1;
						} else {
							result.inOrOut = -1;
						}
					}
					results.add(result);
				}
				createResult(results);
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
	
	protected abstract void afterHandle(String content);
	
	class DailyResult {
		
		LocalDate time;
		int inOrOut = -1;
		String name = "";
		
		@Override
		public String toString() {
			String type;
			switch (inOrOut) {
				case 0:
					type = "in";
					break;
				case 1:
					type = "out";
					break;
				default:
					type = "unknown";
			}
			return "time:" + time + " name:" + name + " " + type;
		}
	}
	
	class Count {
		
		String name;
		
		List<List<DailyResult>> data;
		
		public Count(String name, List<List<DailyResult>> data) {
			this.name = name;
			this.data = data;
		}
		
		@Override
		public String toString() {
			return "name:" + name + " count:" + data.size();
		}
	}
	
	private void createResult(List<DailyResult> results) {
		List<Count> finalResult = new ArrayList<>();
		log("数据总条数" + results.size());
		Map<String, List<DailyResult>> dataOfEveryOne = new HashMap<>();
		results.parallelStream()
				.filter(item -> item != null && item.name != null)
				.forEach(item
						-> dataOfEveryOne.computeIfAbsent(item.name, k -> new ArrayList<>())
						.add(item));
		dataOfEveryOne.forEach((key, value) -> finalResult.add(new Count(key, countEveryOne(value))));
		finalResult.forEach(System.out::println);
	}
	
	private List<List<DailyResult>> countEveryOne(List<DailyResult> data) {
		List<List<DailyResult>> result = new ArrayList<>();
		Map<Integer, List<DailyResult>> dataOfEveryDay = new HashMap<>();
		data.parallelStream()
				.forEach(item -> {
					if (item != null && item.time != null) {
						dataOfEveryDay.computeIfAbsent(item.time.getDayOfYear(),
								k -> new ArrayList<>()).add(item);
					}
				});
		dataOfEveryDay.forEach((k, v) -> result.add(countEveryDay(v)));
		return result;
	}
	
	private List<DailyResult> countEveryDay(List<DailyResult> data) {
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
