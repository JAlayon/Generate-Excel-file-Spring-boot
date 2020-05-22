package fg.binlist;

import static org.assertj.core.api.Assertions.assertThat;
import static fg.binlist.ExcelUtilsTest.*;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.junit.Test;


public class UploadExcelFileTest {

	@Test
	public void uploadFile_whenFileIsFound_UploadedOK() throws IOException {
		FileInputStream fileInputStream = new FileInputStream(new File(PATH + EXCEL_FILE_1));
		assertThat(fileInputStream).isNotNull();
	}

	@Test
	public void uploadFile_whenFileIsFound_RetrieveData() throws IOException {
		XSSFSheet sheet = getWorkSheetFromFile(EXCEL_FILE_2);
		assertThat(sheet).isNotNull();
		sheet.iterator().forEachRemaining(row ->{
			String value = String.valueOf(row.getCell(0).getNumericCellValue()).substring(0,6);
			assertThat(value).matches("[0-9]*");
		});
	}
	
	
	
}
