import json
import re
import socket
import time
import urllib.parse
from concurrent.futures import ThreadPoolExecutor, as_completed
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer


DOCKER_SOCKET = "/var/run/docker.sock"
TARGET_CONTAINER_NAME_PATTERN = re.compile(r"^(waito-mysql|waito-redis|waito-kafka-[0-9]+)$")


def docker_get(path):
    request = (
        f"GET {path} HTTP/1.1\r\n"
        "Host: docker\r\n"
        "Connection: close\r\n"
        "\r\n"
    ).encode("utf-8")

    with socket.socket(socket.AF_UNIX, socket.SOCK_STREAM) as client:
        client.settimeout(3)
        client.connect(DOCKER_SOCKET)
        client.sendall(request)
        response = bytearray()
        while True:
            chunk = client.recv(65536)
            if not chunk:
                break
            response.extend(chunk)

    header_bytes, _, body = bytes(response).partition(b"\r\n\r\n")
    headers = header_bytes.decode("iso-8859-1").lower()
    if "transfer-encoding: chunked" in headers:
        body = decode_chunked(body)
    return json.loads(body.decode("utf-8"))


def decode_chunked(body):
    decoded = bytearray()
    position = 0

    while True:
        line_end = body.find(b"\r\n", position)
        if line_end == -1:
            break

        chunk_size_line = body[position:line_end].split(b";", 1)[0]
        chunk_size = int(chunk_size_line, 16)
        position = line_end + 2

        if chunk_size == 0:
            break

        decoded.extend(body[position:position + chunk_size])
        position += chunk_size + 2

    return bytes(decoded)


def container_name(container):
    names = container.get("Names") or []
    if names:
        return names[0].lstrip("/")
    return container.get("Names", ["unknown"])[0]


def is_target(name):
    return TARGET_CONTAINER_NAME_PATTERN.match(name) is not None


def cpu_cores(stats):
    cpu_stats = stats.get("cpu_stats", {})
    pre_cpu_stats = stats.get("precpu_stats", {})
    cpu_usage = cpu_stats.get("cpu_usage", {})
    pre_cpu_usage = pre_cpu_stats.get("cpu_usage", {})

    cpu_delta = cpu_usage.get("total_usage", 0) - pre_cpu_usage.get("total_usage", 0)
    system_delta = cpu_stats.get("system_cpu_usage", 0) - pre_cpu_stats.get("system_cpu_usage", 0)
    online_cpus = cpu_stats.get("online_cpus") or len(cpu_usage.get("percpu_usage") or []) or 1

    if cpu_delta <= 0 or system_delta <= 0:
        return 0.0

    return (cpu_delta / system_delta) * online_cpus


def memory_mb(stats):
    memory_stats = stats.get("memory_stats", {})
    usage = memory_stats.get("usage", 0)
    cache = (memory_stats.get("stats") or {}).get("cache", 0)
    return max(usage - cache, 0) / 1024 / 1024


def escape_label(value):
    return value.replace("\\", "\\\\").replace('"', '\\"').replace("\n", "\\n")


def collect_metrics():
    lines = [
        "# HELP docker_container_cpu_usage_cores Current Docker container CPU usage in cores.",
        "# TYPE docker_container_cpu_usage_cores gauge",
        "# HELP docker_container_memory_usage_mb Current Docker container memory usage in MB.",
        "# TYPE docker_container_memory_usage_mb gauge",
    ]

    containers = [
        container for container in docker_get("/containers/json")
        if is_target(container_name(container))
    ]

    with ThreadPoolExecutor(max_workers=8) as executor:
        futures = [executor.submit(collect_container_metrics, container) for container in containers]
        for future in as_completed(futures):
            lines.extend(future.result())

    lines.append("")
    return "\n".join(lines).encode("utf-8")


def collect_container_metrics(container):
    name = container_name(container)
    encoded_id = urllib.parse.quote(container["Id"], safe="")
    stats = docker_get(f"/containers/{encoded_id}/stats?stream=false")
    label = escape_label(name)

    return [
        f'docker_container_cpu_usage_cores{{name="{label}"}} {cpu_cores(stats):.6f}',
        f'docker_container_memory_usage_mb{{name="{label}"}} {memory_mb(stats):.2f}',
    ]


class MetricsHandler(BaseHTTPRequestHandler):
    def do_GET(self):
        if self.path == "/health":
            self.send_response(200)
            self.end_headers()
            self.wfile.write(b"ok")
            return

        if self.path != "/metrics":
            self.send_response(404)
            self.end_headers()
            return

        try:
            body = collect_metrics()
            self.send_response(200)
            self.send_header("Content-Type", "text/plain; version=0.0.4; charset=utf-8")
            self.send_header("Content-Length", str(len(body)))
            self.end_headers()
            self.wfile.write(body)
        except Exception as error:
            body = f"docker stats scrape failed: {error}\n".encode("utf-8")
            self.send_response(500)
            self.send_header("Content-Type", "text/plain; charset=utf-8")
            self.send_header("Content-Length", str(len(body)))
            self.end_headers()
            self.wfile.write(body)

    def log_message(self, format, *args):
        return


if __name__ == "__main__":
    server = ThreadingHTTPServer(("0.0.0.0", 9324), MetricsHandler)
    print(f"docker-stats-exporter started at {time.strftime('%Y-%m-%dT%H:%M:%S')}", flush=True)
    server.serve_forever()
