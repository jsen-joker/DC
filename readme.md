#DC

客户端、DC

dc client配置：

0、添加zk配置文件
dc.zk.registry=dc://127.0.0.1:2181?timeout=30000
dubbo.zk=zookeeper://127.0.0.1:2181
dubbo.port=20881

1、配置dubbo使用的zookeeper和扫描包，详见测试客户端com.dryork.config.DubboConfiguration

2、实现DcCoreDbHandleService的一个实现类，并要求注册到spring，可参考测试客户端

3、配置dc配置文件，如dc-config.xml并保证该配置可被spring读取

4、使用DcClientCoreService进行数据同步，详见测试客户端com.dryork.service.impl.Table0ServiceImpl

注意 为了实现外键字段的自动替换，在从dc同步数据到本地的时候，不能同步dc_app_name字段
也就是dc_app_name在各个端的意义：
DC服务端：第一次创建该记录的app的名字，后续对同一条记录的插入，不会覆盖该字段
Client：本应用的app name
