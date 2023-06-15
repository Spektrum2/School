CREATE TABLE cars
(
    id    bigint primary key,
    brand text,
    model text,
    cost  numeric(9, 2)
);

CREATE TABLE people
(
    id      bigint primary key,
    name    text,
    age     integer,
    license boolean,
    car_id  bigint references cars (id)
);