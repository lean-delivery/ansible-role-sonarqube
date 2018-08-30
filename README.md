sonarqube role
=========
[![License](https://img.shields.io/badge/license-Apache-green.svg?style=flat)](https://raw.githubusercontent.com/lean-delivery/ansible-role-sonarqube/master/LICENSE)
[![Build Status](https://travis-ci.org/lean-delivery/ansible-role-sonarqube.svg?branch=master)](https://travis-ci.org/lean-delivery/ansible-role-sonarqube)

## Summary
--------------

This role installs SonarQube with extended set of plugins. It uses postgreSQL database and nginx web server which enables https and serves static content.

In addition to default plugins included into SonarQube installation role installs following extra plugins:
  - checkstyle-sonar-plugin-4.11.jar"
  - sonar-pmd-plugin-2.6.jar"
  - sonar-html-plugin-3.0.1.1444.jar"
  - sonar-findbugs-plugin-3.7.0.jar"
  - sonar-groovy-plugin-1.5.jar"
  - sonar-dependency-check-plugin-1.1.1.jar"
  - sonar-jproperties-plugin-2.6.jar"
  - sonar-jdepend-plugin-1.1.1/sonar-jdepend-plugin-1.1.1.jar"
  - sonar-issueresolver-plugin-1.0.2/sonar-issueresolver-plugin-1.0.2.jar"


Requirements
--------------

 - **Mininmal Ansible version**: 2.5
 - **Supported SonarQube versions**:
   - 7.0 - 7.2.1
   - 7.3 is not supported for now due to broken findbugs, checkstyle and pmd plugins (see https://github.com/spotbugs/sonar-findbugs/issues/204, https://github.com/checkstyle/sonar-checkstyle/issues/157, https://github.com/SonarQubeCommunity/sonar-pmd/issues/49)
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
  - `sonar_optional_plugins` - list of additional plugins, see defaults/main.yml 
    default: []

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
      sonar_optional_plugins:
        - "https://sonarsource.bintray.com/Distribution/sonar-auth-github-plugin/\
          sonar-auth-github-plugin-1.3.jar" 		
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
