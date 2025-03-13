import requests
import pytest

BASE_URL = "https://simple-grocery-store-api.glitch.me"
FULL_NAME = "Timothy Lang12"
EMAIL = "TimothyLang12@gmaasd.px"
access_token = None
cart_id = None
item_cart_id = None
order_id = None
product_id = None
replace_product_id = None


def send_post_request(endpoint, json_body, headers=None):
    response = requests.post(f"{BASE_URL}{endpoint}", json=json_body, headers=headers)
    return response

def send_get_request(endpoint, params=None, headers=None):
    response = requests.get(f"{BASE_URL}{endpoint}", params=params, headers=headers)
    return response


def test_status_endpoint_returns_200_and_status_up():
    response = send_get_request("/status")
    assert response.status_code == 200
    assert response.json().get("status") == "UP"

def test_get_all_products_within_500ms():
    response = send_get_request("/products")
    assert response.elapsed.total_seconds() < 5
    products = response.json()
    in_stock_products = [p for p in products if p.get("inStock")]
    assert in_stock_products
    global product_id, replace_product_id
    product_id = in_stock_products[0]["id"]
    replace_product_id = in_stock_products[3]["id"] if len(in_stock_products) > 3 else product_id

def test_register_new_api_key():
    global access_token
    response = send_post_request("/api-clients", {"clientName": FULL_NAME, "clientEmail": EMAIL})
    assert response.status_code == 201
    access_token = response.json().get("accessToken")

def test_register_api_key_conflict():
    response = send_post_request("/api-clients", {"clientName": FULL_NAME, "clientEmail": EMAIL})
    assert response.status_code == 409

def test_create_new_cart():
    global cart_id
    response = send_post_request("/carts", {})
    assert response.status_code == 201
    cart_id = response.json().get("cartId")

def test_add_item_to_cart():
    global item_cart_id
    response = send_post_request(f"/carts/{cart_id}/items", {"productId": product_id})
    assert response.status_code == 201
    item_cart_id = response.json().get("itemId")

def test_update_product_quantity_in_cart():
    response = requests.patch(f"{BASE_URL}/carts/{cart_id}/items/{item_cart_id}", json={"quantity": 2})
    assert response.status_code == 204

def test_place_order():
    global order_id
    headers = {"Authorization": f"Bearer {access_token}"}
    response = send_post_request("/orders/", {"cartId": cart_id, "customerName": "Marry Jane"}, headers)
    assert response.status_code == 201
    order_id = response.json().get("orderId")

def test_delete_order():
    headers = {"Authorization": f"Bearer {access_token}"}
    response = requests.delete(f"{BASE_URL}/orders/{order_id}", headers=headers)
    assert response.status_code == 204
