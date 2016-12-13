(ns web_karel.core
  (:require [reagent.core :as reagent :refer [atom]]))
  ;; (:require [karel.core :as k])

(enable-console-print!)

(println "This text is printed from src/web_karel/core.cljs. Go ahead and edit it and see reloading in action.")

;; define your app data so that it doesn't get over-written on reload
(defonce app-state (atom {:text "Hello world!"}))

(defn hello-world []
  [:h1 (:text @app-state)])

(reagent/render-component [hello-world]
                          (. js/document (getElementById "app")))

  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
(defn on-js-reload []
  (swap! app-state assoc :text "new text")
  (println "reloaded, man!"))
  
