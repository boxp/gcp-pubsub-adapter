(ns gcp-pubsub-adapter.infra.repository.to-lemming-repository
  (:require [com.stuartsierra.component :as component]
            [clojure.core.async :refer [go put! <! close! chan]]
            [cheshire.core :refer [parse-string]]
            [gcp-pubsub-adapter.infra.datasource.pubsub :refer [create-subscription add-subscriber]]
            [gcp-pubsub-adapter.domain.entity.operation :refer [map->Led map->Operation]]))

(def topic-key :to-lemming)
(def subscription-key :to-lemming-lemming)

(defn message->operation
  [m]
  (map->Operation
    {:led (map->Led (:led m))}))

(defn subscribe
  [comp]
  (:channel comp))

(defrecord ToLemmingRepositoryComponent [pubsub-subscription channel]
  component/Lifecycle
  (start [this]
    (let [c (chan)]
      (println ";; Starting ToLemmingRepositoryComponent")
      (try
        (create-subscription (:pubsub-subscription this) topic-key subscription-key)
        (catch Exception e
          (println "Warning: Already" topic-key "has exists")))
      (-> this
          (update :pubsub-subscription
                  #(add-subscriber % topic-key subscription-key
                                   (fn [m]
                                     (put! c (message->operation m)))))
          (assoc :channel c))))
  (stop [this]
    (println ";; Stopping ToLemmingRepositoryComponent")
    (close! (:channel this))
    (-> this
        (dissoc :channel))))

(defn to-lemming-repository-component
  []
  (map->ToLemmingRepositoryComponent {}))
