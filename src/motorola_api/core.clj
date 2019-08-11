(ns motorola-api.core
  (:require [toucan.db :as db]
            [toucan.models :as models]
            [ring.adapter.jetty :refer [run-jetty]]
            [compojure.api.sweet :refer [api routes]]
            [motorola-api.radio :refer [radio-routes]])
  (:gen-class))

;; Hardcoded for simplicity.
(def db-spec
  "Database Connection."
  {:host "db"
   :dbtype "postgres"
   :dbname "postgres"
   :user "postgres"
   :password "postgres"})

(def swagger-config
  "API Documentation."
  {:ui "/swagger"
   :spec "/swagger.json"
   :options {:ui {:validatorUrl nil}
             :data {:info {:version "1.0.0", :title "Motorola REST API"}}}})

(def app (api {:swagger swagger-config} (apply routes radio-routes)))

(defn -main
  "Entry point for our app."
  [& args]
  (db/set-default-db-connection! db-spec) ; Setup database
  (models/set-root-namespace! 'motorola-api.models) ; Setup models
  (run-jetty app {:port 8080})) ; Run app
