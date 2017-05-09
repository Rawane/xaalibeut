# xaalibeut
Ce projet vous permet de sécuriser vos données avec l'ago RSA.
Si vous avez des données sensible que vous mettez dans vos cloud, mieux vaut les sécuriser avant de les transmettre.  
Premièrement il faut générer une paire de clés privé et public avec la ligne de commande:  
java -jar xoolibeut-crypt.jar -r -d="Saisissez le répértoire de destination de la paire clés" -s="choisir 1024 ou 2048 ou 4096".
Exemple :  
java -jar xoolibeut-crypt.jar -r -d="/home/user/perso/security" -s=2048  
pour crypter vos dossiers :  
java -jar xoolibeut-crypt.jar -ed -a="Saisissez le répértoire à crypter" -b="saisissez la clé public généré avant"   
Exemple:  
java -jar xoolibeut-crypt.jar -ed -a=/home/user/perso/photos -b=/home/user/perso/security/PublicKey.pem   
Pour décrypter vos dossiers :  
java -jar xoolibeut-crypt.jar -dd -a="Saisissez le répértoire à decrypter" -b="saisissez la clé privé généré avant"   

java -jar xoolibeut-crypt.jar -dd -a=/home/user/perso/photos -b=/home/user/perso/security/PrivateKey.pem   

vous pouvez ajouter plusieurs options pour zipper le dossier que vous avez crypter,dézipper  le dossier à dévripter.  

**Pour plus d'informations sur les commandes :**  
java -jar xoolibeut-crypt.jar -h

usage: rsa commande [-a <source>] [-b <key>] [-d <dest>] [-dd] [-dj] [-ed]  
       [-ej] [-n <nocrypt>] [-r] [-s <rsasize>] [-z] [-zi] [-zo]         
 -a,--asource <source>    Répertoire à crypter ou decrypter exemple -a=/home/user/docs  
 -b,--bkey <key>          Clés privé ou public format PEM exemple -b=/home/user/key/Key.pem  
 -d,--dest <dest>         Répertoire de destination de la paire de clés, exemple -d=/home/user/key  
 -dd,--decpdoss           Decrypter un dossier  
 -dj,--decpjava           Decrypter un projet java  
 -ed,--encpdoss           Crypter un dossier, utiliser avec -a   et -b  
 -ej,--encpjava           Crypter un projet java  
 -n,--nocrypt <nocrypt>   choisir un ou plusieurs dossier à ne pas crypter suivi, utiliser avec -a  
 -r,--rsa                 Génération de clé privé et public, utiliser avec option -d et -s
 -s,--rsasize <rsasize>   choisir la taille de la clé pour algo RSA
                          1024,2048 exemple -s=2048
 -z,--supp                supprime le repertoire après traitement, utiliser avec -a
 -zi,--zipin              zipper un repertoire
 -zo,--zipouput           dézipper un repertoire



