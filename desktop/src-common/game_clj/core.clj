(ns game-clj.core
  (:require [play-clj.core :refer :all]
            [play-clj.ui :refer :all]
            [play-clj.g2d :refer :all]
            [play-clj.g2d-physics :refer :all]
            [play-clj.math :refer :all])
  (:import [com.badlogic.gdx.physics.box2d Filter]))

(def ^:const pixels-per-tile 32)

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

(defn create-texture-entity!
  [png screen x y]
  (let [part (texture png)
        width (/ (texture! part :get-region-width) pixels-per-tile)
        height (/ (texture! part :get-region-height) pixels-per-tile)]
    (assoc part :width width :x x :y y
                :height height)))

(defn create-entity! [png screen x y]
  (create-texture-entity! png screen x y))

(defn create-part! [data]
  (create-entity! "circle32.png" (:screen data) (:x data) (:y data)))

(defn assoc-screen [screen positions]
    (if (empty? positions)
      positions
      (vector (assoc (first positions) :screen screen)
              (assoc-screen screen (rest positions)))))

(defn move [entities command]
  (let [karel (first entities)]
    (cond (= command :move) (vector (assoc karel :x (+ 1 (:x karel)))
                                    (rest entities))
          (= command :turn) (vector (assoc karel :angle 90)
                                    (rest entities)))))

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
          ball (create-entity! "head.png" screen (:x (get-karel scenario))
                                                 (:y (get-karel scenario)))]
      ; set the screen width in tiles
      (width! screen game-w)
      ; return the entities
      [(assoc ball :karel? true)
       (map create-part! (flatten (assoc-screen screen (get-chips scenario))))]))

  :on-render
  (fn [screen entities]
    (clear!)
    (->> entities
         (step! screen)
         (render! screen)))

  :on-key-down
  (fn [screen entities]
    (let [command (cond (key-pressed? :m) :move
                        (key-pressed? :t) :turn
                        :else "")]
        (move entities command))))

(defgame game-clj-game
  :on-create
  (fn [this]
    (set-screen! this main-screen)))
