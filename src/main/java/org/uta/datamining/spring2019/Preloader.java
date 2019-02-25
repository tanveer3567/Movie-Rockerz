package org.uta.datamining.spring2019;

import java.io.FileNotFoundException;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

@Component
public class Preloader {

	private Runnable preload = () -> {
		try {
			SearchAlgorithm.preLoad();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	};
	
	@PostConstruct
	private void init() {
		Thread t = new Thread(preload);
		t.start();
	}
}
