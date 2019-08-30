(ns web_karel.core
  (:require [reagent.core :as r :refer [atom]]
            [karel.core :as k]))

(enable-console-print!)

(def scenario1
  [{:x 0 :y 0} {:x 1 :y 1} {:x 0 :y 1} {:x 1 :y 0}])
(defonce app-state (atom {:size 40 :scenario scenario1}))

(defn blank [p]
  [:rect
   {:width 0.95
    :height 0.95
    :fill "green"
    :x (+ 0.05 (:x p))
    :y (+ 0.05 (:y p))}])

(defn blank-goal [p]
  [:rect
   {:width 0.85
    :height 0.85
    :fill "orange"
    :x (+ 0.10  (:x p))
    :y (+ 0.10 (:y p))}])

(defn karel-window []  
  [:div
   [:div 
    [:button
     {:on-click (fn [e] (swap! app-state assoc :size (+ (:size @app-state) 15)))}
     (str "+")]
    [:button
     {:on-click (fn [e] (swap! app-state assoc :size (- (:size @app-state) 15)))}
     (str "-")]]
   [:div
    [:center
     (let [width 2
           height 2
           ]
       (into [:svg
              {:view-box (str "0 0 " width " " height)
               :width (* (:size @app-state) width)
               :preserveAspectRatio "xMinYMin meet"
               :height (* (:size @app-state) height)}]
             (map blank (:scenario @app-state))))]]])

(r/render-component [karel-window]
                          (. js/document (getElementById "app")))

(defn on-js-reload []
  (do
    (println (str "APP-STATE:" @app-state))
    (println (str "APP-STATE:" (map blank-goal (:scenario @app-state))))
))
