# CDH6.3.2安装

## 1 前期准备

### 1.1 软硬件要求

服务器3台，系统要求Centos7

### 1.2 端口规划

| 服务名称         | 端口号 |
| ---------------- | ------ |
| cloudera manager | 7180   |
| resourcemanager  | 8088   |
| hue              | 8888   |
| namennode        | 50070  |
| spark            | 19888  |
| kafka            | 9092   |
| mysql            | 3306   |
| hive             | 10000  |
| hbase            | 60030  |
| zookeeper        | 2181   |

### 1.3 关闭tuned

```java
systemctl stop tuned
systemctl disable tuned
systemctl status tuned
```

## 2 集群环境配置

### 2.1 修改主机名

```shell
#在不同主机执行不同命令
hostnamectl set-hostname cm1
hostnamectl set-hostname cm2
hostnamectl set-hostname cm3
```

### 2.2 关闭防火墙

```she
方法一：
service iptables stop   #关闭防火墙 
chkconfig iptables off  #禁止开机启动
方法二：
systemctl disable firewalld
systemctl status firewalld
```

### 2.3 关闭selinux

```shell
vim /etc/selinux/config
#找到这个改为关闭
selinux=disabled
```

### 2.4 更改文件句柄

```shell
vim /etc/security/limits.conf
添加
*		soft		nofile		1769445
*		hard		nofile		1029345
*		soft		nproc		unlimited
```

### 2.5 关闭透明大页面

```shell
echo never > /sys/kernel/mm/transparent_hugepage/defrag
echo never > /sys/kernel/mm/transparent_hugepage/enabled
 
 
vim /etc/rc.d/rc.local
添加如下内容
echo never > /sys/kernel/mm/transparent_hugepage/defrag
echo never > /sys/kernel/mm/transparent_hugepage/enabled
```

### 2.6 设置swappiness

```shell
sysctl vm.swappiness=1
echo 1 > /proc/sys/vm/swappiness
 
vim /etc/sysctl.conf
vm.swappiness=1
```

### 2.7 配置host映射

```shell
vim /etc/hosts
192.168.10.101 cm1
192.168.10.102 cm2
192.168.10.103 cm3
```

### 2.8 配置免密登录

进入`~/.ssh`文件夹，在每台服务器下执行以下操作：

```shel
ssh-keygen -t rsa //三次回车

ssh-copy-id cm1
ssh-copy-id cm2
ssh-copy-id cm3
```

### 2.9 安装JDK

#### 2.9.1安装CDH自带jdk

```shell
rpm -ivh oracle-j2sdk1.8-1.8.0+update181-1.x86_64.rpm
```

#### 2.9.2配置环境变量

```
export JAVA_HOME=/usr/java/jdk1.8.0_181-cloudera
export CLASSPATH=.:$CLASSPATH:$JAVA_HOME/lib
export PATH=$PATH:$JAVA_HOME/bin
```

分发jdk文件和配置文件

### 2.10 初始化MySQL

```sql
mysql -uroot -p
 
# 新建scm用户
CREATE USER 'cdh'@'%' IDENTIFIED BY '123456'; 
grant all privileges on *.* to 'root'@'%' identified by '123456' with grant option;
grant all privileges on *.* to 'cdh'@'%' identified by '123456' with grant option;
flush privileges;
```

#### 2.10.1 创建CM需要的库

```sql
CREATE DATABASE scm DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;
CREATE DATABASE hive DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;
CREATE DATABASE amon DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;
CREATE DATABASE rman DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;
CREATE DATABASE hue DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;
CREATE DATABASE metastore DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;
CREATE DATABASE sentry DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;
CREATE DATABASE nav DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;
CREATE DATABASE navms DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;
CREATE DATABASE oozie DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;
```

#### 2.10.2 安装MySQL JDBC驱动

```shell
# 下载
wget https://dev.mysql.com/get/Downloads/Connector-J/mysql-connector-java-5.1.49.tar.gz
# 解压
tar -zxf mysql-connector-java-5.1.49.tar.gz
# 创建目录
mkdir /usr/share/java/
# 放到指定目录（一定要改名为mysql-connector-java.jar）
sudo cp mysql-connector-java-5.1.49/mysql-connector-java-5.1.49-bin.jar /usr/share/java/mysql-connector-java.jar
```

## 3 Cloudera Manager 安装部署

#### 3.1 下载CM的压缩包并上传到集群中的某台主机并解压

```shell
tar -zxvf cm6.3.1-redhat7.tar.gz
```

#### 3.2 进入解压之后的路径，执行以下命令，以发布该yum仓库

```shell
cd cm6.3.1
nohup python -m SimpleHTTPServer 8900 &
```

可用浏览器访问，http://cm1:8900 地址，如响应如下页面，则表示yum仓库发布成功

#### 3.3 在所有主机上创建yum仓库repo文件

```
vim /etc/yum.repos.d/cloudera-manager.repo
 
[cloudera-manager]
name=Cloudera Manager 6.3.1
baseurl=http://node-01:8900/
gpgkey=http://node-01:8900/RPM-GPG-KEY-cloudera
gpgcheck=1
enabled=1
autorefresh=0
type=rpm-md
```

#### 3.4 在主节点执行

```shell
yum -y install cloudera-manager-daemons cloudera-manager-agent cloudera-manager-server
```

#### 3.5 在从节点执行

```shell
yum -y install cloudera-manager-daemons cloudera-manager-agent
```

#### 3.6 在所有节点修改配置文件/etc/cloudera-scm-agent/config.ini ,修改其server_host参数

```
[General]
# Hostname of the CM server.
server_host=cm1
```

## 4 初始化

```shell
/opt/cloudera/cm/schema/scm_prepare_database.sh --host node-01 --scm-host node-01  mysql scm root 123456
```

## 5 启动CM

### 5.1 在主节点（cm1）执行以下命令，启动Server 和 Agent

```shell
systemctl start cloudera-scm-server cloudera-scm-agent
```

### 5.2 在其余节点执行以下命令，启动Agent

```she
systemctl start cloudera-scm-agent
```

### 6 部署CDH

### 6.1 上传CDH parcel到Cloudera Manager Server

```shell
并为parcel文件生成SHA1校验文件
sha1sum CDH-6.3.2-1.cdh6.3.2.p0.1605554-el7.parcel | awk '{ print $1 }' > CDH-6.3.2-1.cdh6.3.2.p0.1605554-el7.parcel.sha
更改parcel文件所有者
chown -R cloudera-scm:cloudera-scm /opt/cloudera/parcel-repo/*
重启Cloudera Manager，令其识别到本地库
systemctl restart cloudera-scm-server
```

### 6.2 登录Cloudera Manager，初始用户名和密码均为admin 
