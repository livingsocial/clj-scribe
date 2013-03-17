(ns clj-scribe.client
  (:use [clj-scribe.error :only [print-exception]])
  (:import [scribe.thrift scribe scribe$Client ResultCode LogEntry]
           [org.apache.thrift.transport TFramedTransport TSocket]
           [org.apache.thrift.protocol TBinaryProtocol]))

(deftype Client [socket transport prototype scribe])

(defn create [host port]
  (let [socket (TSocket. host port)
        transport (TFramedTransport. socket)
        protocol (TBinaryProtocol. transport false false)
        scribe (scribe$Client. protocol protocol)]
    (.open transport)
    (Client. socket transport protocol scribe)))

(defn open? [^Client client]
  (and client (.isOpen ^TFramedTransport (.transport client))))

(defn close [^Client client]
  (when client
    (try
      (.close ^TFramedTransport (.transport client))
      (.close ^TSocket (.socket client))
      client
      (catch Exception e
        (print-exception e)
        client))))

(defn ^LogEntry log-entry [^String category ^String message]
  (LogEntry. category message))

(defn ^ResultCode log [^Client client category messages]
  (let [entry-fn (partial log-entry category)
        entries (map entry-fn messages)]
    (.Log ^scribe$Client (.scribe client) entries)))
