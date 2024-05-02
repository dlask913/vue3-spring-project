<template>
    <div 
        class="alert toast-box" 
        :class="`alert-${toast.toastAlertType}`"
        role="alert"
    >
        {{ toast.toastMessage }}
    </div>
</template>

<script>
import { useToastStore } from '@/store/index';
export default {
    setup(){
        const toast = useToastStore();
        return{
            toast
        }
    }
}
</script>

<style scoped>
    .toast-box {
        position: fixed;
        top: 10px;
        right: 10px;
    }
</style>