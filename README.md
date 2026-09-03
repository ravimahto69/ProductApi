# Product API

This is a small Spring Boot project for managing products and items.

The project uses:

- Java 17
- Spring Boot
- MySQL
- Spring Data JPA
- Spring Security
- JWT tokens

## How to run without Docker

You need Java 17, MySQL and Maven.

1. Create a MySQL database called `product_db`.
2. Check the username and password in `src/main/resources/application.properties`.
3. Run the project:

```powershell
./mvnw.cmd spring-boot:run
```

The API will run on:

```text
http://localhost:9090
```

## How to run with Docker

Make sure Docker Desktop is running, then run:

```powershell
docker compose up --build
```

This starts the application and MySQL together.

To stop it:

```powershell
docker compose down
```

## Login first

Most APIs need a token.

### Register

```http
POST http://localhost:9090/api/v1/auth/register
```

Body:

```json
{
  "username": "ravi",
  "email": "ravi@example.com",
  "password": "password123"
}
```

### Login

```http
POST http://localhost:9090/api/v1/auth/login
```

Body:

```json
{
  "username": "ravi",
  "password": "password123"
}
```

The response contains an `accessToken` and a `refreshToken`.

For product and item requests, use the `accessToken` like this:

```text
Authorization: Bearer YOUR_ACCESS_TOKEN
```

In Postman, select **Authorization**, choose **Bearer Token**, and paste only the access token.

## Product APIs

### Create product

```http
POST http://localhost:9090/api/v1/products
```

Body:

```json
{
  "productName": "Laptop"
}
```

### Get product

```http
GET http://localhost:9090/api/v1/products/1
```

### Update product

```http
PUT http://localhost:9090/api/v1/products/1
```

Body:

```json
{
  "productName": "Updated Laptop"
}
```

### Delete product

```http
DELETE http://localhost:9090/api/v1/products/1
```

## Item APIs

### Add item to a product

```http
POST http://localhost:9090/api/v1/products/1/items
```

Body:

```json
{
  "productId": 1,
  "quantity": 10
}
```

### Get items for a product

```http
GET http://localhost:9090/api/v1/products/1/items?page=0&size=10
```

### Get one item

```http
GET http://localhost:9090/api/v1/items/1
```

### Update item

```http
PUT http://localhost:9090/api/v1/items/1
```

Body:

```json
{
  "productId": 1,
  "quantity": 15
}
```

### Delete item

```http
DELETE http://localhost:9090/api/v1/items/1
```

## Refresh token

When the access token expires, call:

```http
POST http://localhost:9090/api/v1/auth/refresh
```

Body:

```json
{
  "refreshToken": "YOUR_REFRESH_TOKEN"
}
```

This gives a new access token and refresh token.

## Run tests

```powershell
./mvnw.cmd test
```

The tests use JUnit 5 and Mockito.

## Common problem

If the API returns `403 Forbidden`, login again and use the new `accessToken`. Do not use the refresh token for product or item APIs.

Also check that the URL and body match. For example, product update uses `productName`, while item update uses `productId` and `quantity`.
