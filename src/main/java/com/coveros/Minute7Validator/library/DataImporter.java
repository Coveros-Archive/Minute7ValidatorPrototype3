package com.coveros.Minute7Validator.library;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.firefox.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.firefox.FirefoxDriver;
//import org.openqa.selenium.firefox.FirefoxProfile;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.core.io.DefaultResourceLoader;

import com.coveros.Minute7Validator.library.models.*;

public class DataImporter {

	private static Logger logger;
	private static Properties appProp;
	private final DefaultResourceLoader loader = new DefaultResourceLoader();

	public DataImporter(Logger logger, Properties appProp) {
		this.logger = logger;
		this.appProp = appProp;
	}

	public String downloadTimecardDataWithSelenium() throws IOException {
		// WebDriver driver=new FirefoxDriver();
		// driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

		String baseUrl = "https://minute7.com/";
		String minute7AccountName = appProp.getProperty("minute7.accountName");
		String minute7Password = appProp.getProperty("minute7.password");
		// loader.getResource("classpath:/data");

		FirefoxProfile profile = new FirefoxProfile();
		profile.setPreference("browser.download.folderList", 2);
		profile.setPreference("browser.download.dir",
				System.getProperty("user.home") + File.separator + "data" + File.separator + "timecardEntries");
		profile.setPreference("browser.helperApps.neverAsk.saveToDisk", "application/force-download");
		profile.setPreference("browser.startup.homepage_override.mstone", "ignore");
		profile.setPreference("startup.homepage_welcome_url.additional",  "about:blank");

		WebDriver driver = new FirefoxDriver(profile);
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.get(baseUrl + "/");
		driver.findElement(By.linkText("Sign In")).click();
		driver.findElement(By.id("email")).clear();
		driver.findElement(By.id("email")).sendKeys(minute7AccountName);
		driver.findElement(By.id("password")).clear();
		driver.findElement(By.id("password")).sendKeys(minute7Password);
		driver.findElement(By.name("submit")).click();
		driver.get(
				"https://www.minute7.com/reports/time_csv/coveros_inc_time_export.xls?report_customer_id%5B%5D=All&report_employee_id%5B%5D=All&report_inventory_item_id%5B%5D=All&report_payroll_item_id%5B%5D=All&reports_start_date=2016-06-07&reports_end_date=2016-07-07&time_approval=&billable=&synced=No&segment_options1=employee_id&segment_options2=&segment_options3=&segment_options5=&export.x=49&export.y=12");
		driver.findElement(By.linkText("Logout")).click();
		driver.quit();

		File directory = new File(
				System.getProperty("user.home") + File.separator + "data" + File.separator + "timecardEntries");
		File[] files = directory.listFiles((FileFilter) FileFileFilter.FILE);
		if (files.length != 0)

		{
			Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_COMPARATOR);
			System.out.println("File downloaded: " + files[files.length - 1].toString());
			Path source = Paths.get(files[files.length - 1].getPath());
			Path destination = Paths.get(files[files.length - 1].toString() + ".txt");
			Files.move(source, destination);
			System.out.println("File downloaded: " + destination);
			return destination.toString();
		} else

