(ns probgame.db.db
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

(defn has-bet [conn player-id]
	(first
			(jdbc/query conn ["select 1 from bet where player_id = ?" player-id])))

(defn all-players-bet [conn room-id]
	(not 
		(first
				(jdbc/query conn ["select 1 from player p left join bet b on p.id = b.player_id 
							where p.room_id = ? and b.id is null" room-id]))))

(defn remove-round-bets [conn room-id]
	(jdbc/execute! conn ["delete from bet where player_id in (select id from player where room_id = ?)" room-id]))

(defn save-bet [conn player-id]
			(jdbc/insert! conn :bet {:player_id player-id}))

(defn update-player-points [conn player-id points]
			(jdbc/update! conn :player {:points points} ["id = ?" player-id]))

(defn get-player [conn player-id]
	(jdbc/query conn ["select * from player where id = ?" player-id] {:result-set-fn first}))

(defn get-table-card [conn room-id]
	(jdbc/query conn ["select * from table_card where room_id = ? order by id desc limit 1" room-id] {:result-set-fn first}))

(defn get-table-cards [conn room-id]
	(jdbc/query conn ["select * from table_card where room_id = ?" room-id]))