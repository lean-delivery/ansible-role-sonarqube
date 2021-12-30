sonarqube role
=========
[![License](https://img.shields.io/badge/license-Apache-green.svg?style=flat)](https://raw.githubusercontent.com/lean-delivery/ansible-role-sonarqube/master/LICENSE)
[![Build Status](https://gitlab.com/lean-delivery/ansible-role-sonarqube/badges/master/pipeline.svg)](https://gitlab.com/lean-delivery/ansible-role-sonarqube/pipelines)
[![Galaxy](https://img.shields.io/badge/galaxy-lean__delivery.sonarqube-blue.svg)](https://galaxy.ansible.com/lean_delivery/sonarqube)
![Ansible](https://img.shields.io/ansible/role/d/29212.svg)
![Ansible](https://img.shields.io/badge/dynamic/json.svg?label=min_ansible_version&url=https%3A%2F%2Fgalaxy.ansible.com%2Fapi%2Fv1%2Froles%2F29212%2F&query=$.min_ansible_version)

This role installs SonarQube with extended set of plugins. It uses openJDK, postgreSQL database and nginx web server with enabled https.

See article here: https://lean-delivery.com/2020/02/how-to-add-sonarqube-to-ci-process.html

In addition to default plugins included into SonarQube installation role installs following extra plugins:
  - checkstyle-sonar-plugin-9.2
  - sonar-pmd-plugin-3.3.1
  - sonar-findbugs-plugin-4.0.5
  - sonar-jdepend-plugin-1.1.1
  - sonar-jproperties-plugin-2.6
  - sonar-groovy-plugin-1.8
  - sonar-dependency-check-plugin-2.0.8
  - sonar-json-plugin-2.3
  - sonar-yaml-plugin-1.7.0
  - sonar-ansible-plugin-2.5.0
  - sonar-shellcheck-plugin-2.5.0
  
Also you may install optional plugins. Be carefull, some of them are not supported in latest SonarQube versions:
  - qualinsight-sonarqube-smell-plugin-4.0.0
  - qualinsight-sonarqube-badges-3.0.1
  - sonar-auth-github-plugin-1.5.0.870
  - sonar-auth-bitbucket-plugin-1.1.0.381
  - sonar-bitbucket-plugin-1.3.0 (for Bitbucket Cloud)
  - sonar-stash-plugin-1.6.0 (for Bitbucket Server)
  - sonar-auth-gitlab-plugin-1.3.2
  - sonar-gitlab-plugin-4.0.0
  - sonar-xanitizer-plugin-2.2.0
  - sonar-build-breaker-plugin-2.3.1.347
  - sonar-issueresolver-plugin-1.0.2
  - sonarqube-community-branch-plugin-1.10.0
  
See plugin matrix here: https://docs.sonarqube.org/latest/instance-administration/plugin-version-matrix/

This role also provides some configuration options:
  - ability to migrate db when updating SonarQube to new version
  - ability to set Jenkins webhook
  - ability to restore custom profiles
  - LDAP configuration
  - ability to change password for admin user

See Jenkins pipeline example here: https://raw.githubusercontent.com/lean-delivery/ansible-role-sonarqube/master/files/example_pipeline.groovy

Requirements
--------------

 - **Supported Ansible versions**:
   - 2.9
   - 3 (2.10)
 - **Supported SonarQube versions**:
   - 7.9.6
   - 8.9.x LTS
   - 9.0 - 9.2.4.50792
 - **Supported Java**:
   - 11
 - **Supported databases**
   - PostgreSQL
   - MySQL (not recommended)
 - **Supported web servers**
   - nginx 
 - **Supported OS**:
   - CentOS
     - 7
	 - 8
   - RHEL
     - 7
	 - 8
   - Ubuntu
     - 18.04

Java, database, web server with self-signed certificate should be installed preliminarily. Use following galaxy roles:
  - lean_delivery.java
  - anxs.postgresql
    version: master
  - jdauphant.ssl-certs
  - nginxinc.nginx
    version: 0.20.0

Role Variables
--------------

  - `sonar_version` - SonarQube version\
    default: 9.2.4.50792
  - `sonar_path` - installation directory\
    default: /opt/sonarqube
  - `sonar_user` - user for installing SonarQube\
    default: sonar
  - `sonar_group` - group of SonarQube user\
    default: sonar
  - `sonar_nofile` - file descriptors amount that user running SonarQube can open\
    default: 65536
  - `sonar_nproc` - threads amount that user running SonarQube can open\
    default: 4096
  - `sonar_max_map_count` - mmap counts limit required for Elasticsearch\
    default: 262144
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
  - `sonar_store` - sonarqube artifact provider\
    default: https://sonarsource.bintray.com/Distribution/sonarqube
  - `sonar_check_url` - url for SonarQube startup verification\
    default: http://{{ web.host }}:{{ web.port }}
  - `sonar_download` - is sonarqube.zip download required. Set to false when not possible to download zip and put zip to sonar_download_path manually before playbook run.
    default: true
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
  - `sonar_install_recommended_plugins` - are recommended plugins required\
    default: true
  - `sonar_recommended_plugins` - list of recommended plugins\
  - `sonar_update_default_plugins` - is update required for default plugins\
    default: true
  - `sonar_default_plugins` - list of default plugins\
  - `sonar_install_optional_plugins` - are optional plugins required\
    default: false
  - `sonar_optional_plugins` - list of optional plugins switched off by default. Not all of them are supported in latest SonarQube versions, so select ones you need and override this property.
  - `sonar_excluded_plugins` - list of old plugins excluded from SonarQube installer
  - `sonar_default_excluded_plugins` - list of default plugins you don't need\
    default: []
  - `sonar_web_user` - username for admin user\
    default: admin
  - `sonar_web_password` - password for admin user\
    default: admin
  - `change_password` - set true to change password\
    default: false
  - `sonar_web_old_password` - current password (before changing)\
    default: admin
  - `sonar_migrate_db` - is DB migrate required. Set to true when updating existing SonarQube to new version.\
    default: false
  - `sonar_set_jenkins_webhook` - is jenkins webhook configuration required\
    default: false
  - `sonar_jenkins_webhook_name` - name of jenkins webhook\
    default: jenkins
  - `sonar_jenkins_webhook_url` - url of jenkins webhook\
    default: https://jenkins.example.com/sonarqube-webhook/
  - `sonar_restore_profiles` - is profile restore required\
    default: false
  - `sonar_profile_list` - list of profiles to restore
  - `sonar_updatecenter_activate` - activate the SonarQube update center
    default: true

  Ldap configuration section. 
  See https://docs.sonarqube.org/latest/instance-administration/delegated-auth/#header-6 to get description
  - `ldap`:\
    default: undefined
      - `authenticator_downcase`\
        default: false
      - `url`\
        default: ldap://myserver.mycompany.com
      - `bind_dn`\
        default: my_bind_dn
      - `bind_password`\
        default: my_bind_password
      - `authentication`\
         default: simple
      - `realm`\
        default: 
      - `contextFactoryClass`\
        default: com.sun.jndi.ldap.LdapCtxFactory
      - `StartTLS`\
        default: false
      - `followReferrals`\
        default: true
      - `user_base_dn`\
        default : ou=Users,dc=mycompany,dc=com
      - `user_request`\
        default: (&(objectClass=inetOrgPerson)(uid={login}))
      - `user_real_name_attribute`\
        default: cn
      - `user_email_attribute`\
        default: mail
      - `group_base_dn`\
        default: ou=Groups,dc=sonarsource,dc=com
      - `group_request`\
        default: (&(objectClass=posixGroup)(memberUid={uid}))
      - `group_idAttribute`\
        default: cn
      
Example Playbook
----------------
```yaml
---
- name: Install SonarQube
  hosts: sonarqube
  become: true
  vars:
    # java
    java_major_version: 11
    transport: repositories
    # postgresql
    postgresql_users:
      - name: sonar
        pass: sonar
    postgresql_databases:
      - name: sonar
        owner: sonar
    # ssl-certs
    ssl_certs_path_owner: nginx
    ssl_certs_path_group: nginx
    ssl_certs_common_name: sonarqube.example.com
    # sonarqube
    sonar_version: 9.2.4.50792 # see versions here https://sonarsource.bintray.com/Distribution/sonarqube
    sonar_check_url: 'http://{{ ansible_fqdn }}:9000'
    sonar_proxy_server_name: sonarqube.example.com
    sonar_install_optional_plugins: true
    sonar_optional_plugins: 
      - 'https://github.com/adnovum/sonar-build-breaker/releases/download/{{ build_breaker_epversion }}'
    sonar_default_excluded_plugins:
      - '{{ sonar_plugins_path }}/sonar-csharp-plugin-8.15.0.24505.jar'
    sonar_web_password: your_new_secure_password
    change_password: true
    sonar_web_old_password: admin
    sonar_migrate_db: false  # set to true if updating SonarQube to new version 
    sonar_set_jenkins_webhook: true
    sonar_jenkins_webhook_url: https://jenkins.example.com/sonarqube-webhook/
    sonar_restore_profiles: true
    sonar_profile_list:
      - files/example_profile.xml
  pre_tasks:
    - name: install rpm key
      rpm_key:
        state: present
        key: https://dl.fedoraproject.org/pub/epel/RPM-GPG-KEY-EPEL-{{ ansible_distribution_major_version }}
      when: ansible_distribution == 'RedHat'
    - name: install epel
      package:
        name: https://dl.fedoraproject.org/pub/epel/epel-release-latest-{{ ansible_distribution_major_version }}.noarch.rpm
        state: present
      when: ansible_distribution == 'RedHat'
    # delete previously installed sonar to prevent plugins conflict
    - name: delete sonar
      file:
        path: '{{ sonar_path }}'
        state: absent
  roles:
    - role: lean_delivery.java
    - role: anxs.postgresql
    - role: nginxinc.nginx
    - role: jdauphant.ssl-certs
    - role: lean_delivery.sonarqube
  tasks:
    - name: delete default nginx config
      file:
        path: /etc/nginx/conf.d/default.conf
        state: absent
    - name: reload nginx
      command: 'nginx -s reload'
```

License
-------
Apache

Author Information
------------------

authors:
  - Lean Delivery Team <team@lean-delivery.com>
