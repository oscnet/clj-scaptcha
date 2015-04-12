(ns clj-scaptcha.core
  (:import nl.captcha.Captcha
           java.io.PipedOutputStream
           javax.imageio.ImageIO)
  (:require [ring.util.io :refer [piped-input-stream]]
            [ring.util.response :refer [response header content-type]]))

(defn correct? [answer req]
  (if-let [^Captcha captcha (get-in req [:session :captcha])]
    (.isCorrect captcha answer)))

(defn- create-captcha [width height]
  (let [captcha (new nl.captcha.Captcha$Builder width height)]
    (-> captcha
        (.addText)
        (.gimp)
        (.addNoise)
        (.addNoise)
        (.addBackground)
        (.build))))

(defn- servlet-context
  [req]
  (if-let [context (:servlet-context req)]
    (try (.getContextPath context)
      (catch IllegalArgumentException _ context))))

(defn create-captcha-png [request width height]
  (let [^Captcha captcha (create-captcha width height)
        image (.getImage captcha)]
    (->
     (piped-input-stream #(ImageIO/write image "png" ^PipedOutputStream %))
     response
     (header "Cache-Control" "private,no-cache,no-store")
     (content-type "image/png")
     (assoc :session (assoc (:session request) :captcha captcha)))))

(defn wrap-captcha [handler & [{:keys [path width height] :or {path "/captcha" width 200 height 50}}]]
  (fn [request]
    (if (and (= (:uri request) (str (servlet-context request) path))
             (= (:request-method request) :get))
      (create-captcha-png request width height)
      (handler request))))
