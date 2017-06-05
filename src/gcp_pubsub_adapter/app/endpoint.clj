(ns gcp-pubsub-adapter.app.endpoint
  (:import (io.reactivex Observable subjects.AsyncSubject))
  (:require [com.stuartsierra.component :as component]
            [compojure.core :refer [defroutes context GET POST routes]]
            [compojure.route :as route]
            [ring.adapter.jetty :as server]
            [ring.middleware.json :refer [wrap-json-params
                                          wrap-json-response
                                          wrap-json-body]]
            [gcp-pubsub-adapter.app.handler :as handler]))

(defn main-routes
  [{:keys [handler] :as comp}]
  (routes
    (POST "/lemming" [req] #(handler/post-lemming handler %))
    (route/not-found "<h1>404 page not found</h1>")))

(defn app
  [comp]
  (-> (main-routes comp)
      (wrap-json-body {:keywords? true :bigdecimals? true})))

(defrecord EndPointComponent [port server handler]
  component/Lifecycle
  (start [this]
    (println ";; Starting EndPointComponent")
    (-> this
        (assoc :server (server/run-jetty (app this) {:port port :join? false}))))
  (stop [this]
    (println ";; Stopping EndPointComponent")
    (.stop (:server this))
    (-> this
        (dissoc :server)
        (dissoc :port)
        (dissoc handler))))

(defn end-point-component
  [port]
  (map->EndPointComponent {:port port}))
