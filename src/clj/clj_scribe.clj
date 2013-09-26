(ns clj-scribe
  (:require [clj-scribe.connection :as cn]
            [clojure.stacktrace :as st])
  (:use [clj-scribe.error :only [print-exception]]
        [clojure.string :only [blank?]]))

(defn- default-error-handler [category messages exception]
  (binding [*out* *err*]
    (println "Error logging" (count messages) "messages to" category)
    (print-exception exception)))

(defn- open-connection [conn host port]
  (if (cn/open? conn)
    conn
    (do
      (cn/close conn)
      (cn/connection host port))))

(defn- log-with-connection [conn host port err-handler category messages]
  (try
    (let [conn (open-connection conn host port)]
      (cn/log conn category messages)
      conn)
    (catch Exception e
      (err-handler category messages e)
      nil)))

(defprotocol Logger
  (log [logger messages] [logger category messages] "Log a sequence of messages with the provided Logger."))

(deftype AsyncLogger [host port category err-handler conn-agent]
  Logger
  (log [logger messages]
    (log logger category messages))
  (log [logger category messages]
    (send-off conn-agent log-with-connection host port err-handler category messages)
    nil))

(defn async-logger
  "Create an asynchronous Scribe logger, where calls to log queue messages to be sent via an Agent.
  Supports these options:

  :host          The hostname of the Scribe server
  :port          The port of the Scribe server
  :category      The default category in which to log messages
  :error-handler An optional function that will be invoked with the category, message and Exception
                 in case of an unexpected error."
  [& {:keys [host port category error-handler] :or {host "localhost" port 1463 error-handler default-error-handler}}]
  (when (blank? category)
    (throw (IllegalArgumentException. "A default category is required.")))
  (AsyncLogger. host port category error-handler (agent nil :error-mode :continue :error-handler (fn [_ excp] (print-exception excp)))))

(deftype SyncLogger [host port category ^:unsynchronized-mutable conn]
  Logger
  (log [logger messages]
    (log logger category messages))
  (log [logger category messages]
    (locking logger
      (set! conn (open-connection conn host port))
      (cn/log conn category messages))))

(defn sync-logger
  "Create a synchronous Scribe logger, where calls to log block until returning a result.
  Supports these options:

  :host          The hostname of the Scribe server
  :port          The port of the Scribe server
  :category      The default category in which to log messages"
  [& {:keys [host port category] :or {host "localhost" port 1463}}]
  (when (blank? category)
    (throw (IllegalArgumentException. "A default category is required.")))
  (SyncLogger. host port category nil))

(deftype MockLogger []
  Logger
  (log [logger messages])
  (log [logger category messages]))

(defn mock-logger
  "Create an implementation of Logger that doesn't actually log anything. Useful for mocking in a test environment."
  []
  (MockLogger.))
