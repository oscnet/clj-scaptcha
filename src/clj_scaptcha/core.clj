(ns clj-scaptcha.core
  (:import nl.captcha.Captcha
           javax.imageio.ImageIO)
  (:require [ring.util.io :refer [piped-input-stream]]
            [ring.util.response :refer [response header content-type]]))

(defn- get-captcha [req]
  (get-in req [:session :captcha]))

(defn correct? [answer req]
  (if-let [^Captcha captcha (get-captcha req)]
    (.isCorrect captcha answer)))

(defn create-captcha [width height]
  (let [captcha (new nl.captcha.Captcha$Builder width height)]
    (-> captcha
      (.addText)
      (.gimp)
      (.addNoise)
      (.addNoise)
      ;(.addBorder)
      (.addBackground)
      (.build))))

(defn- servlet-context
  [req]
  (if-let [context (:servlet-context req)]
    (try (.getContextPath context)
      (catch IllegalArgumentException _ context))))

(defn wrap-captcha [handler & [{:keys [path width height] :or {path "/captcha" width 200 height 50}}]]
  (fn [request]
    (if (and (= (:uri request) (str (servlet-context request) path))
             (= (:request-method request) :get))
      (let [^Captcha captcha (create-captcha width height)
            img (.getImage captcha)]
        (->
         (piped-input-stream
          (fn [output]
            (ImageIO/write img "png" output)))
         response
         (header "Cache-Control" "private,no-cache,no-store")
         (content-type "image/png")
         (assoc :session (assoc (:session request) :captcha captcha))))
      (handler request))))
