package com.wtlc.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

@Service
public class CmsService {

	// 讀取檔案路徑
	String filePath = "C:\\Users\\USER\\Desktop\\銘軒工作用\\停管處IP清單\\停管處防火牆IP.xlsx";
	// 總列樹
	int lastRowNum;

	public void searchForeignIP() throws IOException {

		if (filePath != null) {

			// 讀取xlsx
			InputStream is = new FileInputStream(filePath);
			XSSFWorkbook wb = new XSSFWorkbook(is);

			// 讀取第一個頁簽
			Sheet sheet = wb.getSheetAt(0);
			lastRowNum = sheet.getLastRowNum();

			for (int i = 0; i <= lastRowNum; i++) {

			}

		}
	}

}
