(ns web_karel.core
  (:require [reagent.core :as r :refer [atom]]
            [karel.core :as k]))

(def board-size 3)
(enable-console-print!)

(println "This text is printed from src/web_karel/core.cljs. Go ahead and edit it and see reloading in action.")

(defn blank [i j]
  [:rect
   {:width 0.9
    :height 0.9
    :fill "green"
    :x (+ 0.05 i)
    :y (+ 0.05 j)}])

(defn circle [i j]
  [:circle
   {:r 0.35
    :stroke "black"
    :stroke-width 0.2
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
        (str (get (k/solution1 k/scenario1) @n))]])))

;; define your app data so that it doesn't get over-written on reload
(defonce app-state (atom {:text "Karel the Robot learns Clojure"}))
(defn karel-window []
  [:center
   (into [:svg
          {:view-box (str "0 0 " board-size " " board-size)
           :width 500
           :height 500}]
         (for [i (range 2)] [cross i i]))
   [:h1
    [:button
     {:on-click
      (fn level-1-click [e]
        (swap! app-state assoc :text (str k/scenario1)))}
     "Level 1"]]
   [:h1
    [:button
     {:on-click
      (fn level-2-click [e]
        (swap! app-state assoc :text (str k/scenario2)))}
     "Level 2"]]
   [:h1 (:text @app-state)]
   ])

(r/render-component [karel-window timer-component]
                          (. js/document (getElementById "app")))

  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
(defn on-js-reload []
  (println "reloaded!"))
  
