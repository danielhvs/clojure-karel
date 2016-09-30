(ns clojure-karel.entities
  (:require [play-clj.core :as p]))

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

(defn karel [x y]
  {:karel (transform  x y 0)})

(def scenario1
  [(karel 0 0)
   {:chip [(place 4 4)(place 4 5)(place 5 6)(place 3 1)
           (place 1 3)
           (place 2 4)]}])

(defn get-karel [scenario]
  (:karel (first scenario)))

(defn get-chips [scenario]
  (:chip (first (rest scenario))))

(defn move [entities]
  (let [karel (first entities)]
    (vector (new-position karel (angle->direction (:angle karel)))
            (rest entities))))

(defn up [screen entities]
  (let [step 0.125]
    (p/add-timer! screen :turn step)
    (p/add-timer! screen :move (* 2 step))
    (p/add-timer! screen :turn (* 3 step))
    (p/add-timer! screen :turn (* 4 step))
    (p/add-timer! screen :turn (* 5 step))
    entities))

(defn down [screen entities]
  (let [step 0.125]
    (p/add-timer! screen :turn step)
    (p/add-timer! screen :turn (* 2 step))
    (p/add-timer! screen :turn (* 3 step))
    (p/add-timer! screen :move (* 4 step))
    (p/add-timer! screen :turn (* 5 step))
    entities))
(defn left [screen entities]
  (let [step 0.125]
    (p/add-timer! screen :turn step)
    (p/add-timer! screen :turn (* 2 step))
    (p/add-timer! screen :move (* 3 step))
    (p/add-timer! screen :turn (* 4 step))
    (p/add-timer! screen :turn (* 5 step))
    entities))

(defn right [screen entities]
  (let [step 0.125]
    (p/add-timer! screen :move step)
    entities))
