%define 	play_version play-1.4.4
%define 	uid    199
%define 	gid    199
%define 	user   ovirtdr
%define 	group  ovirtdr
%define		appname web
%define __jar_repack 1

Name:           ovirt-engine-disaster-recovery
Version:        1.0develop
Release:        %{release}
License:        ASL 2.0
URL:            https://github.com/xandradx/ovirt-engine-disaster-recovery
Summary:	oVirt Engine Disaster Recovery Framework (play)
#Source0:	https://github.com/xandradx/ovirt-engine-disaster-recovery
Source0:	%{play_version}.zip
BuildRoot:  	%(mktemp -ud %{_tmppath}/%{name}-%{version}-%{release}-XXXXXX)
BuildArch:  	noarch
BuildRequires:  bash git npm 
Requires: util-linux java-1.8.0-openjdk-headless mariadb-server
Requires(pre): shadow-utils
AutoReqProv: no

%description 
This is The Framework needed to run and build the APP

%package app
Summary: oVirt Engine Disaster Recovery Web Application
Requires: ovirt-engine-disaster-recovery = %{version}-%{release}

%description app
This is a web application, that uses Java + oVirt API, to perform the task needed to recover from a Disaster.

%prep
exit 0

%build
unzip %{SOURCE0}
cp -r %{_sourcedir}/* . || exit 0 
exit 0

%install
rm -Rf %{buildroot}


#bower install


#install

#rm -rf $RPM_BUILD_ROOT

PLAYDIR=%{play_version}


%{__mkdir} -p   %{buildroot}/opt/%{name}
%{__mkdir} -p   %{buildroot}/opt/%{name}/%{appname}
%{__mkdir} -p   %{buildroot}/opt/%{name}/%{appname}/logs
%{__mkdir} -p   %{buildroot}/opt/%{name}/%{appname}/tmp
%{__mkdir} -p   %{buildroot}/etc/systemd/system
%{__mkdir} -p   %{buildroot}/var/log/%{name}
%{__mkdir} -p   %{buildroot}/etc/%{name}


#{play_version}/play clean %{name}
#{play_version}/play deps %{name}


install ${PLAYDIR}/play %{buildroot}/opt/%{name}/
cp docs/ovirt-engine-disaster-recovery.service %{buildroot}/etc/systemd/system/

for dir in framework python resources support modules 
do
	cp -R ${PLAYDIR}/${dir} %{buildroot}/opt/%{name}/
done


for dir in lib app conf public modules
do
	cp -R ${dir} %{buildroot}/opt/%{name}/%{appname}/
done

cp %{buildroot}/opt/%{name}/%{appname}/conf/application.conf %{buildroot}/etc/%{name}/%{name}.conf

ln -sf /etc/%{name}/%{name}.conf %{buildroot}/opt/%{name}/%{appname}/conf/application.conf
sed -i 's/^log4j.appender.Rolling.File=.*/log4j.appender.Rolling.File=\/var\/log\/ovirt-engine-disaster-recovery\/application.log/g' %{buildroot}/opt/%{name}/%{appname}/conf/log4j.properties

%pre 
getent group %{group} >/dev/null || groupadd -f -g %{gid} -r %{group}
if ! getent passwd %{user} > /dev/null
then
	if ! getent passwd %{uid} > /dev/null 
	then
		useradd -r -u %{uid} -g %{group} -c "oVirt Engine Disaster Recovery" -d /opt/%{name} -s /sbin/nologin %{user}
	else
		useradd -r -g %{group} -c "oVirt Engine Disaster Recovery" -d /opt/%{name} -s /sbin/nologin %{user}
	fi
fi
exit 0

%post app
systemctl daemon-reload


%preun app
systemctl stop %{name}
systemctl disable %{name}

%postun app
systemctl daemon-reload


%files 
%defattr(-,%{user},%{group},-)

%dir /opt/%{name}
/opt/%{name}/play
#/opt/%{name}/documentation
/opt/%{name}/framework
/opt/%{name}/modules
/opt/%{name}/python
/opt/%{name}/resources
/opt/%{name}/support


%files app
%defattr(-,%{user},%{group},-)
/etc/systemd/system/ovirt-engine-disaster-recovery.service
/opt/%{name}/%{appname}
%dir /etc/%{name}
%config(noreplace)/etc/%{name}/%{name}.conf
%doc README.md
%doc docs/ovirt-engine-disaster-recovery.sql
/var/log/ovirt-engine-disaster-recovery

%changelog
* Wed Feb 08 2017 Jorge Andrade <jandrade@itm.gt> - 0.1-14
- First public testing release
* Wed Feb 08 2017 Jorge Andrade <jandrade@itm.gt> - 0.1-11
- Fixes dependencies on SPEC file
* Wed Feb 08 2017 Jorge Andrade <jandrade@itm.gt> - 0.1-9
- First version with full API support, required ovirt/rhev 3.6.10+


