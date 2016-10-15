(ns clojure-karel.entities-spec
  (:require [speclj.core :refer :all]
            [clojure-karel.entities :refer :all]))

(describe "scenario1 karel moving"
  (before
    (def entities [{:x 1 :y 1 :angle 0 :karel? true}
                   {:x 2 :y 1 :chip? true}
                   {:x 6 :y 2 :goal? true}
                   {:x 4 :y 1 :wall? true}
                   {:x 5 :y 1 :wall? true}
                   {:x 6 :y 1 :wall? true}]))

  (it "moves karel normally"
    (let [karel (first (move entities))]
      (should= {:x 2 :y 1 :angle 0 :karel? true} karel)))

  (it "doesnt move karel if there is a wall in the way"
    (let [karel (first (->> (move entities)
                            (move)
                            (move)))]
      (should= {:x 3 :y 1 :angle 0 :karel? true} karel))))


(run-specs)
