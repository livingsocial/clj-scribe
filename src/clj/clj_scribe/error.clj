(ns clj-scribe.error
  (:use [clojure.stacktrace :only [print-cause-trace]]))

(defn print-exception [exception]
  (binding [*out* *err*]
    (print-cause-trace exception)
    (flush)))
