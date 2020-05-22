package fg.binlist;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class BInListController {

	@Autowired
	BinListService binListService;
	
	@RequestMapping(value="/index")
	public String index() {
		return "index";
	}

	@PostMapping("/download")
	public ResponseEntity<Resource> downloadExcelFileWithBinListData(@RequestParam("file") MultipartFile excelFile) {
		try (XSSFWorkbook workbook = new XSSFWorkbook(excelFile.getInputStream())) {
			XSSFSheet worksheet = workbook.getSheetAt(0);
			List<String> binList = getBinsFromFile(worksheet);

			XSSFWorkbook downloadFile = binListService.createExcelFile(binList);
			byte[] bytesFromExcel = getBytesFromExcel(downloadFile);
			ByteArrayResource resource = new ByteArrayResource(bytesFromExcel);
			return ResponseEntity
						.ok()
						.contentType(MediaType.APPLICATION_OCTET_STREAM)
						.body(resource);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.body(null);
	}

	private List<String> getBinsFromFile(XSSFSheet sheet) {
		List<String> bins = new ArrayList<>(10);
		for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
			String bin = sheet.getRow(i).getCell(0).getRawValue();
			bins.add(bin);
		}
		return bins;
	}

	private byte[] getBytesFromExcel(XSSFWorkbook workbook) {

		try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
			workbook.write(bos);
			return bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
