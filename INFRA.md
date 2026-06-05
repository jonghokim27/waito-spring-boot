# Waito Infra

개발용 인프라는 루트 `infra` 디렉터리의 Docker Compose로 관리한다.
포트, DB 계정, JWT 키 같은 로컬 설정은 루트 `.env`에서 관리한다.

처음 설정할 때는 `.env.example`을 기준으로 루트 `.env`를 만들고 필요한 값만 바꾼다.
Spring 실행 모듈은 `.env`를 읽은 뒤 `classpath:/common.yml` 공통 설정을 로드한다.
Docker Compose는 `--env-file .env`로 같은 값을 명시적으로 주입한다.

## Start

```powershell
docker compose --env-file .env -f .\infra\docker-compose.yml up -d

docker compose --env-file .env -f infra/docker-compose.yml up -d --build prometheus grafana
docker compose --env-file .env -f infra/docker-compose.yml up -d mysql kafka-1 kafka-2 kafka-3 kafka-exporter kafka-ui mysql-exporter docker-stats-exporter
docker compose --env-file .env -f infra/docker-compose.yml up -d --build waito-consumer waito-api docker-stats-exporter
docker compose --env-file .env -f infra/docker-compose.yml up -d redis redis-exporter docker-stats-exporter
```

## Web UI

| UI | URL | Account | Purpose |
| --- | --- | --- | --- |
| Grafana | http://localhost:3000 | `admin` / `admin` | Waito dashboard, API/MySQL/Redis/Kafka/container metrics |
| Prometheus | http://localhost:9090 | None | PromQL query, scrape target status |
| Kafka UI | http://localhost:18080 | None | Kafka brokers, topics, partitions, consumer groups |

## Grafana

Provisioned dashboard:

- Folder: `Waito`
- Dashboard: `Waito Overview`
- Source file: `infra/grafana/provisioning/dashboards/waito-overview.json`

Grafana datasource:

- Name: `Prometheus`
- URL inside Docker network: `http://prometheus:9090`
- Source file: `infra/grafana/provisioning/datasources/prometheus.yml`

주요 확인 항목:

- API TPS/RPS: `HTTP Request Rate`
- API latency: `HTTP Latency by URI`, `HTTP p95 Latency`
- Tomcat saturation: `Tomcat Busy Threads`, `Tomcat Threads`, `Tomcat Connections`
- DB pool saturation: `Hikari Active Connections`, `Hikari Pool`, `Hikari Timeouts`
- MySQL bottleneck: `MySQL QPS / TPS`, `MySQL Threads / Connections`, `MySQL Slow Queries / Row Locks`
- Redis bottleneck: `Redis Commands / Errors`, `Redis Latency`, `Redis Clients / Memory`
- Kafka bottleneck: `Kafka Consumer Lag`, `Kafka Brokers / CPU`

## Prometheus

Scrape config:

- Source file: `infra/prometheus/prometheus.yml`
- Docker Compose expands API and infra host variables before Prometheus starts.
- waito-api metrics: `http://${WAITO_API_HOST}:${WAITO_API_MANAGEMENT_PORT}/actuator/prometheus`

Useful pages:

- Query: http://localhost:9090/query
- Targets: http://localhost:9090/targets
- Service discovery: http://localhost:9090/service-discovery

Scraped jobs:

- `waito-api`
- `mysql`
- `redis`
- `kafka`
- `docker-stats`
- `prometheus`

## Kafka UI

Kafka UI is backed by `provectuslabs/kafka-ui`.

- URL: http://localhost:18080
- Cluster name: `waito-local`
- Bootstrap servers inside Docker network: `kafka-1:9092,kafka-2:9092,kafka-3:9092`

Host-side Kafka broker ports:

- `localhost:19092` -> `kafka-1`
- `localhost:29092` -> `kafka-2`
- `localhost:39092` -> `kafka-3`

## Metrics Endpoints

These are not full Web UIs, but they are useful for direct checks.

| Service | URL |
| --- | --- |
| waito-api Prometheus metrics | http://localhost:8081/actuator/prometheus |
| waito-api health | http://localhost:8081/actuator/health |
| MySQL exporter metrics | http://localhost:9104/metrics |
| Redis exporter metrics | http://localhost:9121/metrics |
| Kafka exporter metrics | http://localhost:9308/metrics |
| Docker stats exporter metrics | http://localhost:9324/metrics |

## Data Ports

아래 값은 기본 `.env.example` 기준이다. 포트를 바꾸려면 루트 `.env`의 `WAITO_*_PORT` 값을 수정한다.

| Service | Host Port | Internal Port |
| --- |-----------| --- |
| waito-api | `8080`    | local process |
| waito-api management | `8081`    | local process |
| MySQL | `3306`   | `3306` |
| Redis | `6379`    | `6379` |
| Kafka broker 1 | `19092`   | `9092` |
| Kafka broker 2 | `29092`   | `9092` |
| Kafka broker 3 | `39092`   | `9092` |
