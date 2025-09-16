# CMS E-Commerce API Documentation (Server)

CMS E-Commerce App is an application to manage online store data (CRUD operation). This app has:

- RESTful endpoint for CRUD operation
- JSON formatted response

it separate with two domain, admin domain and customer domain

# Admin domain for administrative

## RESTful endpoint

---

### POST /login

> Login process for admin

_Request Header_

```
not needed
```

_Request Body_

```
{
  "email": "<email to get insert into>"
  "password": "<password to get insert into>"
}
```

_Response (200)_

```
{
  "access_token": "<your access token>"
}
```

_Response (400 - Bad Request)_

```
{
  "errors": [
    "invalid email and password"
  ]
}
```

_Response (403- Forbidden)_

```
{
  "errors": [
    "access rejected"
  ]
}
```

_Response (401 - Unauthorized)_

```
{
    "errors": [
        "authentication failed"
    ]
}
```

---

### GET /products

> Get all product list

_Request Header_

```
{
  "access_token": "<your access token>"
}
```

_Request Body_

```
not needed
```

_Response (200)_

```
{
  "data": {
    "totalItems": 1,
    "products": [
      {
        "id": "4a3d09e5-61fa-47fe-aec5-f64f3f2d106e",
        "CategoryId": "238f3d06-9374-4365-8c2c-9f618fc046bb",
        "name": "Asus ZenBook 14",
        "image_url": "https://images-na.ssl-images-amazon.com/images/I/51pdpYTEM%2BL._AC_.jpg",
        "price": "203488300",
        "stock": "7",
        "createdAt": "2020-10-22T12:27:43.588Z",
        "updatedAt": "2020-10-22T12:27:43.588Z",
        "Category": {
          "name": "Trending"
        }
      }
    ],
    "totalPages": 1,
    "currentPage": 0
  }
}
```

_Response (403 - Forbidden)_

```
{
  "errors": [
    "access rejected"
  ]
}
```

_Response (401 - Unauthorized)_

```
{
  "errors": [
    "authentication failed"
  ]
}
```

_Response (500 - Internal Server Error)_

```
{
  "errors": [
    "internal server error"
  ]
}
```

---

### POST /products

> Create new product

_Request Header_

```
{
  "access_token": "<your access token>"
}
```

_Request Body_

```
{
  "product": {
    "id": "4a3d09e5-61fa-47fe-aec5-f64f3f2d106e",
    "CategoryId": "238f3d06-9374-4365-8c2c-9f618fc046bb",
    "name": "Asus ZenBook 14",
    "image_url": "https://images-na.ssl-images-amazon.com/images/I/51pdpYTEM%2BL._AC_.jpg",
    "price": "203488300",
    "stock": "7"
  }
}
```

_Response (201 - Created)_

```
{
  "product": {
    "id": "4a3d09e5-61fa-47fe-aec5-f64f3f2d106e",
    "CategoryId": "238f3d06-9374-4365-8c2c-9f618fc046bb",
    "name": "Asus ZenBook 14",
    "image_url": "https://images-na.ssl-images-amazon.com/images/I/51pdpYTEM%2BL._AC_.jpg",
    "price": "203488300",
    "stock": "7",
    "updatedAt": "2020-10-22T12:27:43.588Z",
    "createdAt": "2020-10-22T12:27:43.588Z"
  }
}
```

_Response (400 - Bad Request)_

```
{
  "errors": [
    "category is required",
    "product name is required",
    "image URL is required",
    "price is required",
    "stock is required",
    "category can not be empty",
    "product name can not be empty",
    "product name length must be between 5 and 50",
    "image URL can not empty",
    "price can not be empty",
    "price is not valid",
    "stock can not be empty",
    "stock is not valid"
  ]
}
```

_Response (403 - Forbidden)_

```
{
  "errors": [
    "access rejected"
  ]
}
```

_Response (401 - Unauthorized)_

```
{
  "errors": [
    "authentication failed"
  ]
}
```

---

### PUT /products/:productId

> Update product by it's product id

_Request Header_

```
{
  "access_token": "<your access token>"
}
```

_Request Params_

```
{
  "id": "req.params.productId"
}
```

_Request Body_

```
{
  "product": {
    "id": "4a3d09e5-61fa-47fe-aec5-f64f3f2d106e",
    "CategoryId": "238f3d06-9374-4365-8c2c-9f618fc046bb",
    "name": "Asus ZenBook 14",
    "image_url": "https://images-na.ssl-images-amazon.com/images/I/51pdpYTEM%2BL._AC_.jpg",
    "price": "203488300",
    "stock": "7"
  }
}
```

