# Continuidade — criação e análise de currículos

Atualizado em 21/11/2026. Trabalho local na branch develop. Nenhum push realizado.

## Pedido
Melhorar criação e análise; vaga opcional; notas separadas de estrutura, conteúdo e compatibilidade; investigar competências do desenvolvedor com evidências; vários commits durante o trabalho.

## Implementado
- 75145f0a: notas structureScore/contentScore e compatibility.score, com valor 0–100 ou null e justificativa. Schema JSON estrito, validação no backend e apresentação separada na análise web.
- d3853d8a: POST /api/cerebras/profile/study investiga o perfil autenticado e devolve até seis hipóteses com competência, trecho literal, interpretação e pergunta. Trechos ausentes do perfil são descartados.
- Geração faz estudo do perfil antes da escrita. O prompt exige seleção, conexão entre experiências e projetos, contribuições específicas e proíbe métricas/qualificações inventadas.
- POST /api/cerebras/curriculum aceita corpo JSON opcional com jobDescription e answers (até 16 mil caracteres cada). Chamadas antigas sem corpo continuam válidas.
- Tela de geração permite investigar perfil, responder perguntas e enviar vaga opcional. Respostas valem para a geração, sem persistência no cadastro.
- Correção de codificação dos arquivos modificados, incluída no segundo commit.
- Testes de análise atualizados e ProfileStudyTest criado para evidência ausente e limites/opcionalidade.

## Validação realizada
- npm.cmd run build: passou (TypeScript e Vite).
- Suíte Maven completa passou com SPRING_PROFILES_ACTIVE=test e ALLOWED_ORIGINS=http://localhost:5173, antes de adicionar ProfileStudyTest.
- Após adicionar os testes, execução focada de ProfileStudyTest e CurriculumAnalysisTest: 9 testes passaram.
- Primeiro teste sem perfil falhou porque H2 precisa do modo PostgreSQL para JSONB. Usar o perfil test.
- Maven Wrapper funciona fora do sandbox; dentro dele houve limitações de cache e ambiente.

## Limitações e próximos passos de validação
- Ainda falta testar o novo fluxo completo com resposta real da Cerebras e revisar visualmente o PDF resultante. Não considerar os testes com mocks prova da qualidade editorial.
- Notas são estimativas editoriais com rubrica no prompt, não resultados de um ATS real. Estrutura avalia texto extraído; não avalia layout visual. Compatibilidade sem requisitos identificáveis recebe null.
- Estudo considera somente perfil/projetos cadastrados ou importados. Não lê código remoto nem comprova autoria/qualidade de repositórios.
- Confirmação pelo candidato é textual. Não existe confirmação individual persistida de cada descoberta.
- Geração usa duas chamadas de IA; investigar manualmente e depois gerar repete o estudo. Considerar cache por versão do perfil no futuro.
- Nenhum teste de volume extremo ou benchmark editorial foi realizado.
- Aplicação local usa H2 em memória no perfil test: reinícios podem apagar dados. Um backup anterior existe em %TEMP%/curriculoai-profile-backup.sql; contém dados pessoais e nunca deve ser commitado. Verificar o estado antes de qualquer restauração.
- Chave fica em backend-spring/.env ignorado pelo Git. Uma variável CEREBRAS_API_KEY herdada sobrescrevia a chave correta; não registrar segredos. Reinicialização anterior limpou essa variável e teste direto à Cerebras retornou HTTP 200.
- Arquivos instagram-sem-retorno*.txt são preexistentes e não fazem parte deste trabalho.

## Retomada
1. Ler este arquivo e git status/log.
2. Usar perfil test para validações Maven e manter segredos fora dos logs.
3. Validar geração real com/sem vaga, descobertas e notas na interface, preservando dados locais.
4. Comparar PDF gerado com fatos do perfil; procurar omissões, repetições e inferências sem confirmação.
