SonarQube role
=========
[![License](https://img.shields.io/badge/license-Apache-green.svg?style=flat)](https://raw.githubusercontent.com/lean-delivery/ansible-role-sonarqube/master/LICENSE)
[![Build Status](https://travis-ci.org/lean-delivery/ansible-role-sonarqube.svg?branch=master)](https://travis-ci.org/lean-delivery/ansible-role-sonarqube)
[![Build Status](https://gitlab.com/lean-delivery/ansible-role-sonarqube/badges/master/build.svg)](https://gitlab.com/lean-delivery/ansible-role-sonarqube/pipelines)
[![Galaxy](https://img.shields.io/badge/galaxy-lean__delivery.sonarqube-blue.svg)](https://galaxy.ansible.com/lean_delivery/sonarqube)
![Ansible](https://img.shields.io/ansible/role/d/29212.svg)
![Ansible](https://img.shields.io/badge/dynamic/json.svg?label=min_ansible_version&url=https%3A%2F%2Fgalaxy.ansible.com%2Fapi%2Fv1%2Froles%2F29212%2F&query=$.min_ansible_version)

## Summary
--------------

This role installs SonarQube with extended set of plugins. It uses postgreSQL database and nginx web server which enables https and serves static content.

In addition to default plugins included into SonarQube installation role installs following extra plugins:
  - checkstyle-sonar-plugin-4.20
  - sonar-pmd-plugin-3.2.1
  - sonar-findbugs-plugin-3.11.0
  - sonar-jdepend-plugin-1.1.1
  - sonar-jproperties-plugin-2.6
  - sonar-dependency-check-plugin-1.2.3
  - sonar-issueresolver-plugin-1.0.2
  - sonar-json-plugin-2.3
  - sonar-yaml-plugin-1.4.2
  - sonar-ansible-extras-plugin-2.1.0
  - sonar-shellcheck-plugin-2.1.0
  
Also you may install optional plugins. Be carefull, not all of them are supported in latest SonarQube versions:
  - qualinsight-sonarqube-smell-plugin-4.0.0
  - qualinsight-sonarqube-badges-3.0.1
  - sonar-auth-github-plugin-1.5.0.870
  - sonar-auth-bitbucket-plugin-1.0
  - sonar-bitbucket-plugin-1.3.0
  - sonar-auth-gitlab-plugin-1.3.2
  - sonar-gitlab-plugin-4.0.0
  - sonar-xanitizer-plugin-2.0.0
  - sonar-groovy-plugin-1.5

Requirements
--------------

 - **Mininmal Ansible version**: 2.5
 - **Supported SonarQube versions**:
   - 6.7.7 LTS
   - 7.0 - 7.7
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
   - Ubuntu
      - 'xenial'
      - 'bionic'
   - Debian
      - 'stretch'
      - 'jessie'

Java, database, web server with self-signed certificate should be installed preliminarily. Use following galaxy roles:
    - lean_delivery.java
    - anxs.postgresql
    - jdauphant.ssl-certs
    - nginxinc.nginx

Role Variables
--------------

  - `sonar_major_version` - major number of SonarQube version\
    default: 7
  - `sonar_minor_version` - minor number of SonarQube version\
    default: 7
  - `sonar_path` - installation directory\
    default: /opt/sonarqube
  - `sonar_user` - user for installing SonarQube\
    default: sonar
  - `sonar_group` - group of SonarQube user\
    default: sonar
  - `sonar_nofile` - file descriptors amount that user running SonarQube can open\
    default: 65536
  - `sonar_nproc` - threads amount that user running SonarQube can open\
    default: 2048
  - `sonar_log_level` - Logging level of SonarQube server\
    default: INFO
  - `sonar_java_opts`:
      - `web` - additional java options for web part of SonarQube\
        default: -Xmx512m -Xms128m
      - `es` - additional java options for Elasticsearch\
        default: -Xms512m -Xmx512m
      - `ce` - additional java options for Compute Engine\
        default: -Xmx512m -Xms128m
  - `web`:
      - `host` - SonarQube binding ip address\
        default: 0.0.0.0
      - `port` - TCP port for incoming HTTP connections\
        default: 9000
      - `path` - web context\
        default: /
  - `sonar_db` - database settings
      - `type`\
        default : postgresql
      - `port`\
        default : 5432
      - `host`\
        default : localhost
      - `name`\
        default: sonar
      - `user`\
        default: sonar
      - `password`\
        default: sonar
      - `options`\
        default:
  - `sonar_check_url` - url for SonarQube startup verification\
    default: http://{{ web.host }}:{{ web.port }}
  - `sonar_store` - sonarqube artifact provider\
    default: https://sonarsource.bintray.com/Distribution/sonarqube
  - `sonar_download_path` - local download path\
    default: /tmp/
  - `sonar_proxy_type` - web server, nginx is only supported for now\
    default: nginx
  - `sonar_proxy_server_name` - server name in webserver config\
    default: '{{ ansible_fqdn }}'
  - `sonar_proxy_http` - is http connection allowed\
    default: false
  - `sonar_proxy_http_port` - http port\
    default: 80
  - `sonar_proxy_ssl` - is https connection allowed\
    default: true
  - `sonar_proxy_ssl_port` - https port\
    default: 443
  - `sonar_proxy_ssl_cert_path` - path to certificate\
    default: '/etc/ssl/{{ sonar_proxy_server_name }}/{{ sonar_proxy_server_name }}.pem'
  - `sonar_proxy_ssl_key_path` - path to key\
    default: '/etc/ssl/{{ sonar_proxy_server_name }}/{{ sonar_proxy_server_name }}.key'
  - `sonar_proxy_client_max_body_size` - client max body size setting in web server config\
    default: 32m
  - `sonar_plugins` - list of plugins
  - `sonar_install_optional_plugins` - are optional plugins required\
    default: false
  - `sonar_optional_plugins` - list of optional plugins switched off by default. Not all of them are supported in latest SonarQube versions, so select ones you need and override this property.
  - `sonar_excluded_plugins` - list of old plugins excluded from SonarQube installer
  - `sonar_default_excluded_plugins` - list of default plugins you don't need\
    default: []

Example Playbook
----------------
```yaml
- name: Install SonarQube
  hosts: sonarqube
  vars:
    sonar_major_version: 7
    sonar_minor_version: 6
    sonar_install_optional_plugins: true
    sonar_optional_plugins:
      - "https://github.com/QualInsight/qualinsight-plugins-sonarqube-smell/releases/download/\
        qualinsight-plugins-sonarqube-smell-4.0.0/qualinsight-sonarqube-smell-plugin-4.0.0.jar"
      - https://github.com/SonarSource/sonar-auth-bitbucket/releases/download/1.0/sonar-auth-bitbucket-plugin-1.0.jar
      - https://github.com/mibexsoftware/sonar-bitbucket-plugin/archive/master.zip
    sonar_default_excluded_plugins:
      - '{{ sonar_plugins_path }}/sonar-scm-svn-plugin-1.9.0.1295.jar'
    sonar_check_url: 'https://{{ ansible_fqdn }}'
    java_major_version: 8
    transport: repositories
    postgresql_users:
      - name: sonar
        pass: sonar
    postgresql_databases:
      - name: sonar
        owner: sonar
    ssl_certs_path_owner: nginx
    ssl_certs_path_group: nginx
  pre_tasks:
    # delete previously installed sonar to prevent plugins conflict
    - name: delete sonar
      file:
        path: '{{ sonar_path }}'
        state: absent
  roles:
    - role: lean_delivery.java
    - role: anxs.postgresql
      become: true
    - role: jdauphant.ssl-certs
    - role: nginxinc.nginx
    # maven role is required for building bitbucket plugin
    - role: gantsign.maven
    - role: lean_delivery.sonarqube
  tasks:
    - name: unzip bitbucket plugin  
      unarchive:
        src: '{{ sonar_installation }}/extensions/plugins/sonar-bitbucket-plugin-master.zip'
        dest: '{{ sonar_installation }}/extensions/plugins/'
        remote_src: yes
    - name: build bitbucket plugin
      command: '/usr/local/bin/mvn clean install -DskipTests'
      args:
        chdir: '{{ sonar_installation }}/extensions/plugins/sonar-bitbucket-plugin-master'
    - name: move bitbucket plugin
      command: 'mv {{ sonar_installation }}/extensions/plugins/sonar-bitbucket-plugin-master/target/sonar-bitbucket-plugin-1.3.0.jar {{ sonar_installation }}/extensions/plugins'
    - name: delete default nginx config
      file:
        path: /etc/nginx/conf.d/default.conf
        state: absent
    - name: restart, enable nginx
      service: 
        name: nginx
        state: restarted
        enabled: true
```

## License

Apache2

## Authors

team@lean-delivery.com
