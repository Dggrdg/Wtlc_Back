package com.wtlc.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.wtlc.bean.IPSearch;

@Service
public class CmsService {

	// 讀取檔案路徑
	String filePath = "C:\\Users\\USER\\Desktop\\銘軒工作用\\停管處IP清單\\停管處防火牆IP.xlsx";

	public void searchForeignIP() throws IOException {

		if (filePath != null) {

			InputStream is = new FileInputStream(filePath);
			XSSFWorkbook workBook = new XSSFWorkbook(is);
			XSSFSheet xssfSheet = workBook.getSheetAt(0);
			int excelCounts = xssfSheet.getLastRowNum();

			DataFormatter dataFormatter = new DataFormatter();
			List<IPSearch> ipList = new ArrayList<>();

			for (int i = 1; i <= excelCounts; i++) {
				IPSearch ipSearch = new IPSearch();
				String cellValue = dataFormatter.formatCellValue(xssfSheet.getRow(i).getCell(0));

				if (cellValue != null && !cellValue.trim().isEmpty()) {
					// 判斷內容是否含有多組IP，如有多組IP根據、拆分IP
					if (cellValue.contains("、")) {
						String[] ip = cellValue.split("、");
						for (String words : ip) {
							IPSearch ipSearch1 = new IPSearch();
							ipSearch1.setIP(words);
							ipList.add(ipSearch1);
						}
					} else {
						ipSearch.setIP(cellValue);
						ipList.add(ipSearch);
					}
				}

				searchIpLocation(ipList);

			}

		}
	}

	// 查詢IP所在國家
	public List<?> searchIpLocation(List<?> list) {
		return null;
	}

}
