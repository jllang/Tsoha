create type Kayttajataso as enum ('TAVALLINEN', 'MODERAATTORI', 'YLLAPITAJA');

create table jasenet (
    tunnus          varchar(64)     primary key,
    rekisteroity    date            not null,
    tiiviste        char(64)        not null,
    sposti          varchar(128)    not null,
    taso            Kayttajataso    not null,
    nimimerkki      varchar(64),
    avatar          varchar(192),
    kuvaus          varchar(512)
);

create table porttikiellot (
    kohde           varchar(64)     primary key references jasenet,
    asetettu        date            not null,
    kesto           int             not null
);

create table alueet (
    nimi            varchar(128)    primary key,
    kuvaus          varchar(384),
    lukittu         date,
    poistettu       date
);

create table ketjut (
    tunnus          integer         primary key,
    aihe            varchar(128)    not null,
    lukittu         date,
    poistettu       date
);

create table ketjujen_sijainnit (
    alue_id         varchar(128)    references alueet,
    ketju_id        integer         references ketjut,
    primary key (alue_id, ketju_id)
);

create table viestit (
    ketju_id        integer         references ketjut,
    numero          integer,
    kirjoittaja     varchar(64)     references jasenet not null,
    kirjoitettu     date            not null,
    muokattu        date,
    moderoitu       date,
    poistettu       date,
    primary key (ketju_id, numero)
);