"""Forward local audit requests to Cerebras and save bodies without credentials."""
import json
import urllib.error
import urllib.request
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
from pathlib import Path
from threading import Lock

OUT = Path(__file__).resolve().parents[1] / "backend-spring/target/prompt-audit/real-provider-calls"
OUT.mkdir(parents=True, exist_ok=True)
lock = Lock()
counter = 0


class Handler(BaseHTTPRequestHandler):
    def do_POST(self):
        global counter
        body = self.rfile.read(int(self.headers.get("Content-Length", "0")))
        with lock:
            counter += 1
            number = counter
        (OUT / f"{number:03d}-request.json").write_bytes(body)
        upstream = urllib.request.Request(
            "https://api.cerebras.ai/v1/chat/completions",
            data=body,
            headers={"Authorization": self.headers.get("Authorization", ""),
                     "Content-Type": "application/json"},
            method="POST",
        )
        try:
            with urllib.request.urlopen(upstream, timeout=90) as response:
                data, status = response.read(), response.status
        except urllib.error.HTTPError as error:
            data, status = error.read(), error.code
        except Exception as error:
            data, status = json.dumps({"error": type(error).__name__,
                                       "reason": str(getattr(error, "reason", error))}).encode(), 502
        (OUT / f"{number:03d}-raw.json").write_bytes(data)
        self.send_response(status)
        self.send_header("Content-Type", "application/json")
        self.send_header("Content-Length", str(len(data)))
        self.end_headers()
        self.wfile.write(data)

    def log_message(self, format, *args):
        pass


print("Real provider capture listening at http://127.0.0.1:8092", flush=True)
ThreadingHTTPServer(("127.0.0.1", 8092), Handler).serve_forever()
