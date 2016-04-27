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
* Java Oracle 1.7, *needs testing with OpenJDK*
* MariaDB/MySQL 5.x
* Play Framework 1.2, *we need some help freeing the app, from this framework, we needed to deliver the app fast for a customer, so we used this framework*

### Preparing OS 

* Always update your OS packages, then install required packages. 

```
yum update -y ; \ 
yum install httpd mariadb-server mariadb bash-completion -y
```
* Installing Java Oracle 1.7 

For RHEL 7.x

```
subscription-manager repos --enable rhel-7-server-thirdparty-oracle-java-rpms ; \
yum install java-1.7.0-oracle -y
```

For Centos 7.x

```

```

## Who do I talk to? ##

* Chocomango - José Eduardo Andrade Escobar <jandrad@chocomando.net>
* ITM - Jorge Luis Andrade Escobar <jandrade@itm.gt>