<template>
    <nav class="navbar navbar-expand-lg navbar-light bg-light">
        <div class="container-fluid">
            <router-link class="navbar-brand" :to="{name: 'Home'}">Home</router-link>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav">                
                    <li class="nav-item">
                    <a class="nav-link" href="#">게시판</a>
                    </li>
                    <li class="nav-item">
                    <router-link class="nav-link" :to="{name: 'RegisterForm'}">회원가입</router-link>
                    </li>
                </ul>
            </div>
        </div>
    </nav>
</template>

<script>
export default {

}
</script>

<style>

</style>