create type Kayttajataso as enum ('TAVALLINEN', 'MODERAATTORI', 'YLLAPITAJA');

create table jasenet (
    numero          serial          primary key, --Redundantti, tietoturvallinen
    tunnus          varchar(32)     unique not null,
    rekisteroity    timestamp       not null,
    tiiviste        char(128)       unique not null,
    suola           char(128)       unique not null,
    sposti          varchar(64)     not null,
    taso            Kayttajataso    not null,
    nimimerkki      varchar(32)     unique,
    avatar          varchar(128),
    kuvaus          varchar(512),
    viesteja        integer         -- johdettu attribuutti
);

create table porttikiellot (
    kohde           serial     	    primary key references jasenet on delete cascade,
    asetettu        timestamp       not null,
    kesto           int             not null
);

create table alueet (
    tunnus          serial          primary key, -- Tilan sääst. yhd. taulussa
    nimi            varchar(128)    unique not null,
    kuvaus          varchar(384)    unique,
    lukittu         timestamp,
    poistettu       timestamp
);

create table ketjut (
    tunnus          serial          primary key, -- Tilan sääst. yhd. taulussa
    aihe            varchar(128)    unique not null,
    muutettu        timestamp, -- Johdettu attribuutti. Viim. viestin lisäys/muokkaus
    siirretty       timestamp,
    moderoitu       timestamp,
    lukittu         timestamp,
    poistettu       timestamp
);

-- Huom. Ketjun osallistuminen yhteyteen on täydellistä. (Muista valvoa javassa.)
create table ketjujen_sijainnit (
    alue_id         serial          references alueet on delete cascade,
    ketju_id        serial          references ketjut on delete cascade,
    primary key (alue_id, ketju_id)
);

create table viestit (
    ketju_id        serial          references ketjut on delete cascade,
    numero          integer,
    kirjoittaja     serial          references jasenet on delete cascade,
    kirjoitettu     timestamp       not null,
    muokattu        timestamp,
    moderoitu       timestamp,
    poistettu       timestamp,
    sisalto         text            not null,
    primary key (ketju_id, numero)
);
