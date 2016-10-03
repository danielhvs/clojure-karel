(ns clojure-karel.entities
  (:require [play-clj.core :as p]))

(def step 0.05)

(defn place [x y]
  {:x x :y y :angle 0})

(defn transform [x y angle]
  (assoc (place x y) :angle angle))

(defn turn
  ([entity degrees] (assoc entity :angle (mod (+ (:angle entity) degrees) 360)))
  ([entities] (let [karel (first entities)]
                (vector (turn karel 90) (rest entities)))))

(defn angle->direction [angle]
  (cond (= angle 0) {:x 1 :y 0}
        (= angle 90) {:x 0 :y 1}
        (= angle 180) {:x -1 :y 0}
        (= angle 270) {:x 0 :y -1}))

(defn new-position [mapxy offset]
  (assoc mapxy :x (+ (:x mapxy) (:x offset)) :y (+ (:y mapxy) (:y offset))))

(def scenario1
  {:karel (transform  1 1 0)
   :chip [{:position (place 2 1) :goal (place 6 2)}]
   :wall [(place 4 1)
          (place 5 1)
          (place 6 1)]})

(defn get-key [chip the-key]
  (if (empty? chip)
      chip
      (vector (the-key (first chip)) (get-key (rest chip) the-key))))

(defn get-karel [scenario]
  (:karel scenario))
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
  (let [karel (first entities)]
    (vector (new-position karel (angle->direction (:angle karel)))
            (rest entities))))

(defn up [screen t]
    (p/add-timer! screen :turn (* t step))
    (p/add-timer! screen :move (* (+ 1 t) step))
    (p/add-timer! screen :turn (* (+ 2 t) step))
    (p/add-timer! screen :turn (* (+ 3 t) step))
    (p/add-timer! screen :turn (* (+ 4 t) step))
  (+ 5 t))

(defn down [screen t]
    (p/add-timer! screen :turn (* t step))
    (p/add-timer! screen :turn (* (+ 1 t) step))
    (p/add-timer! screen :turn (* (+ 2 t) step))
    (p/add-timer! screen :move (* (+ 3 t) step))
    (p/add-timer! screen :turn (* (+ 4 t) step))
  (+ 5 t))
(defn left [screen t]
    (p/add-timer! screen :turn (* t step))
    (p/add-timer! screen :turn (* (+ 1 t) step))
    (p/add-timer! screen :move (* (+ 2 t) step))
    (p/add-timer! screen :turn (* (+ 3 t) step))
    (p/add-timer! screen :turn (* (+ 4 t) step))
  (+ 5 t))

(defn right [screen t]
    (p/add-timer! screen :move (* t step))
  (+ 1 t))

(defn solution1 [screen entities]
  (->> (right screen 1)
       (right screen)
       (up screen)
       (right screen)
       (right screen)
       (right screen))
  entities)
