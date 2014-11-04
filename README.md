# REPL-demo

Live-koodaus demonstraation pohja [http://www.it2014.fi/index.html](Korkeakoulujen IT-päivät 2014) tapahtuman esitystä varten.

Ideana on antaa esimerkki todellisesta tilanteesta, jossa [http://clojure.org/](Clojuren) REPL, funktionaalinen ohjelmointi ja dynaaminen tyypitys mahdollistavat 
tehokkaan työnkulun tavalla joka ei ole normaalia tai mahdollista perinteisissä ohjelmointiympäristöissä. Smalltalkin [http://www.squeak.org/](Squeak) on ehkä
lähin vastine olio-ohjelmoinnin maailmassa, mutta Smalltalk ei ole (enää) laajasti käytössä.

Demonstraation pointti on nimenomaan työprosessi, joten tässä koodirepositoryssa ei kannata kiinnittää huomiota koodin laatuun ja sisältöön.

## Demokoodin tausta

Koodi perustuu [https://github.com/Opetushallitus/aitu](Opetushallituksen Aitu-projektin) todelliseen konversioon. Esimerkkiä on muokattu siten että alkuperäisen
Oracle-tietokannan sijaan käytetään PostgreSQL-kantaa myös lähdejärjestelmän tietokantana. Dataa ja kannan rakennetta on myös
yksinkertaistettu huomattavasti. 

## Konversiomoottori

* Konversio on kaksiosainen: yleiskäyttöinen "moottori" ja sovelluskohtaiset konversiosäännöt. 
* Konversio toimii siten että per kohdetaulu tehdään yksi SQL select lähdekantaan ja konvertoidaan rivi kerrallaan.
* Tämä lähestymistapa on yksinkertainen, mutta ei yleispätevä; Jos konvertoidaan miljoonia rivejä, rivi kerrallaan operointi ei ole järkevää. 

# Demon kannalta mielenkiintoisia pointteja

* Konversiomoottoria ei tarvitse penkoa.
* Säännöt/logiikka voidaan kirjoittaa Clojure-koodina.
* Testidata saadaan copy-pastella REPL:sta. (Clojuren EDN-notaatio)
* Melko pienellä määrällä koodia koko konversiologiikka voidaan kirjoittaa deklaratiivisena Clojure-koodina. 


# Koodin rakenne ja käyttö

Koska tämä on demonstraatioksi tarkoitettu esimerkki, tätä ei ole ajateltu varsinaisesti käytettäväksi kuin mallina. Tässä on lyhyet ohjeet omaehtoiseen
tutkimiseen.

## Virtuaalikoneen käyttö

Koodi haluaa käyttää paikallista tietokantaa jota voidaan ajaa virtuaalikoneessa. Virtuaalikoneen pystyttäminen onnistuu ilmaisen
[Vagrant](http://www.vagrantup.com/) ohjelman avulla. Virtuaalikoneen ajamisesta huolehtii [https://www.virtualbox.org/](Oracle Virtualbox).

# Virtuaalikoneen käynnistys

```
cd vagrant
vagrant up db
```


## Dumpin lataus

Virtuaalikone tarvitsee lähdetietokannan ja kohdetietokannan, jotka voi ladata seuraavilla komennoilla:

```
vagrant ssh db
sudo su - 
cd /dumps
psql -U repl_adm -h localhost -d sourcedb -f sourcedb.dmp 
psql -U repl_adm -h localhost -d targetdb -f targetdb.dmp 
```

Kun kanta on alustettu, konversion kanssa voi touhuta rauhassa. Konversion ajaminen tyhjentää kohdekannan ja konversioprojektien luonteeseen ei kuulu
tyhjentää tai muuttaa lähdetietokantaa.

## Tietokannan dumppaus
```
[root@localhost dumps]# pg_dump --clean -U repl_adm  -h localhost -p 5432 sourcedb > sourcedb.dmp
```

## Konversiodemon alkutilanne, setupin ponnistus

Tästä se lähtee.. 
```
(with-inputdb (execquery "select * from jarjesto" println))
```
