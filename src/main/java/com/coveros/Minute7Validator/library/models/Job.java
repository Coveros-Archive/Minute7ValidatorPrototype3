package com.coveros.Minute7Validator.library.models;

import java.util.ArrayList;
import java.util.Hashtable;

public class Job {
	private String title;
	private Hashtable<String, Service> services;
	public Job (){
		title="No job title";
		services= new Hashtable<String, Service>();
	}
	public Job (String jobTitle){
		title=jobTitle;
		services= new Hashtable<String, Service>();
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Hashtable<String, Service> getServices() {
		return services;
	}
	public void setServices(Hashtable<String, Service> services) {
		this.services = services;
	}
	
}
