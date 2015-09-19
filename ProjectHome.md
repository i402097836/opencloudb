## Downlod ##
  * binary http://code.taobao.org/svn/openclouddb/downloads/
  * document http://code.taobao.org/svn/openclouddb/doc/
  * source svn http://code.taobao.org/svn/openclouddb/trunk/
## Features ##
  * SQL 92 suppported (thanks FoudationDB SQL Parser)
  * support many database/table sharding rules
  * Java AIO supported for high performance
  * Mysql Native binary protocal supported and work as a fake MySQL Server
  * JDBC protocal to support many other DB ,such as Oracle/DB2/SQL Server

## History ##
  * 2014.6.4  MyCat 1.2 DEV bug fix release
  * 2014.3.10  Java AIO supported
  * 2014.3.6   MyCat 1.1 release
  * 2014.2.14  MyCat 1.1 beta2 release (NIO BUG fix)
  * 2014.2.12  MyCat 1.1 beta release (Important Bug Fix)
  * 2014.1.6  MyCat 1.01 release (Bug Fix)
  * 2014.1.3  MyCat 1.0 release
  * 2013.12.25 read-write balance implemented
  * 2013.12.23 Mycat 1.0GA\_b1 released ,new schema config file ,simplified conf ,ready for read-write balance ,ready for JDBC bakend implment for multi database support, implemented. a very important update is shared all DB backend connections to all sharding datanodes on the same db server.
  * 2013.12.11 Mycat 1.0b2 released ,gloabl table and E-R based sharding policy is implemented.
  * 2013.12.5 Mycat 1.0b released ,which fix a NIO Bug and introduced FoundationDB's sql parser and rewrite route module to improve performance and to prepare for some advance features develope.
## Tips ##
    * 目前在考虑1.3中增加在线迁移能力，初步设想是 ，要迁移的数据暂时在旧节点和新节点上都查询，新增的时候，则只放到新节点上，然后不断的把旧数据转移到新节点。实现动态不停机的迁移
    * 另外一个比较有价值的东东，就是改造mysq jdbc driver（包装也行），放入现在的业务系统中，然后截取SQL统计信息，目标是分析出来SQL的执行频率，常用的关联关系，然后给出一个最佳的分片策略。

  * Mycat后端目前是Mysql数据库，Mycat可以用Msql的命令行工具和其他GUI工具连接，Table在schema.xml中定义好分片规则以后，DDL语句可以在命令行执行，自动在多个物理库上执行建表语句
  * Mycat支持标准SQL，即SQL 99语法，部分Mysql特定的SQL目前并不支持，请注意用符合SQL 99的语法，DDL时候特别注意这一点，包括字段的类型，都参照SQL 99的规范，若这样做有困难，则可以直接在后端MYSQL数据库上运行MYSQL特地的DDL 语句，以及数据迁移。
  * 数据迁移，目前正在找一些合适的工具，建议http://flywaydb.org/
Mycat 1.0代码已在 http://code.taobao.org/svn/openclouddb/tags/1.0 下，目前主干代码正式进入1.1研发中。。1.0GA发布以后，BUG修复都在 tags/1.0下进行，同时在主干上也提供修复的代码
## Modules ##
  * Mycat : 核心模块，等价于Mysql
  * Mycat Web: 图形化管理监控模块   （研发中）
  * Mycat Cluster : 集群模块   （研发中）
  * Mycat Balance: 负载均衡模块 （研发中）
## 当前重要模块开发领衔 ##
  * Mycat Web ，rainbow  领衔，参与者 待实名
  * Mycat Cluster，Qing  领衔，参与者 待实名
  * Mycat Balance，提婆谭 领衔，参与者 待实名
  * Mycat 功能测试单元代码集，关注 领衔，参与者 待实名
  * Mycat 数据库智能优化， 沉睡的大雄 领衔，参与者 待实名
  * Mycat .NET管理系统  Jacky Xu 领衔，参与者 待实名
