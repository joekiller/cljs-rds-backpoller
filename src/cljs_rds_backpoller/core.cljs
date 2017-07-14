(ns cljs-rds-backpoller.core
  (:require [cljs-lambda.context :as ctx]
            [cljs-lambda.macros :refer-macros [deflambda]]
            [cljs.reader :refer [read-string]]
            [cljs.nodejs :as nodejs]
            [promesa.core :as p])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(def process (nodejs/require "process"))
(def Pool (nodejs/require "pg-pool"))
(def fs (nodejs/require "fs"))

(defn db-config []
  (clj->js
    {"user" (or (aget (.-env process) "PG_USER") "pguser")
     "database" (or (aget (.-env process) "PG_DATABASE") "poller")
     "password" (or (aget (.-env process) "PG_PASSWORD") "pgpassword")
     "host" (or (aget (.-env process) "PG_HOST") "localhost")
     "port" (read-string (or (aget (.-env process) "PG_PORT") "5432"))
     ;; max number of clients in the pool
     "max" 10
     ;; how long a client is allowed to remain idle before being closed
     "idleTimeoutMillis" 60000000}))

(def pool (Pool. (db-config)))

(def first-query "SELECT * FROM pg_catalog.pg_tables;")

(defn query [query]
  (prn "about to query: " query)
  (prn (js->clj (db-config)))
  (-> (.query pool query)
      (p/then #(prn (js->clj %)))
      (p/timeout 10000)
      (p/catch #(do (prn "error as expected..." %)
                    "caught error"))))

(deflambda
  work-magic
  [event ctx]
  (-> (query first-query)
      (p/then (fn [results]
                (prn results)
                (prn "done.")
                {:status "success"
                 :message "yay"}))
      (p/catch (fn [err]
                 (prn err)
                 {:status "error"
                  :message "boo"}))))
