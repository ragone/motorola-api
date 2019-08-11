(ns motorola-api.radio
  (:require [schema.core :as s]
            [motorola-api.models.radio :refer [Radio]]
            [motorola-api.models.allowed-location :refer [AllowedLocation]]
            [toucan.db :as db]
            [ring.util.http-response :refer [created]]
            [compojure.api.sweet :refer [POST GET]]
            [ring.util.http-response :refer [created ok not-found forbidden]]))

;; Validation

(defn valid-location? [location allowed-locations]
  "Return non-nil if LOCATION exists in ALLOWED-LOCATIONS."
  (some #(= location %)
        (map (fn [allowed-location]
               (:name allowed-location))
             allowed-locations)))

;; Schemas

(s/defschema RadioRequestSchema
  {:alias s/Str
   :allowed_locations [s/Str]})

(s/defschema RadioLocationRequestSchema
  {:location s/Str})

;; Responses

(defn id->created [id]
  (created (str "/radios/" id) {:id id}))

(defn radio->response [radio]
  (if (and radio (:location radio))
    (ok {:location (:location radio)})
    (not-found)))

;; Handlers

(defn create-radio-handler [radio-id create-radio-req]
  (db/insert! Radio
              :id radio-id
              :alias (:alias create-radio-req))
  (db/insert-many! AllowedLocation
                   (into [] (map (fn [name]
                                   {:name name :radio radio-id})
                                 (:allowed_locations create-radio-req))))
  (id->created radio-id))

(defn update-radio-location-handler [radio-id update-radio-location-req]
  (if (valid-location? (:location update-radio-location-req)
                       (db/select AllowedLocation :radio radio-id))
    (do
      (db/update! Radio radio-id :location (:location update-radio-location-req))
      (ok {:id radio-id}))
    (forbidden "Location not allowed")))

(defn get-radio-location-handler [radio-id]
  (-> (Radio radio-id)
      radio->response))

;; Routes

(def radio-routes
  [(POST "/radios/:id" []
         :summary "Create a new Radio with :id"
         :path-params [id :- s/Int]
         :body [create-radio-req RadioRequestSchema]
         (create-radio-handler id create-radio-req))
   (POST "/radios/:id/location" []
         :summary "Update the location for the given :id"
         :path-params [id :- s/Int]
         :body [update-radio-location-req RadioLocationRequestSchema]
         (update-radio-location-handler id update-radio-location-req))
   (GET "/radios/:id/location" []
         :summary "Get the location for the given :id"
         :path-params [id :- s/Int]
         (get-radio-location-handler id))])
