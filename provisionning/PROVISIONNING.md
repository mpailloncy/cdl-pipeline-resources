# Jenkins workshop masters provisionning

Workshop machines are provisionned using Terraform and Ansible on DigitalOcean cloud.

## Prerequisites

* ansible and terraform installed locally

## Machines creation

Display terraform execution plan to provision X machines :

```
$> make plan n=X
```

Trigger provisionning of X machines :

```
$> make provision n=X
```

This action will create (or update) X new machines on DigitalOcean cloud accordingly to configuration specified into `jenkins.tf` file.
Then, a provisionning with Ansible `playbook.yml` file is triggered on all created/updates machines.

## What's the username/password of my instance ?

By default, Jenkins master are provisionned with :

* login = jenkins-${counter}
* password = ${digitalocean_droplet_id}

You can display all ip/login/password provisionned with Terraform using the following command : 

```
$> make infos
```

## Display Jenkins logs of a target machine

Assuming that you direct access to target machine (ssh key pushed), you can execute the following command :

```
$> make logs jenkins-2
```

## Connect to a target machine

Assuming that you direct access to target machine (ssh key pushed), you can execute the following command :

```
$> make go jenkins-10
```

