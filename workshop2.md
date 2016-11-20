### Workshop #2 - Pipeline As Code

### Jenkinsfile

On va maintenant jouer avec les possibilités du job MultiBranch, en améliorant progressivement le pipeline précédent créé.

1. Sur Github, forkez le projet https://github.com/mpailloncy/cdl-workshop-app.git
2. Cloner votre fork sur votre poste local
3. Ajoutez-y un fichier `Jenkinsfile` à la racine, et copiez-y le script Pipeline du workshop précédent.
4. Committez et pusher ces modifications sur votre fork.


### Credentials GitHub 

Revenez sur votre Jenkins, on va maintenant ajouter vos credentials GitHub.

* Si vous n'en avez déjà pas une, générez vous un token d'API GitHub => Aller sur https://github.com/settings/tokens

Le token généré par GitHub correspondra au mot de passe à spécifier par la suite.

> Attention, GitHub ne vous permet pas de revoir votre token par la suite, conservez le bien.

* Sur votre Jenkins, allez ensuite sur la page http://<VOTRE_IP>:8080/credentials/store/system/domain/_/

Ajouter un credentials avec votre compte utilisateur GitHub, le mot de passe à renseigner correspondant au token de l'API GitHub généré précédement.  

> Pour info, vous êtes le seul à avoir accès à votre instance Jenkins et le token d'API et chiffré sur le disque. 


### Job MultiBranch

* Créer ensuite un nouveau job de type MultiBranch en lui donnant le nom qui vous fait plaisir (ce job pointera sur votre fork)

Configuration à renseigner : 

* Ajouter une "Branch Sources" de type `GitHub` 
* Spécifier votre identifiant GitHub en `Owner`
* Sélectionner les credentials que vous venez de configurer 
* Sélectionner le repository que vous avez forké
* Dans la partie Build Triggers, cochez la case "Periodically if not otherwise run" en sélectionnant un intervalle d' une minute  

Une fois la configuration sauvegardée, Jenkins va aller scanner toutes les branches de votre repository afin de trouver des `Jenkinsfile` et créer tous les jobs associés.

Dans votre cas, vous devriez voir apparaître un projet dans lequel un job `master` a été créé et déclenché directement.

* Sur votre poste local dans le repository de votre fork, créer une nouvelle branche basée sur master 
 
```
$> git checkout -b JenkinsfileImprovement
```

A partir de maintenant, vous pouvez commiter+pusher à chaque fois que vous faites des modifications sur votre Jenkinsfile, sur la branche `JenkinsfileImprovement`. 
A bout d'une minute maximum, Jenkins va réindexer les branches de votre fork, détecter les modifications, créer les jobs et les déclencher s'il y a besoin.

> A noter qu'en mode "développement" de pipeline Jenkins, il est souvent plus pratique d'utiliser l'action `Replay` en modifiant le script avec vos corrections, plutôt que de modifier localement + commit + push systématiquement.

> Vous pourrez remarquer une phrase à la fin du build "GitHub has been notified of this commit’s build result". Côté GitHub, pour chacun de vos commits en succès/échec, le status du build sera automatiquement affiché.


###  Construction de vos binaires

1. Modifier votre pipeline afin d'ajouter une étape de récupération des sources Git du projet https://github.com/mpailloncy/cdl-workshop-app.git (step `checkout scm`) dans le stage `build & unit tests`
2. Configurer votre pipeline pour lancer une build Maven dans le stage `build & unit tests` (se baser sur le HelloWorld effectué précédemment pour les commandes si vous ne connaissez pas Maven).
3. Dans le stage `build & unit tests`, après le build Maven, ajoutez une step de `stash` du binaire `target/simple-app-1.0-SNAPSHOT.jar` généré.
4. Dans les stages `staging` et `deploy`, ajouter une step de `unstash` du binaire. Assurez-vous que le binaire est bien présent dans le workspace en déclencheant la commande `ls -l target` après le `unstash`.  
5. Comme Jenkins réutilise les workspaces lorsqu'il le peut, il vaut mieux nettoyer systématiquement les workspaces lors de l'utilisation de step comme `unstash` afin d'être sûr d'avoir un workspace propre à chaque build.
Ajouter une step pour nettoyer le workspace courant juste avant l'utilisation de `unstash`.

###  Restreindre les phases de déploiement à la branche master uniquement

> Comme nous travaillons sur plusieurs branches, il peut être compliqué de gérer des déploiements de version différentes de notre application sur de même environnement cible.
> Il est donc préférable de resteindre les phases de déploiement à la branche `master` uniquement. 

* En vous aidant de la documentation et des variables d'environnements disponibles, faites en sorte que les stages `staging`, `manual-approval` et `deploy` ne se déclenchent pas lorsque la branche actuellement construite n'est pas la branche `master`.

* Sur votre poste locale, repositionnez-vous sur la branche `master`, fusionnez les modifications de la branche `JenkinsfileImprovement` dans la branche `master` puis pushez. 
A la fin du build, les phases de déploiement doivent être exécutées.
