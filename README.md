# padis分布式缓存
###1、介绍
  padis是一套分布式redis缓存解决方案，包含了客户端和管理界面，提供高可用，容量动态伸缩，数据隔离和客户端限流等功能。
  
  
###2、配置
####2.1 admin配置
```config.properties
zkAddr=ip:2181 #zookeeper注册中心地址
```

``` adminaddr
cd /padis
make install
cd padis-admin/target
拷贝padis-admin.war,将包放到tomcat的webapps目录下
确保zookeeper配置是正确的。
打开如下地址：
http://localhost:8080/padis-admin
```

####2.2 client配置
```PadisConfig
instance：实例对应一组redis group，需要admin分配
zkAddr：zookeeper注册中心地址
nameSpace：命名空间，防止key冲突
maxRedirections：最大失败重试次数
connectionTimeout：连接超时时间
soTimeout：数据传输超时时间
maxTotal：连接池最大连接数
```

###3、使用client
```java
import com.yjfei.cache.padis.IPadis;
import com.yjfei.cache.padis.PadisDirectClient;

public class main {

	public static void main(String[] args) throws Exception {

		String zkAddr = "localhost:2181";
		String instance = "test";
		String namespace = "ns";
		IPadis padis =  new PadisDirectClient(zkAddr, instance, namespace);
		
		padis.set("key", "value");
		
		padis.get("key");
		
		padis.delete("key");

		padis.close();
		
	}

}
```


###3、参考
|-|-|
|---|---|
|codis|https://github.com/CodisLabs/codis|
|markdown|http://www.jianshu.com/p/1e402922ee32/|




