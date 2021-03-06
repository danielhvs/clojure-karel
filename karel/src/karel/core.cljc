(ns karel.core)

(defn in? [collection element]
  (->> (some #(= % element) collection)
       (some?)))

(defn turn
  ([entity degrees] (assoc entity :angle (mod (+ (:angle entity) degrees) 360)))
  ([entities] (map #(if (:moving? %) (turn % 90) %) entities)))

(defn angle->direction [angle]
  (cond (zero? angle) {:x 1 :y 0}
        (= angle 90) {:x 0 :y 1}
        (= angle 180) {:x -1 :y 0}
        (= angle 270) {:x 0 :y -1}))

(defn karel-find-chip? [entities]
  (let [karel (->> entities (filter :karel?) first)
        not-moving-chips (->> entities (filter :chip?) (remove :moving?))
        karel-pos (select-keys karel [:x :y])
        chips-pos (map #(select-keys % [:x :y]) not-moving-chips)]
    (in? chips-pos karel-pos)))

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

(def walls-scenario3
  (let [bottom (for [xs (take 11 (range))] (conj (make-wall xs 0)))
        right (for [ys (take 9 (range))] (conj (make-wall 10 ys)))
        up (for [xs (take 11 (range))] (conj (make-wall xs 8)))
        left (for [ys (take 9 (range))] (conj (make-wall 0 ys)))]
    (-> bottom
        (conj right)
        (conj up)
        (conj left))))

(defn rand-y-chip [] (inc (rand-int 6)))

(defn scenario3 []
  (flatten [{:x 1 :y 1 :z 0 :angle 0 :karel? true :moving? true}
            (let [chip-positions [{:x 2 :y (rand-y-chip)} {:x 3 :y (rand-y-chip)} {:x 4 :y (rand-y-chip)}{:x 5 :y (rand-y-chip)} {:x 6 :y (rand-y-chip)} {:x 8 :y (rand-y-chip)}]]
              (map #(for [y (range 1 (inc (:y %)))] (make-chip (:x %) y)) chip-positions))
            (make-goal 9 1)
            walls-scenario3]))

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

(defn _leave [entities]
  (let [karel (first (filter :karel? entities))
        karel-pos (select-keys karel [:x :y])]
    (sort-by :z <
      (map #(if (= karel-pos (select-keys % [:x :y]))
                (if (:chip? %) (assoc % :z -1 :moving? false) %)
                %)
           entities))))

(defn _up [entities]
  (let [s1 (turn entities)
        s2 (move s1)
        s3 (turn s2)
        s4 (turn s3)
        s5 (turn s4)]
    [s1 s2 s3 s4 s5]))

(defn _down [entities]
  (let [s1 (turn entities)
        s2 (turn s1)
        s3 (turn s2)
        s4 (move s3)
        s5 (turn s4)]
    [s1 s2 s3 s4 s5]))

(defn _left [entities]
  (let [s1 (turn entities)
        s2 (turn s1)
        s3 (move s2)
        s4 (turn s3)
        s5 (turn s4)]
    [s1 s2 s3 s4 s5]))

(defn _right [entities]
  [(move entities)])
(defn _grab [entities]
  [(pick entities)])
(defn _drop [entities]
  [(_leave entities)])

(defn solution1 [entities]
  (let [s1 (_right entities)
        s2 (_grab (last s1))
        s3 (_right (last s2))
        s4 (_up (last s3))
        s5 (_right (last s4))
        s6 (_right (last s5))
        s7 (_right (last s6))
        s8 (_drop (last s7))]
    [s1 s2 s3 s4 s5 s6 s7 s8]))

(defn solution2-step [entities]
  (let [s1 (_up entities)
        s2 (_grab (last s1))
        s3 (_down (last s2))
        s4 (_right (last s3))
        s5 (_down (last s4))
        s6 (_drop (last s5))
        s7 (_up (last s6))
        s8 (_right (last s7))]
    [s1 s2 s3 s4 s5 s6 s7 s8]))

(defn solution2 [entities]
  (let [s1 (solution2-step entities)
        s2 (solution2-step (last (last s1)))
        s3 (solution2-step (last (last s2)))
        s4 (solution2-step (last (last s3)))
        result (-> s1 
                   (into [])
                   (into s2)
                   (into s3)
                   (into s4))]
    result))

(defn next-state3 [_entities]
  (loop [steps 0 states [_entities] entities _entities]
    (if (karel-find-chip? entities)
      (let [s1 (_grab entities)
            s2 (_up (last s1))
            next-state (-> s1 (into []) (into s2))]
        (recur (inc steps) (into states next-state) (last next-state)))
      (let [s1 (_down entities)
            next-state (-> s1 (into []))]
        (if (zero? steps)
          states
          (recur (dec steps) (into states next-state) (last next-state)))))))

(defn solution3 [entities]
  (let [
        s1 [entities]
        s2 (next-state3 (last (_right (last s1))))
        s3 (next-state3 (last (_right (last s2))))
        s4 (next-state3 (last (_right (last s3))))
        s5 (next-state3 (last (_right (last s4))))
        s6 (next-state3 (last (_right (last s5))))
        s7 (next-state3 (last (_right (last s6))))
        s8 (next-state3 (last (_right (last s7))))
        s9 (next-state3 (last (_right (last s8))))
        s10 (_drop (last s9))
        result (-> s1 
                   (into [])
                   (into s2)
                   (into s3)
                   (into s4)
                   (into s5)
                   (into s6)
                   (into s7)
                   (into s8)
                   (into s9)
                   (into s10)
                   )] 
    result))
