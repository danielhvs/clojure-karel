(defproject clojure-karel "0.0.1-SNAPSHOT"
  :description "Sandbox"

  :dependencies [[com.badlogicgames.gdx/gdx "1.9.3"]
                 [com.badlogicgames.gdx/gdx-backend-lwjgl "1.9.3"]
                 [com.badlogicgames.gdx/gdx-box2d "1.9.3"]
                 [com.badlogicgames.gdx/gdx-box2d-platform "1.9.3" :classifier "natives-desktop"]
                 [com.badlogicgames.gdx/gdx-bullet "1.9.3"]
                 [com.badlogicgames.gdx/gdx-bullet-platform "1.9.3" :classifier "natives-desktop"]
                 [com.badlogicgames.gdx/gdx-platform "1.9.3" :classifier "natives-desktop"]
                 [org.clojure/clojure "1.8.0"]
                 [play-clj "1.1.1"]
                 [org.clojure/clojurescript "1.7.122"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [reagent "0.5.0"]]

  :plugins [[lein-cljsbuild "1.1.0"]
            [lein-figwheel "0.4.1"]]

  :test-path "spec"
  :profiles {:dev {:plugins [[lein-kibit "0.1.2"]
                             [speclj "2.5.0"]
                             [nightlight/lein-nightlight "1.0.0"]]
                   :dependencies [[speclj "2.5.0"]]}}

  :source-paths ["src"]
  
  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :cljsbuild {
    :builds [{:id "dev"
              :source-paths ["src"]

              :figwheel { :on-jsload "tictactoe.core/on-js-reload" }

              :compiler {:main tictactoe.core
                         :asset-path "js/compiled/out"
                         :output-to "resources/public/js/compiled/tictactoe.js"
                         :output-dir "resources/public/js/compiled/out"
                         :source-map-timestamp true }}
             {:id "min"
              :source-paths ["src"]
              :compiler {:output-to "js/compiled/tictactoe.js"
                         :main tictactoe.core
                         :optimizations :advanced
                         :pretty-print false}}]}


  :javac-options ["-target" "1.6" "-source" "1.6" "-Xlint:-options"]
  :aot [clojure-karel.desktop-launcher]
  :main clojure-karel.desktop-launcher

  :figwheel {
             ;; :http-server-root "public" ;; default and assumes "resources"
             ;; :server-port 3449 ;; default
             ;; :server-ip "127.0.0.1"

             :css-dirs ["resources/public/css"] ;; watch and update CSS

             ;; Start an nREPL server into the running figwheel process
             ;; :nrepl-port 7888

             ;; Server Ring Handler (optional)
             ;; if you want to embed a ring handler into the figwheel http-kit
             ;; server, this is for simple ring servers, if this
             ;; doesn't work for you just run your own server :)
             ;; :ring-handler hello_world.server/handler

             ;; To be able to open files in your editor from the heads up display
             ;; you will need to put a script on your path.
             ;; that script will have to take a file path and a line number
             ;; ie. in  ~/bin/myfile-opener
             ;; #! /bin/sh
             ;; emacsclient -n +$2 $1
             ;;
             ;; :open-file-command "myfile-opener"

             ;; if you want to disable the REPL
             ;; :repl false

             ;; to configure a different figwheel logfile path
             ;; :server-logfile "tmp/logs/figwheel-logfile.log"
             })
