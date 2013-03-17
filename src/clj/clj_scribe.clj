(ns clj-scribe
  (:require [clj-scribe.client :as client]
            [clojure.stacktrace :as st])
  (:use [clj-scribe.error :only [print-exception]]))

(defn- default-error-handler [category messages exception]
  (binding [*out* *err*]
    (println "Error logging" (count messages) "messages to" category)
    (print-exception exception)))

(def ^:private cfg nil)
(def ^:private client-agent (agent nil :error-mode :continue :error-handler (fn [_ excp] (print-exception excp))))

(defn- open-client [client]
  (if (client/open? client)
    client
    (do
      (client/close client)
      (client/create (:host cfg) (:port cfg)))))

(defn- log-with-agent
  [client category messages]
  (try
    (let [client (open-client client)]
      (try
        (client/log client category messages)
        client
        (catch Exception e
          (client/close client)
          (throw e))))
    (catch Exception e
      ((:error-handler cfg) category messages e)
      nil)))

(defn setup
  "Configure the scribe logger. Supports these options:

  :host          The hostname of the Scribe server
  :port          The port of the Scribe server
  :category      The default category under which to log messages
  :error-handler An optional function that will be invoked with the category, message and Exception
                 in case of an unexpected error."
  [& opts]
  (alter-var-root (var cfg)
                  #(merge {:host "localhost" :port 1463 :category "CHANGEME" :error-handler default-error-handler} % (apply hash-map opts)))
  (send-off client-agent (fn [client]
                           (client/close client)
                           nil))
  cfg)

(defn log
  "Log the provided sequence of message Strings to Scribe. If a category is not
  provided, the default configured with the setup function is used."
  ([messages]
    (log (:category cfg) messages))
  ([category messages]
    (when cfg
      (send-off client-agent log-with-agent category messages))
    nil))
