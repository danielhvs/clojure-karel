(ns clojure-karel.core
  (:require [play-clj.core :refer :all]
            [play-clj.ui :refer :all]
            [play-clj.g2d :refer :all]
            [play-clj.g2d-physics :refer :all]
            [play-clj.math :refer :all]
            [clojure-karel.entities :as k]))

(def ^:const pixels-per-tile 32)

(defn move-up [screen entities]
  (k/up screen 1)
 entities)
(defn move-down [screen entities]
   (k/down screen 1)
  entities)
(defn move-left [screen entities]
  (k/left screen 1)
  entities)
(defn move-right [screen entities]
  (k/right screen 1)
  entities)
(defn create-texture-entity!
  [png screen x y angle]
  (let [part (texture png)
        width 1
        height 1]
    (assoc part :width width :x x :y y :angle angle
                :height height)))

(defn create-entity! [png screen x y angle]
  (create-texture-entity! png screen x y angle))

(defn create-chip! [data]
  (create-entity! "circle32.png" (:screen data) (:x data) (:y data) (:angle data)))

(defn create-goals! [data]
  (create-entity! "square.png" (:screen data) (:x data) (:y data) (:angle data)))

(defn assoc-screen [screen positions]
    (if (empty? positions)
      positions
      (vector (assoc (first positions) :screen screen)
              (assoc-screen screen (rest positions)))))

(def scenario k/scenario1)

(defscreen main-screen
  :on-show
  (fn [screen entities]
    (let [screen (update! screen
                          :camera (orthographic)
                          :renderer (stage)
                          :world (box-2d 0 0))
          game-w (/ (game :width) pixels-per-tile)
          game-h (/ (game :height) pixels-per-tile)
          karel (create-entity! "head.png" screen (:x (k/get-karel scenario))
                                                  (:y (k/get-karel scenario))
                                                  (:angle (k/get-karel scenario)))]
      ; set the screen width in tiles
      (width! screen game-w)
      ; return the entities
      [(assoc karel :karel? true)
       (map create-chip!
         (->> (k/get-c scenario)
              (assoc-screen screen)
              (flatten)))
       (map create-goals!
         (->> (k/get-goals scenario)
              (assoc-screen screen)
              (flatten)))]))

  :on-render
  (fn [screen entities]
    (clear!)
    (->> entities
         (render! screen)))

  :on-key-down
  (fn [screen entities]
    (cond (key-pressed? :up) (move-up screen entities)
          (key-pressed? :down) (move-down screen entities)
          (key-pressed? :left) (move-left screen entities)
          (key-pressed? :right) (move-right screen  entities)
          (key-pressed? :s) (k/solution screen entities)
          :else entities))

  :on-timer
  (fn [screen entities]
    (case (:id screen)
      :move (k/move entities)
      :turn (k/turn entities)
      nil)))

(defgame game-clj-game
  :on-create
  (fn [this]
    (set-screen! this main-screen)))
