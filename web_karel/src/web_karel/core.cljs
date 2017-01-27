(ns web_karel.core
  (:require [reagent.core :as r :refer [atom]]
            [karel.core :as k]))

(def board-size 1)
(def MIN_LEVEL 1)
(def MAX_LEVEL 3)
(enable-console-print!)

(defn solutions [n] 
  (get [k/solution1 k/solution2 k/solution3] (dec n)))

(defn scenarios [n] 
  (get [k/scenario1 k/scenario2 (k/scenario3)] (dec n)))

(defn level [n]
  {:level n :solution (solutions n) :scenario (scenarios n)})

;; define your app data so that it doesn't get over-written on reload
(defonce app-state (atom {:size 50 :level 1 :scenario (:scenario (level 1)) :solution (:solution (level 1))}))

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
        l (->> @app-state (:level))]
    (fn []
      (js/setTimeout #(swap! n inc) 1000)
      [:h1
       [:div 
        (let [scenario (if (< l 3) 
                         (first (get (:solution @app-state) @n))
                         (get (:solution @app-state) @n))]
          (if scenario
            (do (swap! app-state assoc :scenario scenario :busy? true) (str "Scenario: " scenario)) 
            (do (reset! n 0) (swap! app-state dissoc :solution :busy? false) "")))]])))

(defn create-scenario [scenario]
  [(create-game-entity scenario :goal? blank-goal)
   (create-game-entity scenario :chip? circle)
   (create-game-entity scenario :karel? cross)
   (create-game-entity scenario :wall? blank)])
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

(defn button-disabled? [app-state]
  (if (:busy? @app-state) "disabled" ""))

(defn next-level [level]
  (min MAX_LEVEL (inc level)))

(defn previous-level [level]
  (max MIN_LEVEL (dec level)))

(defn karel-window []  
  [:div
   [:div 
    "Level "
    [:button 
     {:on-click 
      (fn [e] 
        (let [n (previous-level (:level @app-state))] 
          (swap! app-state assoc :level n :scenario (:scenario (level n)))))
      :disabled (button-disabled? app-state)}
     (str "-")]
    [:button
     {:on-click 
      (fn [e] 
        (let [n (next-level (:level @app-state))] 
          (swap! app-state assoc :level n :scenario (:scenario (level n)))))
      :disabled (button-disabled? app-state)}
     (str "+")]
    [:button
     {:disabled (button-disabled? app-state)
      :on-click
      (fn [e]
        (swap! app-state assoc :solution ((:solution (level (:level @app-state))) (:scenario @app-state)))
        )}
     (str "Solution")]
    [:center (str "Level " (:level @app-state))]]
   [:div 
    "Size "
    [:button 
     {:on-click 
      (fn [e] 
        (swap! app-state assoc :size (- (:size @app-state) 10)))
      :disabled (button-disabled? app-state)}
     (str "-")]
    [:button
     {:on-click 
      (fn [e] 
        (swap! app-state assoc :size (+ (:size @app-state) 10)))
      :disabled (button-disabled? app-state)}
     (str "+")]
]
   [:div
    [:center
     (let [width (->> @app-state (:scenario) (width))
           height (->> @app-state (:scenario) (height))
           ]
       (into [:svg
              {:view-box (str "0 0 " width " " height)
               :width (* (:size @app-state) width) :style {:border "1px solid" :color "green"}
               :preserveAspectRatio "xMinYMin meet"
               :height (* (:size @app-state) height)}]
             (create-scenario (:scenario @app-state))))]]
   [timer-component]
   ])

(r/render-component [karel-window]
                          (. js/document (getElementById "app")))

  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
(defn on-js-reload []
  (println (str "APP-STATE:" @app-state)))


  
  
