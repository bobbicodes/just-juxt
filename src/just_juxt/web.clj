(ns clojure-getting-started.web
  (:require [compojure.core :refer [defroutes GET PUT POST DELETE ANY]]
            [compojure.handler :refer [site]]
            [compojure.route :as route]
            [clojure.java.io :as io]
            [clojure.data.json :as json]
            [clj-http.client :as client]
            [ring.adapter.jetty :as jetty]
            [environ.core :refer [env]]))

(def juxt-query
  (atom (client/get "https://api.github.com/search/code?q=juxt+in:file+language:clojure&sort=indexed"
                    {:oauth-token "secret"})))

(defn most-recent-juxt []
  (str (:html_url (first (:items (json/read-str (str (:body @juxt-query)) :key-fn keyword))))))

(defn raw [url]
  (clojure.string/replace
   (clojure.string/replace url "/blob/" "/")
   "github.com" "raw.githubusercontent.com"))

(defn content []
  (reset! juxt-query (client/get "https://api.github.com/search/code?q=juxt+in:file+language:clojure&sort=indexed"
                                 {:oauth-token "secret"}))
  (str "Source: " (most-recent-juxt) "\n\n" (slurp (raw (most-recent-juxt)))))

(defn splash []
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body (content)})

(defroutes app
  (GET "/" []
       (splash))
  (ANY "*" []
       (route/not-found (slurp (io/resource "404.html")))))

(defn -main [& [port]]
  (let [port (Integer. (or port (env :port) 5000))]
    (jetty/run-jetty (site #'app) {:port port :join? false})))

;; For interactive development:
;; (.stop server)
;; (def server (-main))
