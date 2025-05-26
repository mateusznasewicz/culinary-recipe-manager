CREATE TYPE difficulty_level AS ENUM ('ŁATWY', 'ŚREDNI', 'TRUDNY');

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
);

-- write model

CREATE TABLE recipes_write (
    id SERIAL PRIMARY KEY,
    author_id INT REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    time INT CHECK (time > 0 AND time < 600) NOT NULL,
    portions INT CHECK (portions > 0 AND portions <= 20) NOT NULL,
    difficulty difficulty_level NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE recipe_steps_write (
    id SERIAL PRIMARY KEY,
    recipe_id INT REFERENCES recipes_write(id) ON DELETE CASCADE,
    step_number INT NOT NULL CHECK (step_number > 0),
    description TEXT NOT NULL
);

CREATE TABLE ingredients_write (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE units_write (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE ingredients_units_write (
    id SERIAL PRIMARY KEY,
    ingredient_id INT REFERENCES ingredients_write(id) ON DELETE CASCADE,
    unit_id INT REFERENCES units_write(id) ON DELETE CASCADE,
    quantity NUMERIC(5,1) NOT NULL
);

CREATE TABLE recipe_ingredients_write (
    recipe_id INT REFERENCES recipes_write(id) ON DELETE CASCADE,
    ingredient_unit_id INT REFERENCES ingredients_units_write(id) ON DELETE CASCADE,
    PRIMARY KEY (recipe_id, ingredient_unit_id)
);

CREATE TABLE tags_write (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE recipes_tags_write (
    recipe_id INT REFERENCES recipes_write(id) ON DELETE CASCADE,
    tag_id INT REFERENCES tags_write(id) ON DELETE CASCADE,
    PRIMARY KEY (recipe_id, tag_id)
);

CREATE TABLE reviews (
    user_id SERIAL REFERENCES users(id) ON DELETE CASCADE,
    recipe_id INT REFERENCES recipes_write(id) ON DELETE CASCADE,
    rating NUMERIC(2,1) CHECK (rating >= 0.0 AND rating <= 5.0) NOT NULL,
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, recipe_id)
);

-- read model

CREATE TABLE recipes_read (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    description TEXT,
    time INT CHECK (time > 0 AND time < 600) NOT NULL,
    portions INT CHECK (portions > 0 AND portions <= 20) NOT NULL,
    difficulty difficulty_level NOT NULL,
    average_rating NUMERIC(2,1) CHECK (average_rating >= 0.0 AND average_rating <= 5.0),
    tags TEXT[] NOT NULL,
    steps TEXT[] NOT NULL,
    ingredients TEXT[] NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- CREATE INDEX idx_recipes_read_title_fts ON recipes_read USING GIN (to_tsvector('polish', title));
-- CREATE INDEX idx_recipes_read_difficulty ON recipes_read (difficulty);
-- CREATE INDEX idx_recipes_read_tagsON recipes_read USING GIN (tags);
-- CREATE INDEX idx_recipes_read_ingredients ON recipes_read USING GIN (ingredients);
-- CREATE INDEX idx_recipes_read_time ON recipes_read (time);
-- CREATE INDEX idx_recipes_read_portions ON recipes_read (portions);
-- CREATE INDEX idx_recipes_read_avg_rating ON recipes_read (average_rating);
