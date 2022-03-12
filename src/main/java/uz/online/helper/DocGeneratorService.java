package uz.online.helper;

import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.color.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import uz.online.config.DocGenerator;
import uz.online.model.*;
import uz.online.sms.SendSms;

import java.io.DataInput;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


import static uz.online.DB.*;
import static uz.online.config.BotConfig.adminId;




public class DocGeneratorService implements DocGenerator {

    static SendSms smsService = new SendSms();

    @Override
    public File pdfGenerator(User currentUser) {


        File file = new File("src/main/resources/results/" + currentUser.getId() + ".pdf");

        try (PdfWriter writer = new PdfWriter("src/main/resources/results/" + currentUser.getId() + ".pdf")) {

            PdfDocument pdfDocument = new PdfDocument(writer);

            pdfDocument.setDefaultPageSize(PageSize.A4);
            pdfDocument.addNewPage();

            Document document = new Document(pdfDocument);
            Paragraph paragraph = new Paragraph("STATS\n").setFontSize(25).setHorizontalAlignment(HorizontalAlignment.CENTER).setTextAlignment(TextAlignment.CENTER);
            document.add(paragraph);

            float[] pointColumn = {120F, 380F};
            Table table = new Table(pointColumn);

            table.setTextAlignment(TextAlignment.CENTER).setHorizontalAlignment(HorizontalAlignment.CENTER);
            List<TestHistory> collect = new ArrayList<>();
            if (currentUser.getId().longValue() == adminId) {
                collect.addAll(testHistoryList);
            } else {
                collect = testHistoryList.stream().filter(testHistory -> testHistory.getUser().getId().longValue() == currentUser.getId().longValue()).collect(Collectors.toList());
            }


            for (TestHistory testHistory : collect) {
                table.addCell(new Cell().add("User").setBackgroundColor(new DeviceRgb(51, 153, 102))).setFontColor(Color.WHITE);
                table.addCell(new Cell().add("" + testHistory.getUser().getFirstName()).setBackgroundColor(Color.WHITE)).setFontColor(Color.BLACK);
                table.addCell(new Cell().add("Subject").setBackgroundColor(new DeviceRgb(51, 153, 102))).setFontColor(Color.WHITE);
                table.addCell(new Cell().add("" + testHistory.getSubject().getName()).setBackgroundColor(Color.WHITE)).setFontColor(Color.BLACK);
                table.addCell(new Cell().add("Result").setBackgroundColor(new DeviceRgb(51, 153, 102))).setFontColor(Color.WHITE);
                table.addCell(new Cell().add("" + testHistory.getResult() + " %").setBackgroundColor(new DeviceRgb(230, 230, 230))).setFontColor(Color.BLACK);
                table.addCell(new Cell().add("Duration").setBackgroundColor(new DeviceRgb(51, 153, 102))).setFontColor(Color.WHITE);
                table.addCell(new Cell().add("" + testHistory.getDuration()).setBackgroundColor(Color.WHITE)).setFontColor(Color.BLACK);
                table.addCell(new Cell().add("Date").setBackgroundColor(new DeviceRgb(51, 153, 102))).setFontColor(Color.WHITE);
                table.addCell(new Cell().add("" + testHistory.getCreatedAt().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))).setBackgroundColor(Color.WHITE)).setFontColor(Color.BLACK);
                table.addCell(new Cell().add("")).setBackgroundColor(new DeviceRgb(51, 153, 102));
                table.addCell(new Cell().add("")).setBackgroundColor(new DeviceRgb(51, 153, 102));
            }

