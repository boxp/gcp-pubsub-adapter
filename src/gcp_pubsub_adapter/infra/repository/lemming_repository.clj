(ns gcp-pubsub-adapter.infra.repository.lemming-repository
  (:import (com.google.cloud ServiceOptions)
           (com.google.cloud.pubsub.spi.v1 TopicAdminClient)
           (com.google.pubsub.v1 TopicName))
  (:require [com.stuartsierra.component :as component]
            [gcp-pubsub-adapter.infra.datasource.pubsub :refer [create-publisher publish]]))

(def topic-key :lemming)

(defn publish-message
  [{:keys [pubsub-publisher] :as comp} message]
  (publish pubsub-publisher topic-key message
           (fn [data])
           (fn [e] (println e))))

(defrecord LemmingRepositoryComponent [pubsub-publisher]
  component/Lifecycle
  (start [this]
    (println ";; Starting LemmingRepositoryComponent")
    (-> this
        (update :pubsub-publisher #(create-publisher % topic-key))))
  (stop [this]
    (println ";; Stopping LemmingRepositoryComponent")
    (-> this
        (dissoc :pubsub-publisher))))

(defn lemming-repository-component []
  (map->LemmingRepositoryComponent {}))
