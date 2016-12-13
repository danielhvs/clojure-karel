(defproject karel "1.0.0"
  :description "karel the robot learns clojure"

  :dependencies [[org.clojure/clojure "1.8.0"]]

  :test-path "spec"
  :profiles {:dev {:plugins [[lein-kibit "0.1.2"]
                             [speclj "2.5.0"]]
                   :dependencies [[speclj "2.5.0"]]}}

  :source-paths ["src"]
  :javac-options ["-target" "1.6" "-source" "1.6" "-Xlint:-options"])
