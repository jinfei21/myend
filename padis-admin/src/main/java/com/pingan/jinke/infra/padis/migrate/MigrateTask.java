package com.pingan.jinke.infra.padis.migrate;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Setter
@Getter
@Slf4j
public class MigrateTask {

	private String instance;
	private int from;
	private int to;

	public MigrateTask(String instance, int from, int to) {
		this.from = from;
		this.to = to;
		this.instance = instance;
	}

	public void run() {
		for (int cur = from; cur <= to; cur++) {
			try{
				processSlot(cur);
			}catch(Throwable t){
				log.error("process migrate slot="+cur+" for"+" fail.", t);
			}
		}
	}
	
	private void processSlot(int slot){
		
	}
}
