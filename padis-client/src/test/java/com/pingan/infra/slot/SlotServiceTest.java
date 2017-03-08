package com.pingan.infra.slot;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.pingan.infra.AbstractNestedZookeeperBaseTest;
import com.yjfei.padis.common.Status;
import com.yjfei.padis.node.Slot;
import com.yjfei.padis.slot.SlotService;


public class SlotServiceTest extends AbstractNestedZookeeperBaseTest {

	private SlotService slotService;

	@Before
	public void setUpCase() {
		slotService = new SlotService("test", zkRegCenter);
	}

	@Test
	public void testGet() {

		slotService.setSlot(new Slot(1, Status.ONLINE, 1, 1, -1, 2));
		Slot slot = slotService.getSlot(1);
		Assert.assertEquals(1, slot.getId());
	}

	@Test
	public void testUpdate() {
		Slot slot = slotService.getSlot(1);
		slot.setStatus(Status.MIGRATE);
		slotService.updateSlot(slot);
		slot = slotService.getSlot(1);
		Assert.assertEquals(Status.MIGRATE, slot.getStatus());
	}

	@Test
	public void testList() {
		List<Slot> list = slotService.getAllSlots();
		Assert.assertEquals(1, list.size());
	}
	
}
