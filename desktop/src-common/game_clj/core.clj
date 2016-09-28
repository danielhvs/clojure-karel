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

(defn place [x y]
  {:x x :y y})

(defn karel [x y]
  {:karel (place x y)})

(def scenario1
  [(karel 0 0)
   {:chip [(place 3 1)
           (place 1 3) 
           (place 2 4)]}])

(defn get-karel [scenario]
  (:karel (first scenario)))

(defn get-chips [scenario]
  (:chip (first (rest scenario))))

(defn create-entity! [create-fn! screen x y]
  (create-fn! screen x y))

(defn create-part-entity!
  [screen x y]
  (let [part (texture "circle32.png")
        width (/ (texture! part :get-region-width) pixels-per-tile)
        height (/ (texture! part :get-region-height) pixels-per-tile)]
    (assoc part :width width :x x :y y
                :height height)))

(defn create-ball-entity!
  [screen x y]
  (let [ball (texture "head.png")
        width (/ (texture! ball :get-region-width) pixels-per-tile)
        height (/ (texture! ball :get-region-height) pixels-per-tile)]
    (assoc ball :width width
                :height height
                :x x
                :y y)))

(defn create-part! [data]
  (create-entity! create-part-entity! (:screen data) (:x data) (:y data)))

(defn assoc-screen [screen positions]
    (if (empty? positions)
      positions
      (vector (assoc (first positions) :screen screen)
              (assoc-screen screen (rest positions)))))

(def scenario scenario1)

(defscreen main-screen
  :on-show
  (fn [screen entities]
    (let [screen (update! screen
                          :camera (orthographic)
                          :renderer (stage)
                          :world (box-2d 0 0))
          game-w (/ (game :width) pixels-per-tile)
          game-h (/ (game :height) pixels-per-tile)
          ball (create-entity! create-ball-entity! screen (:x (get-karel scenario))
                                                          (:y (get-karel scenario)))]
      ; set the screen width in tiles
      (width! screen game-w)
      ; return the entities
      [ball (map create-part! (flatten (assoc-screen screen (get-chips scenario))))]))

  :on-render
  (fn [screen entities]
    (clear!)
    (->> entities
         (step! screen)
         (render! screen)))

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
