import axios from '@/axios'

export function createProduct(productDto, productImg) {
  const formData = new FormData()
  formData.append(
    'productDto',
    new Blob([JSON.stringify(productDto)], { type: 'application/json' }),
  ) // JSON 데이터 추가
  if (productImg.value) {
    formData.append('productImg', productImg.value) // 파일이 있는 경우
  }
  return axios.post('/product', formData, {
    needsAuth: true,
    headers: { 'Content-Type': 'multipart/form-data' },
  })
}

export function getProducts(option, value) {
  return axios.get(`products?${option}=${value}`)
}

export function getProductsByKeyword(option, value, sort, order) {
  return axios.get(`/products?${option}=${value}&sort=${sort}&order=${order}`)
}

export function getProductById(productId) {
  return axios.get(`/product/${productId}`)
}

export function updateProduct(productId, data) {
  return axios.put(`/product/${productId}`, data, {
    needsAuth: true,
  })
}

export function deleteProduct(productId) {
  return axios.delete(`/product/${productId}`, {
    needsAuth: true,
  })
}

export function getCategories() {
  return axios.get('/categories')
}

export function createProductBid(data) {
  return axios.post('/bid', data, {
    needsAuth: true,
  })
}

export function getProductsByCategory(category) {
  return axios.get(`/products/${category}`)
}

export function getProductsByMemberId(memberId) {
  return axios.get(`/products/member/${memberId}`, {
    needsAuth: true,
  })
}
