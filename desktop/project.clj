(defproject game-clj "0.0.1-SNAPSHOT"
  :description "Sandbox"

  :dependencies [[com.badlogicgames.gdx/gdx "1.9.3"]
                 [com.badlogicgames.gdx/gdx-backend-lwjgl "1.9.3"]
                 [com.badlogicgames.gdx/gdx-box2d "1.9.3"]
                 [com.badlogicgames.gdx/gdx-box2d-platform "1.9.3" :classifier "natives-desktop"]
                 [com.badlogicgames.gdx/gdx-bullet "1.9.3"]
                 [com.badlogicgames.gdx/gdx-bullet-platform "1.9.3" :classifier "natives-desktop"]
                 [com.badlogicgames.gdx/gdx-platform "1.9.3" :classifier "natives-desktop"]
                 [org.clojure/clojure "1.8.0"]
                 [play-clj "1.1.1"]]

  :test-path "spec"
  :profiles {:dev {:plugins [[lein-kibit "0.1.2"]
                             [speclj "2.5.0"]]
                   :dependencies [[speclj "2.5.0"]]}}

  :source-paths ["src"]
  :javac-options ["-target" "1.6" "-source" "1.6" "-Xlint:-options"]
  :aot [clojure-karel.desktop-launcher]
  :main clojure-karel.desktop-launcher)