## 项目累计参与者 ##
  * shenzhw 性能测试
  * 阿德 排序功能
  * 木糖醇患者 修复BUG

## Coming soon ##
> OpenCloudDB 1.2版本的需求和规划路线，初步计划：
    * Web管理及监控系统 1.0发布
    * 优化服务端归并算法，实现1亿数据集的高效处理，
    * 基于JDBC的多数据库支持，
    * AIO替代NIO
    * XA事务

## 疑难问题 ##
目前cobar以及后继者Mycat都存在一个需要调优的地方：即后端数据流的速度 与前端客户端的速度不匹配的问题，举例来说：后端数据库从多个节点上发送了大量数据，这些数据要写入前端连接的Socket中，但写入流速大于客户端收取流速，因此导致阻塞，线程在此白白消耗。有哪位大侠能有好办法解决这个问题
> 初步设想的方案，当NIO框架发现前端Socket还有积压的数据要发送的时候，不再读取此Session后端对应的所有节点的Socket，此时，根据网络的传输，Mysql服务端会自动降低发送的速度。Mycat线程不再浪费
MyCAT 高可用性计划，某个 schema的所有表并发在多个独立节点上执行，不用XA事务，当某个节点失败，就排除在外，并告警，然后尝试再次执行失败的更新SQL，若还是失败，就彻底隔离，人工排查问题。。
当有5个节点参与，到最后变成1个的时候，估计啥灾害都发生过一遍了，MyCAT依然坚挺。——完美的容灾方案。。

## What is Mycat ##
  1. Base on open source mysql server to store data.
  1. MPP Query for Huge Dataset.
  1. Fully ACID.
  1. Elastic distribute.
  1. Auto sharding.
  1. Multi-tenant support.
  1. Fully java based.
  1. High performance
  1. Open source.


## Todo ##
  * Distributed computer enginee: Find some good distributed computer engine for realtime SQL query execute.
  * Cluster Config: opencloudb cluster config using Zookeep or Taobao Fourinone

# Mycat情报 #

  1. 基于阿里的开源cobar ，可以用于生产系统中，目前在做如下的一些改进：
  1. 非阻塞IO的实现，相对于目前的cobar，并发性能大大提升，而且不会陷入假死状态
  1. 优化线程池的分配，目前cobar的线程池分配效率不高
  1. 修复cobar一些ＢＵＧ
  1. 参考impala中的impala front部分的Java代码，实现高效的Map-Reduce，能够处理上亿的大数据量
  1. 实现自动分片特性，目前cobar需要手工分片，并有一定的编程限制

# Quick Start #
**doc目录中有关于快速搭建Mycat的说明，10分钟即可体验“云”！
# 小知识 #
  ***SQL and MPP**：The Next Phase in Big Data  http://architects.dzone.com/articles/sql-and-mpp-next-phase-big
  ***Cloudera**- Impala:Cloudera is leading the charge to create a next generation open source MPP platform
  ***MapR - Apache Drill**:This is a similar project to Impala but channeled through the Apache organization and primary driven by MapR
  ***Google - Big Query**: This is Google's cloud services that is a combination of a distributed data store coupled with a powerful SQL like ad hoc query engine (based on the Dremel language).
  ***HP - Vertica**: HP's Big Data play. Like IBM and Terradata, HP acquired their way into the Big Data space.
  ***Tez和Impala**：现在竞争非常激烈，前者走的是基于DAG的精细化管理，后者是基于MPP的技术架构重头开始造了一个C++版本的SQL引擎。截止到2013年7月，Hortonworks的Stinger（Hive 0.11 + Tez）还是比Impala慢不少，毕竟Impala的动作更早一些。Hortonworks跟Cloudera这场硬仗干的真是激烈啊。**





'
# Waiting for you #
**欢迎有志于大数据、分布式计算、数据库算法和优化等方面的大侠加入！** QQ群：106088787