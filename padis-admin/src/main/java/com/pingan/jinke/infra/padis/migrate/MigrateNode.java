package com.pingan.jinke.infra.padis.migrate;

public class MigrateNode {

	private String MIGRATE_ROOT;
	private String MIGRATE_FORMAT;
	private String MIGRATE_SLOT_FORMAT;

	public MigrateNode() {
		this.MIGRATE_ROOT = "/migate";
		this.MIGRATE_FORMAT = MIGRATE_ROOT + "/%s";
		this.MIGRATE_SLOT_FORMAT = MIGRATE_FORMAT + "/slot_%s";
	}

	public String getRootMigratePath() {
		return this.MIGRATE_ROOT;
	}

	public String getMigratePath(String instance) {
		return String.format(MIGRATE_FORMAT, instance);
	}

	public String getMigrateSlotPath(String instance, int slotid) {
		return String.format(MIGRATE_SLOT_FORMAT, instance,slotid);
	}
}
