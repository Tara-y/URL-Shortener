# API Documentation

## Base URL
```
http://localhost:8080/api
```

---

### 🔗 POST `/shorten`

Shortens a given URL.

#### Request

- Method: `POST`
- URL: `/api/shorten`
- Body:

```json
{
  "originalUrl": "https://example.com"
}
```

#### Response

- Status: `200 OK`
- Body:

```json
"http://localhost:8080/api/resolve/abc123xy"
```

#### Errors

| Code | Description               |
|------|---------------------------|
| 400  | Invalid URL               |
| 409  | Duplicate short URL       |
| 500  | Server error during hash  |

---

### 🔎 GET `/resolve/{shortUrl}`

returns the original URL based on the given short code.

#### Request

- Method: `GET`
- URL: `/api/resolve/abc123xy`

#### Response

- Status: `302 FOUND`
- Header: `Location: https://example.com`

#### Errors

| Code | Description           |
|------|-----------------------|
| 404  | Short URL not found   |

---

## ✅ Validation Rules

- URLs must begin with `http://` or `https://`.
- Invalid URLs will be rejected with `400 Bad Request`.

---

## 🔁 Collision Handling

- Hashing is done with a configurable algorithm.
- In case of collision, up to `maxRetries` attempts are made.
