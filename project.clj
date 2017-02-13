(defproject gitter-data "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [cheshire "5.7.0"]
                 [org.clojure/data.csv "0.1.3"]
                 [clojure-lanterna "0.9.4"]
                 [environ "1.1.0"]
                 [slingshot "0.12.2"]
                ;  gui stuff can be found in lanterna
                ;  [com.googlecode.lanterna/lanterna "2.1.7"]
                 [org.clojure/core.async "0.2.395"]
                 [clj-http "2.3.0"]]
  :plugins [[lein-environ "1.1.0"]]
  :main ^:skip-aot gitter-data.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
