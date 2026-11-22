"""Build the static ability migration from official ESCO Portuguese skill labels."""
import json
import html
import re
import urllib.parse
import urllib.request
from pathlib import Path

TERMS = [
    "comunicação", "liderança", "trabalho em equipa", "resolução de problemas",
    "negociação", "planeamento", "organização", "criatividade", "adaptação",
    "programação", "software", "dados", "análise", "segurança", "redes",
    "engenharia", "design", "marketing", "vendas", "atendimento", "finanças",
    "contabilidade", "educação", "ensino", "saúde", "enfermagem", "medicina",
    "farmácia", "logística", "transporte", "construção", "eletricidade",
    "manutenção", "produção", "qualidade", "agricultura", "alimentação",
    "hotelaria", "pesquisa", "gestão", "direito", "ambiente", "idiomas",
]
EXISTING = {s.casefold() for s in ["Java", "Python", "JavaScript", "TypeScript", "Spring Boot", "React", "Angular", "Docker", "Kubernetes", "SQL", "Git", "AWS", "Azure", "Linux", "Scrum"]}
SOFT = [
    "Comunicação verbal", "Comunicação escrita", "Escuta ativa", "Empatia",
    "Colaboração", "Trabalho em equipe", "Liderança", "Gestão de conflitos",
    "Resolução de problemas", "Pensamento crítico", "Criatividade", "Adaptabilidade",
    "Organização", "Gestão do tempo", "Proatividade", "Autonomia",
    "Negociação", "Inteligência emocional", "Atenção aos detalhes",
    "Tomada de decisão", "Aprendizagem contínua", "Responsabilidade",
    "Pensamento analítico", "Planejamento", "Flexibilidade", "Resiliência",
    "Comunicação interpessoal", "Orientação a resultados", "Ética profissional",
    "Mentoria", "Facilitação", "Mediação", "Feedback construtivo",
    "Apresentação em público", "Persuasão", "Gestão de prioridades",
    "Pensamento estratégico", "Colaboração interdisciplinar", "Atendimento ao cliente",
    "Relacionamento com clientes", "Solução de conflitos", "Iniciativa",
    "Gestão de equipes", "Delegação", "Coordenação", "Pontualidade",
    "Disciplina", "Comunicação assertiva", "Sensibilidade cultural",
    "Orientação ao cliente", "Capacidade de síntese", "Curiosidade",
    "Raciocínio lógico", "Visão sistêmica", "Autogestão", "Paciência",
    "Confiabilidade", "Diplomacia", "Facilidade de aprendizado",
    "Gestão de mudanças", "Motivação de equipes", "Construção de consenso",
    "Pensamento inovador", "Priorização", "Senso de urgência",
    "Abertura a feedback", "Comunicação não violenta", "Cooperação",
    "Gestão de expectativas", "Oratória", "Atenção concentrada",
    "Orientação para qualidade", "Capacidade de negociação", "Autocontrole",
    "Postura profissional", "Relacionamento interpessoal", "Capacidade de adaptação",
    "Gestão de estresse", "Análise de problemas", "Liderança situacional",
]

def main():
    groups = []
    for term in TERMS:
        query = urllib.parse.urlencode({"text": term, "type": "skill", "language": "pt", "limit": 100, "selectedVersion": "v1.2.0"})
        request = urllib.request.Request("https://ec.europa.eu/esco/api/search?" + query, headers={"User-Agent": "CurriculoAi/1.0"})
        with urllib.request.urlopen(request, timeout=30) as response:
            results = json.load(response).get("_embedded", {}).get("results", [])
        group = []
        for result in results:
            name = " ".join(html.unescape(re.sub(r"<[^>]+>", "", result.get("title", ""))).split())
            if name and len(name) <= 100 and name.casefold() not in EXISTING:
                group.append(name)
        groups.append(group)
        print(term, len(group), flush=True)
    names = []
    seen = set(EXISTING)
    for name in SOFT:
        if name.casefold() not in seen:
            names.append((name, "SOFT"))
            seen.add(name.casefold())
    for index in range(100):
        for group in groups:
            if index >= len(group):
                continue
            name = group[index]
            if name.casefold() not in seen:
                names.append((name, "HARD"))
                seen.add(name.casefold())
            if len(names) == 985:
                break
        if len(names) == 985:
            break
    if len(names) != 985:
        raise RuntimeError(f"Only {len(names)} new abilities available")
    lines = ["-- 985 additional abilities; 15 base abilities in V2 = 1,000 total.", "-- Professional labels: ESCO API v1.2.0, Portuguese; behavioural terms curated in Portuguese.", "ALTER TABLE ability ADD COLUMN category VARCHAR(20) NOT NULL DEFAULT 'HARD';", ""]
    for name, category in names:
        escaped = name.replace("'", "''")
        lines.append(f"INSERT INTO ability (name, category) SELECT '{escaped}', '{category}' WHERE NOT EXISTS (SELECT 1 FROM ability WHERE LOWER(name) = LOWER('{escaped}'));" )
    path = Path(__file__).parents[1] / "src/main/resources/db/migration/V4__expand_abilities.sql"
    path.write_text("\n".join(lines) + "\n", encoding="utf-8")
    print(f"Wrote {path}: {len(names)} rows, {sum(category == 'SOFT' for _, category in names)} soft", flush=True)

if __name__ == "__main__":
    main()
