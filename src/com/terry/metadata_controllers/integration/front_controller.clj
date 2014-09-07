(ns com.terry.metadata-controllers.integration.front-controller)

(defn has-http-method? [meta]
  (contains? meta :get))

(defn controller? [item]
  (let [value (val item)
        var (var-get value)]
    (when (fn? var)
      (let [meta-data (meta value)]
        (when (has-http-method? meta-data)
          (assoc {:function var} :meta-data meta-data))))))

(defn create-mapping [item]
  (when-let [controller-data (controller? item)]
    controller-data))

(defn scan-namespace [namespace]
  (let [ns-items (ns-publics namespace)]
    (doall (remove nil? (map create-mapping ns-items)))))

(defn handler-match [url http-method mapping]
  (when-let [mapping-url (http-method (:meta-data mapping))]
    (when (= mapping-url url)
      (:function mapping))))

(defn find-handler [request mappings]
  (let [uri (:uri request)
        http-method (:request-method request)
        matched-handler (some #(handler-match uri http-method %) mappings)]
    matched-handler))

(defn handle [request namespace]
  (let [mappings (scan-namespace namespace)
        matched-handler (find-handler request mappings)]
    (matched-handler request)))

;(def request {:uri "/test.html" :request-method :get})
;(handle request 'com.terry.metadata-controllers.integration.web)