_Response (200 - OK)_

```
{
  "product": {
    "id": "4a3d09e5-61fa-47fe-aec5-f64f3f2d106e",
    "CategoryId": "238f3d06-9374-4365-8c2c-9f618fc046bb",
    "name": "Asus ZenBook 14",
    "image_url": "https://images-na.ssl-images-amazon.com/images/I/51pdpYTEM%2BL._AC_.jpg",
    "price": "203488300",
    "stock": "7",
    "createdAt": "2020-10-22T12:27:43.588Z",
    "updatedAt": "2020-10-22T12:27:43.588Z"
  }
}
```

_Response (400 - Bad Request)_

```
{
  "errors": [
    "category is required",
    "product name is required",
    "image URL is required",
    "price is required",
    "stock is required",
    "category can not be empty",
    "product name can not be empty",
    "product name length must be between 5 and 50",
    "image URL can not empty",
    "price can not be empty",
    "price is not valid",
    "stock can not be empty",
    "stock is not valid"
  ]
}
```

_Response (403 - Forbidden)_

```
{
  "errors": [
    "access rejected"
  ]
}
```

_Response (401 - Unauthorized)_

```
{
  "errors": [
    "authentication failed"
  ]
}
```

---

### DELETE /products/:productId

> Delete specified product by it's product id

_Request Header_

```
{
  "access_token": "<your access token>"
}
```

_Request Params_

```
{
  "id": "req.params.productId"
}
```

_Request Body_

```
not needed
```

_Response (200)_

```
{
  "message": "product deleted successfully"
}
```

_Response (404 - Not Found)_

```
{
  "errors": [
    "product not found"
  ]
}
```

_Response (403 - Forbidden)_

```
{
  "errors": [
    "access rejected"
  ]
}
```

_Response (401 - Unauthorized)_

```
{
  "errors": [
    "authentication failed"
  ]
}
```

_Response (500 - Internal Server Error)_

```
{
  "errors": [
    "internal server error"
  ]
}
```

# Customer domain for buy the product

## RESTful endpoint

---

### POST /login

> Login process for customer

_Request Header_

```
not needed
```

_Request Body_

```
{
  "email": "<email to get insert into>"
  "password": "<password to get insert into>"
}
```

_Response (200)_

```
{
  "access_token": "<your access token>"
}
```

_Response (400 - Bad Request)_

```
{
  "errors": [
    "invalid email and password"
  ]
}
```

_Response (403- Forbidden)_

```
{
  "errors": [
    "access rejected"
  ]
}
```

_Response (401 - Unauthorized)_

```
{
  "errors": [
    "authentication failed"
  ]
}
```

---

### POST /register

> User register for customers

_Request Header_

```
not needed
```

_Request Body_

```
{
  "displayName": "<customer name>"
  "email": "<email to get insert into>"
  "password": "<password to get insert into>"
}
```

_Response (201 - Created)_

```
{
  "data": {
    "message": "register success",
    "displayName": "Arnold",
    "email": "arnold@gmail.com",
    "role": "customer"
  }
}
```

_Response (400 - Bad Request)_

```
{
    "errors": [
        "name can not be empty",
        "name length must be between 5 and 50",
        "email can not be empty",
        "email format is not valid",
        "password can not be empty",
        "email already has taken"
    ]
}
```

---

### GET /customer/products

> Get all product list

_Request Header_

```
{
  "access_token": "<your access token>"
}
```

_Request Body_

```
not needed
```

_Response (200)_

```
{
  "data": {
    "totalItems": 1,
    "products": [
      {
        "id": "4a3d09e5-61fa-47fe-aec5-f64f3f2d106e",
        "CategoryId": "238f3d06-9374-4365-8c2c-9f618fc046bb",
        "name": "Asus ZenBook 14",
        "image_url": "https://images-na.ssl-images-amazon.com/images/I/51pdpYTEM%2BL._AC_.jpg",
        "price": "203488300",
        "stock": "7",
        "createdAt": "2020-10-22T12:27:43.588Z",
        "updatedAt": "2020-10-22T12:27:43.588Z",
        "Category": {
          "name": "Trending"
        }
      }
    ],
    "totalPages": 1,
    "currentPage": 0
  }
}
```

_Response (403 - Forbidden)_

```
{
  "errors": [
    "access rejected"
  ]
}
```

_Response (401 - Unauthorized)_

