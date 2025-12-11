-- Script para verificar o usuário administrador padrão

-- Selecionar o banco de dados
USE academia;

-- Verificar se o usuário ADM existe
SELECT * FROM usuarios WHERE email = 'ADM';

-- Contar total de usuários
SELECT COUNT(*) as total_usuarios FROM usuarios;

-- Listar todos os usuários ADMIN
SELECT id, nome, email, tipo FROM usuarios WHERE tipo = 'ADMIN';

-- Verificar estrutura da tabela usuarios
DESCRIBE usuarios;
