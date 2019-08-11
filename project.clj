(defproject motorola-api "0.1.0"
  :description "Motorola API"
  :url "https://ragone.io"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 ; Web
                 [prismatic/schema "1.1.9"]
                 [metosin/compojure-api "2.0.0-alpha26"]
                 [ring/ring-jetty-adapter "1.6.3"]
                 ; Database
                 [toucan "1.1.9"]
                 [org.postgresql/postgresql "42.2.4"]]
  :main ^:skip-aot motorola-api.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
