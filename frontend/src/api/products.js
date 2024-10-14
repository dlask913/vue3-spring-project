import axios from '@/axios';

export function createProduct(token, data) {
  return axios.post('/product', data, {
    headers: { Authorization: token },
  });
}

export function getProductById(productId) {
  return axios.get(`/product/${productId}`);
}

export function updateProduct(token, productId, data) {
  return axios.put(`/product/${productId}`, data, {
    headers: { Authorization: token },
  });
}

export function deleteProduct(token, productId) {
  return axios.delete(`/product/${productId}`, {
    headers: { Authorization: token },
  });
}
