(ns gcp-pubsub-adapter.app.handler
  (:require [com.stuartsierra.component :as component]
            [compojure.core :refer [defroutes context GET POST routes]]
            [ring.adapter.jetty :as server]
            [cheshire.core :refer [generate-string]]
            [gcp-pubsub-adapter.domain.usecase.lemming-usecase :as lemming-usecase]))

(defn post-lemming
  [{:keys [lemming-usecase] :as comp} req]
  (-> (lemming-usecase/publish-message
        lemming-usecase
        (generate-string (:body req))))
  "Success!\n")

(defrecord HandlerComponent [lemming-usecase]
  component/Lifecycle
  (start [this]
    (println ";; Starting HandlerComponent")
    this)
  (stop [this]
    (println ";; Stopping HandlerComponent")
    this))

(defn handler-component []
  (map->HandlerComponent {}))
