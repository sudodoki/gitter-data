; (load "gitter_api")
; (in-ns 'gitter-data.gitter-api)
; (set! *print-length* 10)
(ns gitter-data.gitter-api
  (:require [clj-http.client :as client]
            [environ.core :refer [env]])
  (:use [slingshot.slingshot :only [try+ throw+]])
  (:gen-class))

(def api-host "https://api.gitter.im")
(def token (env :gitter-token))
(defn in?
  "true if coll contains elm"
  [coll elm]
  (some #(= elm %) coll))

; (def is-public #(not (:oneToOne %)))
; (def isnt-group #(not (:groupId %)))

(def is-repo-related?
  (partial in? (list "REPO" "REPO_CHANNEL")))

(defn invoke-api
  ([path]
   (invoke-api path {}))
  ([path query-params]
   (try+
      (:body (client/get (str api-host path) {:oauth-token token, :as :json, :query-params query-params}))
      (catch [:status 401] _ (throw+ {:message :unathorized}))
      ; rate-limit
      (catch [:status 429]
        args
        (let
          [headers (:headers args)
           reset-time (Long/parseLong (get headers "X-RateLimit-Reset"))
           request-time (.getTime (.parse (java.text.SimpleDateFormat. "EEE, dd MMM yyyy HH:mm:ss Z") (get headers "Date")))]
          (do
            (println "Gonna sleep for " (- reset-time request-time) "ms")
            (Thread/sleep (- reset-time request-time))
            (invoke-api path query-params)))))))

(defn me [] (invoke-api "/v1/user/me"))

(defn my-rooms [] (invoke-api "/v1/rooms"))

(defn my-repo-rooms [] (filter (comp is-repo-related? :githubType) (my-rooms)))

(defn room-messages
  ([roomId] (room-messages roomId {:limit 50}))
  ([roomId params] (invoke-api (str "/v1/rooms/" roomId "/chatMessages") params)))

; TODO use channels to indicate progress instead
(defn messages-chunk
  ([roomId total] (messages-chunk roomId total roomId))
  ([roomId total label]
   (fn [index offset]
     (do
      (println (str "Requesting messages for " label " " (format "%2d" (+ 1 index)) "/" total))
      (room-messages roomId {:limit 100 :skip (* offset 100)})))))

(defn most-room-messages
  "Trying to get 5000 messages from single room"
  ([roomId] (most-room-messages roomId roomId))
  ([roomId label]
   (let [total 50]
     (reduce concat (map-indexed (messages-chunk roomId total label) (range total 0 -1))))))
