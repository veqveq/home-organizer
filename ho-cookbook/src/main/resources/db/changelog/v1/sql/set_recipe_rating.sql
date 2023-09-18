UPDATE cookbook.recipe
SET rating = CASE
                 WHEN likes is NULL or likes = 0 THEN 0
                 WHEN dislikes is NULL or dislikes = 0 THEN 100
                 ELSE likes * 100 / (likes+dislikes)
    END