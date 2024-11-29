<template>
  <form @submit.prevent="onSave">
    <div class="my-container">
      <div class="card col-5 mt-5 mb-5">
        <h2 class="mt-5 mb-4 text-center"><b>회원가입</b></h2>
        <div class="container col-10">
          <div class="form-group">
            <label class="form-label">이메일</label>
            <input v-model="member.email" type="text" class="form-control" />
            <div v-if="valueError" class="text-red">
              {{ valueError.email }}
            </div>
          </div>
          <div class="form-group mt-3">
            <label class="form-label">이름</label>
            <input v-model="member.username" type="text" class="form-control" />
            <div v-if="valueError" class="text-red">
              {{ valueError.username }}
            </div>
          </div>
          <div class="form-group mt-3">
            <label class="form-label">비밀번호</label>
            <input
              v-model="member.passwordTemp"
              type="password"
              class="form-control"
            />
            <div v-if="valueError" class="text-red">
              {{ valueError.password }}
            </div>
          </div>
          <div class="form-group mt-3">
            <label class="form-label">비밀번호 확인</label>
            <input
              v-model="member.password"
              @input="passwordConfirm"
              type="password"
              class="form-control"
              id="password"
            />
            <div v-if="passwordConfirmError" class="text-red">
              {{ passwordConfirmError }}
            </div>
          </div>
          <!-- 주소 입력 필드 -->
          <div class="form-group mt-3" @click="openPopup">
            <label class="form-label">주소</label>
            <input type="text" class="form-control" />
          </div>
          <div class="form-group mt-5 mb-5">
            <button
              type="submit"
              class="btn btn-primary"
              style="width: 100%; color: white"
            >
              가입하기
            </button>
          </div>
        </div>
      </div>
    </div>
  </form>
  <AddressPopup
    :addresses="kakaoPlaces"
    :isOpen="isPopupOpen"
    @close="closePopup"
    @select="handleSelect"
  />
</template>

<script setup>
import { createMember } from '@/api/users';
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useToastStore } from '@/store/index';
import AddressPopup from './AddressPopup.vue';

const toast = useToastStore();
const router = useRouter();

const member = ref({
  email: '',
  username: '',
  passwordTemp: '',
  password: '',
});
const valueError = ref({
  emailError: '',
  usernameError: '',
  passwordError: '',
});

const onSave = async () => {
  const requiredFields = ['email', 'username', 'password'];
  const fieldDesc = {
    email: '이메일은 필수 값입니다.',
    username: '이름은 필수 값입니다.',
    password: '비밀번호는 필수 값입니다.',
  };
  const hasError = ref(false);

  requiredFields.forEach((field) => {
    if (!member.value[field]) {
      valueError.value[field] = `${fieldDesc[field]}`;
      hasError.value = true;
    }
  });

  if (member.value.password != member.value.passwordTemp) {
    hasError.value = true;
  }

  if (hasError.value) {
    return;
  }
  try {
    const data = { ...member.value };
    createMember(data);
    toast.setToast('회원가입 완료!');
    router.push('/');
  } catch (error) {
    console.error(error);
    toast.setToast('회원가입 실패!', 'danger');
  }
};

const passwordConfirmError = ref('');
const passwordConfirm = (e) => {
  if (e.target.value != member.value.passwordTemp) {
    passwordConfirmError.value = '비밀번호가 일치하지 않습니다.';
  } else {
    passwordConfirmError.value = '';
  }
};

const kakaoPlaces = ref([
  {
    id: '18577297',
    x: '127.11045685440104',
    y: '37.39544768093775',
    road_address_name: '경기 성남시 분당구 판교역로 166',
  },
  {
    id: '18059921',
    x: '126.57066130083415',
    y: '33.450682729588145',
    road_address_name: '제주특별자치도 제주시 첨단로 242',
  },
  {
    id: '22251293',
    x: '126.570875463183',
    y: '33.4526219140826',
    road_address_name: '제주특별자치도 제주시 첨단로 216-19',
  },
  {
    id: '1437795442',
    x: '127.11036420512991',
    y: '37.39541713271851',
    road_address_name: '경기 성남시 분당구 판교역로 166',
  },
  {
    id: '143299114',
    x: '127.1100869772751',
    y: '37.39581744474611',
    road_address_name: '경기 성남시 분당구 판교역로 166',
  },
]);

const isPopupOpen = ref(false);
const selectedAddress = ref(null);

const handleSelect = (address) => {
  selectedAddress.value = address;
  closePopup();
};

const openPopup = () => (isPopupOpen.value = true);
const closePopup = () => (isPopupOpen.value = false);
</script>

<style scoped>
.text-red {
  color: red;
  font-size: 14px;
  margin-top: 2px;
}
.my-container {
  height: 100%;
  display: flex;
  justify-content: center;
}
</style>
