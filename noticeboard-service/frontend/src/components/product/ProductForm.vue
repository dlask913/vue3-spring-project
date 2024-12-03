<template>
  <div class="container mt-5" style="width: 70%">
    <div class="row">
      <div class="col-md-4 text-center">
        <ImageUploader
          v-if="form.imgUrl"
          :imageUrl="form.imgUrl"
          @file-changed="fetchProductImg"
        />
      </div>
      <div class="col-md-8">
        <h3>Product Information</h3>
        <br />
        <form @submit.prevent="onSaveProduct">
          <!-- 카테고리 ui-->
          <div class="mb-3">
            <label for="category" class="form-label">카테고리</label>
            <select class="form-select" v-model="form.category">
              <option
                v-for="category in categories"
                :key="category.id"
                :value="category.name"
              >
                {{ category.name }}
              </option>
            </select>
          </div>

          <div class="mb-3">
            <label for="email" class="form-label">최소 금액 입력</label>
            <input
              v-model="form.standardPrice"
              type="text"
              class="form-control"
              id="standardPrice"
            />
            <div v-if="valueError" class="text-red">
              {{ valueError.standardPrice }}
            </div>
          </div>
          <div class="mb-3">
            <label for="title" class="form-label">제목</label>
            <input
              v-model="form.title"
              type="text"
              class="form-control"
              id="title"
            />
            <div v-if="valueError" class="text-red">
              {{ valueError.title }}
            </div>
          </div>
          <div class="mb-3">
            <label for="content" class="form-label">내용</label>
            <textarea
              v-model="form.content"
              type="text"
              class="form-control"
              id="content"
            ></textarea>
            <div v-if="valueError" class="text-red">
              {{ valueError.content }}
            </div>
          </div>
          <button type="submit" class="btn btn-primary">Save</button>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import ImageUploader from '../common/ImageUploader.vue'
import { createProduct, getCategories } from '@/api/products'
import { useRouter } from 'vue-router'
import { ref } from 'vue'
import { useStorageStore, useToastStore } from '@/store/index'

const storage = useStorageStore()
const router = useRouter()
const toast = useToastStore()

const form = ref({
  title: '',
  content: '',
  category: '',
  standardPrice: 0,
  imgUrl: 'http://localhost:8080/image/productDefaultImg.jpg',
})
const productImg = ref('')
const categories = ref([])
const valueError = ref({})

const onSaveProduct = async () => {
  const requiredFields = ['standardPrice', 'title', 'content']
  const fieldDesc = {
    standardPrice: '가격은 필수 값입니다.',
    title: '상품 이름은 필수 값입니다.',
    content: '상품 설명은 필수 값입니다.',
  }
  const hasError = ref(false)
  requiredFields.forEach(field => {
    if (!form.value[field]) {
      valueError.value[field] = `${fieldDesc[field]}`
      hasError.value = true
    }
  })

  if (hasError.value) {
    return
  }

  try {
    const data = {
      ...form.value,
      ownerId: storage.getUserId,
    }
    await createProduct(storage.getToken, data, productImg)
    toast.setToast('상품 등록 완료!')
    router.push('/product')
  } catch (error) {
    console.error(error)
    toast.setToast('상품 등록 실패!', 'danger')
  }
}

const fetchCategories = async () => {
  try {
    const { data } = await getCategories()
    categories.value = data
  } catch (error) {
    console.error(error)
  }
}

const fetchProductImg = file => {
  productImg.value = file
}

fetchCategories()
</script>

<style scoped>
.text-red {
  color: red;
  font-size: 14px;
  margin-top: 2px;
}
</style>
