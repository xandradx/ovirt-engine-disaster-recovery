# README - Ovirt Dashboard for DR #

This is a web application, that uses Java + Ovirt API + PostgreSQL, to perform the task needed to recover from a Disaster Recovery. 



## Assumptions ##

* All the master Storage Domains for each Datacenter, has been replicated from site A to site B
* There is an exact copy of the RHEV-M/Engine running on site B.
* Please refer to next section. 
 

## How does this work ? ##

* This project try to implement Approach #2, presented on this slides DevConf.cz - 2014 [Disaster Recovery Strategies Using oVirt's new Storage Connection Management Features](http://www.slideshare.net/AllonMureinik/dev-conf-ovirt-dr)
* The was to use 100% the API, but we needed to interact directrly with the database due to current pending feature to edit - Storage Connection, without setting the Storage Domains, in maintenence mode. - Reference [Updating a Storage Connection](https://access.redhat.com/documentation/en-US/Red_Hat_Enterprise_Virtualization/3.5/html/Technical_Guide/sect-Methods6.html)

## Installation Guide ##

### Requirements

* Centos/RHEL 7.x -> latest version __(minimal installation)__
* Java Oracle 8, *needs testing with OpenJDK*
* MariaDB/MySQL 5.x
* Play Framework 1.4, *we need some help freeing the app, from this framework, we needed to deliver the app fast for a customer, so we used this framework.* [Play Framework](https://www.playframework.com/download)

### Preparing OS 

* Always update your OS packages, then install required. 

```
yum update -y 
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

Open port TCP 9000

```
firewall-cmd --add-port=9000/tcp --permanent
systemctl restart firewalld

```

### Installing Overt Dashboard for DR from RPM

* Download YUM REPO

```
cd /etc/yum.repos.d/
curl -O http://supp01.i-t-m.com/ovirt-dashboard-dr.repo

```
This is a temporal repository and can/will be changed in the future !

* Install RPMs

```
yum install -y ovirt-dashboard-dr-app
```  

### Configure Database

* Start/Enable MariaDB

```
systemctl enable mariadb
systemctl start mariadb
```

* MariaDB Secure Installation

```
mysql_secure_installation
```
* Create DB

```
mysqladmin create ovirtdashboarddr -p
```

* Import Initial DB

```
mysql -u root -p ovirtdashboarddr < /usr/share/doc/ovirt-dashboard-dr-${VERSION}-${RELEASE}/ovirt-dashboard-dr.sql
```

* Create DB User

```
mysql -u root -p

MariaDB [(none)]> CREATE USER 'ovirtdr'@'localhost' IDENTIFIED BY 'YOURPASSWOR-HERE';
MariaDB [(none)]> GRANT ALL PRIVILEGES ON ovirtdashboarddr.* TO 'ovirtdr'@'localhost';

```

## Application Configuration

* Configure Database Parameters

Edit configuration file: __/etc/ovirt-dashboard-dr/ovirt-dashboard-dr.conf__

```
# Change Values on Line 91 for MariaDB

90 # To connect to a local MySQL5 database, use:
91 db=mysql://ovirtdr:YOURPASSWOR-HERE@localhost:3306/ovirtdashboarddr

# Change Value on Line 215 for PostgreSQL

215 ovirt.db.host = YOUR-RHEV-M-HOSTNAME-HERE # Example: rhevm.internal.itm.gt
216 ovirt.db.port = 5432
217 ovirt.db.user = engine
218 ovirt.db.password = YOUR-PGSQL-ENGINE-PASSWORD-HERE
219 ovirt.db.name = engine

```

# Service Administration

## Systemctl Administration

* Start

```
systemctl start ovirt-dashboard-dr

```

* Status

```
systemctl status ovirt-dashboard-dr

```

* Stop

```
systemctl stop ovirt-dashboard-dr

```


## LOG FILE
```
/var/log/ovirt-dasshboard-dr/application.log
```

You should monitor this file, for JAVA Exceptions, or SQL Errors.

One common error is MariaDB wrong credentials.

## Next Step

* [Administration Guide](https://bitbucket.org/chocomango/ovirt-dashboard/downloads/AdministrationGuideOvirtDashboardforDR-draft-v1.pdf)
* [Spanish Videos](https://www.youtube.com/watch?v=ef_ZmgixzJM&list=PLgtS4_6D5_KUQGnLpaTVZkHJScKcLUZoX&index=2)
* [English Videos](coming soon)

## Who do I talk to? ##

* [Chocomango](http://chocomango.net) - José Eduardo Andrade Escobar <jandrad@chocomando.net> 
* [ITM](http://itm.gt) - Jorge Luis Andrade Escobar <jandrade@itm.gt> (yes, we are brothers)