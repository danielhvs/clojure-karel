(ns clojure-karel.core
  (:require [play-clj.core :refer :all]
            [play-clj.ui :refer :all]
            [play-clj.g2d :refer :all]
            [play-clj.g2d-physics :refer :all]
            [play-clj.math :refer :all]
            [clojure-karel.entities :as k]))

(def pixels-per-tile 32)

(defn grab [screen entities]
  (k/grab screen 1)
 entities)
(defn leave [screen entities]
  (k/leave screen 1)
 entities)
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

(defn create-entity!
  [png data]
  (let [part (texture png)
        width 1
        height 1]
    (assoc (conj part data) :width width :height height)))

(defn create-game-entity! [scenario key-code screen png]
  (->> (filter key-code scenario)
       (map #(assoc % :screen screen))
       (map #(create-entity! png %))))

(defn create-scenario [scenario screen]
  [(create-game-entity! scenario :chip? screen "circle32.png")
   (create-game-entity! scenario :karel? screen "head.png")
   (create-game-entity! scenario :goal? screen "square.png")
   (create-game-entity! scenario :wall? screen "box32.png")])

(defscreen main-screen
  :on-show
  (fn [screen entities]
    (let [screen (update! screen
                          :camera (orthographic)
                          :renderer (stage)
                          :world (box-2d 0 0))
          game-w (/ (game :width) pixels-per-tile)
          game-h (/ (game :height) pixels-per-tile)]

      ; set the screen width in tiles
      (width! screen game-w)
      ; return the entities
      (create-scenario k/scenario1 screen)))

  :on-render
  (fn [screen entities]
    (clear!)
    (render! screen entities))

  :on-key-down
  (fn [screen entities]
    (cond (key-pressed? :up) (move-up screen entities)
          (key-pressed? :down) (move-down screen entities)
          (key-pressed? :left) (move-left screen entities)
          (key-pressed? :right) (move-right screen  entities)
          (key-pressed? :g) (grab screen entities)
          (key-pressed? :d) (leave screen entities)
          (key-pressed? :q) (create-scenario k/scenario1 screen)
          (key-pressed? :a) (k/solution1 screen entities)
          (key-pressed? :w) (create-scenario k/scenario2 screen)
          (key-pressed? :s) (k/solution2 screen entities)
          :else entities))

  :on-timer
  (fn [screen entities]
    (case (:id screen)
      :move (k/move entities)
      :turn (k/turn entities)
      :pick (k/pick entities)
      :drop (k/_drop entities)
      nil)))

(defgame clj-karel-game
  :on-create
  (fn [this]
    (set-screen! this main-screen)))

(defscreen blank-screen
  :on-render
  (fn [screen entities]
    (clear!)))

(set-screen-wrapper! (fn [screen screen-fn]
                       (try (screen-fn)
                         (catch Exception e
                           (.printStackTrace e)
                           (set-screen! clj-karel-game blank-screen)))))


; (in-ns 'clojure-karel.core)
; (on-gl (set-screen! clj-karel-game main-screen))
; (use 'play-clj.repl)
; (e main-screen)
; (e! wall? main-screen :x 3 :y 4)
; lein nightlight --port 4000

