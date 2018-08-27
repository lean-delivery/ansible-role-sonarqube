sonarqube role
=========
[![License](https://img.shields.io/badge/license-Apache-green.svg?style=flat)](https://raw.githubusercontent.com/lean-delivery/ansible-role-sonarqube/master/LICENSE)
[![Build Status](https://travis-ci.org/lean-delivery/ansible-role-sonarqube.svg?branch=master)](https://travis-ci.org/lean-delivery/ansible-role-sonarqube)

## Summary
--------------

This role installs SonarQube with extended set of plugins. It uses postgreSQL database and nginx web server which enables https and serves static content.


Requirements
--------------

 - Minimal Version of the ansible for installation: 2.5
 - Maximal Version of the ansible for installation: 2.5.*
 - **Supported SonarQube versions**:
   - 7.0 - 7.2.1
   - 7.3 (without findbugs, checkstyle and pmd plugins)
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
      - `host` -
        default: "0.0.0.0"
        `port` -
		default: 9000
        `path` -
        default: "/"


Example Playbook
----------------



## License

Apache2

## Authors

team@lean-delivery.com