            document.add(table);
            System.out.println("PDF DOCUMENT");
            document.close();
        } catch (IOException e) {
            System.out.println("SERVER ERROR");
        }
        return file;

    }

    @Override
    public File answerSheet(User currentUser, TestHistory selectedTest) {
        File file = new File("src/main/resources/results/" + currentUser.getId() + ".pdf");

        try (PdfWriter writer = new PdfWriter("src/main/resources/results/" + currentUser.getId() + ".pdf")) {

            PdfDocument pdfDocument = new PdfDocument(writer);

            pdfDocument.setDefaultPageSize(PageSize.A4);
            pdfDocument.addNewPage();

            Document document = new Document(pdfDocument);
            Paragraph paragraph = new Paragraph("SHEET\n").setFontSize(25).setHorizontalAlignment(HorizontalAlignment.CENTER).setTextAlignment(TextAlignment.CENTER);
            document.add(paragraph);

            float[] pointColumn = {120F, 380F};
            Table table = new Table(pointColumn);

            table.setTextAlignment(TextAlignment.CENTER).setHorizontalAlignment(HorizontalAlignment.CENTER);

            table.addCell(new Cell().add("User").setBackgroundColor(new DeviceRgb(51, 153, 102))).setFontColor(Color.WHITE);
            table.addCell(new Cell().add("" + selectedTest.getUser().getFirstName()).setBackgroundColor(Color.WHITE)).setFontColor(Color.BLACK);
            table.addCell(new Cell().add("Subject").setBackgroundColor(new DeviceRgb(51, 153, 102))).setFontColor(Color.WHITE);
            table.addCell(new Cell().add("" + selectedTest.getSubject().getName()).setBackgroundColor(Color.WHITE)).setFontColor(Color.BLACK);
            table.addCell(new Cell().add("Result").setBackgroundColor(new DeviceRgb(51, 153, 102))).setFontColor(Color.WHITE);
            table.addCell(new Cell().add("" + selectedTest.getResult() + " %").setBackgroundColor(new DeviceRgb(230, 230, 230))).setFontColor(Color.BLACK);
            table.addCell(new Cell().add("Duration").setBackgroundColor(new DeviceRgb(51, 153, 102))).setFontColor(Color.WHITE);
            table.addCell(new Cell().add("" + selectedTest.getDuration()).setBackgroundColor(Color.WHITE)).setFontColor(Color.BLACK);
            table.addCell(new Cell().add("Date").setBackgroundColor(new DeviceRgb(51, 153, 102))).setFontColor(Color.WHITE);
            table.addCell(new Cell().add("" + selectedTest.getCreatedAt().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))).setBackgroundColor(Color.WHITE)).setFontColor(Color.BLACK);
            table.addCell(new Cell().add("")).setBackgroundColor(new DeviceRgb(51, 153, 102));
            table.addCell(new Cell().add("")).setBackgroundColor(new DeviceRgb(51, 153, 102));

            float[] pointColumn2 = {500F};
            Table table2 = new Table(pointColumn2);
            RemoveBorder(table2);
            table2.setTextAlignment(TextAlignment.CENTER).setHorizontalAlignment(HorizontalAlignment.CENTER);

            table2.addCell(new Cell().add(""));
            table2.addCell(new Cell().add(""));
            int i = 1;
            for (UserAnswer userAnswer : selectedTest.getAnswerList()) {
                Test test = findTest(userAnswer.getAnswer());
                table2.addCell(new Cell().add("Test").setBackgroundColor(new DeviceRgb(153, 255, 204)).setFontColor(Color.BLACK));
                table2.addCell(new Cell().add(i + ". " + test.getName()).setBackgroundColor(new DeviceRgb(0, 204, 255))).setFontColor(Color.BLACK);
                table2.addCell(new Cell().add("Answer")).setBackgroundColor(new DeviceRgb(0, 255, 0)).setFontColor(Color.BLACK);;
                if (userAnswer.getAnswer().isCorrect()) {
                    table2.addCell(new Cell().add("Status : Correct " + userAnswer.getAnswer().getBody()).setBackgroundColor(Color.BLUE).setFontColor(Color.BLACK));
                } else {
                    table2.addCell(new Cell().add("Status : InCorrect " + userAnswer.getAnswer().getBody()).setBackgroundColor(Color.RED).setFontColor(Color.BLACK));
                }
                table2.addCell(new Cell().add(""));
                table2.addCell(new Cell().add(""));
                i++;
            }

            RemoveBorder(table2);

            document.add(table);
            document.add(table2);
            System.out.println("PDF DOCUMENT");
            document.close();
        } catch (IOException e) {
            System.out.println("SERVER ERROR");
        }
        return file;
    }

    private Test findTest(Answer userAnswer) {
        Test selectedTest = null;
        for (Subject subject : subjectList) {
            for (Test test : subject.getTests()) {
                for (Answer answer : test.getAnswers()) {
                    if (answer.getId().equals(userAnswer.getId())) {
                        selectedTest = test;
                        break;
                    }
                }
            }
        }
        return selectedTest;
    }

    public File excelConventor(User currentUser) {

        File file = new File(userExcel);
        try {
            FileOutputStream out = new FileOutputStream(new File(userExcel));
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet spreadsheet = workbook.createSheet(" Cloth Data ");
            XSSFRow row;
            XSSFRow row2;

            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);


            int rowid = 0;
            row = spreadsheet.createRow(rowid++);
            row.createCell(0).setCellValue("#");
            row.getCell(0).setCellStyle(cellStyle);
            spreadsheet.autoSizeColumn(0);
            row.createCell(1).setCellValue("Name");
            row.getCell(1).setCellStyle(cellStyle);
            spreadsheet.autoSizeColumn(1);
            row.createCell(2).setCellValue("Surname");
            row.getCell(2).setCellStyle(cellStyle);
            spreadsheet.autoSizeColumn(2);

            row.createCell(3).setCellValue("Phone Number");
            row.getCell(3).setCellStyle(cellStyle);
            spreadsheet.autoSizeColumn(3);

            int i = 1;
            for (User user : userList) {
                row = spreadsheet.createRow(rowid++);
                row.createCell(0).setCellValue(i++);
                row.getCell(0).setCellStyle(cellStyle);
                spreadsheet.autoSizeColumn(0);
                row.createCell(1).setCellValue(user.getFirstName());
                row.getCell(1).setCellStyle(cellStyle);
                spreadsheet.autoSizeColumn(1);
                row.createCell(2).setCellValue(user.getLastName());
                row.getCell(2).setCellStyle(cellStyle);
                spreadsheet.autoSizeColumn(2);
                row.createCell(3).setCellValue(user.getPhoneNumber());
                row.getCell(3).setCellStyle(cellStyle);
                spreadsheet.autoSizeColumn(3);
            }

            workbook.write(out);
            out.close();
            file = new File(userExcel);
            return file;
        } catch (IOException e) {
        }
        return file;
    }


    private void RemoveBorder(Table table) {
        for (IElement iElement : table.getChildren()) {
            ((Cell) iElement).setBorder(Border.NO_BORDER);
        }
    }

}
