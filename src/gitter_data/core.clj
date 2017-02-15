(ns gitter-data.core
  (:require
   [gitter-data.gitter-api :as api]
   [gitter-data.dump-collection :as dump]
   [gitter-data.prompt :as prompt])
  (:use [slingshot.slingshot :only [try+]])
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [prompt (prompt/init)
        hide-global-loader! (prompt/display-loader! prompt)]
    (try+
      (let [rooms (api/my-repo-rooms)]
        (hide-global-loader!)
        (let [labeled-rooms (for [room rooms]
                              {:label (:name room)
                               :value room})
              selected-rooms (prompt/process-checkboxes! prompt labeled-rooms)]
          (prompt/stop prompt)
          (when (= 0 (count selected-rooms))
            (println "No rooms selected. Bye-bye!")
            (System/exit 0))
          (doseq [{:keys [id name] :as room} selected-rooms
                  :let [messages (api/most-room-messages id name)]]
            (dump/exec! room messages))))
      (catch [:message :unathorized] _
        (do
          (prompt/stop prompt)
          (println "Unathorized. Either missing or bad :gitter-token (env variable/profiles.clj)")
          (System/exit 1))))))
