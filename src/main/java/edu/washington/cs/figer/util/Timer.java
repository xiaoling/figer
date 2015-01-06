package edu.washington.cs.figer.util;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;


public class Timer {
	private static final Logger logger = LoggerFactory.getLogger(Timer.class);
	public String task = "it";
	public long duration = 0;
	public long time = 0;
	public Timer(){}
	public Timer(String s){task = s;}
	public Timer start(String s){
		task = s; 
		return start();
	}
	public Timer start(){
		logger.info(task+" starts...");
		duration = 0;
		time = System.currentTimeMillis();
		return this;
	}
	public void pause() {
		if (time != 0) {
			duration += (System.currentTimeMillis() - time);
		}
		time = 0;
	}
	public void resume() {
		time = System.currentTimeMillis();
	}
	public void end(){
		duration += (System.currentTimeMillis() - time);
	}
	public void print(){
		logger.info(task + " takes "+ (duration/1000) +" seconds.");
	}
	public void endPrint(){
		end();
		print();
	}
}
