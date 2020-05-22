package fg.binlist;

import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class BinListService {

	private final String API_BINLIST = "https://lookup.binlist.net/";

	RestTemplate restTemplate = new RestTemplate();

	public XSSFWorkbook createExcelFile(List<String> binList) {
		XSSFWorkbook newWorkbook = new XSSFWorkbook();
        XSSFSheet newSheet = newWorkbook.createSheet("Bin List");
        Row header = newSheet.createRow(0);
        header.createCell(0).setCellValue("Bin");
        header.createCell(1).setCellValue("Tipo de tarjeta");
        header.createCell(2).setCellValue("Crédito/Débito");
        header.createCell(3).setCellValue("Banco");
        
        int rowCount = 1;
		for(String bin: binList) {
			ResponseEntity<BinModel> response = getBinData(bin);
			Row row = newSheet.createRow(rowCount++);
			row.createCell(0).setCellValue(bin); //55790990
			row.createCell(1).setCellValue(response.getBody().getScheme());//mastercard
			row.createCell(2).setCellValue(response.getBody().getType());//debit
			row.createCell(3).setCellValue(response.getBody().getBank());//santander
		}
		return newWorkbook;
	}

	private ResponseEntity<BinModel> getBinData(String bin){
		return restTemplate.getForEntity(API_BINLIST+bin, BinModel.class);
	}
}
