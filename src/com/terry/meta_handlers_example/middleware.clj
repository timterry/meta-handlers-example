(ns com.terry.meta-handlers-example.middleware
  (:require [com.terry.meta-handlers.front-controller :as front-controller]))

(defn wrap-front-controller [namespace handler]
  (fn [request]
    (front-controller/handle request namespace)))