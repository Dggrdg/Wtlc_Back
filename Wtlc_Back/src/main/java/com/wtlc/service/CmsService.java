package com.wtlc.service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
public class CmsService {

	String filePath = "C:\\Users\\USER\\Desktop\\銘軒工作用\\停管處IP清單\\停管處防火牆IP.xlsx";
	String outputFilePath = "C:\\Users\\USER\\Desktop\\銘軒工作用\\停管處IP清單\\停管處防火牆IP_結果.xlsx";
	int sheetNo = 0;

	private final HttpClient client = HttpClient.newHttpClient();
	private final ObjectMapper mapper = new ObjectMapper();

	// 主流程：批量查 IP 並寫入國家
	public void batchLookupAndWrite() throws IOException, InterruptedException {
		Workbook workbook = getExcel(filePath);
		Sheet sheet = workbook.getSheetAt(sheetNo);

		DataFormatter formatter = new DataFormatter();

		// 新增標題欄
		Row header = sheet.getRow(0);
		if (header == null)
			header = sheet.createRow(0);
		Cell countryHeaderCell = header.createCell(1);
		countryHeaderCell.setCellValue("Country");

		for (Row row : sheet) {
			Cell ipCell = row.getCell(0);
			if (ipCell == null)
				continue;

			String ipString = formatter.formatCellValue(ipCell).trim();
			if (ipString.isEmpty())
				continue;

			List<String> ips = splitIpsFromCell(ipString);

			List<String> countries = new ArrayList<>();
			for (String ip : ips) {
				String country = getCountryFromIpApi(ip);
				countries.add(country);
				Thread.sleep(1000); // 避免 ip-api 過多請求
			}

			Cell countryCell = row.getCell(1);
			if (countryCell == null)
				countryCell = row.createCell(1);
			countryCell.setCellValue(String.join(", ", countries));
		}

		// 寫出新檔
		try (FileOutputStream fos = new FileOutputStream(outputFilePath)) {
			workbook.write(fos);
		}
		workbook.close();
		System.out.println("完成! 結果已寫入: " + outputFilePath);
	}

	// 讀取 Excel
	private Workbook getExcel(String filePath) throws IOException {
		InputStream is = new FileInputStream(filePath);
		return new XSSFWorkbook(is);
	}

	private List<String> splitIpsFromCell(String cellValue) {
		List<String> ips = new ArrayList<>();
		if (cellValue == null || cellValue.isBlank())
			return ips;

		// 統一分隔符號
		cellValue = cellValue.replace("~", " ").replace(">", " ").replace("、", " ").replace(",", " ").replace("•", " ");

		String[] tokens = cellValue.split("\\s+");

		for (String token : tokens) {
			token = token.trim();
			if (token.isEmpty())
				continue;

			// 處理範圍 IP，例如 101.13.5.113-114
			if (token.matches("\\d+\\.\\d+\\.\\d+\\.\\d+-\\d+")) {
				String base = token.substring(0, token.lastIndexOf('.'));
				String[] parts = token.split("-");
				int start = Integer.parseInt(parts[0].substring(parts[0].lastIndexOf('.') + 1));
				int end = Integer.parseInt(parts[1]);
				for (int i = start; i <= end; i++) {
					ips.add(base + "." + i);
				}
				continue;
			}

			// 單個 IP
			if (token.matches("\\d+\\.\\d+\\.\\d+\\.\\d+")) {
				ips.add(token);
			}
		}

		return ips;
	}

	// 查 ip-api
	public String getCountryFromIpApi(String ip) throws IOException, InterruptedException {
		String url = "http://ip-api.com/json/" + ip + "?fields=status,country,countryCode,message";
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		JsonNode json = mapper.readTree(response.body());

		if ("success".equals(json.get("status").asText())) {
			return json.get("country").asText() + " (" + json.get("countryCode").asText() + ")";
		} else {
			return "查詢失敗: " + json.get("message").asText();
		}
	}
}
