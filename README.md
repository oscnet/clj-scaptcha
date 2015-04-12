# clj-scaptcha

A Clojure library that wrapper simplecaptcha

## Install

add the following dependency to your project.clj file:

```clojure
[clj-scaptcha "0.1.1"]

```

## Usage

apply this middleware to a handler:

```clojure
(def app
  (-> (routes home-routes)
      (wrap-captcha)
      (wrap-defaults site-defaults)))

```

(wrap-captcha handler options)

options are

{:path default "/captcha"

:width default 200

:height default 50}


show captcha image

```
<img src="/captcha" />
```

check the answer

```clojure
(ns test-captcha.routes.home
  (:require [test-captcha.layout :as layout]
            [compojure.core :refer [defroutes GET]]
            [clojure.java.io :as io]
            [clj-scaptcha.core :refer [correct?]]))

(defn check-ans [ans req]
  (str (correct? ans req)))

(defn home-page []
  (layout/render
    "home.html" {:docs (-> "docs/docs.md" io/resource slurp)}))

(defn about-page []
  (layout/render "about.html"))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/ans" [ans :as req] (check-ans ans req))
  (GET "/about" [] (about-page)))
```


## License

Copyright © 2015 oscnet

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
