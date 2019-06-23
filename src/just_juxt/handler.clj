(ns just-juxt.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [clojure.data.json :as json]
            [clj-http.client :as client]
            [hiccup.page :as page]
            [ring.adapter.jetty :as jetty]))

(def juxt-url
  (client/get "https://api.github.com/search/code?q=juxt+in:file+language:clojure&sort=indexed"
              {:oauth-token "secret"}))

(def juxt1 (str (:html_url (first (:items (json/read-str (str (:body juxt-url)) :key-fn keyword))))))

(defn raw [url]
  (clojure.string/replace
   (clojure.string/replace url "/blob/" "/")
   "github.com" "raw.githubusercontent.com"))

(defn handler [request]
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body (slurp (raw juxt1))})



(defn -main []
  (jetty/run-jetty handler {:port 3000}))