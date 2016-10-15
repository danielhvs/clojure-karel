(ns clojure-karel.entities
  (:require [play-clj.core :as p]))

(def step 0.05)

(defn println-wrapper [f entities]
  (println f entities)
  (f entities))

(defn in? [collection element]
  (some #(= % element) collection))

(defn in-first [collection element]
  (when (seq collection)
    (let [chip (first collection)
          element-pos (select-keys element [:x :y])
          chip-pos (select-keys chip [:x :y])]
      (if (= chip-pos element-pos)
          chip
          (in-first (rest collection) element)))))

(defn turn
  ([entity degrees] (assoc entity :angle (mod (+ (:angle entity) degrees) 360)))
  ([entities] (let [karel (first entities)]
                (vector (turn karel 90) (rest entities)))))

(defn angle->direction [angle]
  (cond (zero? angle) {:x 1 :y 0}
        (= angle 90) {:x 0 :y 1}
        (= angle 180) {:x -1 :y 0}
        (= angle 270) {:x 0 :y -1}))

(defn new-position [mapxy offset]
  (assoc mapxy :x (+ (:x mapxy) (:x offset)) :y (+ (:y mapxy) (:y offset))))

(defn make-wall [x y]
  {:x x :y y :wall? true})

(defn make-walls-scenario1 []
  (let [bottom (for [xs (take 7 (range))] (conj (make-wall xs 0)))
        right (for [ys (take 4 (range))] (conj (make-wall 7 ys)))
        up (for [xs (take 7 (range))] (conj (make-wall xs 3)))
        left (for [ys [1 2]] (conj (make-wall 0 ys)))]
    (-> bottom
        (conj right)
        (conj up)
        (conj left)
        (conj (make-wall 4 1))
        (conj (make-wall 5 1))
        (conj (make-wall 6 1)))))

(def scenario1
  (flatten [{:x 1 :y 1 :angle 0 :karel? true}
            {:x 2 :y 1 :chip? true}
            {:x 6 :y 2 :goal? true}
            (make-walls-scenario1)]))

(defn get-key [chip the-key]
  (if (empty? chip)
      chip
      (vector (the-key (first chip)) (get-key (rest chip) the-key))))

(defn get-karel [scenario]
  (first scenario))

(defn vector-of-maps [the-map the-key]
  (-> the-map
      (get-key the-key)
      (flatten)))

(defn get-chips
  ([scenario] (:chip scenario))
  ([scenario the-key] (vector-of-maps (get-chips scenario) the-key)))

(defn get-walls [scenario]
  (:wall scenario))

(defn move [entities]
  (let [karel (first entities)
        walls (filter :wall? entities)
        karel-new-pos (new-position karel (angle->direction (:angle karel)))
        karel-pos (select-keys karel-new-pos [:x :y])
        walls-pos (map #(select-keys % [:x :y]) walls)]
    (if (in? walls-pos karel-pos)
        entities
        (flatten (vector karel-new-pos (rest entities))))))

(defn pick [entities]
  (let [karel (first entities)
        chips (filter :chip? entities)
        karel-pos (select-keys karel [:x :y])]
      (map #(if (= karel-pos (select-keys % [:x :y]))
                (assoc % :z 1)
                %)
           entities)))

(defn up [screen t]
    (p/add-timer! screen :turn (* t step))
    (p/add-timer! screen :move (* (inc t) step))
    (p/add-timer! screen :turn (* (+ 2 t) step))
    (p/add-timer! screen :turn (* (+ 3 t) step))
    (p/add-timer! screen :turn (* (+ 4 t) step))
  (+ 5 t))

(defn down [screen t]
    (p/add-timer! screen :turn (* t step))
    (p/add-timer! screen :turn (* (inc t) step))
    (p/add-timer! screen :turn (* (+ 2 t) step))
    (p/add-timer! screen :move (* (+ 3 t) step))
    (p/add-timer! screen :turn (* (+ 4 t) step))
  (+ 5 t))
(defn left [screen t]
    (p/add-timer! screen :turn (* t step))
    (p/add-timer! screen :turn (* (inc t) step))
    (p/add-timer! screen :move (* (+ 2 t) step))
    (p/add-timer! screen :turn (* (+ 3 t) step))
    (p/add-timer! screen :turn (* (+ 4 t) step))
  (+ 5 t))

(defn right [screen t]
  (p/add-timer! screen :move (* t step))
  (inc t))

(defn grab [screen t]
  (p/add-timer! screen :pick (* t step))
  (inc t))

(defn solution1 [screen entities]
  (->> (right screen 1)
       (grab screen)
       (right screen)
       (up screen)
       (right screen)
       (right screen)
       (right screen))
  entities)
