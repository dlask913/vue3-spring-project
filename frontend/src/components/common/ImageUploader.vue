<template>
  <div class="image-uploader">
    <div class="image-container">
      <div class="image-preview">
        <img :src="imageUrl" alt="Preview" class="profile-image" />
        <button
          @click="triggerFileInput"
          class="btn btn-primary btn-sm edit-button"
        >
          Edit
        </button>
      </div>
      <input
        ref="fileInput"
        type="file"
        class="form-control"
        @change="onFileChange"
        style="display: none"
      />
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const props = defineProps({
  imageUrl: {
    type: String,
    required: true,
  },
});

const emit = defineEmits(['file-changed']);

const imageUrl = ref(props.imageUrl); // 초기 이미지
const fileInput = ref(null);

const onFileChange = (event) => {
  const file = event.target.files[0];
  if (file) {
    const reader = new FileReader();
    reader.onload = (e) => {
      imageUrl.value = e.target.result;
      emit('file-changed', file);
    };
    reader.readAsDataURL(file);
  }
};

const triggerFileInput = () => {
  fileInput.value.click();
};
</script>

<style scoped>
.image-container {
  position: relative;
  display: inline-block;
}

.profile-image {
  width: 200px;
  height: 200px;
  object-fit: cover;
  border-radius: 50%;
  margin-top: 20px;
  margin-bottom: 5px;
}

.edit-button {
  position: absolute;
  bottom: 10px;
  right: 10px;
  z-index: 1;
}
</style>
