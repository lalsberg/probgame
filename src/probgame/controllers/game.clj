(ns probgame.controllers.game
	(:import (com.mchange.v2.c3p0 ComboPooledDataSource))
	(:require [probgame.logic.game :as logic]
						[probgame.db.game :as db]
						[clojure.java.jdbc :as jdbc]))

; tirar isso daqui start
(def db-spec
  {:classname "com.mysql.jdbc.Driver"
   :subprotocol "mysql"
   :subname "//127.0.0.1:3306/prob_game"
   :user "root"
   :password "root"})

(defn pool
  [spec]
  (let [cpds (doto (ComboPooledDataSource.)
               (.setDriverClass (:classname spec))
               (.setJdbcUrl (str "jdbc:" (:subprotocol spec) ":" (:subname spec)))
               (.setUser (:user spec))
               (.setPassword (:password spec))
               ;; expire excess connections after 30 minutes of inactivity:
               (.setMaxIdleTimeExcessConnections (* 30 60))
               ;; expire connections after 3 hours of inactivity:
               (.setMaxIdleTime (* 3 60 60)))]
    {:datasource cpds}))

(def pooled-db (delay (pool db-spec)))

(defn db-connection [] @pooled-db)
; tirar isso daqui end

(defn create-response [card-taken room-id player-id player-name player-points]
	{:cart card-taken
	 :room_id room-id
	 :player {
	   :id player-id
	   :name player-name
	   :points player-points}})

(defn create-room [player-name]
	(let [card-taken (logic/take-card logic/initial-deck)]
		(jdbc/with-db-transaction [db-connection db-spec]
			(let [room-id (db/create-room db-connection)]
				(let [player-id (db/create-player db-connection room-id player-name 20)]
					(db/save-table-card db-connection room-id card-taken)
					(create-response card-taken room-id player-id player-name 20))))))

(defn create-join-room-response [player-id player-points]
	{:player {
	   :id player-id
	   :points player-points}})

(defn join-room [{room-id :room-id player-name :player-name}]
	(println (str "player-name " player-name))
	(println (str "roomID " room-id))
	(let [player-id (db/create-player (db-connection) room-id player-name 20)]
		(create-join-room-response player-id 20)))