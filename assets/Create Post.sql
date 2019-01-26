use note;

create table note(
note_id int not null auto_increment,
user_id int not null,
note_title varchar(20) not null,
note_content text not null,
note_date date not null,
note_type int not null,
primary key(note_id),
FOREIGN KEY (user_id) REFERENCES users(user_id)
);

insert into note(user_id,note_title,note_content,note_date,note_type) 
values (1, "Title", "Content", "date", 1);