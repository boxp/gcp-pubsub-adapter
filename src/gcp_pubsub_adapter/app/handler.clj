(ns gcp-pubsub-adapter.app.handler
  (:require [clojure.core.async :refer [go put! <! <!! close! chan]]
            [com.stuartsierra.component :as component]
            [compojure.core :refer [defroutes context GET POST routes]]
            [ring.adapter.jetty :as server]
            [cheshire.core :refer [generate-string]]
            [gcp-pubsub-adapter.domain.usecase.lemming-usecase :as lemming-usecase]
            [gcp-pubsub-adapter.domain.usecase.to-lemming-usecase :as to-lemming-usecase]))

(defn get-index
  [req]
  "OK\n")

(defn post-lemming
  [{:keys [lemming-serial lemming-usecase] :as comp} serial req]
  (if (= lemming-serial serial)
    (do
      (-> (lemming-usecase/publish-message
            lemming-usecase
            (generate-string (:body req))))
      "Success!\n")
    "Serial number has wrong.\n"))

(defn get-to-lemming
  [{:keys [lemming-serial to-lemming-usecase] :as comp} serial]
  (if (= lemming-serial serial)
    (-> (<!! (to-lemming-usecase/subscribe-message
               to-lemming-usecase))
        generate-string)
    "Serial number has wrong.\n"))

(defrecord HandlerComponent [leimming-serial lemming-usecase to-lemming-usecase]
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
