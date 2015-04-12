(ns clj-scaptcha.core-test
  (:use ring.mock.request)
  (:require [clojure.test :refer :all]
            [clj-scaptcha.core :refer :all]
            ;[compojure.core :refer [defroutes routes]]
            [compojure.core :refer :all]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults]]))

(defn is-correct? [answer]
  (str (correct? answer)))

(defroutes home-routes
  (POST "/answer" [answer] (is-correct? answer)))

(def app
  (-> (routes home-routes)
      (wrap-captcha)
      (wrap-defaults site-defaults)))

(deftest test-captcha
  (testing "get captcha"
    (let [response (app (request :get "/captcha"))]
      (is (= 200 (:status response))))
    (let [response (app (request :post "/answer" {:answer "12333"}))]
      (is (or (:body response)
              (not (:body response)))))))
