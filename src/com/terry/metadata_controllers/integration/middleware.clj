(ns com.terry.metadata-controllers.integration.middleware
  (:require [com.terry.metadata-controllers.integration.front-controller :as front-controller]))

(defn wrap-front-controller [namespace handler]
  (fn [request]
    (front-controller/handle request namespace)))