package fg.binlist;

import static org.assertj.core.api.Assertions.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import static fg.binlist.ExcelUtilsTest.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class BinListControllerTest {

	private static final String API_BINLIST = "https://lookup.binlist.net/";

	@Autowired
	TestRestTemplate testRestTemplate;

	@Test
	public void getBinData_whenBinIsValid_ReceiveOk() {
		String bin = "55790990";
		ResponseEntity<String> response = getBinData(String.class, bin);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
	}

	@Test
	public void getBinData_whenBinIsInvalid_ReceiveBadRequest() {
		String bin = "557";
		ResponseEntity<String> response = getBinData(String.class, bin);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	public void getBinData_whenBinIsValid_receiveResponseBody() {
		String bin = "5579";
		ResponseEntity<String> response = getBinData(String.class, bin);
		assertThat(response.getBody()).isNotNull();
	}

	@Test
	public void getBinData_whenBinIsValid_MapToCorrectBean() {
		String bin = "557909";
		ResponseEntity<BinModel> response = getBinData(BinModel.class, bin);
		assertThat(response.getBody().getScheme()).isEqualTo("mastercard");
		assertThat(response.getBody().getType()).isEqualTo("debit");
		assertThat(response.getBody().getBank()).isEqualTo("SANTANDER");
	}

	@Test
	public void getBinDataFromExcelFile_whenBinsAreValid_ReceiveCorrect() {
		XSSFSheet sheet = getWorkSheetFromFile(EXCEL_FILE_2);
		List<String> binList = getBinsFromFile(sheet);
		binList.forEach(bin -> {
			ResponseEntity<BinModel> response = getBinData(BinModel.class, bin);
			assertThat(response.getBody().getScheme()).isEqualTo("visa");
			assertThat(response.getBody().getType()).isEqualTo("credit");
		});

	}

	@Test
	public void getBinDataFromExcelFile_whenBinsAreValid_CreateNewFileWithFullData() {
		XSSFSheet sheet = getWorkSheetFromFile(EXCEL_FILE_2);
		List<String> binList = getBinsFromFile(sheet);

		try (XSSFWorkbook newWorkbook = new XSSFWorkbook();) {
			XSSFSheet newSheet = newWorkbook.createSheet("Bin List");
			int rowCount = 0;
			for (String bin : binList) {
				ResponseEntity<BinModel> response = getBinData(BinModel.class, bin);
				Row row = newSheet.createRow(rowCount++);
				row.createCell(0).setCellValue(bin); // 55790990
				row.createCell(1).setCellValue(response.getBody().getScheme());// mastercard
				row.createCell(2).setCellValue(response.getBody().getType());// debit
				row.createCell(3).setCellValue(response.getBody().getBank());// santander
			}

			try (FileOutputStream outputStream = new FileOutputStream(ExcelUtilsTest.PATH + "binList_response.xlsx")) {
				newWorkbook.write(outputStream);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}catch(IOException e) {
			e.printStackTrace();
		}

		assertThat(new File(ExcelUtilsTest.PATH + "binList_response.xlsx")).exists();
	}

	private List<String> getBinsFromFile(XSSFSheet sheet) {
		List<String> bins = new ArrayList<>(10);
		for (int i = 1; i <= 10; i++) {
			String bin = sheet.getRow(i).getCell(0).getRawValue();
			bins.add(bin);
		}
		return bins;
	}

	private <T> ResponseEntity<T> getBinData(Class<T> response, String bin) {
		return testRestTemplate.getForEntity(API_BINLIST + bin, response);
	}
}
