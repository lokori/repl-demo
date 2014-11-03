(defproject repl-demokonversio "0.1.0-SNAPSHOT"
  :description "Demo konversio"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/java.jdbc "0.3.0-alpha5"]
                 [postgresql "9.1-901.jdbc4"]
                 [korma "0.3.0-RC5"]
                 [clj-time "0.6.0"]

                 [org.clojure/tools.logging "0.2.4"]
                 [org.slf4j/slf4j-log4j12 "1.7.1"]
                 [log4j/log4j "1.2.17" :exclusions [javax.mail/mail
                                                    javax.jms/jms
                                                    com.sun.jmdk/jmxtools
                                                    com.sun.jmx/jmxri]]]
  :source-paths ["src/clj"]
  :test-paths ["test"]
  :main repl-konversio.core)
