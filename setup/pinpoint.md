# 搭建PinPoint


## 安装JDK
* 配置JAVA环境变量
    export JAVA_HOME=xxxxxxxxx

## 安装HBase
* [下载HBase](https://hbase.apache.org/downloads.html)
* 在环境中解压
    tar -xzvf hbase-2.0.4-bin.tar.gz
* 修改hbase-env.sh
    使用Hbase自带的zookeeper，如使用外置zookeeper需置为false
    export HBASE_MANAGES_ZK=true
* 修改hbase-site.xml

    <property>
        <name>zookeeper.znode.parent</name>
        <value>/hbase</value>
    </property>

