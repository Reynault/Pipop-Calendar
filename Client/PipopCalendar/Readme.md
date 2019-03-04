# Guide d'installation du projet android (Client / Cordova)




## Etape 1

Clonez le projet du git.

ATTENTION : Un chemin de fichier trop long entraînera un soucis de compilation dans Android Studio (ou autre IDE Android)

## Etape 2

Ouvrez votre IDE (ici je prendrais l'exemple d'Android Studio).

Fermer le projet en cours de sorte a obtenir la page d’accueil
(Aide : *File > Close Project*)

Une fois sur l'écran d'accueil sélectionnez "Import Project (Gradle, Eclipse ADT, ..)

Naviguez jusqu'à l'endroit où vous avez clonez le dépôt git.

Ouvrez le dossier *"android"*

(Chemin vers "android" : *"Pipop-Calendar\Client\PipopCalendar\platforms\android"*)

Cliquez sur *"OK"*


## Etape 3
 
Ici Android Studio devrait démarrer et afficher un message disant que le projet n'est pas correctement configuré.

Cliquez sur *"OK"* et patientez jusqu'à ce qu'Android Studio démarre l'interface.

Si vous avez de la chance le projet se construit sans aucune intervention.
Sinon assurez vous que vous possédez bien les bonnes version SDK d'android.

Ne pas hésiter a demander des conseils auprès des autres membres du groupe.

## Etape 4

Le projet et configuré et prêt à être modifié.
Enjoy!





# **NE JAMAIS METTRE A JOUR LE GRADLE**




# INFORMATIONS
Il existe 2 dossiers dans le projet client. 
L'un à la racine du projet et l'autre dans le dossier *"android"*

Respectivement : 

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; *"D:\PPIL\Pipop-Calendar\Client\PipopCalendar\www"*

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;et

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;*"Pipop-Calendar\Client\PipopCalendar\platforms\android\app\src\main\assets\www"*

Le premier est utilisé lors de la commande de compilation de cordova. Il peut être utilisé comme sauvegarde.
Le second est le dossier utilisé pour les ressources de l'application android elle même. Elle est issue de la de la commande précédente.

<span style='color:red'>ATTENTION : </span> Sauvegardez **régulièrement** le dossier 

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;*"Pipop-Calendar\Client\PipopCalendar\platforms\android\app\src\main\assets\www"*

C'est dans celui-ci que vous effectuerez la mojorité du travail.
Si une compilation est éffectué avec la commande *cordova build android*, le dossier 

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;*"Pipop-Calendar\Client\PipopCalendar\platforms\android\app\src\main\assets\www"*  

**sera remplacé par le dossier**  

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;*"D:\PPIL\Pipop-Calendar\Client\PipopCalendar\www"* 




