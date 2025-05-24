CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
)

CREATE TABLE reviews (
    user_id SERIAL REFERENCES users(id) ON DELETE CASCADE,
    recipe_id SERIAL REFERENCES recipes_write(id) ON DELETE CASCADE,
    rating NUMERIC(2,1) CHECK (rating >= 0.0 AND rating <= 5.0),,
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, recipe_id)
);

-- write model

CREATE TABLE recipes_write (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE recipe_steps_write (
    id SERIAL PRIMARY KEY,
    recipe_id INT REFERENCES recipes_write(id) ON DELETE CASCADE,
    step_number INT NOT NULL CHECK (step_number > 0),
    description TEXT NOT NULL
);

CREATE TABLE recipe_ingredients_write (
    recipe_id SERIAL REFERENCES recipes_wirte(id) ON DELETE CASCADE,
    ingredient_id SERIAL REFERENCES ingredients_write(id),
    quantity NUMERIC(2,1),
    unit TEXT NOT NULL
)

CREATE TABLE ingredients_write (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50)
)

CREATE TABLE tags_write (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE recipes_tags_write (
    recipe_id SERIAL REFERENCES recipes_write(id) ON DELETE CASCADE,
    tag_id SERIAL REFERENCES tags_write(id) ON DELETE CASCADE,
    PRIMARY KEY (recipe_id, tag_id)
);

-- read model

CREATE TABLE recipes_read (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    description TEXT,
    average_rating NUMERIC(2,1) CHECK (rating >= 0.0 AND rating <= 5.0),
    tags TEXT[],
    steps TEXT[],
    ingredients TEXT[]
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
)