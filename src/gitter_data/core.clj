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
        hide-global-loader (prompt/display-loader prompt)]
    (try+
      (let [rooms (api/my-repo-rooms)]
        (hide-global-loader)
        (let
          [selected-rooms (prompt/process-checkboxes prompt (map #(hash-map :label (:name %) :value %) rooms))]
          (do
            (prompt/stop prompt)
            (if (= 0 (count selected-rooms))
              (do (println "No rooms selected. Bye-bye!") (System/exit 0))
              (dorun
               (map
                ; TODO: use chan and display progress from here in prompt with loaders and stuff
                  (fn [room]
                    (let [messages (api/most-room-messages (:id room) (:name room))]
                      (dump/exec room messages)))
                selected-rooms))))))
      (catch [:message :unathorized] _
        (do
          (prompt/stop prompt)
          (println "Unathorized. Either missing or bad :gitter-token (env variable/profiles.clj)")
          (System/exit 1))))))
