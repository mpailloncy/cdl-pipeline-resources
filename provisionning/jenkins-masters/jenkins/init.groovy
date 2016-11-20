import jenkins.model.*
import hudson.model.*
import hudson.security.*

def getMandatoryParameter(String parameterName) {
  def env = System.getenv()
  def value = env[parameterName]
  if(value == null || value.equals("")){
    println "[ERROR] Mandatory parameter ${parameterName} not found in environment variables. Killing Jenkins instance... Bye"
    System.exit(1)
  }
  return value
}

def hostname = getMandatoryParameter('hostname')
def adminUsername = getMandatoryParameter('admin_username')
def adminPassword = getMandatoryParameter('admin_password')
def numExecutors = getMandatoryParameter('master_numexecutors')

def digiOceanApiToken = getMandatoryParameter('digitalocean_api_token')
def digiOceanRegion = getMandatoryParameter('digitalocean_region')
def digiOceanImageId = getMandatoryParameter('digitalocean_image_id')
def digiOceanIdleTerminationInMinutes = getMandatoryParameter('digitalocean_idle_termination_in_minutes')
def digiOceanInitScript = getMandatoryParameter('digitalocean_init_script')

def digiOceanNodeHeavyTaskLabels = getMandatoryParameter('digitalocean_node_heavy_tasks_labels')
def digiOceanNodeHeavyTasksSize = getMandatoryParameter('digitalocean_node_heavy_tasks_size_id')
def digiOceanNodeHeavyTasksNumexecutors = getMandatoryParameter('digitalocean_node_heavy_tasks_numexecutors')
def digiOceanNodeHeavyTasksCap = getMandatoryParameter('digitalocean_node_heavy_tasks_cap')

def digiOceanNodeLightTaskLabels = getMandatoryParameter('digitalocean_node_ligth_tasks_labels')
def digiOceanNodeLightTasksSize = getMandatoryParameter('digitalocean_node_ligth_tasks_size_id')
def digiOceanNodeLightTasksNumexecutors = getMandatoryParameter('digitalocean_node_ligth_tasks_numexecutors')
def digiOceanNodeLightTasksCap = getMandatoryParameter('digitalocean_node_ligth_tasks_cap')

def env = System.getenv()
def jenkinsUrl=env['jenkins_url']

if(jenkinsUrl == null || jenkinsUrl.isEmpty()) {
  jenkinsUrl="http://${hostname}:8080"
}

// master setup
Jenkins.instance.setNumExecutors(Integer.parseInt(numExecutors))
jlc = JenkinsLocationConfiguration.get()
jlc.setUrl(jenkinsUrl)
jlc.setAdminAddress("michael.pailloncy@gmail.com")
jlc.save()

// create admin Jenkins account
def hudsonRealm = new HudsonPrivateSecurityRealm(false)
hudsonRealm.createAccount(adminUsername, adminPassword)
Jenkins.instance.setSecurityRealm(hudsonRealm)
def strategy = new FullControlOnceLoggedInAuthorizationStrategy()
strategy.setAllowAnonymousRead(false)
Jenkins.instance.setAuthorizationStrategy(strategy)

// auto install Maven
def mavenExtension = Jenkins.instance.getExtensionList(hudson.tasks.Maven.DescriptorImpl.class)[0]
def mavenInstallationList = (mavenExtension.installations as List)
mavenInstallationList.add(new hudson.tasks.Maven.MavenInstallation('M3', null, [new hudson.tools.InstallSourceProperty([new hudson.tasks.Maven.MavenInstaller("3.3.9")])]))
mavenExtension.installations = mavenInstallationList
mavenExtension.save()

// add DigitalOcean cloud configuration
def cloudTemplates = [
	new com.dubture.jenkins.digitalocean.SlaveTemplate(
			"heavy.tasks.node", 
			digiOceanImageId, 
			digiOceanNodeHeavyTasksSize, 
			digiOceanRegion, 
			"root", 
			"/jenkins/",
			22,
			digiOceanIdleTerminationInMinutes, 
			digiOceanNodeHeavyTasksNumexecutors, 
			digiOceanNodeHeavyTaskLabels,
			digiOceanNodeHeavyTasksCap, 
			"",
			digiOceanInitScript
	),
	new com.dubture.jenkins.digitalocean.SlaveTemplate(
			"light.tasks.node", 
			digiOceanImageId, 
			digiOceanNodeLightTasksSize, 
			digiOceanRegion,
			"root", 
			"/jenkins/",
			22,
			digiOceanIdleTerminationInMinutes, 
			digiOceanNodeLightTasksNumexecutors, 
			digiOceanNodeLightTaskLabels,
			digiOceanNodeLightTasksCap, 
			"",
			digiOceanInitScript
		)
]

