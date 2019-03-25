package org.uta.datamining.spring2019;

import java.io.FileNotFoundException;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

@Component
public class Preloader {

	@PostConstruct
	private void init() {
		try {
			SearchAlgorithm.preLoad();
			ClassiferAlgorithm.preload();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
