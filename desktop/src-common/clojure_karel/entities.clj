(ns clojure-karel.entities)

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

(defn up [entities]
  (->> (turn entities)
       (move)
       (turn)
       (turn)
       (turn)))
(defn down [entities]
  (->> (turn entities)
       (turn)
       (turn)
       (move)
       (turn)))
(defn left [entities]
  (->> (turn entities)
       (turn)
       (move)
       (turn)
       (turn)))
(defn right [entities]
  (move entities))
