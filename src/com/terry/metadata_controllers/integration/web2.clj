(ns com.terry.metadata-controllers.integration.web2)

(defn ^{:get "/test3.html"} test-page3 [request]
  {:body "hello3" :status 200})

