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

import org.apache.commons.mail.EmailException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

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
	@Autowired
	private JavaMailSender mailSender;

	// private @Value("#{minute7.accountName}") String m7accountName;

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
	 * 
	 * @throws IOException
	 * @throws ParseException
	 * @throws EmailException
	 */
	@RequestMapping(value = "/validate/notify", method = RequestMethod.GET)
	public ModelAndView test(Locale locale, Model model) throws IOException, ParseException, EmailException {

		String filePathTimecardEntries = System.getProperty("user.home") + File.separator + "data" + File.separator
				+ "timecardEntries" + File.separator + "coveros_inc_time_export.xls.txt";
		String filePathDependencies = System.getProperty("user.home")
				+ "/data/dependencies/M7 Entry Dependency Model.xlsx";
		String filePathEmails = System.getProperty("user.home") + File.separator + "data" + File.separator + "directory"
				+ File.separator + "coveros_directory.xlsx";
		System.out.println("Loading application...");
		jobList = new Hashtable<String, Job>();
		timecardEntries = new ArrayList<TimecardEntry>();
		employeeDirectory = new Hashtable<String, Employee>();

		Properties appProp = (Properties) context.getBean("myPropertiesBean");
		// Initialize data importer
		DataImporter di = new DataImporter(logger, appProp);
		// Initialize validator
		Validator v = new Validator();
		// initialize notification manager
		NotificationManager nm = new NotificationManager(mailSender);

		// Purge previous timecard data if any
		di.purgeDataFolder(
				new File(System.getProperty("user.home") + File.separator + "data" + File.separator + "timecardEntries"
						+ File.separator + "coveros_inc_time_export.xls.txt"),
				new File(System.getProperty("user.home") + File.separator + "data" + File.separator
						+ "timecardEntries"));

		// Use Selenium to download the timecard file
		System.out.println(filePathTimecardEntries);
		filePathTimecardEntries = di.downloadTimecardDataWithSelenium();
		System.out.println(filePathTimecardEntries);

		di.importDependencies(filePathDependencies, jobList);
		// di.importTimeEntries(filePathTimecardEntries, jobList,
		// timecardEntries);
		di.importTimecardEntriesTabDelimitedTXT(filePathTimecardEntries, timecardEntries);
		di.importEmails(filePathEmails, employeeDirectory);

		ArrayList<String> report = v.validateTimcardEntries(jobList, timecardEntries);

		boolean notify = false;
		nm.asyncCheckValidityAndSend(timecardEntries, employeeDirectory, notify);

		di.purgeDataFolder(
				new File(System.getProperty("user.home") + File.separator + "data" + File.separator + "timecardEntries"
						+ File.separator + "coveros_inc_time_export.xls.txt"),
				new File(System.getProperty("user.home") + File.separator + "data" + File.separator
						+ "timecardEntries"));

		ModelAndView modelNView = new ModelAndView("test");
		modelNView.addObject("report", report);
		return modelNView;
	}

	/**
	 * Simply selects the test view to render by returning its name.
	 * 
	 * @throws IOException
	 * @throws ParseException
	 * @throws EmailException
	 */
	@RequestMapping(value = "/validate/dontnotify", method = RequestMethod.GET)
	public ModelAndView testNoNotification(Locale locale, Model model)
			throws IOException, ParseException, EmailException {

		String filePathTimecardEntries = System.getProperty("user.home") + File.separator + "data" + File.separator
				+ "timecardEntries" + File.separator + "coveros_inc_time_export.xls.txt";
		String filePathDependencies = System.getProperty("user.home")
				+ "/data/dependencies/M7 Entry Dependency Model.xlsx";
		String filePathEmails = System.getProperty("user.home") + File.separator + "data" + File.separator + "directory"
				+ File.separator + "coveros_directory.xlsx";
		System.out.println("Loading application...");
		jobList = new Hashtable<String, Job>();
		timecardEntries = new ArrayList<TimecardEntry>();
		employeeDirectory = new Hashtable<String, Employee>();

		Properties appProp = (Properties) context.getBean("myPropertiesBean");
		// Initialize data importer
		DataImporter di = new DataImporter(logger, appProp);
		// Initialize validator
		Validator v = new Validator();
		// initialize notification manager
		NotificationManager nm = new NotificationManager(mailSender);

		// Purge previous timecard data if any
		di.purgeDataFolder(
				new File(System.getProperty("user.home") + File.separator + "data" + File.separator + "timecardEntries"
						+ File.separator + "coveros_inc_time_export.xls.txt"),
				new File(System.getProperty("user.home") + File.separator + "data" + File.separator
						+ "timecardEntries"));

		// Use Selenium to download the timecard file
		// System.out.println(filePathTimecardEntries);
		filePathTimecardEntries = di.downloadTimecardDataWithSelenium();
		System.out.println(filePathTimecardEntries);

		di.importDependencies(filePathDependencies, jobList);
		// di.importTimeEntries(filePathTimecardEntries, jobList,
		// timecardEntries);
		di.importTimecardEntriesTabDelimitedTXT(filePathTimecardEntries, timecardEntries);
		di.importEmails(filePathEmails, employeeDirectory);

		ArrayList<String> report = v.validateTimcardEntries(jobList, timecardEntries);

		boolean notify = false;
		nm.asyncCheckValidityAndSend(timecardEntries, employeeDirectory, notify);

		di.purgeDataFolder(
				new File(System.getProperty("user.home") + File.separator + "data" + File.separator + "timecardEntries"
						+ File.separator + "coveros_inc_time_export.xls.txt"),
				new File(System.getProperty("user.home") + File.separator + "data" + File.separator
						+ "timecardEntries"));

		ModelAndView modelNView = new ModelAndView("test");
		modelNView.addObject("report", report);
		return modelNView;
	}

	/**
	 * Test for sending email
	 */
	@RequestMapping(value = "/email", method = RequestMethod.GET)
	public String emailTest(Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);

		String formattedDate = dateFormat.format(date);

		model.addAttribute("serverTime", formattedDate);
		SimpleMailMessage email = new SimpleMailMessage();
		email.setTo("jy4ny@virginia.edu");
		email.setSubject("FAFAFAFAF");
		email.setText("greetings!");
		mailSender.send(email);

		return "home";
	}

	// Spring Security see this :
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login(@RequestParam(value = "error", required = false) String error,
			@RequestParam(value = "logout", required = false) String logout) {

		return "login";

	}

}
