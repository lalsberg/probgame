(ns probgame.service
	(:gen-class)
	(:require [io.pedestal.http.route.definition.table :as table]
											[io.pedestal.http.body-params :as body-params]
											[io.pedestal.http.body-params :as body-params]
											[probgame.controllers.game :as controller]
											[clojure.data.json :as json]))

(defn create-room [request]
	{:status 200
	 :body (json/write-str 
	 					(controller/create-room (:user (:json-params request))))
	 :headers {"Content-Type" "application/json"}})

(defn join-room [request]
	{:status 200
	 :body (json/write-str 
	 					(controller/join-room (:json-params request)))
	 :headers {"Content-Type" "application/json"}})

(defn bet [request]
	(println (:json-params request))
	{:status 200
	 :body (json/write-str 
	 					(controller/bet (:json-params request)))
	 :headers {"Content-Type" "application/json"}})


(def rotas
	(table/table-routes
		[["/create-room" :post [(body-params/body-params) create-room] :route-name :create-room]
		 ["/join-room" :post [(body-params/body-params) join-room] :route-name :join-room]
		 ["/bet" :post [(body-params/body-params) bet] :route-name :bet]]))
