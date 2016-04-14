package com.pingan.jinke.infra.padis.migrate;

public class MigrateNode {

	private String MIGRATE_ROOT;
	private String MIGRATE_FORMAT;

	public MigrateNode(String instance) {
		this.MIGRATE_ROOT = "/migate/" + instance;
		this.MIGRATE_FORMAT = MIGRATE_ROOT +"/%s";
	}
	
	public  String getRootMigratePath(){
		return this.MIGRATE_ROOT;
	}
	
	public String getMigratePath(String ip){
		return String.format(MIGRATE_FORMAT, ip);
	}
}
