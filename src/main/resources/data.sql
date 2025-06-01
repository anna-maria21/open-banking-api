insert into Client (first_name, last_name, second_name, birth_date) values
        ('Test Name1', 'Test Surname1', 'Test Second Name1', '2000-10-10'),
        ('Test Name2', 'Test Surname2', 'Test Second Name2', '2000-10-11'),
        ('Test Name3', 'Test Surname3', 'Test Second Name3', '2000-10-13'),
        ('Test Name4', 'Test Surname4', 'Test Second Name4', '2000-10-14');

insert into currency (code, short_name) values
        ('USD', 'US Dollar'),
        ('EUR', 'Euro'),
        ('UAH', 'Гривня');

insert into account (client_id, iban, currency_id, balance) values
        (1, 'UA21322313000000260000004111', 1, 1000),
        (2, 'UA21322313000000260000004222', 2, 2000),
        (3, 'UA21322313000000260000004333', 3, 30000),
        (4, 'UA21322313000000260000004444', 2, 40000),
        (4, 'UA21322313000000260000004555', 3, 500000);

insert into transaction (account_from, currency_id_from, account_to, currency_id_to, sum, status, changed_at) values
        (1, 1, 3, 3, 100, 'PAID', '2025-06-01T15:00:00'),
        (2, 2, 4, 2, 1000, 'PAID', '2025-06-01T16:00:00'),
        (3, 3, 5, 3, 10000, 'PAID', '2025-06-01T12:00:00'),
        (4, 2, 2, 2, 1000, 'CANCELED', '2025-06-01T15:15:00'),
        (5, 3, 3, 3, 10000, 'PAID', '2025-06-01T10:00:00'),
        (5, 3, 2, 2, 1000, 'PAID', '2025-06-01T10:00:30'),
        (2, 2, 1, 1, 10000, 'CANCELED', '2025-06-01T11:00:00');
