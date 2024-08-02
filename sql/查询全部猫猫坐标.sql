SELECT coord.id,coord.cat_id,c.catname,coord.longitude,coord.latitude,coord.update_time,coord.area,coord.description
FROM cat c
LEFT JOIN (
	SELECT coord1.id,coord1.cat_id,coord1.longitude,coord1.latitude,coord1.update_time,coord1.area,coord1.description
	FROM coordinate coord1
	INNER JOIN (
		SELECT cat_id, MAX(update_time) AS latest_timestamp
		FROM coordinate
		GROUP BY cat_id
	) coord2 ON coord1.cat_id = coord2.cat_id AND coord1.update_time = coord2.latest_timestamp
) coord ON c.cat_id = coord.cat_id;
