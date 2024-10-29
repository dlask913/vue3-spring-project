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
</template>

<script setup>
import { createMember } from '@/api/users';
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useToastStore } from '@/store/index';

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
