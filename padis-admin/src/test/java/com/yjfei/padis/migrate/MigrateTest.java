package com.yjfei.padis.migrate;

import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;

import org.apache.curator.framework.listen.Listenable;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.alibaba.fastjson.JSON;
import com.yjfei.padis.common.CoordinatorRegistryCenter;
import com.yjfei.padis.common.Migrate;
import com.yjfei.padis.common.TaskInfo;
import com.yjfei.padis.node.CustomNode;
import com.yjfei.padis.node.Group;
import com.yjfei.padis.node.GroupNode;
import com.yjfei.padis.node.Slot;
import com.yjfei.padis.node.SlotNode;
import com.yjfei.padis.util.HostPortUtils;


@RunWith(Parameterized.class)
public class MigrateTest {

	@Mock
	private CoordinatorRegistryCenter coordinatorRegistryCenter;
	
	@Mock
	private TreeCache cache;
	
	@Mock
	private Listenable<TreeCacheListener> listeners;
	
	private MigrateTask migrateTask;
	
	@Parameters
	public static Collection data(){
		return Arrays.asList(new Object[][]{{			
			"test",//instance
			2,//from slot
			250,//to slot
			200,//delay
			"10.20.22.87:6379",//from redis
			"10.20.22.88:6379"//to redis
		}});
	}
	
	public MigrateTest(String instance,int from,int to,int delay,String fromRedis,String toRedis){
		MockitoAnnotations.initMocks(this);
		
		MigrateNode migrateNode = new MigrateNode();
		SlotNode slotNode = new SlotNode(instance);
		
		for(int i=from;i<=to;i++){
			Migrate migrate = new Migrate();
			migrate.setSlot_id(i);
			migrate.setDelay(delay);
			migrate.setFrom_gid(1);
			migrate.setTo_gid(2);
			when(coordinatorRegistryCenter.getDirectly(migrateNode.getMigrateSlotPath(instance, i))).thenReturn(JSON.toJSONString(migrate));
			
			Slot slot = new Slot();
			slot.setId(i);
			slot.setSrc_gid(1);
			slot.setTo_gid(-1);
			when(coordinatorRegistryCenter.getDirectly(slotNode.getSlotPath(i))).thenReturn(JSON.toJSONString(slot));
			
		}
		
		GroupNode groupNode = new GroupNode();
		
		for(int i=1;i<3;i++){
			when(coordinatorRegistryCenter.isExisted(groupNode.getGroupPath(i))).thenReturn(true);
			
			Group group = new Group();
			group.setId(i);
			String redis = fromRedis;
			if(i==2){
				redis = toRedis;
			}
			
			group.setMaster(HostPortUtils.mapHostAndPort(redis));
			group.setSlave(HostPortUtils.mapHostAndPort("89:21"));
			
			when(coordinatorRegistryCenter.getDirectly(groupNode.getGroupPath(i))).thenReturn(JSON.toJSONString(group));
		}
		
		when(coordinatorRegistryCenter.getRawCache(new CustomNode(instance).getRootCustomPath())).thenReturn(cache);
		when(cache.getListenable()).thenReturn(listeners);
		
		this.migrateTask = new MigrateTask(new TaskInfo("test", from, to), coordinatorRegistryCenter,"admin");
		
	}
	

	@Test
	public void test(){
		migrateTask.run();
	}
	
}
