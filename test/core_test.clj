;; Copyright (c) 2013 The Finnish National Board of Education - Opetushallitus
;;
;; This program is free software:  Licensed under the EUPL, Version 1.1 or - as
;; soon as they will be approved by the European Commission - subsequent versions
;; of the EUPL (the "Licence");
;;
;; You may not use this work except in compliance with the Licence.
;; You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
;;
;; This program is distributed in the hope that it will be useful,
;; but WITHOUT ANY WARRANTY; without even the implied warranty of
;; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;; European Union Public Licence for more details.

(ns repl-konversio.core-test
  (:require [clojure.java.io :as io]
            [clojure.java.jdbc :as jdbc]
            [clojure.java.jdbc.sql :as s]
            [clojure.set])
  (:use clojure.test
        repl-konversio.core
        repl-konversio.konversiodemo))


(def sampleData
  {:tutkintotoimikunta
   {:paatosseq nil, :nimi "Lattianpäällystyksen tutkintotoimikunta", :paatospvm #inst "2001-06-12T21:00:00.000000000-00:00",
    :historioitu #inst "2005-06-06T07:51:49.000000000-00:00", :paatosno "68/042/2001", :tkunta "T28122002",
    :paivityspvm #inst "2001-06-12T21:00:00.000000000-00:00", :toimialuenimiru "Riksomfattande", :paivittaja "AMTU",
    :kieli "3", :toimikausialku #inst "2001-07-31T21:00:00.000000000-00:00", :toimialuenimi "Valtakunnallinen",
    :laskutusnumero "H568", :nimiru nil, :toimikausiloppu #inst "2004-07-30T21:00:00.000000000-00:00", :maararaha 2800M}

   :henkilo
   {:jasenseq 12136, :sukunimi "Aesir", :etunimi "Odin", :posti_pos_numero "02631"
    :tanimi "Asgard Instruments Analytical Oy", :sukupuoli "1", :aidinkieli "1", :titteli nil
    :edustus "ta", :lahiosoite "Valhalla", :puhelin "(09) 22 944", :sahkoposti "odin@asgard.valhalla"}

 })


(deftest mapping-has-proper-columns
  (testing "mapping results with the proper db columns"
           (is (= (:columns (to-insert-map :tutkintotoimikunta (:tutkintotoimikunta sampleData)))
                  [:diaarinumero :nimi_sv :tilikoodi :toimikausi_id :tkunta :nimi_fi :toimiala :kielisyys :toimikausi_loppu :toimikausi_alku]))))


(defn konvertoi-rivi-sampledata!
  [taulu]
  (with-targetdb (jdbc/db-transaction [t jdbcurl-target]
                                      (tyhjenna! (reverse taulut))
                                      (konvertoi-rivi! taulu (get sampleData taulu)))))

(defn konvertoi-seq!
  [dataseq taulu]
  (with-targetdb (jdbc/db-transaction [t jdbcurl-target]
                                      (tyhjenna! (reverse taulut))
                                      (konvertoi! taulu dataseq))))

(deftest konversio-toimikunta-mock-input!
  (testing "yksi rivi mock-dataa menee kohdekantaan"
           (konvertoi-rivi-sampledata! :tutkintotoimikunta)
           (is (= 1
                  (with-targetdb (jdbc/db-transaction [t jdbcurl-target]
                                                        (:count (execquery "select count(*) from tutkintotoimikunta" first))))))))

(deftest konversio-toimikunta-mock-input-full!
  (testing "yksi rivi mock-dataa menee kohdekantaan"
           (is (= 1
                  (with-targetdb (jdbc/db-transaction [t jdbcurl-target]
                                                      (konvertoi-seq! [(:tutkintotoimikunta sampleData)] :tutkintotoimikunta )
                                                      (:count (execquery "select count(*) from tutkintotoimikunta" first))))))))

(deftest konversio-henkilo-mock-input-full!
  (testing "yksi rivi mock-dataa menee kohdekantaan"
           (is (= 1
                  (with-targetdb (jdbc/db-transaction [t jdbcurl-target]
                                                      (konvertoi-seq! [(:henkilo sampleData)] :henkilo )
                                                      (:count (execquery "select count(*) from henkilo" first))))))))

(defn konvertoi-e2e-test!
  [& tauluargs]
;  (konvertoi-e2e! println "20073107" tauluargs #(logwrapper %1 %2 konvertoi!)))
  (with-targetdb
    (tyhjenna! (reverse taulut))
    (konvertoi-e2e! println "20073107" tauluargs konvertoi!)))

(deftest assert-datarakenteet
  (testing "varmistetaan että datarakenteet ovat loogisesti konsistentteja"
           (assert (= (set (keys (input-select "19000101"))) (set taulut))))
           (assert (= (set (keys kanta-mappays))   (set taulut)))
           (assert (clojure.set/subset? (set (keys kanta-defaultit)) (set taulut)))
           (assert (clojure.set/subset? (set (keys kanta-konversiot))   (set taulut)))
           (assert (clojure.set/subset? (set (keys lopputoimet))   (set taulut))))

