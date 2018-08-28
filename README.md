sonarqube role
=========
[![License](https://img.shields.io/badge/license-Apache-green.svg?style=flat)](https://raw.githubusercontent.com/lean-delivery/ansible-role-sonarqube/master/LICENSE)
[![Build Status](https://travis-ci.org/lean-delivery/ansible-role-sonarqube.svg?branch=master)](https://travis-ci.org/lean-delivery/ansible-role-sonarqube)

## Summary
--------------

This role installs SonarQube with extended set of plugins. It uses postgreSQL database and nginx web server which enables https and serves static content.


Requirements
--------------

 - **Supported Ansible versions**:
   - 2.5.*
   - 2.6.* is not supported, see https://github.com/lean-delivery/ansible-role-sonarqube/issues/3
 - **Supported SonarQube versions**:
   - 7.0 - 7.2.1
   - 7.3 without findbugs, checkstyle and pmd plugins (see https://github.com/spotbugs/sonar-findbugs/issues/204, https://github.com/checkstyle/sonar-checkstyle/issues/157, https://github.com/SonarQubeCommunity/sonar-pmd/issues/49)
   - _lower and higher versions should be retested_
 - **Supported databases**
   - PostgreSQL
   - MySQL (not recommended)
 - **Supported web servers** 
   - nginx 
 - **Supported OS**:
   - CentOS
     - 7
   - RHEL
     - 7   

Java, database, web server with self-signed certificate should be installed preliminarily:
    - lean_delivery.java
    - anxs.postgresql
    - jdauphant.ssl-certs
    - nginxinc.nginx


Role Variables
--------------

  - `sonar_major_version` - major number of SonarQube version
    default: 7
  - `sonar_minor_version` - minor number of SonarQube version
    default: 2.1
  - `sonar_path` - installation directory
    default: "/opt/sonarqube"
  - `sonar_user` - user for installing SonarQube
    default: "sonar"
  - `sonar_group` - group of SonarQube user
    default: "sonar"
  - `web`:
      - `host` - SonarQube binding ip address
        default: "0.0.0.0"
        `port` - TCP port for incoming HTTP connections
		default: 9000
        `path` - web context
        default: "/"
  - `sonar_db` - database settings
      - `type` 
	    default : "postgresql"
        `port`
		default : 5432
        `host`
		default : "localhost"
        `name`
		default: "sonar"
        `user`
		default: "sonar"
        `password`
		default: "sonar"
        `options`
		default: ""
  - `sonar_store` - sonarqube artifact provider
    default: "https://sonarsource.bintray.com/Distribution/sonarqube"
  - `download_path` - local download path
    default: "/tmp/"		
  - `sonar_proxy` - web server settings
      - `type`
	    default: "nginx"
	    `port`
	    default: 443
        `ssl`
	    default: True
        `ssl_cert_path`
		default: "/etc/pki/tls/certs/{{ ansible_hostname }}.ca-cert.pem"
        `ssl_key_path`
		default: "/etc/pki/tls/private/{{ ansible_hostname }}.ca-pkey.pem"	
  - `sonar_plugins` - list of plugins that not included into SonarQube installation and should be also installed (override this variable excluding findbugs, checkstyle, pmd when installing SonarQube 7.3)
    default: see in /vars/main.yml
  - `sonar_exclude_plugins` - list of default plugins that should be excluded during installation
    default: see in /vars/main.yml

Example Playbook
----------------
```yaml
- name: "Install SonarQube 7.2.1"
  hosts: "all"
  become: True
  pre_tasks:
    - name: "install epel"
      package:
        name: "https://dl.fedoraproject.org/pub/epel/epel-release-latest-7.noarch.rpm"
        state: "present"
      when: ansible_distribution == 'RedHat'
  roles:
    - "lean_delivery.java"
    - "ANXS.postgresql"
	  postgresql_users:
        - name: sonar
          pass: sonar
	  postgresql_databases:
        - name: sonar
          owner: sonar	  
    - "jdauphant.ssl-certs"
	  ssl_certs_path_owner: "root"
      ssl_certs_path_group: "root"
      ssl_certs_mode: "0755"
    - "nginxinc.nginx"
    - "ansible-role-sonarqube"
      web:
        host: "localhost"
        port: 9000
        path: "/" 
      sonar_proxy:
        type: "nginx"
        port: 443
        ssl: True  
        ssl_cert_path: "/etc/ssl/{{ ansible_fqdn }}/{{ ansible_fqdn }}.pem"
        ssl_key_path: "/etc/ssl/{{ ansible_fqdn }}/{{ ansible_fqdn }}.key"    
  post_tasks:
    - name: "start sonarqube"
      service: name="sonarqube" state="started"
    - name: "restart nginx"
      service: name="nginx" state="restarted"
```

## License

Apache2

## Authors

team@lean-delivery.com
