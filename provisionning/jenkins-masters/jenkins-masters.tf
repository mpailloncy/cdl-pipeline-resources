resource "digitalocean_droplet" "jenkins" {
    
    count = "${var.count}"
    
    image = "docker-16-04"
    name = "jenkins-${count.index + 1}"
    region = "lon1"
    size = "2gb"
    
    ssh_keys = [ "84:e9:bc:7d:01:63:cc:f4:96:41:71:50:8c:0a:e6:4e" ]

    connection {
          user = "root"
          type = "ssh"
          private_key = "${var.ssh_private_key}"
          timeout = "1m"
    }

    tags   = ["${digitalocean_tag.jenkins.id}"]
}

output "ip" {
  value = [ "${digitalocean_droplet.jenkins.*.ipv4_address}" ]
}