def digitalOcean = new com.dubture.jenkins.digitalocean.Cloud(
	"digitalocean.cloud", 
	digiOceanApiToken,
	"""-----BEGIN RSA PRIVATE KEY-----
MIIEpAIBAAKCAQEAzqRaoufAuKUGHYxZulegretJKB0z2XkL/+DMayiSOqldBKrO
UuChOJptvPVbvhz/ZtixuUHD7ua5KqrNn4MqyzV1CrOSpxicjJMhbdd+ajq0TQrC
S5cgwKyh2DPXhjfPCFyNm9fuxV08i5BKpCj6W/5K2+aEaB976PqJHAaPqT2C3eQ2
1mJd/ucAUp9nKeA7EaA8zhZ5h1yLUGfy8Voe7Vv9CVIEbbUNvvSwUOnlCChe8SO0
5fVk3tvvIEEYngZy7MG49ZDrByPNQYXKaMufAcRKplLQGvdnNhQeYS+7LkV1QCVB
ZIaYll+/8iWd9YGsSgAx8F0Kf3W+r4K161HkHQIDAQABAoIBAQDITeqJtMpMcbKe
29hyV9SqIcOVPdFvwfVOwHKGgRpWAKvpBUTgc1v0CUXMf63BeExePjjwUf6CSmY9
JLoTIEtQFW5xxCWpEaNWZJxn5BZnFKaHc6FpMoKOIgj0ETSepuRpv4WJBmQuLjWt
N14TCKG+oJ2h0S6R28r5yDk10GpbakXU2fUeXy64RLCxTvJY7LS6HSJUpIj91Dij
DgfWpGnyahRsdVpFVnyf/0nEpC29y9G1FDpmG9p9SG9wsIHie3BoYtF4Qx6llh1b
6VM66m/p6Gc+FQEPlCSVuPjU5VZooOGRKEX1U/6bX+g1nf4IwxhqqageAD4qj+dH
jv5DwWFlAoGBAOaJiN04RhIX1n2EWpMue5Bw0pNI3FasQz5kdCqJt5Tm5PX0pkEm
SIHt4rLvnoBb05mxrXFMXyxhclcPjL0UtCRjerfi8fASZT/0+mnhPPp4wa5HxLwQ
kSkPRqzzSyFhi/0A5ZUVqqSO3rs2o8GhDP5KhUxSJNNYR6sUJ33ihUJPAoGBAOV3
LdVzlkkj784uzcLuuS8+twmF2Bz4jTktvWSuXVl1gWi2T3pUP2zB15Ip+EcGsl5E
aCI7Y72/BpRPGhVE5gXfNSDvl35Kt3jKlMk58N6N+07pM+Qj6T8PDom3oRauzFQM
IKa+mOG9nJ/JzuMoR+E+5X5fU0hmu8CWYvHXMbPTAoGBAOPwDHCeGTn+c8a6vEtV
5DpWMv4JRkI0JHBmJi2A6RDCkpNVB6TW004UBNdi8FZ9dGQ8Nv8Uwe1BlCncaAzz
kYGBEm8KZqg1qAJql8VkPERybR2CPFSuFLiCMCEElL42kpDe+CBFwejekf6M8bCb
KhurVsy1U0/bs1DFpp5i/9slAoGALGSlOmF1VsscDtChQDA9JXXFpZL+X4R6lfBS
btMI/lCsa5yxvaHk716FHSL/hIY4JeKoHs9MmZ4GYNT439qHgeVP+wI/VdCQtE7F
ADU0c2oWj4qwXlfQPUHldyFOifvhTfVWr8u5+hpb3dIGM45McbhgX9WDYEN32Wd+
1vjiBb8CgYB/Ps5uidafE9wWff/S9kIKovR4bPo+4M2eWc+vm+aBT6AbGSMTuP55
MwgeEEe0wJ+2jVOoH4CFvvJJBjHKXvHNL+hVbVN+M+2fxe5M33ldxGaNXwJNB2fl
PS+ARORIGfYp55XGAvMIPRLDzdXt6LdoqOvUMA9LQCgimWtD9DYK8A==
-----END RSA PRIVATE KEY-----""", "4597575", "4", "5", cloudTemplates)

Jenkins.instance.clouds.replace(digitalOcean)
Jenkins.instance.save()