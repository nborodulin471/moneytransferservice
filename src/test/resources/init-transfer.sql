insert into card (number, balance, cvv, valid_till) values ('20200222222222', 10000, '123', '10-10-2022');
insert into card (number, balance, cvv, valid_till) values ('20204444444444', 10000, '123', '10-10-2022');
insert into transfer (id, card_to_number, card_from_number, "value" , currency)
values (-1, '20200222222222', '20204444444444', 10, 'rur');
commit ;