```
{
  "errors": [
    "authentication failed"
  ]
}
```

_Response (500 - Internal Server Error)_

```
{
  "errors": [
    "internal server error"
  ]
}
```

---

### GET /customer/carts

> Get all cart item list

_Request Header_

```
{
  "access_token": "<your access token>"
}
```

_Request Body_

```
not needed
```

_Response (200)_

```
{
  "data": {
    "customerName": "Julian Razif Figaro",
    "customerEmail": "julianrazif@gmail.com",
    "carts": [],
    "itemCount": 0,
    "totalPrice": 0
  }
}
```

_Response (403 - Forbidden)_

```
{
  "errors": [
    "access rejected"
  ]
}
```

_Response (401 - Unauthorized)_

```
{
  "errors": [
    "authentication failed"
  ]
}
```

_Response (500 - Internal Server Error)_

```
{
    "errors": [
        "internal Server Error"
    ]
}
```

### POST /customer/carts/:productId

> Add product to cart

_Request Header_

```
{
  "access_token": "<your access token>"
}
```

_Request Params_

```
{
  "ProductId": "req.params.productId"
}
```

_Request Body_

```
not needed
```

_Response (201 - Created)_

```
{
  "data": {
    "customerName": "Julian Razif Figaro",
    "customerEmail": "julianrazif@gmail.com",
    "carts": [
      {
        "product": {
          "id": "183e1f7e-4310-4b70-b987-e527d10bcb67",
          "name": "Final Fantasy VII Remake",
          "image_url": "https://i.ytimg.com/vi/mZHC3nYlKXk/maxresdefault.jpg",
          "price": "1200000",
          "stock": 9
        },
        "quantity": 1
      }
    ],
  "itemCount": 1,
  "totalPrice": 1200000
  }
}
```

_Response (400 - Bad Request)_

```
{
  "errors": [
    "invalid input syntax for type uuid: \"text\""
  ]
}
```

_Response (403 - Forbidden)_

```
{
  "errors": [
    "access rejected"
  ]
}
```

_Response (401 - Unauthorized)_

```
{
  "errors": [
    "authentication failed"
  ]
}
```

_Response (500 - Internal Server Error)_

```
{
    "errors": [
        "internal Server Error"
    ]
}
```

_Response (404 - Not Found)_

```
{
  "errors": [
    "page not found"
  ]
}
```

### PUT /customer/carts/:productId

> update product quantity in the cart

_Request Header_

```
{
  "access_token": "<your access token>"
}
```

_Request Params_

```
{
  "ProductId": "req.params.productId",
  "quantity": "<desire quantity>"
}
```

_Request Body_

```
not needed
```

_Response (200 - OK)_

```
{
  "data": {
    "customerName": "Julian Razif Figaro",
    "customerEmail": "julianrazif@gmail.com",
    "carts": [
      {
        "product": {
          "id": "183e1f7e-4310-4b70-b987-e527d10bcb67",
          "name": "Final Fantasy VII Remake",
          "image_url": "https://i.ytimg.com/vi/mZHC3nYlKXk/maxresdefault.jpg",
          "price": "1200000",
          "stock": 9
        },
        "quantity": 1
      }
    ],
  "itemCount": 1,
  "totalPrice": 1200000
  }
}
```

_Response (400 - Bad Request)_

```
{
  "errors": [
    "invalid input syntax for type uuid: \"text\"",
    "out of stock"
  ]
}
```

_Response (403 - Forbidden)_

```
{
  "errors": [
    "access rejected"
  ]
}
```

_Response (401 - Unauthorized)_

```
{
  "errors": [
    "authentication failed"
  ]
}
```

_Response (500 - Internal Server Error)_

```
{
    "errors": [
        "internal Server Error"
    ]
}
```

_Response (404 - Not Found)_

```
{
  "errors": [
    "page not found"
  ]
}
```

### DELETE /customer/carts/:productId

> Delete specified cart by it's product id

_Request Header_

```
{
  "access_token": "<your access token>"
}
```

_Request Body_

```
not needed
```

_Response (200)_

```
{
    "message": "products has been removed"
}
```

_Response (404 - Not Found)_

```
{
    "errors": [
        "product not found"
    ]
}
```

_Response (403 - Forbidden)_

```
{
  "errors": [
    "access rejected"
  ]
}
```

_Response (401 - Unauthorized)_

```
{
  "errors": [
    "authentication failed"
  ]
}
```

_Response (500 - Internal Server Error)_

```
{
    "errors": [
        "internal Server Error"
    ]
}
```
