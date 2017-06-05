(ns gcp-pubsub-adapter.system
  (:require [com.stuartsierra.component :as component]
            [environ.core :refer [env]]
            [gcp-pubsub-adapter.infra.datasource.pubsub :refer [pubsub-publisher-component]]
            [gcp-pubsub-adapter.infra.repository.lemming-repository :refer [lemming-repository-component]]
            [gcp-pubsub-adapter.domain.usecase.lemming-usecase :refer [lemming-usecase-component]]
            [gcp-pubsub-adapter.app.handler :refer [handler-component]]
            [gcp-pubsub-adapter.app.endpoint :refer [end-point-component]]))

(defn gcp-pubsub-adapter-system
  [{:keys [gcp-pubsub-adapter-project-id
           gcp-pubsub-adapter-port
           gcp-pubsub-adapter-lemming-serial] :as conf}]
  (component/system-map
    :pubsub-publisher (pubsub-publisher-component gcp-pubsub-adapter-project-id)
    :lemming-repository (component/using
                          (lemming-repository-component)
                          [:pubsub-publisher])
    :lemming-usecase (component/using
                       (lemming-usecase-component)
                       [:lemming-repository])
    :endpoint (component/using
                (end-point-component gcp-pubsub-adapter-port)
                [:handler])
    :handler (component/using
               (handler-component gcp-pubsub-adapter-lemming-serial)
               [:lemming-usecase])))

(defn load-config []
  {:gcp-pubsub-adapter-project-id (env :gcp-pubsub-adapter-project-id)
   :gcp-pubsub-adapter-port (-> (or (env :gcp-pubsub-adapter-port) "8080") Integer/parseInt)
   :gcp-pubsub-adapter-lemming-serial (env :gcp-pubsub-adapter-lemming-serial)})

(defn -main []
  (component/start
    (gcp-pubsub-adapter-system (load-config))))
