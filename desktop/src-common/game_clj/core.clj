(ns game-clj.core
  (:require [play-clj.core :refer :all]
            [play-clj.ui :refer :all]
            [play-clj.g2d :refer :all]
            [play-clj.g2d-physics :refer :all]
            [play-clj.math :refer :all])
  (:import [com.badlogic.gdx.physics.box2d Filter]))

(def ^:const pixels-per-tile 32)
(def x0 (/ 100 pixels-per-tile))
(def y0 x0)

(defn create-part-entity!
  [screen]
  (let [part (texture "circle32.png")
        width (/ (texture! part :get-region-width) pixels-per-tile)
        height (/ (texture! part :get-region-height) pixels-per-tile)]
    (assoc part :width width
                :height height)))

(defn create-ball-entity!
  [screen]
  (let [ball (texture "head.png")
        width (/ (texture! ball :get-region-width) pixels-per-tile)
        height (/ (texture! ball :get-region-height) pixels-per-tile)]
    (assoc ball :width width
                :height height)))

(defn create-rect-entity!
  [screen block width height]
  (assoc block :width width
               :height height))

(defn update-screen!
  [screen entities]
  entities)

(defscreen main-screen
  :on-show
  (fn [screen entities]
    (let [screen (update! screen
                          :camera (orthographic)
                          :renderer (stage)
                          :world (box-2d 0 0))
          game-w (/ (game :width) pixels-per-tile)
          game-h (/ (game :height) pixels-per-tile)
          floor-h (/ 1 pixels-per-tile)
          block-w (/ 100 pixels-per-tile)
          block-h (/ 30 pixels-per-tile)
          block (shape :line
                       :set-color (color :yellow)
                       :rect 0 0 block-w block-h)
          block-cols (int (/ game-w block-w))
          block-rows (int (/ game-h 2 block-h))
          ball (create-ball-entity! screen)]
      ; set the screen width in tiles
      (width! screen game-w)
      ; return the entities
      [(assoc ball :ball? true)]))

  :on-render
  (fn [screen entities]
    (clear!)
    (->> entities
         (step! screen)
         (render! screen)
         (update-screen! screen)))


  :on-key-down
  (fn [screen entities]
    (let [x (cond (key-pressed? :a) -20 (key-pressed? :d) 20 :else 0)
          y (cond (key-pressed? :s) -20 (key-pressed? :w) 20 :else 0)]
        entities))

  :on-begin-contact
  (fn [screen entities]
    (when-let [entity (first-entity screen entities)]
      (cond
        (:floor? entity) entities))))

(defgame game-clj-game
  :on-create
  (fn [this]
    (set-screen! this main-screen)))
