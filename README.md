# REPL-demo

Esimerkki todellisesta tilanteesta, jossa Clojuren REPL, funktionaalinen ohjelmointi ja dynaaminen tyypitys mahdollistavat 
tehokkaan työnkulun tavalla joka ei ole normaalia tai mahdollista perinteisissä ohjelmointiympäristöissä. 

Koodi perustuu Opetushallituksen Aitu-projektin todelliseen konversioon. Esimerkkiä on muokattu siten että alkuperäisen
Oracle-tietokannan sijaan käytetään PostgreSQL-kantaa myös lähdejärjestelmän tietokantana. Dataa ja kannan rakennetta on myös
yksinkertaistettu huomattavasti. 

Demokoodissa on jäljellä demon kannalta epäolennaisia koodinpätkiä jonkin verran. Tämä on osittain tahallista. Kyse on REPL:n 
käytön ja Clojuren dynaamisen tyypityksen avulla saatavan tehokkaan työtavan demonstraatiosta, ei toteutuskoodin demonstraatiosta
tai konversiomoottorin demonstraatiosta.

# Konversiomoottori

* Konversio on kaksiosainen: yleiskäyttöinen "moottori" ja sovelluskohtaiset konversiosäännöt. 
* Konversio toimii siten että per kohdetaulu tehdään yksi SQL select lähdekantaan ja konvertoidaan rivi kerrallaan.
* Tämä lähestymistapa on yksinkertainen, mutta ei yleispätevä; Jos konvertoidaan miljoonia rivejä, rivi kerrallaan operointi ei ole järkevää. 

# Demon kannalta mielenkiintoisia pointteja

* Konversiomoottoria ei tarvitse penkoa.
* Säännöt/logiikka voidaan kirjoittaa Clojure-koodina.
* Testidata saadaan copy-pastella REPL:sta.

# Komentoja virtuaalikoneen hallintaan

## Tietokannan dumppaus
```
[root@localhost dumps]# pg_dump --clean -U repl_adm  -h localhost -p 5432 sourcedb > sourcedb.dmp
```

## Dumpin lataus
```
psql -U repl_adm -h localhost -d sourcedb -f sourcedb.dmp 
```

