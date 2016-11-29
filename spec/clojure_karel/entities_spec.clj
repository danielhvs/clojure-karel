(ns clojure-karel.entities-spec
  (:require [speclj.core :refer :all]
            [clojure-karel.entities :refer :all]))

(describe "karel moving:"
  (before
    (def entities [{:x 1 :y 1 :angle 0 :z 0 :karel? true :moving? true}
                   {:x 2 :y 1 :z 0 :chip? true}
                   {:x 6 :y 2 :z 0 :goal? true}
                   {:x 4 :y 1 :z 0 :wall? true}
                   {:x 5 :y 1 :z 0 :wall? true}
                   {:x 6 :y 1 :z 0 :wall? true}]))

  (it "moves karel normally"
    (let [karel (first (move entities))]
      (should= {:x 2 :y 1  :z 0 :angle 0 :karel? true :moving? true} karel)))

  (it "doesnt move karel if there is a wall in the way"
    (let [karel (first (->> (move entities)
                            (move)
                            (move)))]
      (should= {:x 3 :y 1 :z 0 :angle 0 :karel? true :moving? true} karel))))

(describe "picking chips:"
  (before
    (def entities [{:x 1 :y 1 :z 0 :angle 0 :karel? true :moving? true}
                   {:x 1 :y 1 :z 0 :chip? true}
                   {:x 6 :y 2 :z 0 :goal? true}
                   {:x 4 :y 1 :z 0 :wall? true}
                   {:x 5 :y 1 :z 0 :wall? true}
                   {:x 6 :y 1 :z 0 :wall? true}]))

  (it "does not get the chip"
    (let [chip (first (filter :chip? (pick (move entities))))]
      (should= {:x 1 :y 1 :z 0 :chip? true} chip)))

  (it "gets the chip"
    (let [chip (first (filter :chip? (pick entities)))]
      (should= {:x 1 :y 1 :z 1 :chip? true :moving? true} chip))))

(describe "detect chips:"
  (before
    (def entities [{:x 1 :y 1 :z 0 :angle 0 :karel? true :moving? true}
                   {:x 1 :y 1 :z 0 :chip? true}]))
  (it "karel detects when there is a chip"
      (should= true (karel-find-chip? entities)))
  (it "karel doesn't detects when it is grabbing a chip"
      (should= false (karel-find-chip? (pick entities))))
  (it "karel doesn't detects when there isn't a chip"
      (should= false (karel-find-chip? (move entities)))))

(describe "up returns all states:"
  (before
    (def entities [{:x 1 :y 1 :z 0 :angle 0 :karel? true :moving? true}]))
  (it "returns all karel states"
      (should= [
                [{:x 1 :y 1 :z 0 :angle 90 :karel? true :moving? true}]
                [{:x 1 :y 2 :z 0 :angle 90 :karel? true :moving? true}]
                [{:x 1 :y 2 :z 0 :angle 180 :karel? true :moving? true}]
                [{:x 1 :y 2 :z 0 :angle 270 :karel? true :moving? true}]
                [{:x 1 :y 2 :z 0 :angle 0 :karel? true :moving? true}]
               ] (_up entities))))

(run-specs)
