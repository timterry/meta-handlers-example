(ns com.terry.metadata-controllers.integration.front-controller)

(defn has-http-method? [meta]
  (contains? meta :get))

(defn controller? [item]
  (let [value (val item)
        var (var-get value)]
    (when (fn? var)
      (let [meta-data (meta value)]
        (when (has-http-method? meta-data)
          {:function var :meta-data {:get (:get meta-data)}}
          )))))

(defn create-mapping [item]
  (when-let [controller-data (controller? item)]
    controller-data))

(defn scan-namespace [namespace]
  (let [ns-items (ns-publics namespace)]
    (doall (map create-mapping ns-items))))
