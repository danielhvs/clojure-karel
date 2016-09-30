(ns clojure-karel.core
  (:require [play-clj.core :refer :all]
            [play-clj.ui :refer :all]
            [play-clj.g2d :refer :all]
            [play-clj.g2d-physics :refer :all]
            [play-clj.math :refer :all]
            [clojure-karel.entities :as k]))

(def ^:const pixels-per-tile 32)

(defn create-texture-entity!
  [png screen x y angle]
  (let [part (texture png)
        width (/ (texture! part :get-region-width) pixels-per-tile)
        height (/ (texture! part :get-region-height) pixels-per-tile)]
    (assoc part :width width :x x :y y :angle angle
                :height height)))

(defn create-entity! [png screen x y angle]
  (create-texture-entity! png screen x y angle))

(defn create-part! [data]
  (create-entity! "circle32.png" (:screen data) (:x data) (:y data) (:angle data)))

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
       (map create-part! (flatten (assoc-screen screen (k/get-chips scenario))))]))

  :on-render
  (fn [screen entities]
    (clear!)
    (->> entities
         (render! screen)))

  :on-key-down
  (fn [screen entities]
    (cond (key-pressed? :up) (k/up entities)
          (key-pressed? :down) (k/down entities)
          (key-pressed? :left) (k/left entities)
          (key-pressed? :right) (k/right entities)
          :else entities)))


(defgame game-clj-game
  :on-create
  (fn [this]
    (set-screen! this main-screen)))
