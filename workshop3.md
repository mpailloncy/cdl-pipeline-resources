## Workshop #3 - Deploy to environment

### Using Shared Libraries

* Au niveau de la configuration du job de votre job Multibranch, ajouter la librairie partagée https://github.com/mpailloncy/cdl-pipeline-shared-lib.git

Configuration nécessaire : 

* Name : cdl-pipeline-shared-lib
* Default version = master
* Source Code Management => Git 
* Project Repository : https://github.com/mpailloncy/cdl-pipeline-shared-lib.git
 
* Améliorer votre Jenkinsfile afin de pouvoir utiliser cette librairie partagée

> Vous remarquerez que dans vos prochains `Replay`, toutes les méthodes/classes importées par votre script Pipeline vous seront proposés pour modification. Très pratique lorsqu'on veut juste tester une modification rapidement, sans devoir commiter.

### Déploiement des binaires 

On va maintenant ajouter la configuration SSH nécessaire pour la connexion aux environnements de staging/production

* Aller dans la page http://<JENKINS_IP>:8080/credentials/store/system/domain/_/ + Add Credentials
* Ajouter une entrée de type `SSH username with private key`

ID : deploy_key
description : deploy_key
Username : root
Private key : your private key

* Utiliser les méthodes `scp(username,host,localpath,remotepath)` et `sshExec(username,host,command)` de votre librairie partagée pour copier, vérifier et éventuellement démarrer votre application (éventuellement car vous devriez avoir des problèmes, je vous laisse les découvrir :-)).

Pour démarrer l'application, il faut utiliser la commande `java -jar path/to/app.jar` sur l'environnement cible.

> Afin de charger les credentials précédement créés, il vous faut utiliser `ssh-agent`. Voir https://jenkins.io/doc/pipeline/steps/ssh-agent/ 

> Ici, on est sur un déploiement HYPER basique. L'objectif est juste de vous montrer les fonctionnalités de Jenkins Pipeline et la "glue" qui permet de passer une étape à l'autre de votre pipeline de livraison. 
> Pour aller plus loin dans la mise en place du Continuous Delivery, on devrait aborder des problèmatiques spécifiques à chaque techno et ce n'est pas le but de ce workshop centré sur Jenkins Pipeline.

* En réutilisant les steps utilisés pour le déploiement sur l'environnement de staging, créer une méthode `def deploy(target) { ... }` à l'intérieur de votre script Pipeline.
 
Utiliser ensuite cette méthode pour déployer les binaires de la même manière entre `staging` et `deploy`.

> Dans un cas réel, il serait intéressant que cette méthode soit ajoutée par la suite à une Shared Libary pour potentiellement être réutilisée dans d'autres pipelines.
 
### Pour ceux qui ont la tremblotte, un tout peu de Docker quand même :-)

* Si cela vous fait plaisir, vous pouvez utiliser Docker pour builder l'application au niveau de l'étape `build & unit tests`.

> Voir https://go.cloudbees.com/docs/cloudbees-documentation/cje-user-guide/index.html#docker-workflow => vous pouvez utiliser l'image `maven:3.3.9-jdk-8`
