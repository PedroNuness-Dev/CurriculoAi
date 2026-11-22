"""Local fixture chat-completions server for end-to-end HTTP contract tests.

Never sends requests to an external provider. Run alongside the audited backend
with --cerebras.api.url=http://127.0.0.1:8092.
"""
import json
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
from pathlib import Path
from threading import Lock

OUT = Path(__file__).resolve().parents[1] / "backend-spring/target/prompt-audit/fake-provider-calls"
OUT.mkdir(parents=True, exist_ok=True)
lock = Lock()
counter = 0

class Handler(BaseHTTPRequestHandler):
    def do_POST(self):
        global counter
        body = self.rfile.read(int(self.headers.get("Content-Length", "0")))
        request = json.loads(body)
        source = json.loads(request["messages"][-1]["content"])
        profile = source["profile"]
        name = profile["name"]
        language = source.get("outputLanguage", "pt-BR")
        markdown = "# " + name
        if profile.get("email"):
            markdown += "\n\n" + profile["email"]
        projects = [item for group in profile.get("projects", []) for item in group.get("projects", [])]
        if projects:
            markdown += "\n\n## " + ("Projects" if language == "en-US" else "Projetos")
            for item in projects[:4]:
                markdown += "\n### " + item["title"]
                if item.get("link"):
                    markdown += "\n" + item["link"]
        response = {"choices": [{"finish_reason": "stop", "message": {
            "content": json.dumps({"markdown": markdown}, ensure_ascii=False)}}]}
        with lock:
            counter += 1
            number = counter
        (OUT / f"{number:03d}-request.json").write_text(json.dumps(request, ensure_ascii=False, indent=2), encoding="utf-8")
        (OUT / f"{number:03d}-raw.json").write_text(json.dumps(response, ensure_ascii=False, indent=2), encoding="utf-8")
        data = json.dumps(response, ensure_ascii=False).encode("utf-8")
        self.send_response(200)
        self.send_header("Content-Type", "application/json")
        self.send_header("Content-Length", str(len(data)))
        self.end_headers()
        self.wfile.write(data)

    def log_message(self, format, *args):
        pass

print("Fixture provider listening at http://127.0.0.1:8092", flush=True)
ThreadingHTTPServer(("127.0.0.1", 8092), Handler).serve_forever()
