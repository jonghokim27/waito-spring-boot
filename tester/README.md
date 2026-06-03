# k6 Tester

상품 상세 API 부하 테스트 스크립트는 `product-detail.ts`에 있다.

## Product Detail

기본 실행:

```bash
k6 run tester/product-detail.ts
```

초당 요청 수, 실행 시간, 상품 ID를 지정해서 실행:

```bash
k6 run -e BASE_URL=http://10.0.10.217:8080 -e PRODUCT_ID=1 -e USER_ID=1 -e RPS=100 -e DURATION=1m tester/product-detail.ts
```

옵션:

| Env | Default | Description |
| --- | --- | --- |
| `BASE_URL` | `http://10.0.10.217:8080` | API base URL |
| `PRODUCT_ID` | `1` | 상품 ID |
| `USER_ID` | `1` | JWT `userId` claim |
| `RPS` | `10` | 초당 요청 수 |
| `DURATION` | `30s` | 테스트 실행 시간 |
| `PRE_ALLOCATED_VUS` | `max(RPS, 10)` | 미리 할당할 VU 수 |
| `MAX_VUS` | `max(RPS * 2, 20)` | 최대 VU 수 |
| `JWT_PRIVATE_KEY` | `waito-local-development-jwt-private-key-change-me` | HS256 JWT signing key |
