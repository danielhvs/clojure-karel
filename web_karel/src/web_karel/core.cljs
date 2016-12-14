(ns web_karel.core
  (:require [reagent.core :as r :refer [atom]]
            [karel.core :as k]))

(enable-console-print!)

(println "This text is printed from src/web_karel/core.cljs. Go ahead and edit it and see reloading in action.")

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
   [timer-component]
   [:h1
    [:button
     {:on-click
      (fn level-1-click [e]
        (swap! app-state assoc :text (str k/scenario1)))}
     "Level 1"]]
   [:h1
    [:button
     {:on-click
      (fn level-1-click [e]
        (swap! app-state assoc :text (str (k/solution1 k/scenario1))))}
     "Solution 1"]]
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
  
