sonarqube role
=========
[![License](https://img.shields.io/badge/license-Apache-green.svg?style=flat)](https://raw.githubusercontent.com/lean-delivery/ansible-role-sonarqube/master/LICENSE)
[![Build Status](https://travis-ci.org/lean-delivery/ansible-role-sonarqube.svg?branch=master)](https://travis-ci.org/lean-delivery/ansible-role-sonarqube)

## Summary
--------------

This role installs SonarQube with extended set of plugins. It uses postgreSQL database and nginx web server which enables https and serves static content.

In addition to default plugins included into SonarQube installation role installs following extra plugins:
  - checkstyle-sonar-plugin-4.17
  - sonar-pmd-plugin-3.1.3
  - sonar-findbugs-plugin-3.9.1
  - sonar-html-plugin-3.0.1.1444
  - sonar-groovy-plugin-1.5.jar
  - sonar-yaml-plugin-1.1.0.jar
  - sonar-jproperties-plugin-2.6.jar
  - sonar-dependency-check-plugin-1.1.1
  - sonar-jdepend-plugin-1.1.1
  - sonar-issueresolver-plugin-1.0.2

Requirements
--------------

 - **Mininmal Ansible version**: 2.5
 - **Supported SonarQube versions**:
   - 7.0 - 7.2.1
   - 7.3 - 7.4 are not supported for now due to broken checkstyle plugin (see https://github.com/checkstyle/sonar-checkstyle/issues/157)
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

Java, database, web server with self-signed certificate should be installed preliminarily. Use following galaxy roles:
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
  - `sonar_nofile` - file descriptors amount that user running SonarQube can open
    default: "65536"
  - `sonar_nproc` - threads amount that user running SonarQube can open
    default: "2048"
  - `sonar_log_level` - Logging level of SonarQube server
    default: "INFO"
  - `sonar_java_opts`:
      - `web` - additional java options for web part of SonarQube
        default: "-Xmx512m -Xms128m"
        `es` - additional java options for Elasticsearch 
        default: "-Xms512m -Xmx512m"
        `ce` - additional java options for Compute Engine 
        default: "-Xmx512m -Xms128m"
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
  - `sonar_proxy_type` - web server, nginx is only supported for now
    default: `nginx`
  - `sonar_proxy_server_name` - server name in webserver config
    default: `{{ ansible_hostname }}`
  - `sonar_proxy_http` - is http connection allowed
    default: `False`
  - `sonar_proxy_http_port` - http port
    default: `80`
  - `sonar_proxy_ssl` - is https connection allowed
    default: `True`
  - `sonar_proxy_ssl_port` - https port
    default: `443`
  - `sonar_proxy_ssl_cert_path` - path to certificate
    default: `/etc/ssl/{{ sonar_proxy_server_name }}/{{ sonar_proxy_server_name }}.pem`
  - `sonar_proxy_ssl_key_path` - path to key
    default: `/etc/ssl/{{ sonar_proxy_server_name }}/{{ sonar_proxy_server_name }}.key`
  - `sonar_proxy_client_max_body_size` - client max body size setting in web server config
    default: `32m`
  - `sonar_optional_plugins` - list of additional plugins, see playbook example below
    default: []
  - `sonar_exclude_plugins` - list of plugins excluded from SonarQube installer

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
    # delete plugins installed on previous run to prevent conflict in case if any plugin is updated
    - name: "delete plugins"
      file:
        path: "{{ sonar_path }}/sonarqube-{{ sonar_major_version }}.{{ sonar_minor_version }}/extensions/plugins"
        state: absent      
  roles:
    - role: lean_delivery.java
    - role: ANXS.postgresql
      postgresql_users:
        - name: sonar
          pass: sonar
      postgresql_databases:
        - name: sonar
          owner: sonar
    - role: jdauphant.ssl-certs
      ssl_certs_common_name: "example.com"
      ssl_certs_path_owner: "root"
      ssl_certs_path_group: "root"
      ssl_certs_mode: 0755
    - role: nginxinc.nginx
    - role: ansible-role-sonarqube
      sonar_java_opts:
        web: "-server -Xmx1g -Xms1g"
        es: "-Xmx2g -Xms2g" 
        ce: "-Xmx1g -Xms1g"
      web:
        host: "localhost"
        port: 9000
        path: "/" 
      sonar_proxy_server_name: "{{ ssl_certs_common_name }}"
      sonar_optional_plugins:
        - "https://sonarsource.bintray.com/Distribution/sonar-auth-github-plugin/\
          sonar-auth-github-plugin-1.3.jar
        - "https://github.com/QualInsight/qualinsight-plugins-sonarqube-smell/releases/download/\
          qualinsight-plugins-sonarqube-smell-4.0.0/qualinsight-sonarqube-smell-plugin-4.0.0.jar"
        - "https://github.com/QualInsight/qualinsight-plugins-sonarqube-badges/releases/download/\
          qualinsight-plugins-sonarqube-badges-3.0.1/qualinsight-sonarqube-badges-3.0.1.jar"
        - "https://github.com/racodond/sonar-json-plugin/releases/download/2.3/\
          sonar-json-plugin-2.3.jar"
        - "https://github.com/SonarSource/sonar-auth-bitbucket/releases/download/1.0/\
          sonar-auth-bitbucket-plugin-1.0.jar"
        # you have to build this plugin manually after role is installed, use "mvn clean install" command
        - "https://github.com/mibexsoftware/sonar-bitbucket-plugin/archive/\
          v1.2.3.zip"
        - "https://github.com/RIGS-IT/sonar-xanitizer/releases/download/1.5.0/\
          sonar-xanitizer-plugin-1.5.0.jar"
        - "https://github.com/gabrie-allaigre/sonar-gitlab-plugin/releases/download/3.0.1/\
          sonar-gitlab-plugin-3.0.1.jar"
        - "https://github.com/gabrie-allaigre/sonar-auth-gitlab-plugin/releases/download/1.3.2/\
          sonar-auth-gitlab-plugin-1.3.2.jar"
        - "https://binaries.sonarsource.com/Distribution/sonar-css-plugin/\
          sonar-css-plugin-1.0.2.611.jar"
        - "https://binaries.sonarsource.com/Distribution/sonar-kotlin-plugin/\
          sonar-kotlin-plugin-1.2.1.2009.jar"
  post_tasks:
    - name: "start sonarqube"
      service: name="sonarqube" state="started"
    - name: "delete default nginx config"
      file:
        path: /etc/nginx/conf.d/default.conf
        state: absent
    - name: "restart, enable nginx"
      service: name="nginx" state="restarted" enabled=True
    # see https://github.com/ANXS/postgresql/issues/363
    - name: "enable postgresql"
      service: name="postgresql-{{ postgresql_version }}" enabled=True
      when: ansible_distribution == 'CentOS'
```

## License

Apache2

## Authors

team@lean-delivery.com
