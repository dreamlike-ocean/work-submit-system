create table login_user
(
    id       bigint auto_increment
        primary key,
    username varchar(36) not null comment 'username',
    password varchar(36) not null,
    role     varchar(36) not null,
    constraint key_name
        unique (username)
)
    charset = latin1;

create index index_name
    on login_user (password);

INSERT INTO worksubmit.login_user (id, username, password, role) VALUES (1, '13', 'password', 'ADMIN');
INSERT INTO worksubmit.login_user (id, username, password, role) VALUES (2, 'dreamlike', 'password', 'STUDENT');
INSERT INTO worksubmit.login_user (id, username, password, role) VALUES (3, 'ad1', 'ads', 'ADMIN');
INSERT INTO worksubmit.login_user (id, username, password, role) VALUES (4, 'sunchenxi', 'ads', 'TEACHER');
INSERT INTO worksubmit.login_user (id, username, password, role) VALUES (5, 'new', 'ads', 'TEACHER');
INSERT INTO worksubmit.login_user (id, username, password, role) VALUES (6, 'adi', 'ads', 'ADMIN');
INSERT INTO worksubmit.login_user (id, username, password, role) VALUES (7, 'DDADAWDAWD', 'ads', 'STUDENT');
