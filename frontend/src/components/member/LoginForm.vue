<template>
    <form @submit.prevent = "onLogin">
        <div class="my-container">
            <div class="card col-5 mt-5 mb-5">
                <h2 class="mt-5 mb-4 text-center"><b>로그인</b></h2>
                <div class="container col-10">
                    <div class="form-group">
                        <label class="form-label">이메일</label>
                        <input v-model="login.email" type="text" class="form-control">
                        <div 
                            v-if="valueError"
                            class="text-red"
                        >
                            {{ valueError.email }}
                        </div>
                    </div>
                    <div class="form-group mt-3">
                        <label class="form-label">비밀번호</label>
                        <input v-model="login.password" type="password" class="form-control">
                        <div 
                            v-if="valueError"
                            class="text-red"
                        >
                            {{ valueError.password }}
                        </div>
                    </div>
                    <div class="form-group mt-5 mb-5">
                        <button type="submit" class="btn btn-primary" style="width: 100%; color:white;">로그인</button>
                    </div>
                </div>
            </div>
        </div>
    </form>
</template>
<script>
import axios from '@/axios';
import { ref,computed } from 'vue';
import { useRouter } from 'vue-router';
import { useToastStore, useStorageStore } from '@/store/index';
export default {
    setup(){
        const toast = useToastStore();
        const token = useStorageStore();
        const router = useRouter();
        const login = ref({
            email: '',
            password: ''
        });

        const valueError = ref({
            emailError: '',
            passwordError: ''
        });

        const onLogin = async () => {
            const requiredFields = ['email', 'password'];
            const fieldDesc = {
                email: '이메일을 입력해주세요.',
                password: '비밀번호를 입력해주세요.'
            };
            let hasError = false;

            requiredFields.forEach(field => {
                if (!login.value[field]) {
                    valueError.value[field] = `${fieldDesc[field]}`;
                    hasError = true;
                }
            });

            if(hasError){
                return;
            }

            try {
                let res;
                const data = {
                    email: login.value.email,
                    password: login.value.password,
                };
                res = await axios.post(`login`, data);
                toast.setToast('로그인 완료!');
                token.setToken(res.data);
                console.log(token.jwt);
                
            } catch (error){
                console.log(error);
                toast.setToast('로그인 실패!','danger');
            }
            
        };

        return {
            login,
            valueError,
            onLogin
        }
    }
}
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