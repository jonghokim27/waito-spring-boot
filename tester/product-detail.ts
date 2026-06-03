import crypto from "k6/crypto";
import encoding from "k6/encoding";
import http, { RefinedResponse, ResponseType } from "k6/http";
import { check } from "k6";
import { Options } from "k6/options";

const BASE_URL = __ENV.BASE_URL ?? "http://10.0.10.217:8080";
const PRODUCT_ID = __ENV.PRODUCT_ID ?? "1";
const USER_ID = Number(__ENV.USER_ID ?? "1");
const JWT_PRIVATE_KEY = __ENV.JWT_PRIVATE_KEY ?? "waito-local-development-jwt-private-key-change-me";
const RPS = Number(__ENV.RPS ?? "10");

export const options: Options = {
  scenarios: {
    productDetail: {
      executor: "constant-arrival-rate",
      rate: RPS,
      timeUnit: "1s",
      duration: __ENV.DURATION ?? "30s",
      preAllocatedVUs: Number(__ENV.PRE_ALLOCATED_VUS ?? Math.max(RPS, 10).toString()),
      maxVUs: Number(__ENV.MAX_VUS ?? Math.max(RPS * 2, 20).toString()),
    },
  },
  thresholds: {
    http_req_failed: ["rate<0.01"],
    http_req_duration: ["p(95)<500"],
  },
};

export default function (): void {
  const response = getProductDetail(PRODUCT_ID);

  check(response, {
    "product detail status is 200": (res) => res.status === 200,
    "product detail response has body": (res) => Boolean(res.body),
  });
}

function getProductDetail(productId: string): RefinedResponse<ResponseType> {
  return http.get(`${BASE_URL}/api/products/${productId}`, {
    headers: {
      Authorization: `Bearer ${createJwt(USER_ID)}`,
      Accept: "application/json",
    },
    tags: {
      name: "GET /api/products/{productId}",
    },
  });
}

function createJwt(userId: number): string {
  const now = Math.floor(Date.now() / 1000);
  const header = base64UrlEncode({
    alg: "HS256",
    typ: "JWT",
  });
  const payload = base64UrlEncode({
    userId,
    iat: now,
    exp: now + 60 * 60,
  });
  const unsignedToken = `${header}.${payload}`;
  const signature = crypto.hmac("sha256", JWT_PRIVATE_KEY, unsignedToken, "base64rawurl");

  return `${unsignedToken}.${signature}`;
}

function base64UrlEncode(value: unknown): string {
  return encoding.b64encode(JSON.stringify(value), "rawurl");
}
