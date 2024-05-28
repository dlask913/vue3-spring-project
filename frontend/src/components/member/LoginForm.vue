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
import { loginMember } from '@/api/users';
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useToastStore } from '@/store/index';
import { useCookies } from 'vue3-cookies';
import { useStorageStore } from '@/store/index';
export default {
    setup(){
        const toast = useToastStore();
        const router = useRouter();
        const { cookies } = useCookies();
        const storage = useStorageStore();
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
                const loginDto = { ...login.value };
                const { data } = await loginMember(loginDto); // POST /login
                cookies.set("userId", data.memberId); // 쿠키에 userId 저장
                cookies.set("token", data.token); // 쿠키에 토큰 저장
                storage.login(data.memberId, data.token); // store 에 userId / 토큰 저장
                
                toast.setToast('로그인 완료');
                router.push('/');
                
            } catch (error){
                console.error(error);
                if (error.response.data.status == 500){
                    toast.setToast('알 수 없는 오류가 발생하였습니다.','danger');    
                } else{
                    toast.setToast(error.response.data.message,'danger');    
                }
            }
            
        };
        return {
            login,
            valueError,
            onLogin,
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