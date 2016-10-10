(ns clojure-karel.desktop-launcher
  (:require [clojure-karel.core :refer :all])
  (:import [com.badlogic.gdx.backends.lwjgl LwjglApplication]
           [org.lwjgl.input Keyboard])
  (:gen-class))

(defn -main
  []
  (LwjglApplication. clj-karel-game "clojure-karel" 800 600)
  (Keyboard/enableRepeatEvents true))
