create type Kayttajataso as enum ('TAVALLINEN', 'MODERAATTORI', 'YLLAPITAJA');

create table jasenet (
    tunnus          varchar(24)     primary key,
    rekisteroity    date            not null,
--     numero          serial          unique not null,
    tiiviste        char(128)       not null,
    suola           char(128)       not null,
    sposti          varchar(64)     not null,
    taso            Kayttajataso    not null,
    nimimerkki      varchar(32),
    avatar          varchar(128),
    kuvaus          varchar(512)
);

create table porttikiellot (
    kohde           varchar(24)     primary key references jasenet,
    asetettu        date            not null,
    kesto           int             not null
);

create table alueet (
    tunnus          serial          primary key, -- Tilan säästämiseksi yhd. taulussa
    nimi            varchar(128)    unique not null,
    kuvaus          varchar(384),
    lukittu         date,
    poistettu       date
);

create table ketjut (
    tunnus          serial          primary key,
    aihe            varchar(128)    not null,
    siirretty       date,
    moderoitu       date,
    lukittu         date,
    poistettu       date
);

create table ketjujen_sijainnit (
    alue_id         serial          references alueet,
    ketju_id        serial          references ketjut,
    primary key (alue_id, ketju_id)
);

create table viestit (
    ketju_id        serial          references ketjut,
    numero          integer,
    kirjoittaja     varchar(24)     references jasenet not null,
    kirjoitettu     date            not null,
    muokattu        date,
    moderoitu       date,
    poistettu       date,
    sisalto         text,
    primary key (ketju_id, numero)
);