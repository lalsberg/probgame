(ns probgame.db.game
	(:require [clojure.java.jdbc :as jdbc]))

(defn create-room [conn]
	(:generated_key
		(first
			(jdbc/insert! conn :room {:created_by "leandro.alsberg"}))))

(defn create-player [conn room-id player-name points]
	(:generated_key
		(first
			(jdbc/insert! conn :player {:room_id room-id :name player-name :points points}))))

(defn save-table-card [conn room-id card]
	(:generated_key
		(first
			(jdbc/insert! conn :table_card {:room_id room-id :card card}))))

(defn update-player-points [conn player-id points]
			(jdbc/update! conn :player {:points points} ["id = ?" player-id]))

(defn get-player [conn player-id]
	(jdbc/query conn ["select * from player where id = ?" player-id] {:result-set-fn first}))

(defn get-table-card [conn room-id]
	(jdbc/query conn ["select * from table_card where room_id = ? order by id desc limit 1" room-id] {:result-set-fn first}))

(defn get-table-cards [conn room-id]
	(jdbc/query conn ["select * from table_card where room_id = ?" room-id]))