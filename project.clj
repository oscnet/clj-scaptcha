(defproject clj-scaptcha "0.1.2"
  :description "A Clojure library that wrapper simplecaptcha"
  :url "http://github.com/oscnet/clj-scaptcha"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [ring/ring-core "1.3.2"]
                 [org.clojars.smallrivers/simplecaptcha "1.2.1"]]
  :profiles {:test {:dependencies [[ring/ring-defaults "0.1.4"]
				   [compojure "1.3.3"]
                                   [ring/ring-mock "0.2.0"]]}})

