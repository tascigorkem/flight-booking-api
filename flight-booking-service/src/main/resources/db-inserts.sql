INSERT INTO aircraft
(id, creation_time, deletion_time, update_time, code, country, manufacture_date, model_name, seat)
VALUES
(gen_random_uuid(), current_timestamp, NULL, current_timestamp, 'AIRBUS_A220_1', 'EU', '2021-03-01 10:00:00.000', 'AIRBUS_A220', 200),
(gen_random_uuid(), current_timestamp, NULL, current_timestamp, 'AIRBUS_A220_2', 'EU', '2021-03-01 10:00:00.000', 'AIRBUS_A220', 200),
(gen_random_uuid(), current_timestamp, NULL, current_timestamp, 'AIRBUS_A300_1', 'EU', '2020-03-01 10:00:00.000', 'AIRBUS_A300', 300),
(gen_random_uuid(), current_timestamp, NULL, current_timestamp, 'AIRBUS_A310_1', 'EU', '2019-03-01 10:00:00.000', 'AIRBUS_A310', 310);
