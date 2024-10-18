create table flash_sale.flash_sale_tokens
(
    item_id varchar(32) not null,
    token   varchar(64) not null,
    status  varchar(16) not null,
    primary key (item_id, token),
    constraint flash_sale_tokens_pk
        unique (token)
);



create table flash_sale.transaction_2_flash_sale_token
(
    transaction_id varchar(32) not null
        primary key,
    token          varchar(64) not null,
    constraint transaction_2_flash_sale_token_pk
        unique (token),
    constraint transaction_2_flash_sale_token_flash_sale_tokens_token_fk
        foreign key (token) references flash_sale.flash_sale_tokens (token)
);

