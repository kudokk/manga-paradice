<script setup lang="ts">
const manga = {
  id: 1,
  title: '回復術師のやり直し',
  imgs: [
    'https://dummyimage.com/320x400/000/fff',
    'https://dummyimage.com/320x400/000/fff',
    'https://dummyimage.com/320x400/000/fff',
    'https://dummyimage.com/320x400/000/fff',
    'https://dummyimage.com/320x400/000/fff',
    'https://dummyimage.com/320x400/000/fff',
    'https://dummyimage.com/320x400/000/fff',
    'https://dummyimage.com/320x400/000/fff',
    'https://dummyimage.com/320x400/000/fff',
    'https://dummyimage.com/320x400/000/fff'
  ]
}

</script>
<template>
  <article class="views">
    <h2 class="views-title">{{ manga.title }}</h2>
    <div class="views-img-wrapper">
      <img v-for="img in manga.imgs" :src="img" />
    </div>
  </article>
</template>

<style lang="sass" scoped>
.views
  display: flex
  flex-direction: column
  align-items: center
  width: 100vw
  &-title
    color: black
    font-size: 1.56rem
    margin-bottom: 10px
  &-img-wrapper
    display: flex
    flex-wrap: wrap
    gap: 10px
    width: 700px
</style>