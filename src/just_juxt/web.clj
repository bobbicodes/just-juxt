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
            [twitter.api.restful :as rest]
            [next.jdbc :as jdbc]
            [hiccup.page :as page]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

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

(def db {:dbtype "postgresql" :dbname "juxts"})

(def ds (jdbc/get-datasource db))

;(jdbc/execute! ds ["
;create table results (
;  id serial primary key,
;  url varchar(255) not null
;)"])

(defn insert [juxt-url]
  (jdbc/execute! ds [(str "
    insert into results(url)
    values ('" juxt-url "');")]))

(defn get-all-juxts []
  (jdbc/execute! ds ["select * from results"]))

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
  (re-find #"\([^(]*\([^(]*juxt[^\)]*\)[^\)]*\)" s))

(defn juxt3 [s]
  (re-find #"\([^(]*\([^(]*\([^(]*juxt[^\)]*\)[^\)]*\)[^\)]*\)" s))

(defn juxt4 [s]
  (re-find #"\([^(]*\([^(]*\([^(]*\([^(]*juxt[^\)]*\)[^\)]*\)[^\)]*\)[^\)]*\)" s))

(defn juxt5 [s]
  (re-find #"\([^(]*\([^(]*\([^(]*\([^(]*\([^(]*juxt[^\)]*\)[^\)]*\)[^\)]*\)[^\)]*\)[^\)]*\)" s))

(defn juxt6 [s]
  (re-find #"\([^(]*\([^(]*\([^(]*\([^(]*\([^(]*\([^(]*juxt[^\)]*\)[^\)]*\)[^\)]*\)[^\)]*\)[^\)]*\)[^\)]*\)" s))

(defn content []
  ((juxt #(str (juxt2 (slurp (raw %))) "\n\n")
         #(str "Source: " %)) (most-recent-juxt)))

(defn gen-page-head [title]
   [:head
    [:title (str "Juxts: " title)]
    (page/include-css "/css/styles.css")])

(defn all-juxts-page
  []
  (let [all-juxts (get-all-juxts)]
    (page/html5
     (gen-page-head "All juxts in the db")
     [:h1 "All juxts"]
     [:table
      [:tr [:th "url"]]
      (for [url all-juxts]
        [:tr [:td (:results/url url)]])])))

(defn splash []
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body (apply str (content))})

(defroutes app-routes
  (GET "/" []
       (all-juxts-page))
  (ANY "*" []
    (route/resources "/")
    (route/not-found (slurp (io/resource "404.html")))))

(def app
  (wrap-defaults app-routes site-defaults))

(defn -main [& [port]]
  (let [port (Integer. (or port (env :port) 5000))]
    (jetty/run-jetty (site #'app) {:port port :join? false})))
