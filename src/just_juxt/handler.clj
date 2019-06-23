(ns just-juxt.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [clojure.data.json :as json]
            [clj-http.client :as client]
            [hiccup.page :as page]
            [ring.adapter.jetty :as jetty]))

(def juxt-url
  (client/get "https://api.github.com/search/code?q=juxt+in:file+language:clojure&sort=indexed"
              {:oauth-token "nope"}))
  
(defn handler [request]
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body (str (first (:items (json/read-str (str (:body juxt-url)) :key-fn keyword))))})

(defn -main []
  (jetty/run-jetty handler {:port 3000}))