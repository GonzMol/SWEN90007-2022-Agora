CREATE TABLE shipping_details
(
    id           uuid        NOT NULL,
    first_name   varchar(35) NOT NULL,
    last_name    varchar(35) NOT NULL,
    phone_number varchar(15) NOT NULL,
    country      varchar(56) NOT NULL,
    street       varchar(35) NOT NULL,
    city         varchar(35) NOT NULL,
    state        varchar(35) NOT NULL,
    postcode     varchar(10) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE "admin"
(
    id        uuid         NOT NULL,
    username  varchar(50)  NOT NULL,
    email     varchar(254) NOT NULL,
    pass_hash varchar(256) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE "user"
(
    id               uuid         NOT NULL,
    is_admin         bool         NOT NULL,
    is_active        bool,
    username         varchar(50)  NOT NULL UNIQUE,
    email            varchar(254) NOT NULL UNIQUE,
    pass_hash        varchar(256) NOT NULL,
    shipping_details uuid,
    PRIMARY KEY (id),
    FOREIGN KEY (shipping_details) REFERENCES shipping_details (id)
);

CREATE TABLE listing
(
    id          uuid           NOT NULL,
    "type"      varchar(7)     NOT NULL,
    is_active   bool           NOT NULL,
    title       varchar(100)   NOT NULL,
    description varchar(2000)  NOT NULL,
    date_listed timestamptz    NOT NULL,
    "category"  varchar(24)    NOT NULL,
    condition   varchar(4)     NOT NULL,
    price       decimal(10, 2) NOT NULL,
    stock       int,
    start_time  timestamptz,
    end_time    timestamptz,
    "owner"     uuid           NOT NULL,
    "version"   int            NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY ("owner") REFERENCES "user" (id)
);

CREATE TABLE bid
(
    id      uuid           NOT NULL,
    "time"  timestamptz    NOT NULL,
    amount  decimal(10, 2) NOT NULL,
    listing uuid           NOT NULL,
    "user"  uuid           NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (listing) REFERENCES listing (id),
    FOREIGN KEY ("user") REFERENCES "user" (id)
);

CREATE TABLE "order"
(
    id               uuid        NOT NULL,
    is_cancelled     bool        NOT NULL,
    "date"           timestamptz NOT NULL,
    quantity         int         NOT NULL,
    buyer            uuid        NOT NULL,
    listing          uuid        NOT NULL,
    shipping_details uuid        NOT NULL,
    "version"        int         NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (buyer) REFERENCES "user" (id),
    FOREIGN KEY (listing) REFERENCES listing (id),
    FOREIGN KEY (shipping_details) REFERENCES shipping_details (id)
);

CREATE TABLE co_seller
(
    "user"  uuid NOT NULL,
    listing uuid NOT NULL,
    FOREIGN KEY (listing) REFERENCES listing (id),
    FOREIGN KEY ("user") REFERENCES "user" (id)
);

-- Updates to original schema

DROP TABLE admin;

ALTER TABLE "user"
    ADD is_admin bool NOT NULL DEFAULT FALSE;
ALTER TABLE "user"
    ALTER COLUMN is_active DROP NOT NULL;
ALTER TABLE "user"
    ADD UNIQUE (username);
ALTER TABLE "user"
    ADD UNIQUE (email);
ALTER TABLE "user"
    ALTER COLUMN shipping_details DROP NOT NULL;

ALTER TABLE "listing"
    ADD "version" int NOT NULL DEFAULT 1;

ALTER TABLE "order"
    ADD "version" int NOT NULL DEFAULT 1;
