(ns clojure-karel.entities-spec
  (:require [speclj.core :refer :all]))

(defn equals-5? [n]
  (= 5 n))

(describe "Understanding Speclj Components"
  (before-all
    (println "Start testing"))

  (after-all
    (println "Finished testing"))

  (before
    (println "Before each test"))

  (after
    (println "After each test"))

  (it "4 plus 1 equals 5"
    (println "During 1st Test")
    (should (equals-5? (+ 4 1))))

  (it "4 plus 2 doesn't equal 5"
    (println "During 2nd Test")
    (should-not (equals-5? (+ 4 2))))

  (it "4 plus 2 equals 6"
    (println "During 3rd Test")
    (should= 6 (+ 4 2))))

(run-specs)
