/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package daily;

import daily.common.RawPerson;
import daily.model.DataExport;
import daily.model.XlsToRawData;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Controller implements Initializable {

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
	XlsToRawData handler = new XlsToRawData(new XlsToRawData.CallBack() {
	    @Override
	    public void showText(String text) {
		tipLabel.setText(text);
	    }

	    @Override
	    public void afterHandle(String content) {
		mainBtn.setDisable(false);
		tipLabel.setText(content);
	    }

	    @Override
	    public void finish(List<RawPerson> data) {
		rawDataCreated(data);
	    }
	});
	mainBtn.setOnMouseClicked(event -> {
	    tipLabel.setText("处理中...");
	    mainBtn.setDisable(true);
	    handler.countXlsFile(dataXlsFile, peopleXlsFile);
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

    private void rawDataCreated(List<RawPerson> data) {
	new DataExport(tipLabel::setText).export(data, "./result.xls");
    }

}
