package fg.binlist;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Ignore;

@Ignore
public class ExcelUtilsTest {

	public static final String PATH = "src/test/resources/excel/";
	public static final String EXCEL_FILE_1 = "test1.xlsx";
	public static final String EXCEL_FILE_2 = "test2.xlsx";

	public static XSSFSheet getWorkSheetFromFile(String file) {
		try (FileInputStream fis = new FileInputStream(new File(PATH + file));
				XSSFWorkbook workbook = new XSSFWorkbook(fis);
				) {
			return workbook.getSheetAt(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
