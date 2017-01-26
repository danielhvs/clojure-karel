(ns web_karel.core
  (:require [reagent.core :as r :refer [atom]]
            [karel.core :as k]))

(def board-size 16)
(enable-console-print!)

(println "This text is printed from src/web_karel/core.cljs. Go ahead and edit it and see reloading in action.")

;; define your app data so that it doesn't get over-written on reload
(defonce app-state (atom {:scenario [] :solution []}))

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
  (let [n (r/atom 0)
        level (->> @app-state (:level))]
    (fn []
      (js/setTimeout #(swap! n inc) 1000)
      [:h1
       [:div 
        (let [scenario (if (->> level (< 2))
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

(defn solutions [n] 
  (get [k/solution1 k/solution2 k/solution3] (dec n)))

(defn scenarios [n] 
  (get [k/scenario1 k/scenario2 (k/scenario3)] (dec n)))

(defn level [n]
  {:level n :solution (solutions n) :scenario (scenarios n)})

(defn width [scenario] 
  (let [xs (map :x (filter :wall? scenario))]
    (if (empty? xs) 
      board-size
      (inc (apply max xs)))))

(defn height [scenario] 
  (let [ys (map :y (filter :wall? scenario))]
    (if (empty? ys) 
      board-size
      (inc (apply max ys)))))

(defn karel-window []  
  [:div
   [:div [:left (str "LEVEL:" (:level @app-state))]]
   [timer-component]
   (for [n (map inc (take 3 (range)))]
     (let [l (level n)]
       [:h1 
        [:button 
         {:on-click 
          (fn [e] 
            (swap! app-state assoc :level (:level l) :scenario (:scenario l)))} 
         (str "Level " n)]
        [:button 
         {:on-click 
          (fn [e] 
            (swap! app-state assoc :solution ((:solution l) (:scenario @app-state)))
            )}
         (str "Solution " n)]]))
   [:div
    [:center
     (let [width (->> @app-state (:scenario) (width))
           height (->> @app-state (:scenario) (height))
           ]
       (into [:svg
              {:view-box (str "0 0 " width " " height)
               :width (* 50 width) :style {:border "5px solid" :color "black"}
               :preserveAspectRatio "xMinYMin meet"
               :height (* 50 height)}]
             (create-scenario (:scenario @app-state))))]]
   
])

(r/render-component [karel-window]
                          (. js/document (getElementById "app")))

  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
(defn on-js-reload []
  (println (str "APP-STATE:" @app-state)))


  
  
