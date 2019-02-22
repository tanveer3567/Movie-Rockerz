package org.uta.datamining.spring2019;

import java.io.FileNotFoundException;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

@Component
public class Preloader {
	
	public Runnable r = () -> {
		try {
			SearchAlgorithm.preLoad();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	};

	@PostConstruct
    private void init() { 
		Thread t = new Thread(r);
		t.start();
    }
}

