(defproject clj-scribe "0.2.0"
  :description "An asynchronous Scribe log server client"
  :url "http://code.livingsocial.net/mjroghelia/clj-scribe"
  :license {:name "MIT" :url "http://opensource.org/licenses/MIT"}
  :source-paths ["src/clj"]
  :java-source-paths ["src/java"]
  :dependencies [[org.apache.thrift/libthrift "0.9.0"]
                 [org.apache.thrift/libfb303 "0.9.0"]
                 [org.clojure/clojure "1.4.0"]
                 [org.slf4j/slf4j-api "1.6.0"]])
