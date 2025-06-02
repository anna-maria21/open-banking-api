insert into client (first_name, last_name, second_name, birth_date) values
    ('Test Name1', 'Test Surname1', 'Test Second Name1', '2000-10-10'),
    ('Test Name2', 'Test Surname2', 'Test Second Name2', '2000-10-11');

insert into currency (code, short_name) values
    ('UAH', 'Гривня');

insert into account (client_id, iban, currency_id, balance) values
    (1, 'UA21322313000000260000004111', 1, 10000),
    (2, 'UA21322313000000260000004222', 1, 20000);

insert into transaction (account_from, currency_id_from, account_to, currency_id_to, sum, status, changed_at) values
      (1, 1, 2, 1, 100, 'PAID', '2025-06-01T15:00:00'),
      (2, 1, 1, 1, 1000, 'PAID', '2025-06-01T16:00:00');
