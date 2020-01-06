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
			CountData countData = new CountData() {
				@Override
				protected void showText(String text) {
					tipLabel.setText(text);
				}

				@Override
				protected void afterHandle(String content) {
					mainBtn.setDisable(false);
					tipLabel.setText(content);
				}
			};
			countData.countXlsFile(dataXlsFile, peopleXlsFile);
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

}
