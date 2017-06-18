(ns gcp-pubsub-adapter.domain.usecase.to-lemming-usecase
  (:require [com.stuartsierra.component :as component]
            [gcp-pubsub-adapter.infra.repository.to-lemming-repository :as r]))

(defn subscribe-message
  [{:keys [to-lemming-repository] :as comp}]
  (r/subscribe to-lemming-repository))

(defrecord ToLemmingUseCaseComponent [to-lemming-repository]
  component/Lifecycle
  (start [this]
    (println ";; Starting ToLemmingUseCaseComponent")
    this)
  (stop [this]
    (println ";; Stopping ToLemmingUseCaseComponent")
    this))

(defn to-lemming-usecase-component []
  (map->ToLemmingUseCaseComponent {}))
