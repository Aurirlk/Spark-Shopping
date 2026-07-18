#!/bin/bash
# =====================================================
# Hadoop环境配置脚本
# 适用于Linux/WSL环境
# =====================================================

echo "=== Hadoop环境配置 ==="

# 1. 设置环境变量
cat >> ~/.bashrc << 'EOF'

# Hadoop环境变量
export HADOOP_HOME=/opt/hadoop
export HADOOP_CONF_DIR=$HADOOP_HOME/etc/hadoop
export PATH=$PATH:$HADOOP_HOME/bin:$HADOOP_HOME/sbin

# Java环境变量
export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
export PATH=$PATH:$JAVA_HOME/bin
EOF

source ~/.bashrc

# 2. 创建必要目录
mkdir -p /opt/hadoop/tmp
mkdir -p /opt/hadoop/hdfs/namenode
mkdir -p /opt/hadoop/hdfs/datanode

# 3. 格式化NameNode（首次运行时执行）
echo "注意：首次运行需要格式化NameNode"
echo "执行命令：hdfs namenode -format"

# 4. 启动Hadoop
echo "启动Hadoop命令："
echo "  start-dfs.sh    # 启动HDFS"
echo "  start-yarn.sh   # 启动YARN"

# 5. 验证安装
echo "验证命令："
echo "  hdfs dfs -ls /          # 查看HDFS根目录"
echo "  hdfs dfsadmin -report   # 查看HDFS状态"
echo "  yarn node -list         # 查看YARN节点"

echo "=== 配置完成 ==="
