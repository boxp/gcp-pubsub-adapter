(ns gcp-pubsub-adapter.domain.usecase.lemming-usecase
  (:require [com.stuartsierra.component :as component]
            [gcp-pubsub-adapter.infra.repository.lemming-repository :as r]))

(defn publish-message
  [{:keys [lemming-repository] :as comp} message]
  (r/publish-message lemming-repository message))

(defrecord LemmingUseCaseComponent [lemming-repository]
  component/Lifecycle
  (start [this]
    (println ";; Starting LemmingUseCaseComponent")
    this)
  (stop [this]
    (println ";; Stopping LemmingUseCaseComponent")
    this))

(defn lemming-usecase-component []
  (map->LemmingUseCaseComponent {}))
