(defproject gcp-pubsub-adapter "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/core.async "0.2.395"]
                 [environ "1.1.0"]
                 [com.stuartsierra/component "0.3.2"]
                 [ring "1.6.1"]
                 [ring/ring-json "0.4.0"]
                 [compojure "1.6.0"]
                 [cheshire "5.7.1"]
                 [io.reactivex.rxjava2/rxjava "2.1.0"]
                 [org.clojure/tools.namespace "0.2.10"]
                 [com.google.cloud/google-cloud-pubsub "0.17.2-alpha"]]
  :profiles
  {:dev {:source-paths ["src" "dev"]}
   :uberjar {:main gcp-pubsub-adapter.system}})
