-- =============================================
-- V2__inserts_initial_data.sql
-- Initial data for the system CurriculoAi
-- =============================================

-- Area
INSERT INTO area (name) VALUES ('Tecnologia');
INSERT INTO area (name) VALUES ('Saúde');
INSERT INTO area (name) VALUES ('Direito');
INSERT INTO area (name) VALUES ('Educação');
INSERT INTO area (name) VALUES ('Engenharia');
INSERT INTO area (name) VALUES ('Administração');
INSERT INTO area (name) VALUES ('Marketing');
INSERT INTO area (name) VALUES ('Finanças');
INSERT INTO area (name) VALUES ('Recursos Humanos');
INSERT INTO area (name) VALUES ('Contabilidade');
INSERT INTO area (name) VALUES ('Arquitetura e Urbanismo');
INSERT INTO area (name) VALUES ('Design');
INSERT INTO area (name) VALUES ('Comunicação');
INSERT INTO area (name) VALUES ('Jornalismo');
INSERT INTO area (name) VALUES ('Publicidade e Propaganda');
INSERT INTO area (name) VALUES ('Logística');
INSERT INTO area (name) VALUES ('Comércio Exterior');
INSERT INTO area (name) VALUES ('Agronegócio');
INSERT INTO area (name) VALUES ('Meio Ambiente');
INSERT INTO area (name) VALUES ('Segurança');
INSERT INTO area (name) VALUES ('Construção Civil');
INSERT INTO area (name) VALUES ('Indústria');
INSERT INTO area (name) VALUES ('Varejo');
INSERT INTO area (name) VALUES ('Turismo e Hotelaria');
INSERT INTO area (name) VALUES ('Gastronomia');
INSERT INTO area (name) VALUES ('Arte e Cultura');
INSERT INTO area (name) VALUES ('Esporte');
INSERT INTO area (name) VALUES ('Ciências');
INSERT INTO area (name) VALUES ('Pesquisa e Desenvolvimento');
INSERT INTO area (name) VALUES ('Serviço Social');
INSERT INTO area (name) VALUES ('Backend');
INSERT INTO area (name) VALUES ('Frontend');
INSERT INTO area (name) VALUES ('Full Stack');
INSERT INTO area (name) VALUES ('Mobile');
INSERT INTO area (name) VALUES ('DevOps');
INSERT INTO area (name) VALUES ('Data Engineering');
INSERT INTO area (name) VALUES ('QA');
INSERT INTO area (name) VALUES ('UI/UX Engineering');
INSERT INTO area (name) VALUES ('Software Engineering');
INSERT INTO area (name) VALUES ('Segurança da informação');
INSERT INTO area (name) VALUES ('Machine Learning');

-- Enterprise
INSERT INTO enterprise (name) VALUES ('Accenture');
INSERT INTO enterprise (name) VALUES ('Sicap');
INSERT INTO enterprise (name) VALUES ('Google');
INSERT INTO enterprise (name) VALUES ('Amazon');
INSERT INTO enterprise (name) VALUES ('Microsoft');
INSERT INTO enterprise (name) VALUES ('Itaú');
INSERT INTO enterprise (name) VALUES ('Nubank');
INSERT INTO enterprise (name) VALUES ('Magazine Luiza');
INSERT INTO enterprise (name) VALUES ('Outra');

-- Institution
INSERT INTO institution (name) VALUES ('DIO');
INSERT INTO institution (name) VALUES ('Alura');
INSERT INTO institution (name) VALUES ('Coursera');
INSERT INTO institution (name) VALUES ('Udemy');
INSERT INTO institution (name) VALUES ('SENAC');
INSERT INTO institution (name) VALUES ('SENAI');
INSERT INTO institution (name) VALUES ('Outra');

-- Role
INSERT INTO role (type) VALUES ('ROLE_CANDIDATO');
INSERT INTO role (type) VALUES ('ROLE_RECRUTADOR');

-- Language
INSERT INTO language (name) VALUES ('PORTUGUES');
INSERT INTO language (name) VALUES ('INGLES');
INSERT INTO language (name) VALUES ('ESPANHOL');
INSERT INTO language (name) VALUES ('FRANCES');
INSERT INTO language (name) VALUES ('ALEMAO');
INSERT INTO language (name) VALUES ('ITALIANO');
INSERT INTO language (name) VALUES ('MANDARIM');
INSERT INTO language (name) VALUES ('JAPONES');

-- Ability
INSERT INTO ability (name) VALUES ('Java');
INSERT INTO ability (name) VALUES ('Python');
INSERT INTO ability (name) VALUES ('JavaScript');
INSERT INTO ability (name) VALUES ('TypeScript');
INSERT INTO ability (name) VALUES ('Spring Boot');
INSERT INTO ability (name) VALUES ('React');
INSERT INTO ability (name) VALUES ('Angular');
INSERT INTO ability (name) VALUES ('Docker');
INSERT INTO ability (name) VALUES ('Kubernetes');
INSERT INTO ability (name) VALUES ('SQL');
INSERT INTO ability (name) VALUES ('Git');
INSERT INTO ability (name) VALUES ('AWS');
INSERT INTO ability (name) VALUES ('Azure');
INSERT INTO ability (name) VALUES ('Linux');
INSERT INTO ability (name) VALUES ('Scrum');

-- Social network
INSERT INTO social_network (name) VALUES ('LinkedIn');
INSERT INTO social_network (name) VALUES ('GitHub');
INSERT INTO social_network (name) VALUES ('Instagram');
INSERT INTO social_network (name) VALUES ('Twitter');
INSERT INTO social_network (name) VALUES ('Portfolio');
INSERT INTO social_network (name) VALUES ('Behance');
INSERT INTO social_network (name) VALUES ('Dribbble');

-- Course
INSERT INTO course (name) VALUES ('Análise e Desenvolvimento de Sistemas');
INSERT INTO course (name) VALUES ('Direito');
INSERT INTO course (name) VALUES ('Psicologia');
INSERT INTO course (name) VALUES ('Ciência da Computação');
INSERT INTO course (name) VALUES ('Engenharia de Software');
INSERT INTO course (name) VALUES ('Sistemas de Informação');
INSERT INTO course (name) VALUES ('Administração');
INSERT INTO course (name) VALUES ('Medicina');
INSERT INTO course (name) VALUES ('Enfermagem');
INSERT INTO course (name) VALUES ('Engenharia Civil');
INSERT INTO course (name) VALUES ('Arquitetura');
INSERT INTO course (name) VALUES ('Ciência de dados');
INSERT INTO course (name) VALUES ('Inteligência artificial');
INSERT INTO course (name) VALUES ('Segurança da informação');
INSERT INTO course (name) VALUES ('Design Gráfico');
INSERT INTO course (name) VALUES ('Engenharia de controle e automação');
INSERT INTO course (name) VALUES ('Outro');