(ns clojure-karel.entities
  (:require [play-clj.core :as p]))

(def step 0.05)

(defn println-wrapper [f entities]
  (println f entities)
  (f entities))

(defn in? [collection element]
  (some #(= % element) collection))

(defn turn
  ([entity degrees] (assoc entity :angle (mod (+ (:angle entity) degrees) 360)))
  ([entities] (map #(if (:moving? %) (turn % 90) %) entities)))

(defn angle->direction [angle]
  (cond (zero? angle) {:x 1 :y 0}
        (= angle 90) {:x 0 :y 1}
        (= angle 180) {:x -1 :y 0}
        (= angle 270) {:x 0 :y -1}))

(defn new-position [entity offset]
  (assoc entity :x (+ (:x entity) (:x offset)) :y (+ (:y entity) (:y offset))))

(defn make-entity [x y filter-code]
  {:x x :y y :z 0 :angle 0 filter-code true})

(defn make-wall [x y]
  (make-entity x y :wall?))
(defn make-chip [x y]
  (make-entity x y :chip?))
(defn make-goal [x y]
  (make-entity x y :goal?))

(def walls-scenario1
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
  (flatten [{:x 1 :y 1 :z 0 :angle 0 :karel? true :moving? true}
            {:x 2 :y 1 :z 0 :angle 0 :chip? true}
            {:x 6 :y 2 :z 0 :goal? true}
            walls-scenario1]))

(def walls-scenario2
  (let [bottom (for [xs (take 11 (range))] (conj (make-wall xs 0)))
        right (for [ys (take 5 (range))] (conj (make-wall 10 ys)))
        up (for [xs (take 11 (range))] (conj (make-wall xs 4)))
        left (for [ys (take 5 (range))] (conj (make-wall 0 ys)))]
    (-> bottom
        (conj right)
        (conj up)
        (conj left)
        (conj (for [xs (->> (range)
                            (take 10)
                            (filter odd?))]
                (conj (make-wall xs 1)))))))

(def scenario2
  (flatten [{:x 1 :y 2 :z 0 :angle 0 :karel? true :moving? true}
            (for [xs (->> (range)
                          (take 9)
                          (filter odd?))]
              (conj (make-chip xs 3)))
            (for [xs (->> (range)
                          (take 10)
                          (filter even?)
                          (remove zero?))]
              (conj (make-goal xs 1)))
            walls-scenario2]))

(defn move [entities]
  (let [karel (first (filter :karel? entities))
        walls (filter :wall? entities)
        karel-new-pos (new-position karel (angle->direction (:angle karel)))
        karel-pos (select-keys karel-new-pos [:x :y])
        walls-pos (map #(select-keys % [:x :y]) walls)]
    (map #(if (:moving? %)
              (let [new-pos (new-position % (angle->direction (:angle %)))
                    pos (select-keys new-pos [:x :y])]
                (if (in? walls-pos pos) % new-pos))
              %)
         entities)))

(defn pick [entities]
  (let [karel (first (filter :karel? entities))
        karel-pos (select-keys karel [:x :y])]
    (sort-by :z <
      (map #(if (= karel-pos (select-keys % [:x :y]))
                (if (:chip? %) (assoc % :z 1 :moving? true) %)
                %)
           entities))))

(defn _drop [entities]
  (let [karel (first (filter :karel? entities))
        karel-pos (select-keys karel [:x :y])]
    (sort-by :z <
      (map #(if (= karel-pos (select-keys % [:x :y]))
                (if (:chip? %) (assoc % :z -1 :moving? false) %)
                %)
           entities))))

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

(defn leave [screen t]
  (p/add-timer! screen :drop (* t step))
  (inc t))

(defn solution1 [screen entities]
  (->> (right screen 1)
       (grab screen)
       (right screen)
       (up screen)
       (right screen)
       (right screen)
       (right screen)
       (leave screen))
  entities)

(defn iterate-solution2
 ([screen] (iterate-solution2 screen 1))
 ([screen t]
   (->> (up screen t)
        (grab screen)
        (right screen)
        (down screen)
        (down screen)
        (leave screen)
        (up screen)
        (right screen))))

(defn solution2 [screen entities]
  (->> (iterate-solution2 screen)
       (iterate-solution2 screen)
       (iterate-solution2 screen)
       (iterate-solution2 screen))
  entities)
