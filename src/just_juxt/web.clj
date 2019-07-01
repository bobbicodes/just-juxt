(ns just-juxt.web
  (:require [compojure.core :refer [defroutes GET PUT POST DELETE ANY]]
            [compojure.handler :refer [site]]
            [compojure.route :as route]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.data.json :as json]
            [clj-http.client :as client]
            [ring.adapter.jetty :as jetty]
            [environ.core :refer [env]]
            [clojure.string :as str]
            [twitter.oauth :as oauth]
            [twitter.api.restful :as rest]))

(defn juxt-query []
  (client/get "https://api.github.com/search/code?q=juxt+in:file+language:clojure&sort=indexed"
                    {:oauth-token "github token"}))

(defn juxt-urls []
  (map #(:html_url %)
       (:items
        (json/read-str
         (:body (juxt-query))
         :key-fn keyword))))

(defn raw [url]
  (str/replace (str/replace url "/blob/" "/")
               "github.com" "raw.githubusercontent.com"))

(defn juxt-sources []
  (map #(slurp (raw %))
       (juxt-urls)))

(def creds (oauth/make-oauth-creds
            "API key"
            "API secret key"
            "Access token"
            "Access token secret"))

(defn tweet [msg]
  (rest/statuses-update :oauth-creds creds :params {:status msg}))

(defn most-recent-juxt []
  (:html_url (first
    (:items
      (json/read-str (:body (juxt-query))
      :key-fn keyword)))))

(defn juxt2 [s]
  (re-find #"\([^(]*\(juxt[^\)]*\)[^\)]*\)" s))

(defn juxt3 [s]
  (re-find #"\([^(]*\([^(]*\(juxt[^\)]*\)[^\)]*\)[^\)]*\)" s))

(defn juxt4 [s]
  (re-find #"\([^(]*\([^(]*\([^(]*\(juxt[^\)]*\)[^\)]*\)[^\)]*\)[^\)]*\)" s))

(defn juxt5 [s]
  (re-find #"\([^(]*\([^(]*\([^(]*\([^(]*\(juxt[^\)]*\)[^\)]*\)[^\)]*\)[^\)]*\)[^\)]*\)" s))

(defn content []
  ((juxt #(str (juxt2 (slurp (raw %))) "\n\n")
         #(str "Source: " %)) (most-recent-juxt)))

(defn splash []
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body (apply str (content))})

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
