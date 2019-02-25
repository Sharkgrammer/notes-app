use note;

create table users(
user_id int not null auto_increment,
user_email varchar(50),
user_pass varchar(255),
user_key varchar(255),
primary key(user_id)
);