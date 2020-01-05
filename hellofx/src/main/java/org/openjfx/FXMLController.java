package org.openjfx;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import org.apache.poi.poifs.filesystem.NotOLE2FileException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class FXMLController {

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

    public void initialize() {
        dataBtn.setOnMouseClicked(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("选择Excel文件");
            File file = fileChooser.showOpenDialog(MainApp.mainStage);
            if (file != null) {
                dataXlsFile = file;
                dataLabel.setText(file.getPath());
                afterSelectFile();
            }
        });
        peopleBtn.setOnMouseClicked(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("选择Excel文件");
            File file = fileChooser.showOpenDialog(MainApp.mainStage);
            if (file != null) {
                peopleXlsFile = file;
                peopleLabel.setText(file.getPath());
                afterSelectFile();
            }
        });
        mainBtn.setOnMouseClicked(event -> {
            tipLabel.setText("处理中...");
            mainBtn.setDisable(true);
//            new Thread(() -> {
                try {
                    if (dataXlsFile.exists() && peopleXlsFile.exists()) {
                        System.out.println("exists");
                    }
                    Workbook dataWorkbook = WorkbookFactory.create(dataXlsFile);
                    Workbook peopleWorkbook = WorkbookFactory.create(peopleXlsFile);
                    System.out.println("after create");
                    int numberOfSheets = dataWorkbook.getNumberOfSheets();
                    if (numberOfSheets <= 0) throw new Exception("文件内容不符合要求(1001)");
                    Sheet sheet = dataWorkbook.getSheetAt(0);
                    int rowNumbers = sheet.getLastRowNum() + 1;
                    if (rowNumbers < 1) throw new Exception("文件内容不符合要求(1002)");
                    String content = sheet.getRow(0).getCell(1).toString();
                    Platform.runLater(() -> afterHandle(content));
                } catch (NotOLE2FileException e) {
                    Platform.runLater(() -> afterHandle("文件不是Excel文件(1003)"));
                } catch (Exception e) {
                    e.printStackTrace();
                    Platform.runLater(() -> afterHandle("出错了，" + e.getMessage() + "(1004)"));
                }
//            }).start();
        });
    }

    private void afterSelectFile() {
        if (dataXlsFile != null && peopleXlsFile != null)
            mainBtn.setDisable(false);
        else mainBtn.setDisable(true);
        tipLabel.setText("");
    }

    private void afterHandle(String content) {
        mainBtn.setDisable(false);
        tipLabel.setText(content);
    }

}
