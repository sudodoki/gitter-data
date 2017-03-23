(ns gitter-data.dump-collection
  (:require
    [clojure.data.json :as json]
    [clojure.java.io :as io])
  (:refer clojure.java.io :only [writer make-parents])
  (:gen-class))

(defn write-json [path jsoned]
  (make-parents path)
  (with-open [file (writer path :append false)]
    (json/write jsoned file)))

(defn dashify-slash [str] (clojure.string/replace str \/ \-))
(defn exec! [{room-name :name :as room} messages]
  (let [date-str (.format (java.text.SimpleDateFormat. "yyyy-MM-dd") (java.util.Date.))]
    (println (str "Dumping into dump/" (dashify-slash room-name) "/" date-str "-meta.json"))
    (write-json (str "./dump/" (dashify-slash room-name) "/" date-str "-meta.json") room)
    (println (str "Dumping into dump/" (dashify-slash room-name) "/" date-str "-messages.json"))
    (write-json (str "./dump/" (dashify-slash room-name) "/" date-str "-messages.json") messages)))
