(defproject clj-scribe "0.3.0"
  :description "A Scribe log server client"
  :url "http://github.com/livingsocial/clj-scribe"
  :license {:name "MIT" :url "http://opensource.org/licenses/MIT"}
  :source-paths ["src/clj"]
  :java-source-paths ["src/java"]
  :dependencies [[org.apache.thrift/libthrift "0.9.0"]
                 [org.apache.thrift/libfb303 "0.9.0"]
                 [org.clojure/clojure "1.5.1"]
                 [org.slf4j/slf4j-api "1.6.0"]])
