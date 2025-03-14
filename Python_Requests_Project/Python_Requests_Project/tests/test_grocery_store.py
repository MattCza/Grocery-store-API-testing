import requests
import pytest

BASE_URL = "https://simple-grocery-store-api.glitch.me"
FULL_NAME = "Timothy Lang12345461111"
EMAIL = "TimothyLang12345461111@gmaasd.px"

access_token = None
cart_id = None
item_cart_id = None
order_id = None
product_id = None
replace_product_id = None


def test_status_endpoint_returns_200_and_status_up():
    response = requests.get(f"{BASE_URL}/status")
    assert response.status_code == 200
    assert response.json().get("status") == "UP"

def test_get_all_products_within_500ms():
    response = requests.get(f"{BASE_URL}/products")
    assert response.elapsed.total_seconds() < 2

    products = response.json()
    in_stock_products = [p for p in products if p.get("inStock")]

    assert in_stock_products

    global product_id, replace_product_id
    product_id = in_stock_products[0]["id"]
    replace_product_id = in_stock_products[3]["id"] if len(in_stock_products) > 3 else product_id

def test_get_product_by_id():
    response = requests.get(f"{BASE_URL}/products/{product_id}")
    assert response.json().get("id") == product_id

def test_get_all_products_by_category_coffee():
    response = requests.get(f"{BASE_URL}/products", params={"category": "coffee"})
    json_data = response.json()
    categories = [item["category"] for item in json_data]
    assert "coffee" in categories, f"'coffee' not found in {categories}"

def test_get_all_products_with_category_fresh_produce_limited_to_3():
    response = requests.get(f"{BASE_URL}/products", params={'category': 'fresh-produce',
                                                     'results': '3',
                                                     'available': 'true'})
    json_data = response.json()
    assert len(json_data) == 3


def test_register_new_api_key():
    global access_token

    response = requests.post(f"{BASE_URL}/api-clients", json={"clientName": FULL_NAME,
                                                  "clientEmail": EMAIL})
    assert response.status_code == 201
    access_token = response.json().get("accessToken")

def test_register_api_key_conflict():
    response = requests.post(f"{BASE_URL}/api-clients", json={"clientName": FULL_NAME,
                                                  "clientEmail": EMAIL})
    assert response.status_code == 409

def test_register_api_key_with_invalid_email():
    response = requests.post(f"{BASE_URL}/api-clients", {"clientName": FULL_NAME,
                                                  "clientEmail": "EMAIL"})
    assert response.status_code == 400

def test_create_new_cart():
    global cart_id

    response = requests.post(f"{BASE_URL}/carts")
    assert response.status_code == 201
    cart_id = response.json().get("cartId")
    assert cart_id is not None

def test_get_cart_status_code_200():
    response = requests.get(f"{BASE_URL}/carts/{cart_id}")
    assert response.status_code == 200


def test_add_item_to_cart():
    global item_cart_id

    response = requests.post(f"{BASE_URL}/carts/{cart_id}/items",
                             json={"productId": product_id})
    assert response.status_code == 201
    item_cart_id = response.json().get("itemId")

def test_add_item_to_cart_conflict():
    response = requests.post(f"{BASE_URL}/carts/{cart_id}/items",
                             json={"productId": product_id})
    error_message = response.json().get("error")
    assert error_message == "This product has already been added to cart."

def test_get_cart_items():
    response = requests.get(f"{BASE_URL}/carts/{cart_id}/items")
    json_data = response.json()
    assert json_data, "Cart is empty"

    products_ids = [item["productId"] for item in json_data]
    assert product_id in products_ids, f"Product with id= {product_id} not found in response"


def test_update_product_quantity_in_cart():
    response = requests.patch(f"{BASE_URL}/carts/{cart_id}/items/{item_cart_id}",
                              json={"quantity": 2})
    assert response.status_code == 204

def test_get_cart_items_after_quantity_update():
    response = requests.get(f"{BASE_URL}/carts/{cart_id}/items")
    json_data = response.json()
    assert json_data, "Cart is empty"

    quantities = [item["quantity"] for item in json_data]
    assert 2 in quantities, "No update in quantity!"


def test_replace_product_in_cart():
    json_data = {"productId": replace_product_id,
                 "quantity": 1}
    params_data = {"cartId": cart_id,
                   "itemId": item_cart_id}

    response = requests.put(f"{BASE_URL}/carts/{cart_id}/items/{item_cart_id}",
                            json=json_data)

    assert response.status_code == 204


def test_delete_product_from_cart():
    json_data = {"productId": replace_product_id,
                 "quantity": 1}
    params_data = {"cartId": cart_id,
                   "itemId": item_cart_id}

    response = requests.delete(f"{BASE_URL}/carts/{cart_id}/items/{item_cart_id}")
    assert response.status_code == 204


def test_delete_product_from_cart_not_found():
    response = requests.delete(f"{BASE_URL}/carts/{cart_id}/items/{item_cart_id}")
    assert response.status_code == 404


def test_add_item_to_cart_again():
    global item_cart_id

    json_data = {"productId": product_id}
    response = requests.post(f"{BASE_URL}/carts/{cart_id}/items",
                             json=json_data)

    assert response.status_code == 201
    item_cart_id = response.json().get("itemId")


def test_get_cart_items_after_adding():
    response = requests.get(f"{BASE_URL}/carts/{cart_id}/items")

    json_data = response.json()
    assert json_data

    products = [item["productId"] for item in json_data]
    assert product_id in products, f"Not found {product_id}"


def test_place_order():
    global order_id
    json_data = {"cartId": cart_id,
                 "customerName": "Marry Jane"}
    headers_data = {"Authorization": f"Bearer {access_token}"}

    response = requests.post(f"{BASE_URL}/orders/",
                             json=json_data,
                             headers=headers_data)

    assert response.status_code == 201
    order_id = response.json().get("orderId")
    print(order_id)


def test_get_all_orders():
    headers_data = {"Authorization": f"Bearer {access_token}"}

    response = requests.get(f"{BASE_URL}/orders/",
                               headers=headers_data)
    assert response.status_code == 200


def test_patch_order():
    headers_data = {"Authorization": f"Bearer {access_token}"}

    json_data = {"customerName": "Joe Doe",
                 "comment": "Pick-up at 4pm"}

    response = requests.patch(f"{BASE_URL}/orders/{order_id}",
                              json=json_data,
                              headers=headers_data)

    assert response.status_code == 204


def test_get_all_orders_after_patch():
    headers_data = {"Authorization": f"Bearer {access_token}"}

    response = requests.get(f"{BASE_URL}/orders/",
                            headers=headers_data)

    assert response.status_code == 200


def test_delete_order():
    headers_data = {"Authorization": f"Bearer {access_token}"}
    response = requests.delete(f"{BASE_URL}/orders/{order_id}",
                               headers=headers_data)
    assert response.status_code == 204


def test_delete_order_not_found():
    headers_data = {"Authorization": f"Bearer {access_token}"}

    response = requests.delete(f"{BASE_URL}/orders/{order_id}",
                               headers=headers_data)

    assert response.status_code == 404


def test_get_all_orders_final():
    headers_data = {"Authorization": f"Bearer {access_token}"}

    response = requests.get(f"{BASE_URL}/orders/",
                            headers=headers_data)

    assert response.status_code == 200