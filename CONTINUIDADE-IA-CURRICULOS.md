# Estado atual — 21/11/2026

Esta seção substitui as pendências históricas abaixo.

## Concluído nesta retomada
- Prompts antigos contraditórios corrigidos: geração estuda trajetória e análise fornece notas separadas com justificativas.
- Coleta de amostras públicas de até 3 repositórios vinculados, até 3 arquivos por repositório, 4 mil caracteres por trecho. Guarda caminho e SHA; falhas do GitHub não bloqueiam o perfil. Não comprova autoria ou proficiência.
- UTF-8 explícito na integração com Cerebras. O erro descartava evidências acentuadas; teste real passou após a correção.
- Compactação de frases/descritivos repetidos sem eliminar os links dos projetos; saída limitada a 4096 tokens.
- Estudo do mesmo contexto reutilizado por até 5 minutos, limitado a 100 entradas; não mistura perfis porque a chave cobre o contexto completo.
- Revisão factual adicional, separando rascunho de fatos confirmados. Código remoto é excluído das fontes que podem comprovar realizações pessoais nessa revisão.
- Para seções de projetos com links cadastrados, o texto final usa até três frases dos fatos originais, removendo frases iniciadas por “Não ”. Isso impede que a reescrita acrescente ferramentas aos bullets desses projetos.
- Sugestões com citações não encontradas são descartadas. Exemplos que inventam números ou contatos/URLs são removidos. Resposta inválida recebe no máximo uma tentativa adicional.
- Limites temporários da Cerebras retornam 429, com orientação para aguardar, em vez de serem confundidos com erro de quota financeira.
- PDF normaliza hífens/espaços especiais e usa espaçamento mais compacto.

## Validação
- Backend: 88 testes contabilizados, zero falhas/erros, um teste de provedor ignorado por padrão (opt-in). Portanto 87 executados com sucesso.
- Frontend: npm.cmd run build passou.
- Teste real HTTP com autenticação, 33 projetos fictícios e aproximadamente 60.933 caracteres no perfil; descoberta, geração PDF e análise com/sem vaga. Os dois cenários passaram em execuções completas. Houve limites temporários de tokens e retentativas espaçadas.
- Artefatos e respostas ficam em backend-spring/target/live-validation (ignorado): profile.json, insights.json, without-job.pdf, with-job.pdf e respectivos JSONs de análise. São dados fictícios; não são o currículo real do usuário.
- PDFs renderizados com pdftoppm e revisados visualmente; várias correções editoriais e de codificação surgiram desse teste.
- scripts/validate_resume_live.py --live reproduz o teste e cria conta fictícia; chama IA real e consome quota. Não executar sem intenção explícita de teste.

## Limites que permanecem explícitos
- As notas são estimativas da IA, não resultados de ATS. A pontuação pode variar e não garante aprovação.
- Resumo e habilidades ainda são texto generativo e exigem revisão humana. A proteção determinística cobre projetos reconhecidos por links, não uma prova semântica universal do currículo.
- O teste de volume cobre um perfil extenso com repetição; não demonstra suporte a volume ilimitado ou a todos os perfis profissionais.
- Descobertas continuam hipóteses com evidências/perguntas; confirmação é textual e não persistida individualmente no cadastro.
- Leitura GitHub é amostral de arquivos públicos, nunca auditoria completa nem atribuição de autoria.
- Teste funcional final foi via HTTP; não houve validação completa da interação visual de todas as telas web.
- Não houve push ou PR nesta retomada. Commits pequenos foram feitos ao longo das correções (git log é a fonte atual dos hashes).

---

## Histórico anterior (preservado para contexto)

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
