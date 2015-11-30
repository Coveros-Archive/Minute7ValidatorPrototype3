package com.coveros.Minute7Validator.controller;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.coveros.Minute7Validator.library.DataImporter;
import com.coveros.Minute7Validator.library.NotificationManager;
import com.coveros.Minute7Validator.library.Validator;
import com.coveros.Minute7Validator.library.models.Employee;
import com.coveros.Minute7Validator.library.models.Job;
import com.coveros.Minute7Validator.library.models.TimecardEntry;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	private static Hashtable<String, Job> jobList;
	private static ArrayList<TimecardEntry> timecardEntries;
	private static Hashtable<String, Employee> employeeDirectory;
	@Autowired
    ApplicationContext context;
	//private @Value("#{minute7.accountName}") String m7accountName;

	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);

		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);

		String formattedDate = dateFormat.format(date);

		model.addAttribute("serverTime", formattedDate);

		return "home";
	}

	/**
	 * Simply selects the test view to render by returning its name.
	 * @throws IOException 
	 * @throws ParseException 
	 */
	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public String test(Locale locale, Model model) throws IOException, ParseException {
		Properties appProp = (Properties) context.getBean("myPropertiesBean");
		String filePathTimecardEntries= System.getProperty("user.home") + "\\data\\timecardEntries\\coveros_inc_time_export.xls.txt";
		String filePathDependencies= System.getProperty("user.home") + "\\data\\dependencies\\M7 Entry Dependency Model.xlsx";
		String filePathEmails=System.getProperty("user.home") + "\\data\\directory\\coveros_directory.xlsx";
		System.out.println("Loading application...");
		jobList=new Hashtable<String, Job>();
		timecardEntries=new ArrayList<TimecardEntry>();
		employeeDirectory=new Hashtable<String, Employee>();
		
		//Initialize data importer
		DataImporter di=new DataImporter(logger, appProp);
		//Initialize validator
		Validator v = new Validator();
		//initialize notification manager
		NotificationManager nm=new NotificationManager();
		
		//Purge previous timecard data if any
		di.purgeDataFolder(new File(System.getProperty("user.home") + "\\data\\timecardEntries"));
		
		//Use Selenium to download the timecard file
		filePathTimecardEntries= di.downloadTimecardDataWithSelenium();
		
		di.importDependencies(filePathDependencies, jobList);
		//di.importTimeEntries(filePathTimecardEntries, jobList, timecardEntries);
		di.importTimecardEntriesTabDelimitedTXT(filePathTimecardEntries, timecardEntries);
		//di.importEmails(filePathEmails, employeeDirectory);
		
		
		v.validateTimcardEntries(jobList, timecardEntries);
		
		di.purgeDataFolder(new File(System.getProperty("user.home") + "\\data\\timecardEntries"));
		return "test";
	}

}
