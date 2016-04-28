# README - Ovirt Dashboard for DR #

This is a web application, that uses Java + Ovirt API + PostgreSQL, to perform the task needed to recover from a Disaster Recovery. 



## Assumptions ##

* All the master Storage Domains for each Datacenter, has been replicated from site A to site B
* There is an exact copy of the RHEV-M/Engine running on site B. 
 

## How does this work ? ##

* TBD

## Installation Guide ##

### Requirements

* Centos/RHEL 7.x -> latest version __(minimal installation)__
* Java Oracle 8, *needs testing with OpenJDK*
* MariaDB/MySQL 5.x
* Play Framework 1.4, *we need some help freeing the app, from this framework, we needed to deliver the app fast for a customer, so we used this framework.* [Play Framework](https://www.playframework.com/download)

### Preparing OS 

* Always update your OS packages, then install required. 

```
yum update -y ; \ 
yum install httpd mariadb-server mariadb bash-completion -y
```
* Installing Java Oracle 1.8 

For RHEL 7.x

```
subscription-manager repos --enable rhel-7-server-thirdparty-oracle-java-rpms ; \
yum install java-1.8.0-oracle -y
```

For Centos 7.x

Download from: [Java 1.8 Oracle](http://www.oracle.com/technetwork/java/javase/downloads/index.html) 

```
yum localinstall jre-8u91-linux-x64.rpm -y
```

Verify Java version
```
[root@localhost ~]# java -version
java version "1.8.0_XX"
Java(TM) SE Runtime Environment (build 1.8.0_XX)
Java HotSpot(TM) 64-Bit Server VM (build XX, mixed mode)
```

### Installing Overt Dashboard for DR from RPM

* Download YUM REPO
```
```

* Install RPMs
```
yum install ovirt-dashboard-dr ovirt-dashboard-dr-play
```  

### Configure Database

* Start/Enable MariaDB

```
systemctl enable mariadb
systemctl start mariadb
```

* Create DB

```
mysqladmin create ovirtdrpdashboarddr
```

* Import Initial DB

```
mysql -u root -p ovirtdrpdashboarddr < /usr/share/doc/ovirt-dashboard-dr-${VERSION}-${RELEASE}/ovirt-dashboard-dr.sql
```
## Who do I talk to? ##

* Chocomango - José Eduardo Andrade Escobar <jandrad@chocomando.net>
* ITM - Jorge Luis Andrade Escobar <jandrade@itm.gt>