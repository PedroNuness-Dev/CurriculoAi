"""One isolated API smoke test with fictional data; never logs credentials.

Run only against a local test server: python scripts/prompt_audit_smoke.py --live
The provider is invoked by the server. Artifacts stay in ignored target/.
"""
import argparse
import json
import secrets
import urllib.error
import urllib.request
from pathlib import Path

parser = argparse.ArgumentParser()
parser.add_argument("--live", action="store_true")
parser.add_argument("--base", default="http://127.0.0.1:8080")
args = parser.parse_args()
if not args.live or args.base not in {"http://127.0.0.1:8080", "http://localhost:8080"}:
    parser.error("Run only with --live against the local test server on port 8080")

out = Path(__file__).resolve().parents[1] / "backend-spring/target/prompt-audit/live-baseline"
out.mkdir(parents=True, exist_ok=True)
token = None

def post(path, payload):
    data = json.dumps(payload, ensure_ascii=False).encode("utf-8")
    headers = {"Content-Type": "application/json"}
    if token:
        headers["Authorization"] = "Bearer " + token
    request = urllib.request.Request(args.base + path, data=data, headers=headers, method="POST")
    try:
        with urllib.request.urlopen(request, timeout=120) as response:
            body = response.read()
            return body if "application/pdf" in response.headers.get("Content-Type", "") else json.loads(body)
    except urllib.error.HTTPError as exc:
        raise RuntimeError(f"{path}: HTTP {exc.code}") from None

identity = {"name": "Marina Auditoria Fictícia", "email": "prompt-audit-" + secrets.token_hex(5) + "@example.invalid",
            "password": secrets.token_urlsafe(24)}
post("/api/users", identity)
token = post("/api/auth/login", {"email": identity["email"], "password": identity["password"]})["token"]
project = {"title": "API de pedidos", "description": "Implementei API Java para pedidos com validação de entradas.",
           "link": "https://example.com/projeto-ficticio", "abilitiesIds": []}
post("/api/projects", [project])
input_data = {"name": identity["name"], "project": project, "jobDescription": "Vaga Java; desejável Docker."}
(out / "input.json").write_text(json.dumps(input_data, ensure_ascii=False, indent=2), encoding="utf-8")
pdf = post("/api/cerebras/curriculum", {"jobDescription": input_data["jobDescription"], "answers": ""})
assert pdf.startswith(b"%PDF"), "Expected a PDF"
(out / "resume.pdf").write_bytes(pdf)
print(f"Synthetic local API run completed: {len(pdf)} PDF bytes in {out}")
