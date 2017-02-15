(ns gitter-data.prompt
  (:require [lanterna.screen :as s])
  (:gen-class))

(defn init []
  (let [scr (s/get-screen :text)]
    (s/start scr)
    scr))

(defn stop [scr] (s/stop scr))

(defn loader-char
  [step]
  (let [char-map ["⠋" "⠙" "⠹" "⠸" "⠼" "⠴" "⠦" "⠧" "⠇" "⠏"]
        index-in-bounds (mod step (count char-map))]
    (nth char-map index-in-bounds)))

(defn display-loader!
  ([scr] (display-loader! scr 0 0))
  ([scr x y]
   (let [loading (atom true)
         stop-loading! (fn [] (reset! loading false))]
     (s/put-string scr x y "Loading")
     (s/redraw scr)
     (.start (Thread.
               (fn []
                 (loop [cycle 0]
                   (do
                     (s/put-string scr x y (str "Loading " (loader-char cycle)))
                     (s/redraw scr)
                     (Thread/sleep 80)
                     (if @loading (recur (+ cycle 1))))))))
     stop-loading!)))

(defn get-single-checkbox
  [selected]
  (fn [index {label :label}]
    (let [is-selected (get selected index)
          checkbox (str "[" (if is-selected "X" " ") "]")]
      (str checkbox " " label))))


(defn draw-single-checkbox!
  [screen y with-label]
  (s/put-string screen 0 y with-label))

(defn put-on-screen-checkboxes!
  [screen checkboxes selected start-offset from-index to-take]
  (let [checkboxes-strs (map-indexed (get-single-checkbox selected) checkboxes)]
    (doall
     (map-indexed #(draw-single-checkbox! screen (+ start-offset %1) %2)
        (take to-take (drop from-index checkboxes-strs))))))


(defn put-on-screen-info-bar!
  [screen]
  (s/put-string screen 0 0 "Press ↓↑ to move cursor and scroll list. ESC or q to quit"))

(defn put-on-screen-scroll-info!
  [screen bottom-y from count total]
  (s/put-string screen 0 (- bottom-y 1) (str "Showing: " from "-" (+ from count) " of " total)))

(defn toggle-selection!
  [selected index]
  (let [value (@selected index)]
    (swap! selected assoc index (not value))))

(defn get-new-row [current-row number-of-lines direction]
  (if (= direction :up)
    (max 0 (- current-row 1))
    (min (- number-of-lines 1) (+ current-row 1))))

(defn position-cursor!
  [screen top-padding current-row current-top]
  (s/move-cursor screen 1 (+ top-padding (- current-row current-top))))

(defn resulting-set
  [checkboxes selected]
  (let [selected-values @selected]
    (->> checkboxes
         (map-indexed #(if (selected-values %1) (:value %2) nil))
         (filter identity))))
(defn process-checkboxes!
  [screen checkboxes]
  (let [max-index (count checkboxes)
        selected (atom (reduce #(assoc %1 %2 false) {} (range max-index)))
        ; s/get-screen returns [width height]
        available-height (last (s/get-size screen))
        visible-count (- available-height 2)
        top-padding 1]

    (loop [current-row 0 current-top 0]
      (do
       (s/clear screen)
       (put-on-screen-info-bar! screen)
       (put-on-screen-checkboxes! screen checkboxes @selected top-padding current-top visible-count)
       (put-on-screen-scroll-info! screen available-height current-top visible-count max-index)
       (position-cursor! screen top-padding current-row current-top)
       (s/redraw screen)
       (let [keypressed (s/get-key-blocking screen)]
         (case keypressed
          \space (do
                   (toggle-selection! selected current-row)
                   (recur current-row current-top))
          :down (let [new-row (get-new-row current-row max-index :down)]
                  (recur new-row (min (- max-index visible-count) (+ current-top 1))))
          :up (let [new-row (get-new-row current-row max-index :up)]
                (recur new-row (if (> current-top new-row)
                                 (max 0 (- current-top 1))
                                 current-top)))
          \q (s/stop screen)
          :escape (s/stop screen)
          :enter (resulting-set checkboxes selected)
          (recur current-row current-top)))))))
