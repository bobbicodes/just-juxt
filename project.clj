(defproject just-juxt "1.0.0-SNAPSHOT"
  :description "Just-juxt web app"
  :url "just-juxt.herokuapp.com"
  :license {:name "Eclipse Public License v1.0"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [compojure "1.4.0"]
                 [ring/ring-jetty-adapter "1.4.0"]
                 [environ "1.0.0"]
                 [clj-http "3.10.0"]
                 [org.clojure/data.json "0.2.6"]
                 [instaparse "1.4.10"]
                 [twitter-api "1.8.0"]]
  :min-lein-version "2.0.0"
  :plugins [[environ/environ.lein "0.3.1"]]
  :hooks [environ.leiningen.hooks]
  :uberjar-name "just-juxt-standalone.jar"
  :profiles {:production {:env {:production true}}})
