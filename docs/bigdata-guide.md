# Shopping 电商平台 - 完整部署文档

## 文档说明

本文档详细说明如何从零开始部署 Shopping 电商平台，包括：
- 基础环境（MySQL、Node.js、JDK）
- 大数据环境（Hadoop、Hive、Spark）
- 后端服务（Spring Boot）
- 前端应用（Vue 3）
- 数据初始化与验证

---

## 目录

1. [环境要求](#一环境要求)
2. [基础环境安装](#二基础环境安装)
3. [数据库初始化](#三数据库初始化)
4. [大数据环境部署](#四大数据环境部署)
5. [后端服务部署](#五后端服务部署)
6. [前端应用部署](#六前端应用部署)
7. [数据同步与验证](#七数据同步与验证)
8. [Spark任务执行](#八spark任务执行)
9. [可视化页面访问](#九可视化页面访问)
10. [常见问题FAQ](#十常见问题faq)

---

## 一、环境要求

### 1.1 硬件要求

| 组件 | 最低配置 | 推荐配置 |
|------|----------|----------|
| CPU | 4核 | 8核 |
| 内存 | 8GB | 16GB |
| 磁盘 | 50GB | 100GB SSD |
| 网络 | 100Mbps | 1Gbps |

### 1.2 软件要求

| 软件 | 版本 | 用途 |
|------|------|------|
| **操作系统** | Ubuntu 20.04/22.04 LTS | 推荐Linux环境 |
| **JDK** | 11 | Hadoop/Spark依赖 |
| **MySQL** | 8.0 | 业务数据库 |
| **Node.js** | 18.x | 前端构建 |
| **Hadoop** | 3.3.x | 分布式存储计算 |
| **Hive** | 3.1.x | 数据仓库 |
| **Spark** | 3.5.x | 计算引擎 |
| **Python** | 3.8+ | PySpark/脚本 |

### 1.3 网络端口

| 端口 | 服务 | 说明 |
|------|------|------|
| 3306 | MySQL | 数据库 |
| 8080 | Spring Boot | 后端API |
| 3000 | Vue Dev Server | 前端开发 |
| 9870 | HDFS NameNode | HDFS Web UI |
| 8088 | YARN ResourceManager | YARN Web UI |
| 9083 | Hive Metastore | Hive元数据 |
| 10000 | HiveServer2 | Hive JDBC |
| 4040 | Spark Web UI | Spark任务监控 |
| 18080 | Spark History | Spark历史服务器 |

---

## 二、基础环境安装

### 2.1 系统准备

```bash
# 更新系统
sudo apt update && sudo apt upgrade -y

# 安装基础工具
sudo apt install -y wget curl vim net-tools ssh pdsh

# 配置主机名
sudo hostnamectl set-hostname shopping-master
echo "127.0.0.1 shopping-master" | sudo tee -a /etc/hosts

# 配置SSH免密登录
ssh-keygen -t rsa -P '' -f ~/.ssh/id_rsa
cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys
chmod 0600 ~/.ssh/authorized_keys
```

### 2.2 安装JDK 11

```bash
# 安装OpenJDK 11
sudo apt install -y openjdk-11-jdk

# 配置JAVA_HOME
cat >> ~/.bashrc << 'EOF'
export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
export PATH=$PATH:$JAVA_HOME/bin
EOF
source ~/.bashrc

# 验证安装
java -version
```

### 2.3 安装MySQL 8.0

```bash
# 安装MySQL
sudo apt install -y mysql-server

# 启动MySQL
sudo systemctl start mysql
sudo systemctl enable mysql

# 配置MySQL（设置root密码）
sudo mysql_secure_installation

# 创建数据库和用户
sudo mysql -u root << 'EOF'
CREATE DATABASE IF NOT EXISTS shopping DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
CREATE USER IF NOT EXISTS 'shopping'@'localhost' IDENTIFIED BY 'shopping123';
GRANT ALL PRIVILEGES ON shopping.* TO 'shopping'@'localhost';
FLUSH PRIVILEGES;
EOF

# 验证连接
mysql -u shopping -pshopping123 -e "SHOW DATABASES;"
```

### 2.4 安装Node.js 18

```bash
# 安装Node.js 18.x
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt install -y nodejs

# 验证安装
node -v
npm -v

# 安装pnpm（推荐）
npm install -g pnpm
```

---

## 三、数据库初始化

### 3.1 执行基础表结构

```bash
# 进入项目目录
cd /path/to/Shopping

# 执行基础表结构和测试数据
mysql -u shopping -pshopping123 --default-character-set=utf8 shopping < SQL/shopping.sql

# 验证表结构
mysql -u shopping -pshopping123 -e "
SELECT table_name, table_rows 
FROM information_schema.tables 
WHERE table_schema = 'shopping' 
ORDER BY table_name;
"
```

### 3.2 生成大数据量测试数据

```bash
# 执行数据生成脚本（生成1000+订单、5000+行为日志、1500+评价）
mysql -u shopping -pshopping123 --default-character-set=utf8 shopping < SQL/init_order_data.sql

# 验证数据量
mysql -u shopping -pshopping123 -e "
SELECT '用户表' as table_name, COUNT(*) as count FROM user
UNION ALL SELECT '商品表', COUNT(*) FROM product
UNION ALL SELECT '订单表', COUNT(*) FROM \`order\`
UNION ALL SELECT '订单明细表', COUNT(*) FROM order_detail
UNION ALL SELECT '用户行为日志表', COUNT(*) FROM user_behavior_log
UNION ALL SELECT '商品评价表', COUNT(*) FROM product_review;
"
```

**预期输出：**
```
+------------------+-------+
| table_name       | count |
+------------------+-------+
| 用户表           |   103 |
| 商品表           |    65 |
| 订单表           |  2004 |
| 订单明细表       |  4039 |
| 用户行为日志表   |  5000 |
| 商品评价表       |  1500 |
+------------------+-------+
```

---

## 四、大数据环境部署

### 4.1 安装Hadoop 3.3

```bash
# 下载Hadoop
cd /opt
sudo wget https://dlcdn.apache.org/hadoop/common/hadoop-3.3.6/hadoop-3.3.6.tar.gz
sudo tar -xzf hadoop-3.3.6.tar.gz
sudo mv hadoop-3.3.6 hadoop
sudo chown -R $USER:$USER /opt/hadoop

# 配置环境变量
cat >> ~/.bashrc << 'EOF'
export HADOOP_HOME=/opt/hadoop
export HADOOP_CONF_DIR=$HADOOP_HOME/etc/hadoop
export PATH=$PATH:$HADOOP_HOME/bin:$HADOOP_HOME/sbin
export YARN_CONF_DIR=$HADOOP_HOME/etc/hadoop
EOF
source ~/.bashrc
```

### 4.2 配置Hadoop

**core-site.xml**
```xml
<!-- /opt/hadoop/etc/hadoop/core-site.xml -->
<configuration>
    <property>
        <name>fs.defaultFS</name>
        <value>hdfs://localhost:9000</value>
    </property>
    <property>
        <name>hadoop.tmp.dir</name>
        <value>/opt/hadoop/tmp</value>
    </property>
</configuration>
```

**hdfs-site.xml**
```xml
<!-- /opt/hadoop/etc/hadoop/hdfs-site.xml -->
<configuration>
    <property>
        <name>dfs.replication</name>
        <value>1</value>
    </property>
    <property>
        <name>dfs.namenode.name.dir</name>
        <value>/opt/hadoop/data/namenode</value>
    </property>
    <property>
        <name>dfs.datanode.data.dir</name>
        <value>/opt/hadoop/data/datanode</value>
    </property>
</configuration>
```

**mapred-site.xml**
```xml
<!-- /opt/hadoop/etc/hadoop/mapred-site.xml -->
<configuration>
    <property>
        <name>mapreduce.framework.name</name>
        <value>yarn</value>
    </property>
</configuration>
```

**yarn-site.xml**
```xml
<!-- /opt/hadoop/etc/hadoop/yarn-site.xml -->
<configuration>
    <property>
        <name>yarn.nodemanager.aux-services</name>
        <value>mapreduce_shuffle</value>
    </property>
    <property>
        <name>yarn.resourcemanager.hostname</name>
        <value>localhost</value>
    </property>
</configuration>
```

### 4.3 启动Hadoop

```bash
# 创建必要目录
mkdir -p /opt/hadoop/tmp /opt/hadoop/data/namenode /opt/hadoop/data/datanode

# 格式化NameNode（仅首次）
hdfs namenode -format

# 启动HDFS
start-dfs.sh

# 启动YARN
start-yarn.sh

# 验证服务
jps
# 应该看到：NameNode, DataNode, ResourceManager, NodeManager

# 创建HDFS目录
hdfs dfs -mkdir -p /data/ecommerce/warehouse
hdfs dfs -mkdir -p /data/ecommerce/ods
hdfs dfs -mkdir -p /data/ecommerce/dwd
hdfs dfs -mkdir -p /data/ecommerce/dws
hdfs dfs -mkdir -p /data/ecommerce/ads
hdfs dfs -chmod -R 777 /data/ecommerce

# 访问Web UI
# HDFS: http://localhost:9870
# YARN: http://localhost:8088
```

### 4.4 安装Hive 3.1

```bash
# 下载Hive
cd /opt
sudo wget https://dlcdn.apache.org/hive/hive-3.1.3/apache-hive-3.1.3-bin.tar.gz
sudo tar -xzf apache-hive-3.1.3-bin.tar.gz
sudo mv apache-hive-3.1.3-bin hive
sudo chown -R $USER:$USER /opt/hive

# 配置环境变量
cat >> ~/.bashrc << 'EOF'
export HIVE_HOME=/opt/hive
export PATH=$PATH:$HIVE_HOME/bin
export HIVE_CONF_DIR=$HIVE_HOME/conf
EOF
source ~/.bashrc

# 下载MySQL JDBC驱动
wget https://dev.mysql.com/get/Downloads/Connector-J/mysql-connector-java-8.0.28.tar.gz
tar -xzf mysql-connector-java-8.0.28.tar.gz
cp mysql-connector-java-8.0.28/mysql-connector-java-8.0.28.jar $HIVE_HOME/lib/
```

### 4.5 配置Hive

**hive-site.xml**
```xml
<!-- /opt/hive/conf/hive-site.xml -->
<configuration>
    <!-- MySQL元数据库配置 -->
    <property>
        <name>javax.jdo.option.ConnectionURL</name>
        <value>jdbc:mysql://localhost:3306/hive_metastore?createDatabaseIfNotExist=true&amp;useSSL=false</value>
    </property>
    <property>
        <name>javax.jdo.option.ConnectionDriverName</name>
        <value>com.mysql.cj.jdbc.Driver</value>
    </property>
    <property>
        <name>javax.jdo.option.ConnectionUserName</name>
        <value>shopping</value>
    </property>
    <property>
        <name>javax.jdo.option.ConnectionPassword</name>
        <value>shopping123</value>
    </property>
    
    <!-- Hive仓库目录 -->
    <property>
        <name>hive.metastore.warehouse.dir</name>
        <value>/data/ecommerce/warehouse</value>
    </property>
    
    <!-- Metastore配置 -->
    <property>
        <name>hive.metastore.uris</name>
        <value>thrift://localhost:9083</value>
    </property>
    
    <!-- HiveServer2配置 -->
    <property>
        <name>hive.server2.thrift.bind.host</name>
        <value>localhost</value>
    </property>
    <property>
        <name>hive.server2.thrift.port</name>
        <value>10000</value>
    </property>
</configuration>
```

### 4.6 初始化Hive元数据库

```bash
# 创建Hive元数据库
mysql -u shopping -pshopping123 -e "CREATE DATABASE IF NOT EXISTS hive_metastore;"

# 初始化Schema
schematool -dbType mysql -initSchema

# 启动Metastore服务（后台运行）
nohup hive --service metastore > /tmp/metastore.log 2>&1 &

# 启动HiveServer2（后台运行）
nohup hive --service hiveserver2 > /tmp/hiveserver2.log 2>&1 &

# 等待服务启动
sleep 10

# 验证Hive连接
beeline -u "jdbc:hive2://localhost:10000" -e "SHOW DATABASES;"
```

### 4.7 安装Spark 3.5

```bash
# 下载Spark
cd /opt
sudo wget https://dlcdn.apache.org/spark/spark-3.5.1/spark-3.5.1-bin-hadoop3.tgz
sudo tar -xzf spark-3.5.1-bin-hadoop3.tgz
sudo mv spark-3.5.1-bin-hadoop3 spark
sudo chown -R $USER:$USER /opt/spark

# 配置环境变量
cat >> ~/.bashrc << 'EOF'
export SPARK_HOME=/opt/spark
export PATH=$PATH:$SPARK_HOME/bin:$SPARK_HOME/sbin
export PYTHONPATH=$SPARK_HOME/python:$PYTHONPATH
EOF
source ~/.bashrc

# 安装PySpark
pip3 install pyspark==3.5.1
```

### 4.8 配置Spark

**spark-defaults.conf**
```properties
# /opt/spark/conf/spark-defaults.conf
spark.master                     yarn
spark.eventLog.enabled           true
spark.eventLog.dir               hdfs:///spark-logs
spark.history.fs.logDirectory    hdfs:///spark-logs
spark.yarn.historyServer.address localhost:18080
spark.sql.warehouse.dir          /data/ecommerce/warehouse
spark.sql.hive.metastore.version 3.1.3
spark.sql.hive.metastore.jars    path
```

**spark-env.sh**
```bash
# /opt/spark/conf/spark-env.sh
export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
export HADOOP_HOME=/opt/hadoop
export HADOOP_CONF_DIR=$HADOOP_HOME/etc/hadoop
export HIVE_HOME=/opt/hive
export HIVE_CONF_DIR=$HIVE_HOME/conf
export SPARK_HOME=/opt/spark
```

```bash
# 创建Spark日志目录
hdfs dfs -mkdir -p /spark-logs
hdfs dfs -chmod -R 777 /spark-logs

# 启动Spark History Server
$SPARK_HOME/sbin/start-history-server.sh

# 验证Spark
spark-submit --version
pyspark --version
```

---

## 五、后端服务部署

### 5.1 配置数据库连接

编辑 `backend/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/shopping?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: shopping
    password: shopping123
    driver-class-name: com.mysql.cj.jdbc.Driver
```

### 5.2 构建后端

```bash
cd backend

# 使用Maven构建
./mvnw clean package -DskipTests

# 或使用IDE构建
```

### 5.3 启动后端服务

```bash
# 运行Spring Boot应用
java -jar target/shopping-backend-*.jar

# 或使用Maven运行
./mvnw spring-boot:run
```

**验证后端服务：**
```bash
curl http://localhost:8080/api/products
```

---

## 六、前端应用部署

### 6.1 安装依赖

```bash
cd frontend

# 使用pnpm安装（推荐）
pnpm install

# 或使用npm
npm install
```

### 6.2 配置API地址

编辑 `frontend/vite.config.js`：

```javascript
export default defineConfig({
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
```

### 6.3 启动前端

```bash
# 开发模式
pnpm dev

# 或
npm run dev
```

**访问前端：** http://localhost:3000

---

## 七、数据同步与验证

### 7.1 创建Hive数据仓库

```bash
# 执行Hive DDL脚本
hive -f bigdata/hive/ddl/create_tables.sql

# 验证表创建
beeline -u "jdbc:hive2://localhost:10000" -e "
USE shopping_warehouse;
SHOW TABLES;
"
```

**预期输出：**
```
+---------------------------+
|         tab_name          |
+---------------------------+
| ads_category_ranking      |
| ads_conversion_funnel     |
| ads_hot_products          |
| ads_monthly_trend         |
| ads_user_value            |
| dwd_order_detail_wide     |
| dwd_user_behavior_detail  |
| dws_category_sales_daily  |
| dws_product_sales_daily   |
| dws_user_behavior_daily   |
| dws_user_consume_daily    |
| ods_order                 |
| ods_order_detail          |
| ods_product               |
| ods_user                  |
| ods_user_behavior         |
+---------------------------+
```

### 7.2 同步MySQL数据到Hive

```bash
# 使用Sqoop同步（如果已安装Sqoop）
bash bigdata/hive/etl/sync_mysql_to_hive.sh $(date -d "yesterday" +%Y-%m-%d)

# 或使用Spark JDBC同步
spark-submit --master local[*] bigdata/spark/src/main/python/spark_etl.py
```

### 7.3 验证数据同步

```bash
# 使用PyHive验证
python3 bigdata/hive/etl/verify_import.py --method pyhive

# 或使用SparkSQL验证
python3 bigdata/hive/etl/verify_import.py --method spark

# 手动验证
beeline -u "jdbc:hive2://localhost:10000" -e "
USE shopping_warehouse;
SELECT 'ods_order' as table_name, COUNT(*) as count FROM ods_order
UNION ALL
SELECT 'ods_order_detail', COUNT(*) FROM ods_order_detail
UNION ALL
SELECT 'ods_user_behavior', COUNT(*) FROM ods_user_behavior;
"
```

**预期输出：**
```
+---------------------+--------+
|     table_name      | count  |
+---------------------+--------+
| ods_order           | 2004   |
| ods_order_detail    | 4039   |
| ods_user_behavior   | 5000   |
+---------------------+--------+
```

---

## 八、Spark任务执行

### 8.1 执行统计任务

```bash
# 提交Spark统计任务
spark-submit \
    --master local[*] \
    --name "ShoppingStats" \
    --conf spark.sql.warehouse.dir=/data/ecommerce/warehouse \
    bigdata/spark/src/main/python/spark_stats.py
```

### 8.2 查看Spark Web UI

1. **执行任务时**：访问 http://localhost:4040
2. **任务完成后**：访问 http://localhost:18080

**需要截图的页面：**
- Jobs 页面：显示任务执行状态
- Stages 页面：显示任务执行过程
- Tasks 页面：显示每个Task的执行详情

### 8.3 验证统计结果

```bash
# 查询统计结果
beeline -u "jdbc:hive2://localhost:10000" -e "
USE shopping_warehouse;

-- 总数统计
SELECT * FROM ads_table_stats;

-- 分类销售统计
SELECT * FROM ads_category_sales_stats ORDER BY total_revenue DESC LIMIT 10;

-- 热销商品Top10
SELECT * FROM ads_top_products_stats ORDER BY rank_num;
"
```

---

## 九、可视化页面访问

### 9.1 启动前端服务

```bash
cd frontend
pnpm dev
```

### 9.2 访问数据大屏

打开浏览器访问：**http://localhost:3000/admin/data-screen**

**页面功能：**
- 顶部：核心指标卡片（总订单数、总销售额、用户数、转化率）
- 左侧：月度销售趋势折线图
- 中间：品类销售占比饼图 + 热销商品柱状图
- 右侧：用户行为漏斗图 + 设备分布饼图
- 底部：用户价值分层图表

### 9.3 截图说明

按以下顺序截图保存：
1. 数据大屏全屏截图
2. 月度销售趋势图
3. 品类销售占比图
4. 热销商品Top10图
5. 用户转化漏斗图
6. 用户价值分层图

---

## 十、常见问题FAQ

### 10.1 Hadoop相关

**Q: NameNode启动失败**
```bash
# 检查日志
cat /opt/hadoop/logs/hadoop-*-namenode-*.log

# 重新格式化（会丢失数据）
hdfs namenode -format
```

**Q: DataNode无法连接NameNode**
```bash
# 检查防火墙
sudo ufw status
sudo ufw allow 9000
sudo ufw allow 9870

# 检查hosts配置
cat /etc/hosts
```

### 10.2 Hive相关

**Q: Metastore连接失败**
```bash
# 检查Metastore服务
jps | grep RunJar

# 重启Metastore
kill $(jps | grep RunJar | awk '{print $1}')
nohup hive --service metastore > /tmp/metastore.log 2>&1 &
```

**Q: Hive执行查询报错**
```bash
# 检查HDFS权限
hdfs dfs -ls /data/ecommerce/
hdfs dfs -chmod -R 777 /data/ecommerce/
```

### 10.3 Spark相关

**Q: Spark无法连接Hive**
```bash
# 检查Hive配置
echo $HIVE_CONF_DIR

# 确保Hive Metastore运行
netstat -tlnp | grep 9083
```

**Q: PySpark导入错误**
```bash
# 设置PYTHONPATH
export PYTHONPATH=$SPARK_HOME/python:$PYTHONPATH

# 或在spark-submit中指定
spark-submit --py-files $SPARK_HOME/python/lib/py4j-*-src.zip ...
```

### 10.4 MySQL相关

**Q: 连接被拒绝**
```bash
# 检查MySQL服务
sudo systemctl status mysql

# 检查用户权限
mysql -u root -p -e "SELECT user, host FROM mysql.user;"
```

**Q: 字符编码问题**
```bash
# 检查MySQL字符集
mysql -u shopping -pshopping123 -e "SHOW VARIABLES LIKE 'character_set%';"

# 使用utf8mb4连接
mysql -u shopping -pshopping123 --default-character-set=utf8mb4
```

---

## 附录A：项目目录结构

```
Shopping/
├── SQL/
│   ├── shopping.sql              # 基础表结构和测试数据
│   └── init_order_data.sql       # 大数据量生成脚本
├── backend/
│   ├── shopping-backend/         # Spring Boot主模块
│   ├── shopping-common/          # 公共模块
│   ├── shopping-gateway/         # API网关
│   ├── shopping-order/           # 订单服务
│   ├── shopping-product/         # 商品服务
│   └── shopping-user/            # 用户服务
├── frontend/
│   ├── src/
│   │   ├── views/
│   │   │   └── admin/
│   │   │       └── DataScreen.vue  # 数据大屏页面
│   │   └── ...
│   └── package.json
├── bigdata/
│   ├── hadoop/
│   │   ├── core-site.xml
│   │   ├── hdfs-site.xml
│   │   ├── mapred-site.xml
│   │   ├── yarn-site.xml
│   │   └── setup.sh
│   ├── hive/
│   │   ├── ddl/
│   │   │   └── create_tables.sql   # Hive建表脚本
│   │   ├── etl/
│   │   │   ├── sync_mysql_to_hive.sh  # Sqoop同步脚本
│   │   │   └── verify_import.py       # 数据验证脚本
│   │   └── report/
│   │       └── queries.sql            # 统计查询SQL
│   ├── spark/
│   │   └── src/main/python/
│   │       ├── spark_stats.py         # 统计任务
│   │       └── spark_etl.py           # ETL任务
│   └── kafka/
│       ├── create_topics.sh
│       └── pipeline.sh
└── docs/
    └── bigdata-guide.md              # 本文档
```

---

## 附录B：课程要求对照表

| 要求 | 完成情况 | 对应文件/操作 |
|------|----------|---------------|
| ① 数据1000+条 | ✅ | SQL/init_order_data.sql |
| ② MySQL→Hive导入 | ✅ | bigdata/hive/etl/sync_mysql_to_hive.sh |
| ③ 连接Hive验证 | ✅ | bigdata/hive/etl/verify_import.py |
| ④ Spark读取Hive | ✅ | bigdata/spark/src/main/python/spark_stats.py |
| ⑤ 三种统计操作 | ✅ | spark_stats.py (总数/分组/TopN) |
| ⑥ 结果保存Hive | ✅ | write.saveAsTable() |
| ⑦ 可视化展示 | ✅ | frontend/src/views/admin/DataScreen.vue |
| ⑧ Web UI截图 | ✅ | 访问 http://localhost:4040 截图 |

---

## 附录C：快速启动脚本

创建 `start-all.sh` 一键启动所有服务：

```bash
#!/bin/bash
echo "=== 启动Shopping电商平台 ==="

# 1. 启动MySQL
echo "启动MySQL..."
sudo systemctl start mysql

# 2. 启动Hadoop
echo "启动Hadoop..."
start-dfs.sh
start-yarn.sh

# 3. 启动Hive
echo "启动Hive..."
nohup hive --service metastore > /tmp/metastore.log 2>&1 &
nohup hive --service hiveserver2 > /tmp/hiveserver2.log 2>&1 &
sleep 10

# 4. 启动Spark History Server
echo "启动Spark History Server..."
$SPARK_HOME/sbin/start-history-server.sh

# 5. 启动后端
echo "启动后端服务..."
cd backend
nohup java -jar target/shopping-backend-*.jar > /tmp/backend.log 2>&1 &
cd ..

# 6. 启动前端
echo "启动前端服务..."
cd frontend
nohup pnpm dev > /tmp/frontend.log 2>&1 &
cd ..

echo "=== 所有服务已启动 ==="
echo "前端访问: http://localhost:3000"
echo "数据大屏: http://localhost:3000/admin/data-screen"
echo "HDFS: http://localhost:9870"
echo "YARN: http://localhost:8088"
echo "Spark: http://localhost:18080"
```

---

**文档版本：** v1.0
**最后更新：** 2026-06-23
**维护者：** Shopping Team
