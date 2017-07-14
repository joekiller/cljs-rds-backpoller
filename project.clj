(defproject cljs-rds-backpoller "0.1.0-SNAPSHOT"
  :description "AWS Lambda to back poll an RDS database"
  :url "https://github.com/Type-Zero"
  :dependencies [[org.clojure/clojure       "1.8.0"]
                 [org.clojure/clojurescript "1.9.542"]
                 [org.clojure/core.async    "0.3.442"]
                 [funcool/promesa           "1.8.1"]
                 [io.nervous/cljs-lambda    "0.3.5"]
                 [figwheel-sidecar "0.5.0"]]
  :plugins [[lein-cljsbuild "1.1.4"]
            [lein-npm       "0.6.0"]
            [lein-doo       "0.1.7"]
            [io.nervous/lein-cljs-lambda "0.6.6"]]
  :npm {:dependencies [[source-map-support "0.4.0"]
                       [pg-pool "1.7.1"]
                       [pg "6.2.2"]
                       [source-map-support "0.4.0"]]}
  :source-paths ["src" "script"]
  :cljs-lambda
  {:cljs-build-id "prod"
   :resource-dirs ["static"]
   :functions
   [{:name   "poller-Lambda-TSL5VA56G98J"
     :invoke cljs-rds-backpoller.core/work-magic}]}
  :cljsbuild
  {:builds [{:id "dev"
             :source-paths ["src"]
             :compiler {:output-to     "target/cljs-rds-backpoller/cljs_rds_backpoller.js"
                        :output-dir    "target/cljs-rds-backpoller"
                        :source-map    true
                        :target        :nodejs
                        :language-in   :ecmascript5
                        :externs       ["externs/pg.js"]
                        :optimizations :none
                        :main cljs-rds-backpoller.core}}
            {:id "prod"
             :source-paths ["src"]
             :compiler {:output-to     "target/prod/cljs_rds_backpoller.js"
                        :output-dir    "target/prod"
                        :source-map    "target/prod/cljs_rds_backpoller.js.map"
                        :target        :nodejs
                        :language-in   :ecmascript5
                        :externs       ["externs/pg.js"]
                        :optimizations :advanced}}
            {:id "cljs-rds-backpoller-test"
             :source-paths ["src" "test"]
             :compiler {:output-to     "target/cljs-rds-backpoller-test/cljs_rds_backpoller.js"
                        :output-dir    "target/cljs-rds-backpoller-test"
                        :target        :nodejs
                        :language-in   :ecmascript5
                        :optimizations :none
                        :main          cljs-rds-backpoller.test-runner}}]})
