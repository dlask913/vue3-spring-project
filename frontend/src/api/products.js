import axios from '@/axios';

export function createProduct(token, productDto, productImg) {
  const formData = new FormData();
  formData.append(
    'productDto',
    new Blob([JSON.stringify(productDto)], { type: 'application/json' })
  ); // JSON 데이터 추가
  if (productImg.value) {
    formData.append('productImg', productImg.value); // 파일이 있는 경우
  }
  return axios.post('/product', formData, {
    headers: { Authorization: token, 'Content-Type': 'multipart/form-data' },
  });
}

export function getProducts() {
  return axios.get('products');
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
