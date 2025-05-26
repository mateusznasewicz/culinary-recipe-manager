-- Użytkownik
INSERT INTO users (username, password, role)
VALUES ('user', 'password', 'USER');

-- Składniki
INSERT INTO ingredients_write (name)
VALUES ('Pasta'),
       ('Tomato Sauce');

-- Jednostki
INSERT INTO units_write (name)
VALUES ('Gram');

-- Powiązania składnik-jednostka
INSERT INTO ingredients_units_write (ingredient_id, unit_id, quantity)
VALUES (1, 1, 200.0),
       (2, 1, 150.0);

-- Tagi
INSERT INTO tags_write (name)
VALUES ('Italian'),
       ('Meat');

-- Przepis 1
INSERT INTO recipes_write (author_id, title, description, time, portions, difficulty)
VALUES (1, 'Spaghetti Carbonara', 'Classic Italian pasta dish...', 30, 4, 'ŚREDNI');

-- Przepis 2
INSERT INTO recipes_write (author_id, title, description, time, portions, difficulty)
VALUES (1, 'Bolognese', 'Hearty meat-based pasta sauce.', 45, 4, 'ŚREDNI');

-- Kroki przepisów
INSERT INTO recipe_steps_write (recipe_id, step_number, description)
VALUES
    (1, 1, 'Boil pasta'),
    (1, 2, 'Fry pancetta'),
    (1, 3, 'Mix eggs and cheese'),
    (1, 4, 'Combine everything'),
    (2, 1, 'Sauté onions and garlic'),
    (2, 2, 'Add minced meat'),
    (2, 3, 'Pour tomato sauce and simmer');

-- Składniki przepisów
INSERT INTO recipe_ingredients_write (recipe_id, ingredient_unit_id)
VALUES (1, 1),
       (1, 2),
       (2, 1),
       (2, 2);

-- Tagi do przepisów
INSERT INTO recipes_tags_write (recipe_id, tag_id)
VALUES (1, 1),
       (1, 2),
       (2, 1),
       (2, 2);

INSERT INTO reviews (user_id, recipe_id, rating, comment, created_at, updated_at)
VALUES
    (1, 1, 4.5, 'Great recipe, easy to follow!', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
