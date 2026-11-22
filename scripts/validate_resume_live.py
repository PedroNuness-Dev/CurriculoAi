"""Opt-in HTTP smoke test with fictional data; uses the server's configured AI provider.
Run: python scripts/validate_resume_live.py --live
Artifacts go to the ignored backend target directory. Does not touch existing users.
"""
import argparse
import json
import secrets
import time
import urllib.request
import urllib.error
from pathlib import Path

parser = argparse.ArgumentParser()
parser.add_argument('--live', action='store_true')
parser.add_argument('--base', default='http://localhost:8080')
args = parser.parse_args()
if not args.live:
    parser.error('--live is required: this calls the AI provider and creates a fictional local account')
out = Path(__file__).resolve().parents[1] / 'backend-spring/target/live-validation'
out.mkdir(parents=True, exist_ok=True)
token = None

def call(path, payload=None, content_type='application/json', attempt=0):
    headers = {'Content-Type': content_type}
    if token:
        headers['Authorization'] = 'Bearer ' + token
    data = payload if isinstance(payload, bytes) else json.dumps(payload, ensure_ascii=False).encode('utf-8') if payload is not None else None
    req = urllib.request.Request(args.base + path, data=data, headers=headers, method='POST' if payload is not None else 'GET')
    try:
        with urllib.request.urlopen(req, timeout=240) as response:
            body = response.read()
            return body if 'application/pdf' in response.headers.get('Content-Type', '') else json.loads(body)
    except urllib.error.HTTPError as exc:
        if exc.code in (429, 503) and attempt < 3:
            print(f'{path}: HTTP {exc.code}; retry after 60s', flush=True)
            time.sleep(60)
            return call(path, payload, content_type, attempt + 1)
        raise RuntimeError(f'{path}: HTTP {exc.code}: {exc.read().decode()}') from None

identity = {'name': 'Marina Teste de Qualidade', 'email': 'validation-' + secrets.token_hex(5) + '@example.com', 'password': secrets.token_urlsafe(24)}
call('/api/users', identity)
token = call('/api/auth/login', {'email': identity['email'], 'password': identity['password']})['token']
themes = [
    ('Integração de pedidos', 'Implementei API Java com Spring Boot e PostgreSQL. Modelei pedidos e itens, criei validação de entradas e testes de integração. Investiguei duplicidade em reenvios e implementei idempotência por chave de requisição. Documentei decisões e erros HTTP.'),
    ('Processamento assíncrono', 'Desenvolvi em Python um consumidor de tarefas com retentativas limitadas e fila de mensagens rejeitadas. Reproduzi falhas de rede em testes e registrei correlação de eventos para diagnosticar problemas. Não medi impacto financeiro.'),
    ('Busca documental', 'Integrei recuperação de trechos de documentos a uma API de linguagem. Separei fontes e instruções e montei 40 casos de avaliação manual. Registrei respostas sem evidência e apresentei as fontes junto à resposta. Não treinei modelos.'),
    ('Observabilidade', 'Instrumentei endpoints com logs estruturados e métricas de latência em um ambiente de estudo. Automatizei execução dos testes em GitHub Actions e empacotei a aplicação em Docker. Documentei como reproduzir o ambiente local.'),
]
projects = []
for i in range(32):
    title, detail = themes[i % len(themes)]
    description = detail + ' Projeto pessoal de estudo, sem vínculo empregatício. ' + ('Durante o desenvolvimento comparei alternativas, registrei hipóteses, reproduzi erros com dados de teste e expliquei no README como executar e verificar os cenários. ' * 8)
    projects.append({'title': f'{title} - estudo {i+1}', 'description': description, 'link': f'https://example.com/portfolio/{i+1}', 'abilitiesIds': []})
projects.append({'title': 'Leitura de exemplo Spring', 'description': 'Estudei o exemplo público de serviço REST. Não sou autora deste repositório; usei-o como referência de aprendizado.', 'link': 'https://github.com/spring-guides/gs-rest-service', 'abilitiesIds': []})
call('/api/projects', projects)
profile = call('/api/users/me/profile')
(out / 'profile.json').write_text(json.dumps(profile, ensure_ascii=False, indent=2), encoding='utf-8')
print('Profile:', len(projects), 'projects;', len(json.dumps(profile)), 'characters', flush=True)
insights = call('/api/cerebras/profile/study', {})
(out / 'insights.json').write_text(json.dumps(insights, ensure_ascii=False, indent=2), encoding='utf-8')
assert insights, 'Expected evidence-backed insights for this rich profile'
print('Insights:', len(insights), flush=True)
job = 'Desenvolvimento backend Java. Obrigatórios: Java, Spring Boot, PostgreSQL, APIs REST e testes de integração. Desejáveis: Docker e Kubernetes. Trabalho com idempotência e diagnóstico de falhas.'
for mode in ('without-job', 'with-job'):
    options = {'answers': 'Meus projetos são pessoais e acadêmicos. Minha contribuição foi implementar e testar os cenários descritos, exceto o exemplo Spring público, que apenas estudei. Não tenho experiência profissional nem liderança de equipe.'}
    if mode == 'with-job': options['jobDescription'] = job
    pdf = call('/api/cerebras/curriculum', options)
    assert pdf.startswith(b'%PDF'), 'Expected a PDF'
    (out / (mode + '.pdf')).write_bytes(pdf)
    boundary = 'ResumeBoundary' + secrets.token_hex(12)
    body = f'--{boundary}\r\nContent-Disposition: form-data; name="file"; filename="resume.pdf"\r\nContent-Type: application/pdf\r\n\r\n'.encode() + pdf + b'\r\n'
    if mode == 'with-job':
        body += f'--{boundary}\r\nContent-Disposition: form-data; name="jobDescription"\r\n\r\n{job}\r\n'.encode()
    body += f'--{boundary}--\r\n'.encode()
    analysis = call('/api/cerebras/curriculum/analysis', body, 'multipart/form-data; boundary=' + boundary)
    assert ('compatibility' in analysis) == (mode == 'with-job')
    for key in ('structureScore', 'contentScore'):
        assert 0 <= analysis[key]['value'] <= 100 and analysis[key]['rationale']
    (out / (mode + '-analysis.json')).write_text(json.dumps(analysis, ensure_ascii=False, indent=2), encoding='utf-8')
    print(mode, 'PDF bytes:', len(pdf), 'scores:', analysis['structureScore']['value'], analysis['contentScore']['value'], flush=True)
print('Artifacts:', out, flush=True)
