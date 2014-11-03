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

(ns repl-konversio.core
  "konversio engine. melko yleiskäyttöistä logiikkaa."
 (:require [clojure.java.io :as io]
           [clojure.java.jdbc :as jdbc]
           [clojure.java.jdbc.sql :as s]
           [clojure.tools.logging :as log])
 (:use ttkr-konversio.aitu-konversio))

(defmacro with-targetdb
  [& body]
  `(jdbc/with-connection jdbcurl-target
     (jdbc/do-commands "set session aitu.kayttaja = 'KONVERSIO'")
     ~@body))

(defmacro with-inputdb
  [& body]
  `(jdbc/with-connection input-db ~@body))

(defn execquery
  [query rowmapper]
  (jdbc/with-query-results res [query]
    (doall (rowmapper res))))

(defn to-target-map
  [taulu row]
  (let [kentta-mappays (get kanta-mappays taulu)
        default-mappays (get kanta-defaultit taulu {})
        konversiot (get kanta-konversiot taulu {})]
    (into default-mappays
      (for [[k v] kentta-mappays :when (contains? row v)]
        {k ((get konversiot v identity) (get row v))}))))

(defn to-insert-map
  [taulu x]
  (let [rowmap (to-target-map taulu x)]
    {
     :columns (vec (keys rowmap))
     :values (vec (vals rowmap))}))

(defn tyhjenna-target-kaikki!
  [taulu]
  (jdbc/execute! jdbcurl-target [(str "delete from " (name taulu))]))

(defn to-sql-type
  "converts x to JDBC type used by the SQL API"
  [x]
  (if (instance? java.util.Date x) (java.sql.Date. (.getTime x)) x))

(defn konvertoi-rivi!
  [taulu rivi]
  (let [insert-mappi (to-insert-map taulu rivi)]
    (jdbc/insert-values (name taulu) (:columns insert-mappi) (mapv to-sql-type (:values insert-mappi)))
    insert-mappi
    ))

(defn konvertoi!
  [taulu rivi-seq]
  (let [tulos (doall
    (map #(konvertoi-rivi! taulu %) rivi-seq))]
    (if (contains? lopputoimet taulu)
      ((get lopputoimet taulu)))
    tulos))

(defn tyhjenna!
  [tauluseq]
  (with-targetdb (jdbc/db-transaction [t jdbcurl-target]
                                      (doseq [taulu tauluseq]
                                        (tyhjenna-target-kaikki! taulu)))))
(defn konvertoi-e2e!
  "konvertoi tauluja, palauttaa rivilukumäärän viimeisestä taulusta. logfn on logitusfunktio, startdate on aloituspäivä inputin rajauksessa"
  [logfn startdate tauluargs targetfn]
  (logfn "konversio is go!")
  (with-targetdb
    (tyhjenna! (reverse taulut))
    (logfn "tyhjennys ok"))
  (with-targetdb
    (doseq [taulu tauluargs]
      (logfn "konvertoidaan " (name taulu))

      (if-let [input-query (get (input-select startdate) taulu)]
        (let [_ (logfn "  sql:" input-query)
              input (with-inputdb (execquery input-query identity))]
          (logfn "  inputin kentät" (keys (first input)))
          (logfn "-- \n")
          (time (targetfn taulu input))
          (logfn "-- \n")
          (logfn taulu "luettuja rivejä yhteensä: " (count input))
          (logfn taulu "konvertoituja rivejä yhteensä: " (:count (execquery (str "select count(*) from " (name taulu)) first))))
        (logfn "ei konvertoitavaa")))
    (:count (execquery (str "select count(*) from " (name (last tauluargs))) first))))

(defn logwrapper
  [taulu input targetfn]
  (let [inputdata (with-out-str
                    (doseq [rivi input]  (prn (vals rivi))))]
    (println inputdata))
  (targetfn taulu input))

(defn dumppaa-kaikki!
  []
  (let [log (with-out-str
              (time (konvertoi-e2e! println "20103107" taulut #(logwrapper %1 %2 (constantly nil)))))]
    (spit "aitu-konversiodump.log" log)
    (println "konversio valmis!")))

(defn konvertoi-kaikki!
  []
  (without-triggers [:tutkintotoimikunta :jarjestamissopimus]
    #(with-targetdb
      (tyhjenna! (reverse taulut))
      (println "tyhjennys ok")
      (time (konvertoi-e2e! println "19103107" taulut konvertoi!))
      (println "konversio valmis!"))))

(defn -main []
  (konvertoi-kaikki!))
