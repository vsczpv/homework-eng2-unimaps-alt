CREATE TABLE Usuario (
	id_usuario SERIAL,
	nome VARCHAR,
	email VARCHAR,
	isAdmin BOOLEAN,
	
	PRIMARY KEY (id_usuario)
);

CREATE TABLE Categoria (
	id_categoria SERIAL,
	nome VARCHAR,
	
	PRIMARY KEY (id_categoria)
);

CREATE TABLE Servico (
	id_servico SERIAL,
	idf_categoria INTEGER,
	idf_dono INTEGER,
	nome VARCHAR,
	nome_dono VARCHAR,
	horario_aberto TIME,
	horario_fechado TIME,
	horario_pico TIME,
	latitute DECIMAL,
	longitude DECIMAL,
	nota INTEGER,
	icone BYTEA,
	banner BYTEA,
	local VARCHAR,
	complemento VARCHAR,
	
	PRIMARY KEY (id_servico),
	FOREIGN KEY (idf_categoria) REFERENCES Categoria(id_categoria)
		ON UPDATE CASCADE,
	FOREIGN KEY (idf_dono) REFERENCES Usuario(id_usuario)
		ON UPDATE CASCADE
);

CREATE TABLE Item (
	id_item SERIAL,
	idf_servico INTEGER,
	nome VARCHAR,
	tipo VARCHAR,
	preco DECIMAL,
	
	PRIMARY KEY (id_item),
	FOREIGN KEY (idf_servico) REFERENCES Servico(id_servico)
		ON UPDATE CASCADE
		ON DELETE CASCADE
);

CREATE TABLE Contribuicao (
	id_contribuicao SERIAL,
	idf_usuario INTEGER,
	idf_servico INTEGER,
	conteudo VARCHAR,
	avaliacao INTEGER,
	informacao_alvo VARCHAR,
	nova_informacao VARCHAR,
	
	PRIMARY KEY (id_contribuicao),
	FOREIGN KEY (idf_usuario) REFERENCES Usuario(id_usuario)
		ON UPDATE CASCADE,
	FOREIGN KEY (idf_servico) REFERENCES Servico(id_servico)
		ON UPDATE CASCADE
		ON DELETE CASCADE
);

CREATE TABLE Comentario (
	id_comentario SERIAL,
	idf_usuario INTEGER,
	idf_servico INTEGER,
	conteudo VARCHAR,
	avaliacao INTEGER,
	
	PRIMARY KEY (id_comentario),
	FOREIGN KEY (idf_usuario) REFERENCES Usuario(id_usuario)
		ON UPDATE CASCADE
		ON DELETE CASCADE,
	FOREIGN KEY (idf_servico) REFERENCES Servico(id_servico)
		ON UPDATE CASCADE
		ON DELETE CASCADE
);

CREATE TABLE Catalogo (

	id_item SERIAL,
	idf_servico INTEGER,
	nome VARCHAR,
	preco DECIMAL,
	icone BYTEA,

	PRIMARY KEY(id_item),
	FOREIGN KEY (idf_servico) REFERENCES Servico(id_servico)
		ON UPDATE CASCADE
		ON DELETE CASCADE

);

--INSERT INTO Categoria(nome) VALUES ('banana');

CREATE USER default_user WITH PASSWORD '1234';
GRANT SELECT ON Categoria to default_user;
GRANT SELECT, INSERT, UPDATE, DELETE ON Comentario to default_user;
GRANT USAGE, SELECT ON SEQUENCE comentario_id_comentario_seq TO default_user;
GRANT SELECT ON Item to default_user;
GRANT SELECT ON Servico to default_user;
GRANT SELECT ON Usuario to default_user;
GRANT SELECT ON Catalogo to default_user;

--REVOKE ALL PRIVILEGES ON ALL TABLES IN SCHEMA public FROM default_user
--REVOKE ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public FROM default_user

--INSERT INTO usuario(nome,email,isadmin) VALUES ('fred','fred1',false)
--INSERT INTO servico(idf_categoria,nome) VALUES (1,'teste123')

--UPDATE Servico SET icone = pg_read_binary_file('C:\Program Files\PostgreSQL\16\img\Uno_reverse.webp') WHERE id_servico = 1

--SELECT * FROM Categoria
--SELECT * FROM Servico
--SELECT * FROM usuario
--SELECT * FROM Comentario
