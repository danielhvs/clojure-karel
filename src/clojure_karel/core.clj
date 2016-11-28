(ns clojure-karel.core
  (:require [play-clj.core :refer :all]
            [clojure.pprint :refer :all]
            [play-clj.ui :refer :all]
            [play-clj.g2d :refer :all]
            [play-clj.g2d-physics :refer :all]
            [play-clj.math :refer :all]
            [clojure-karel.entities :as k]))

(def pixels-per-tile 32)
(def step 0.005)
(def queue (atom []))

(defn add-state [state]
  (into [] (flatten (conj @queue state))))

(defn move-up [screen entities]
  (swap! queue add-state (k/_up entities))
  (add-timer! screen :tick step))

(defn move-down [screen entities]
  (swap! queue add-state (k/_down entities))
  (add-timer! screen :tick step))

(defn move-left [screen entities]
  (swap! queue add-state (k/_left entities))
  (add-timer! screen :tick step))

(defn move-right [screen entities]
  (swap! queue add-state (k/_right entities))
  (add-timer! screen :tick step))

(defn grab [screen entities]
  (swap! queue add-state (k/_grab entities))
  (add-timer! screen :tick step))

(defn leave [screen entities]
  (swap! queue add-state (k/_drop entities))
  (add-timer! screen :tick step))

(defn solution1 [screen entities]
  (->> (move-right screen entities)
       (grab screen)
       (move-right screen)
       (move-up screen)
       (move-right screen)
       (move-right screen)
       (move-right screen)
       (leave screen))
  entities)

(defn iterate-solution2
 ([screen entities] (->> (move-up screen entities)
                         (grab screen)
                         (move-down screen)
                         (move-right screen)
                         (move-down screen)
                         (leave screen)
                         (move-up screen)
                         (move-right screen))))

(defn solution2 [screen entities]
  (->> (iterate-solution2 screen entities)
       (iterate-solution2 screen)
       (iterate-solution2 screen)
       (iterate-solution2 screen))
  entities)

(comment 
  (defn iterate-solution3
    ([screen entities] (iterate-solution3 screen entities 1))
    ([screen entities t] (if (karel-find-chip? entities)
                           (let [time (->> (grab screen t) (move-up screen))
                                 next-state (->> (_grab entities) (_up))]
                             (iterate-solution3 screen next-state time))
                           (let [time (->> (move-down screen t))
                                 next-state (->> (_down entities))]
                             (if (= next-state entities)
                               time
                               (iterate-solution3 screen next-state time)))))))


(comment
  (defn solution3 [screen entities]
    (let [x (_right entities)
          states (take 8 (iterate next-state3 x))]
      (->> (move-right screen 1)
           (iterate-solution3 screen (nth states 0))
           (move-right screen)
           (iterate-solution3 screen (nth states 1))
           (move-right screen)
           (iterate-solution3 screen (nth states 2))
           (move-right screen)
           (iterate-solution3 screen (nth states 3))
           (move-right screen)
           (iterate-solution3 screen (nth states 4))
           (move-right screen)
           (iterate-solution3 screen (nth states 5))
           (move-right screen)
           (iterate-solution3 screen (nth states 6))
           (move-right screen)
           (iterate-solution3 screen (nth states 7))
           (leave screen))
      entities)))

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
          (key-pressed? :h) (leave screen entities)
          (key-pressed? :q) (create-scenario k/scenario1 screen)
          (key-pressed? :a) (solution1 screen entities)
          (key-pressed? :w) (create-scenario k/scenario2 screen)
          (key-pressed? :s) (solution2 screen entities)
          (key-pressed? :e) (create-scenario (k/scenario3) screen)
          :else entities))

  :on-timer
  (fn [screen entities]
    (case (:id screen)
      :tick (when (complement (empty? @queue))
              (let [state (first @queue)]
                (swap! queue drop 1)
                (add-timer! screen :tick step)
                state))
      entities)))

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
