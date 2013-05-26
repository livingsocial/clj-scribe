(ns clj-scribe.connection
  (:use [clj-scribe.error :only [print-exception]])
  (:import [scribe.thrift scribe scribe$Client ResultCode LogEntry]
           [org.apache.thrift.transport TFramedTransport TSocket]
           [org.apache.thrift.protocol TBinaryProtocol]))

(defprotocol Connection
  (open? [conn])
  (close [conn])
  (log [conn category messages]))

(defn ^LogEntry log-entry [^String category ^String message]
  (LogEntry. category message))

(deftype ThriftConnection [^:unsynchronized-mutable socket
                           ^:unsynchronized-mutable transport
                           ^:unsynchronized-mutable prototype
                           ^:unsynchronized-mutable scribe]
  Connection
  (open? [conn]
    (and conn (.isOpen ^TFramedTransport transport)))
  (close [conn]
    (when conn
      (try
        (.close ^TFramedTransport transport)
        (.close ^TSocket socket)
        conn
        (catch Exception e
          (print-exception e)
          conn))))
  (log [conn category messages]
    (let [^scribe$Client client (.scribe conn)
          entries (map (partial log-entry category) messages)]
      (try
        (.getValue (.Log client entries))
        (catch Exception e
          (close conn)
          (throw e))))))

(extend-type nil
  Connection
  (open? [_] false)
  (close [_] nil))

(defn connection [host port]
  (let [socket (TSocket. host port)
        transport (TFramedTransport. socket)
        protocol (TBinaryProtocol. transport false false)
        scribe (scribe$Client. protocol protocol)]
    (.open transport)
    (ThriftConnection. socket transport protocol scribe)))
