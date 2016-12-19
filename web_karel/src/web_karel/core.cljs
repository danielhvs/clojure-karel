(ns web_karel.core
  (:require [reagent.core :as r :refer [atom]]
            [karel.core :as k]))

(def board-size 16)
(enable-console-print!)

(println "This text is printed from src/web_karel/core.cljs. Go ahead and edit it and see reloading in action.")

;; define your app data so that it doesn't get over-written on reload
(defonce app-state (atom {:text "Karel the Robot learns Clojure" :scenario [] :solution []}))

(defn create-entity [component entity]
  [component (:x entity) (:y entity)])

(defn create-game-entity [scenario key-code component]
  (->> (filter key-code scenario)
       (map #(create-entity component %))))

(defn blank [i j]
  [:rect
   {:width 0.95
    :height 0.95
    :fill "green"
    :x (+ 0.05 i)
    :y (+ 0.05 j)}])

(defn blank-goal [i j]
  [:rect
   {:width 0.85
    :height 0.85
    :fill "orange"
    :x (+ 0.10 i)
    :y (+ 0.10 j)}])

(defn circle [i j]
  [:circle
   {:r 0.35
    :stroke "black"
    :stroke-width 0.1
    :fill "none"
    :cx (+ 0.5 i)
    :cy (+ 0.5 j)}])

(defn cross [i j]
  [:g {:stroke "blue"
       :stroke-width 0.3
       :transform
       (str "translate(" (+ 0.5 i) "," (+ 0.5 j) ") "
            "scale(0.3)")}
   [:line {:x1 -1 :y1 -1 :x2 1 :y2 1}]
   [:line {:x1 1 :y1 -1 :x2 -1 :y2 1}]])

(defn timer-component []
  (let [n (r/atom 0)]
    (fn []
      (js/setTimeout #(swap! n inc) 1000)
      [:h1
       [:div 
        (let [scenario (if (->> @app-state (:level) (< 2)) 
                         (get (:solution @app-state) @n)
                         (first (get (:solution @app-state) @n)))]
          (if scenario
            (swap! app-state assoc :scenario scenario) 
            (do (reset! n 0) (swap! app-state dissoc :solution))))]])))

(defn create-scenario [scenario]
  [(create-game-entity scenario :goal? blank-goal)
   (create-game-entity scenario :chip? circle)
   (create-game-entity scenario :karel? cross)
   (create-game-entity scenario :wall? blank)])

(defn make-scenario-svg [scenario]
  (into [:svg
         {:view-box (str "0 0 " board-size " " board-size)
          :width 750
          :height 750}]
        (create-scenario scenario)))

(defn karel-window []
  [:center
   [timer-component]
   [:h1
    [:button
     {:on-click
      (fn [e]
        (swap! app-state assoc :level 1 :scenario k/scenario1))}
     "Level 1"]]
   [:h1
    [:button
     {:on-click
      (fn [e]
        (swap! app-state assoc :solution (k/solution1 (:scenario @app-state)))
       )}
     "Solution 1"]]
   [:h1
    [:button
     {:on-click
      (fn [e]
        (swap! app-state assoc :level 2 :scenario k/scenario2))}
     "Level 2"]]
   [:h1
    [:button
     {:on-click
      (fn [e]
        (swap! app-state assoc :solution (k/solution2 (:scenario @app-state)))
        )}
     "Solution 2"]]
   [:h1
    [:button
     {:on-click
      (fn [e]
        (swap! app-state assoc :level 3 :scenario (k/scenario3)))}
     "Level 3"]]
   [:h1
    [:button
     {:on-click
      (fn [e]
        (swap! app-state assoc :solution (k/solution3 (:scenario @app-state)))
        )}
     "Solution 3"]]
   (into [:svg
          {:view-box (str "0 0 " board-size " " board-size)
           :width 750
           :height 750}]
         (create-scenario (:scenario @app-state)))
   ])

(r/render-component [karel-window timer-component]
                          (. js/document (getElementById "app")))

  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
(defn on-js-reload []
  (println (str "SOLUTION:" (:solution @app-state))))
  
