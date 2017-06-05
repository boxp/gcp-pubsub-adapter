(ns gcp-pubsub-adapter.infra.datasource.pubsub
  (:import (com.google.protobuf ByteString)
           (com.google.api.core ApiFutures
                                ApiFutureCallback)
           (com.google.cloud ServiceOptions)
           (com.google.cloud.pubsub.spi.v1 TopicAdminClient
                                           Publisher)
           (com.google.pubsub.v1 TopicName
                                 PubsubMessage))
  (:require [com.stuartsierra.component :as component]))

(defn create-topic
  [comp topic-key]
  (if-not (get (:publishers comp) topic-key)
    (let [topic-admin-cli (TopicAdminClient/create)]
      (try
        (->> (TopicName/create (:project-id comp)
                               (name topic-key))
             (.createTopic topic-admin-cli)
             Publisher/defaultBuilder
             .build
             (assoc-in comp [:publishers topic-key]))
        (catch Exception e
          (->> (TopicName/create (:project-id comp)
                                 (name topic-key))
               Publisher/defaultBuilder
               .build
               (assoc-in comp [:publishers topic-key])))))
    comp))

(defn publish
  [{:keys [publishers project-id] :as comp} topic-key message on-success on-failure]
  (let [data (ByteString/copyFromUtf8 message)
        pubsub-message (-> (PubsubMessage/newBuilder) (.setData data) .build)
        publisher (-> publishers topic-key)
        message-id-future (.publish publisher pubsub-message)
        callback (reify ApiFutureCallback
                   (onSuccess [this message-id]  #(on-success message-id))
                   (onFailure [this e] #(on-failure e)))]
    (ApiFutures/addCallback message-id-future callback)))

(defrecord PubSubPublisherComponent [project-id publishers]
  component/Lifecycle
  (start [this]
    (println ";; Starting PubSubPublisherComponent")
    this)
  (stop [this]
    (println ";; Stopping PubSubPublisherComponent")
    (-> this
        (dissoc :publishers)
        (dissoc :project-id))))

(defn pubsub-publisher-component
  [project-id]
  (map->PubSubPublisherComponent {:project-id project-id}))
