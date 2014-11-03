(ns ttkr-konversio.konversiodemo
  "Demo tiimipalaveria varten. Konvertoidaanpa keskusjärjestö tietomalliin!"
   (:require [clojure.java.jdbc :as jdbc]))

(def conf (binding [*read-eval* false]
    (read-string (slurp "kantaparametrit.properties"))))

(def jdbcurl-target (:target-db conf))

(def input-db (:input-db conf))
 
(def taulut
  "järjestyksessä dependencyjen kannalta"
  [:keskusjarjesto])

(defn input-select
  "generoidaan halutulla rajauksella input-selectit. YYYYDDMM, esim. 20073107"
  [toimikausialku]
  {
   :keskusjarjesto "select * from ..."
})

(def kanta-mappays
  {:keskusjarjesto 
     {:nimi xx
      :keskusjarjestoid xx
      :postitoimipaikka xx
      }
   })

(def kanta-defaultit
  {} )
  
(def kanta-konversiot
;  {:henkilo ; kohdekannan taulu
;   {:sukupuoli {"1" "mies", "2" "nainen"} ; lähtötaulun sarake, konversiofunktio
{
 :keskusjarjesto ;kohdekannan taulu
   {:nimi clojure.string/lower-case}
 }
)

(def lopputoimet
  {})

