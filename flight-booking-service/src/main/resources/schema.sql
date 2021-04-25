-- Database: flight-booking-db

-- DROP DATABASE "flight-booking-db";
-- CREATE DATABASE "flight-booking-db"
--     WITH
--     OWNER = postgres
--     ENCODING = 'UTF8'
--     LC_COLLATE = 'en_US.UTF-8'
--     LC_CTYPE = 'en_US.UTF-8'
--     TABLESPACE = pg_default
--     CONNECTION LIMIT = -1;

-- Schema: public

-- DROP SCHEMA public ;
-- CREATE SCHEMA public AUTHORIZATION postgres;
-- COMMENT ON SCHEMA public IS 'standard public schema';
-- GRANT ALL ON SCHEMA public TO PUBLIC;
-- GRANT ALL ON SCHEMA public TO postgres;

-- Tables:

-- DROP TABLE IF EXISTS booking;
-- DROP TABLE IF EXISTS flight;
-- DROP TABLE IF EXISTS customer;
-- DROP TABLE IF EXISTS airplane;
-- DROP TABLE IF EXISTS aircraft;
-- DROP TABLE IF EXISTS airport;

-- Table: public.aircraft
CREATE TABLE IF NOT EXISTS aircraft
(
    id uuid NOT NULL,
    creation_time timestamp without time zone,
    deletion_time timestamp without time zone,
    update_time timestamp without time zone,
    code character varying(255) COLLATE pg_catalog."default",
    country character varying(255) COLLATE pg_catalog."default",
    manufacture_date timestamp without time zone,
    model_name character varying(255) COLLATE pg_catalog."default",
    seat smallint,
    CONSTRAINT aircraft_pkey PRIMARY KEY (id)
);

-- Table: public.airplane
CREATE TABLE IF NOT EXISTS airplane
(
    id uuid NOT NULL,
    creation_time timestamp without time zone,
    deletion_time timestamp without time zone,
    update_time timestamp without time zone,
    country character varying(255) COLLATE pg_catalog."default",
    name character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT airplane_pkey PRIMARY KEY (id)
);

-- Table: public.airport
CREATE TABLE IF NOT EXISTS airport
(
    id uuid NOT NULL,
    creation_time timestamp without time zone,
    deletion_time timestamp without time zone,
    update_time timestamp without time zone,
    city character varying(255) COLLATE pg_catalog."default",
    code character varying(255) COLLATE pg_catalog."default",
    name character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT airport_pkey PRIMARY KEY (id)
);

-- Table: public.customer
CREATE TABLE IF NOT EXISTS customer
(
    id uuid NOT NULL,
    creation_time timestamp without time zone,
    deletion_time timestamp without time zone,
    update_time timestamp without time zone,
    age smallint,
    city character varying(255) COLLATE pg_catalog."default",
    country character varying(255) COLLATE pg_catalog."default",
    email character varying(255) COLLATE pg_catalog."default",
    name character varying(255) COLLATE pg_catalog."default",
    password character varying(255) COLLATE pg_catalog."default",
    phone character varying(255) COLLATE pg_catalog."default",
    surname character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT customer_pkey PRIMARY KEY (id)
);

-- Table: public.flight
CREATE TABLE IF NOT EXISTS flight
(
    id uuid NOT NULL,
    creation_time timestamp without time zone,
    deletion_time timestamp without time zone,
    update_time timestamp without time zone,
    arrival_date timestamp without time zone,
    departure_date timestamp without time zone,
    price numeric(19,2),
    aircraft_id uuid,
    airline_id uuid,
    dept_airport_id uuid,
    dest_airport_id uuid,
    CONSTRAINT flight_pkey PRIMARY KEY (id),
    CONSTRAINT fk_airline_id FOREIGN KEY (airline_id)
        REFERENCES public.airline (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk_dept_airport_id FOREIGN KEY (dept_airport_id)
        REFERENCES public.airport (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk_dest_airport_id FOREIGN KEY (dest_airport_id)
        REFERENCES public.airport (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk_aircraft_id FOREIGN KEY (aircraft_id)
        REFERENCES public.aircraft (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

-- Table: public.booking
CREATE TABLE IF NOT EXISTS booking
(
    id uuid NOT NULL,
    creation_time timestamp without time zone,
    deletion_time timestamp without time zone,
    update_time timestamp without time zone,
    has_insurance boolean,
    luggage smallint,
    payment_amount numeric(19,2),
    payment_date timestamp without time zone,
    state character varying(255) COLLATE pg_catalog."default",
    customer_id uuid,
    flight_id uuid,
    CONSTRAINT booking_pkey PRIMARY KEY (id),
    CONSTRAINT fk_flight_id FOREIGN KEY (flight_id)
        REFERENCES public.flight (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk_customer_id FOREIGN KEY (customer_id)
        REFERENCES public.customer (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

--TABLESPACE pg_default;
ALTER TABLE aircraft OWNER to postgres;
ALTER TABLE airplane OWNER to postgres;
ALTER TABLE airport OWNER to postgres;
ALTER TABLE booking OWNER to postgres;
ALTER TABLE customer OWNER to postgres;
ALTER TABLE flight OWNER to postgres;
