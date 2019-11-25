Vagrant.configure(2) do |config|

    config.ssh.forward_agent = true
    config.vm.hostname = "vagrant-box"
    config.vm.network :private_network, ip: '10.1.100.11'
    config.vm.define "main", primary: true do |node|
        #node.vm.box = "ubuntu/xenial64"
        #node.vm.box = "ubuntu/bionic64"
        #node.vm.box = "debian/stretch64"
        #node.vm.box = "debian/jessie64"
        node.vm.box = "bento/centos-7.6"
        #node.vm.box = "bento/centos-6.9"
    end

  # SetUp Machine
  config.vm.provision  "prepare", type:'ansible' do |ansible|
    ansible.playbook = 'molecule/resources/prepare_vagrant.yml'
    ansible.sudo = true
    ansible.verbose = "vvv"
  end

  # Setup SonarQube
  config.vm.provision "sonar", type:'ansible' do |ansible|
    ansible.playbook = 'molecule/default/playbook.yml'
    ansible.sudo = true
    ansible.verbose = "vvv"
  end

  # Verify
  config.vm.provision  "test", type:'ansible' do |ansible|
    ansible.playbook = 'molecule/resources/tests/verify.yml'
    ansible.sudo = true
    ansible.verbose = "vvv"
  end
end
