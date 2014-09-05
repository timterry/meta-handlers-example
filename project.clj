(defproject metadata-controllers "0.1.0-SNAPSHOT"
            :description "FIXME: write description"
            :url "http://example.com/FIXME"
            :license {:name "Eclipse Public License"
                      :url "http://www.eclipse.org/legal/epl-v10.html"}
            :dependencies [[org.clojure/clojure "1.6.0"]
                           ;web framework
                           [ring/ring-jetty-adapter "1.2.1"]
                           [ring/ring-devel "0.2.0"]
                           [compojure "1.1.6"]
                           ;templating
                           [selmer "0.5.9"]
                           [org.clojure/tools.nrepl "0.2.3"]
                           ;logging
                           [org.slf4j/slf4j-api "1.7.5"]
                           [ch.qos.logback/logback-core "1.0.10"]
                           [ch.qos.logback/logback-classic "1.0.10"]
                           [org.slf4j/jcl-over-slf4j "1.7.5"]
                           [org.slf4j/jul-to-slf4j "1.7.5"]
                           [org.clojure/tools.logging "0.2.6"]
                           ;joda-time wrapper
                           [clj-time "0.6.0"]
                          ]
            :plugins [[lein-ring "0.8.5"]]
            :ring {:handler com.terry.metadata-controllers.integration.web/app
                   :auto-reload? true
                   :auto-refresh? true
                   :init com.terry.metadata-controllers.integration.web/init
                   :nrepl {:start? true :port 9000}
                   :adapter {:max-threads 250}}
            :main com.terry.metadata-controllers.integration.web
            )

