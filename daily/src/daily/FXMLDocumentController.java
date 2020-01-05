/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package daily;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.poifs.filesystem.NotOLE2FileException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Coin
 */
public class FXMLDocumentController implements Initializable {

	@FXML
	private Button dataBtn;
	@FXML
	private Button peopleBtn;
	@FXML
	private Label dataLabel;
	@FXML
	private Label peopleLabel;
	@FXML
	private Button mainBtn;
	@FXML
	private Label tipLabel;

	private File dataXlsFile;
	private File peopleXlsFile;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		dataBtn.setOnMouseClicked(event -> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("选择Excel文件");
			File file = fileChooser.showOpenDialog(new Stage());
			if (file != null) {
				dataXlsFile = file;
				dataLabel.setText(file.getPath());
				afterSelectFile();
			}
		});
		peopleBtn.setOnMouseClicked(event -> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("选择Excel文件");
			File file = fileChooser.showOpenDialog(new Stage());
			if (file != null) {
				peopleXlsFile = file;
				peopleLabel.setText(file.getPath());
				afterSelectFile();
			}
		});
		mainBtn.setOnMouseClicked(event -> {
			tipLabel.setText("处理中...");
			mainBtn.setDisable(true);
			new Thread(() -> {
				try {
					if (dataXlsFile.exists() && peopleXlsFile.exists()) {
						System.out.println("exists");
					}
					Workbook dataWorkbook = WorkbookFactory.create(dataXlsFile);
					Workbook peopleWorkbook = WorkbookFactory.create(peopleXlsFile);
					System.out.println("after create");
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
						result.time = sheet.getRow(index).getCell(0).getDateCellValue();
						result.name = sheet.getRow(index).getCell(9).toString();
						String inOrOut = sheet.getRow(index).getCell(1).toString().toUpperCase();
						if (inOrOut.contains("IN")) {
							result.inOrOut = 0;
						} else if (inOrOut.contains("OUT")) {
							result.inOrOut = 1;
						} else {
							result.inOrOut = -1;
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
		});
	}

	private void afterSelectFile() {
		if (dataXlsFile != null && peopleXlsFile != null) {
			mainBtn.setDisable(false);
		} else {
			mainBtn.setDisable(true);
		}
		tipLabel.setText("");
	}

	private void afterHandle(String content) {
		mainBtn.setDisable(false);
		tipLabel.setText(content);
	}

	class DailyResult {

		Date time;
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

	private void createResult(List<DailyResult> results) {
		results.forEach(item -> System.out.println(item.toString()));
		Map<String, List<DailyResult>> dataOfEveryOne = new HashMap<>();
		results.forEach(item -> dataOfEveryOne
				.computeIfAbsent(item.name, k -> new ArrayList<>()).add(item));
		dataOfEveryOne.forEach((key, value) -> {
			Map<String, List<DailyResult>> dataOfEveryDay = new HashMap<>();
			value.forEach(item -> {
				String date = new SimpleDateFormat("MM-dd").format(item.time);
				dataOfEveryDay.computeIfAbsent(date, k -> new ArrayList<>()).add(item);
			});
			dataOfEveryDay.forEach((k, v) -> countEveryDay(v));
		});
	}

	private void countEveryDay(List<DailyResult> data) {
		data.sort((item1, item2) -> {
			if (item1.time.before(item2.time)) {
				return -1;
			} else if (item1.time.after(item2.time)) {
				return 1;
			} else {
				return 0;
			}
		});
		System.err.println("count " + data.toString());
	}

}
