(ns gitter-data.dump-collection
  (:require [clojure.data.csv :as csv] [clojure.java.io :as io])
  (:refer clojure.java.io :only [writer make-parents])
  (:gen-class))

(defonce room-columns [:tags :name :public :topic :userCount :id :url :uri :avatarUrl :githubType])
(defonce msg-columns [:mentions :urls :meta :sent :id :readBy :fromUser :text])
(defn write-csv [path columns row-data]
  (let [headers (map name columns)
        rows (map #(map % columns) row-data)]
    (do
      (make-parents path)
      (with-open [file (writer path :append false)]
        (csv/write-csv file (cons headers rows))))))

(defn dashify-slash [str] (clojure.string/replace str \/ \-))
(defn exec! [{room-name :name :as room} messages]
  (let [date-str (.format (java.text.SimpleDateFormat. "yyyy-MM-dd") (java.util.Date.))]
    (println (str "Dumping into dump/" (dashify-slash room-name) "/" date-str "-meta.csv"))
    (write-csv (str "./dump/" (dashify-slash room-name) "/" date-str "-meta.csv") room-columns [room])
    (println (str "Dumping into dump/" (dashify-slash room-name) "/" date-str "-messages.csv"))
    (write-csv (str "./dump/" (dashify-slash room-name) "/" date-str "-messages.csv") msg-columns messages)))
