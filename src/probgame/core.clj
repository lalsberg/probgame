(ns probgame.core
	(:gen-class)
	(:require [io.pedestal.http :as http]
  					[probgame.service :as service]))

(defn create-server []
	(http/create-server 
		{::http/routes service/rotas
		 ::http/type :jetty
		 ::http/port 3000}))

(defn -main []
	(http/start (create-server)))
