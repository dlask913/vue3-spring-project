<template>
<div class="container mt-5">
    <div class="row">
      <div class="col-md-4 text-center">
        <img alt="Profile Image" class="profile-image">
        <h2 class="mt-3">Username</h2>
        <p class="text-muted">@username</p>
      </div>
      <div class="col-md-8">
        <h3>Personal Information</h3>
        <form>
          <div class="mb-3">
            <label for="email" class="form-label">Email address</label>
            <input type="email" class="form-control" id="email" placeholder="name@example.com">
          </div>
          <div class="mb-3">
            <label for="phone" class="form-label">Usernamer</label>
            <input type="text" class="form-control" id="username" placeholder="nickname">
          </div>
          <div class="mb-3">
            <label for="address" class="form-label">Address</label>
            <input type="text" class="form-control" id="address" placeholder="1234 Main St">
          </div>
          <button type="submit" class="btn btn-primary">Save Changes</button>
        </form>
      </div>
    </div>

    <div class="row mt-5">
      <div class="col-md-6">
        <h3>My Posts</h3>
        <div class="card mb-3">
          <div class="card-body">
            <h5 class="card-title">Post Title 1</h5>
            <p class="card-text">This is a brief content of the post.</p>
            <p class="card-text"><small class="text-muted">Posted on 2024-07-25</small></p>
          </div>
        </div>
        <div class="card mb-3">
          <div class="card-body">
            <h5 class="card-title">Post Title 2</h5>
            <p class="card-text">This is a brief content of the post.</p>
            <p class="card-text"><small class="text-muted">Posted on 2024-07-20</small></p>
          </div>
        </div>
      </div>
      <div class="col-md-6">
        <h3>My Comments</h3>
        <div class="card mb-3">
          <div class="card-body">
            <p class="card-text">This is a comment on a post.</p>
            <p class="card-text"><small class="text-muted">Commented on 2024-07-25</small></p>
          </div>
        </div>
        <div class="card mb-3">
          <div class="card-body">
            <p class="card-text">This is another comment on a different post.</p>
            <p class="card-text"><small class="text-muted">Commented on 2024-07-22</small></p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup></script>
<style>
.profile-image {
      width: 150px;
      height: 150px;
      object-fit: cover;
      border-radius: 50%;
      margin-bottom: 20px;
    }
</style>
