(ns gcp-pubsub-adapter.app.handler
  (:require [com.stuartsierra.component :as component]
            [compojure.core :refer [defroutes context GET POST routes]]
            [ring.adapter.jetty :as server]
            [cheshire.core :refer [generate-string]]
            [gcp-pubsub-adapter.domain.usecase.lemming-usecase :as lemming-usecase]))

(defn post-lemming
  [{:keys [lemming-serial lemming-usecase] :as comp} serial req]
  (if (= lemming-serial serial)
    (do
      (-> (lemming-usecase/publish-message
            lemming-usecase
            (generate-string (:body req))))
      "Success!\n")
    "Serial number has wrong.\n"))

(defrecord HandlerComponent [leimming-serial lemming-usecase]
  component/Lifecycle
  (start [this]
    (println ";; Starting HandlerComponent")
    this)
  (stop [this]
    (println ";; Stopping HandlerComponent")
    this))

(defn handler-component
  [lemming-serial]
  (map->HandlerComponent {:lemming-serial lemming-serial}))