		{
			System.out.println("File could not be downloaded.");
			return "";
		}

	}

	public void importTimecardEntriesTabDelimitedTXT(String filePath, ArrayList<TimecardEntry> timecardEntries)
			throws IOException, ParseException {
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		String line;
		String delimiter = "\t";
		br.readLine();
		while ((line = br.readLine()) != null) {
			String[] columns = line.split(delimiter);
			for (int i = 0; i < columns.length; i++) {
				columns[i] = columns[i].replaceAll("^\"|\"$", "");
			}
			BillabilityEnum currentBillability = BillabilityEnum.NO;

			switch (columns[10]) {
			case "Yes":
				currentBillability = BillabilityEnum.YES;
				break;
			case "No":
				currentBillability = BillabilityEnum.NO;
				break;
			}
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

			timecardEntries.add(new TimecardEntry(formatter.parse(columns[0]), new Employee(columns[2]),
					new Job(columns[3]), new Service(columns[4]), new Payroll(columns[6]), currentBillability));
		}
		br.close();
	}

	public void importDependencies(String filePath, Hashtable<String, Job> jobList) throws IOException {

		File myFile = new File(filePath);
		FileInputStream fis = new FileInputStream(myFile); // Finds the workbook
															// instance for XLSX
															// file
		// HSSFWorkbook myWorkBook = new HSSFWorkbook(fis);
		XSSFWorkbook myWorkBook = new XSSFWorkbook(fis); // Return first
		// sheet from the XLSX workbook
		// HSSFSheet mySheet = myWorkBook.getSheetAt(0);
		XSSFSheet mySheet = myWorkBook.getSheetAt(0); // Get iterator to all
		// the rows in current sheet
		Iterator<Row> rowIterator = mySheet.iterator();

		// Traversing over each row of XLSX file
		rowIterator.next(); // skip the first line
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next(); // For each row, iterate through each
											// columns
			if (row.getCell(0) == null) {
				break;
			}
			if (row.getCell(1) == null) {
				break;
			}
			if (row.getCell(2) == null) {
				break;
			}
			// System.out.println(row.getCell(0).getStringCellValue());
			// System.out.println(row.getCell(1).getStringCellValue());
			// System.out.println(row.getCell(2).getStringCellValue());
			jobList.putIfAbsent(row.getCell(0).getStringCellValue(), new Job(row.getCell(0).getStringCellValue()));
			jobList.get(row.getCell(0).getStringCellValue()).getServices()
					.putIfAbsent(row.getCell(1).getStringCellValue(), new Service(row.getCell(1).getStringCellValue()));
			jobList.get(row.getCell(0).getStringCellValue()).getServices().get(row.getCell(1).getStringCellValue())
					.getPayrollItems()
					.putIfAbsent(row.getCell(2).getStringCellValue(), new Payroll(row.getCell(2).getStringCellValue()));

			// By default, the dependency is not billable
			BillabilityEnum currentBillability = BillabilityEnum.NO;

			switch (row.getCell(3).getStringCellValue()) {
			case "Yes":
				currentBillability = BillabilityEnum.YES;
				break;
			case "No":
				currentBillability = BillabilityEnum.NO;
				break;
			case "Either":
				currentBillability = BillabilityEnum.EITHER;
				break;
			}

			if (jobList.get(row.getCell(0).getStringCellValue()).getServices().get(row.getCell(1).getStringCellValue())
					.getPayrollItems().get(row.getCell(2).getStringCellValue()).getBillable() == null) {
				jobList.get(row.getCell(0).getStringCellValue()).getServices().get(row.getCell(1).getStringCellValue())
						.getPayrollItems().get(row.getCell(2).getStringCellValue()).setBillable(currentBillability);

			} else {
				if (jobList.get(row.getCell(0).getStringCellValue()).getServices()
						.get(row.getCell(1).getStringCellValue()).getPayrollItems()
						.get(row.getCell(2).getStringCellValue()).getBillable() != currentBillability) {
					Job fdfdf = jobList.get(row.getCell(0).getStringCellValue());
					Service fdsa = jobList.get(row.getCell(0).getStringCellValue()).getServices()
							.get(row.getCell(1).getStringCellValue());
					Payroll asdf = jobList.get(row.getCell(0).getStringCellValue()).getServices()
							.get(row.getCell(1).getStringCellValue()).getPayrollItems()
							.get(row.getCell(2).getStringCellValue());
					jobList.get(row.getCell(0).getStringCellValue()).getServices()
							.get(row.getCell(1).getStringCellValue()).getPayrollItems()
							.get(row.getCell(2).getStringCellValue()).setBillable(BillabilityEnum.EITHER);
				}
			}
		}
		myWorkBook.close();
		fis.close();
	}

	public void importTimeEntries(String filePath, ArrayList<TimecardEntry> timecardEntries) throws IOException {
		File myFile = new File(filePath);
		FileInputStream fis = new FileInputStream(myFile); // Finds the workbook
															// instance for XLSX
															// file
		HSSFWorkbook myWorkBook = new HSSFWorkbook(fis);
		// XSSFWorkbook myWorkBook = new XSSFWorkbook (fis); // Return first
		// sheet from the XLSX workbook
		HSSFSheet mySheet = myWorkBook.getSheetAt(0);
		// XSSFSheet mySheet = myWorkBook.getSheetAt(0); // Get iterator to all
		// the rows in current sheet
		Iterator<Row> rowIterator = mySheet.iterator();

		// Traversing over each row of XLSX file
		rowIterator.next(); // skip the first line
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next(); // For each row, iterate through each
											// columns
			// System.out.println(row.getCell(2));
			System.out.println("Row number: " + row.getRowNum());
			BillabilityEnum currentBillability = BillabilityEnum.NO;

			switch (row.getCell(10).getStringCellValue()) {
			case "Yes":
				currentBillability = BillabilityEnum.YES;
				break;
			case "No":
				currentBillability = BillabilityEnum.NO;
				break;
			}

			timecardEntries.add(new TimecardEntry(row.getCell(0).getDateCellValue(),
					new Employee(row.getCell(1).getStringCellValue()), new Job(row.getCell(2).getStringCellValue()),
					new Service(row.getCell(3).getStringCellValue()), new Payroll(row.getCell(5).getStringCellValue()),
					currentBillability));

		}
		myWorkBook.close();
		fis.close();
	}

	public void importEmails(String filePath, Hashtable<String, Employee> employeeDirectory) throws IOException {
		File myFile = new File(filePath);
		FileInputStream fis = new FileInputStream(myFile); // Finds the workbook
															// instance for XLSX
															// file
		// HSSFWorkbook myWorkBook = new HSSFWorkbook(fis);
		XSSFWorkbook myWorkBook = new XSSFWorkbook(fis); // Return first
		// sheet from the XLSX workbook
		// HSSFSheet mySheet = myWorkBook.getSheetAt(0);
		XSSFSheet mySheet = myWorkBook.getSheetAt(0); // Get iterator to all
		// the rows in current sheet
		Iterator<Row> rowIterator = mySheet.iterator();
		rowIterator.next();
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			if (row.getCell(1) == null) {
				break;
			}
			employeeDirectory.put(row.getCell(0).getStringCellValue(),
					new Employee(row.getCell(0).getStringCellValue(), row.getCell(1).getStringCellValue()));

		}

		myWorkBook.close();
		fis.close();
	}

	public void purgeDataFolder(File filepath, File directoryPath) throws IOException {
		// FileUtils.cleanDirectory(filepath);
		if (filepath.exists()) {
			Path filePathVar = filepath.toPath();
			Files.delete(filePathVar);
		}
		FileUtils.cleanDirectory(directoryPath);
	}
}
