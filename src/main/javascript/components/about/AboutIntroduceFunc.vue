<script setup lang="ts">
import ImageMangaka10 from '@/assets/image/sample_mangaka10.jpg'
import CardHalfFrame from '@/commons/frame/CardHalfFrame.vue'
</script>
<template>
  <CardHalfFrame header="読者との絆を深める機能 " :src="ImageMangaka10" alt="sns">
    <div class="func">
      <div class="func-header">
        <svg class="func-header-icon" viewBox="0 0 512 512" xmlns="http://www.w3.org/2000/svg" data-icon="heart"
          data-prefix="fal" aria-hidden="true">
          <path
            d="M244 130.6l-12-13.5-4.2-4.7c-26-29.2-65.3-42.8-103.8-35.8c-53.3 9.7-92 56.1-92 110.3v3.5c0 32.3 13.4 63.1 37.1 85.1L253 446.8c.8 .7 1.9 1.2 3 1.2s2.2-.4 3-1.2L443 275.5c23.6-22 37-52.8 37-85.1v-3.5c0-54.2-38.7-100.6-92-110.3c-38.5-7-77.8 6.6-103.8 35.8l-4.2 4.7-12 13.5c-3 3.4-7.4 5.4-12 5.4s-8.9-2-12-5.4zm34.9-57.1C311 48.4 352.7 37.7 393.7 45.1C462.2 57.6 512 117.3 512 186.9v3.5c0 36-13.1 70.6-36.6 97.5c-3.4 3.8-6.9 7.5-10.7 11l-184 171.3c-.8 .8-1.7 1.5-2.6 2.2c-6.3 4.9-14.1 7.5-22.1 7.5c-9.2 0-18-3.5-24.8-9.7L47.2 299c-3.8-3.5-7.3-7.2-10.7-11C13.1 261 0 226.4 0 190.4v-3.5C0 117.3 49.8 57.6 118.3 45.1c40.9-7.4 82.6 3.2 114.7 28.4c6.7 5.3 13 11.1 18.7 17.6l4.2 4.7 4.2-4.7c4.2-4.7 8.6-9.1 13.3-13.1c1.8-1.5 3.6-3 5.4-4.5z"
            fill="currentColor"></path>
        </svg>
        <span class="func-header-text">いいね機能</span>
      </div>
      <p class="func-desc"> 読者が作品に「いいね」をつけられます。人気度が一目で分かり、モチベーションアップにつながります。</p>
    </div>
    <div class="func">
      <div class="func-header">
        <svg class="func-header-icon" viewBox="0 0 512 512" xmlns="http://www.w3.org/2000/svg" data-icon="comment"
          data-prefix="fal" aria-hidden="true">
          <path
            d="M256 64C125.8 64 32 148.6 32 240c0 37.1 15.5 70.6 40 100c5.2 6.3 8.4 14.8 7.4 23.9c-3.1 27-11.4 52.5-25.7 76.3c-.5 .9-1.1 1.8-1.6 2.6c11.1-2.9 22.2-7 32.7-11.5L91.2 446l-6.4-14.7c17-7.4 33-16.7 48.4-27.4c8.5-5.9 19.4-7.5 29.2-4.2C193 410.1 224 416 256 416c130.2 0 224-84.6 224-176s-93.8-176-224-176zM0 240C0 125.2 114.5 32 256 32s256 93.2 256 208s-114.5 208-256 208c-36 0-70.5-6.7-103.8-17.9c-.2-.1-.5 0-.7 .1c-16.9 11.7-34.7 22.1-53.9 30.5C73.6 471.1 44.7 480 16 480c-6.5 0-12.3-3.9-14.8-9.8s-1.1-12.8 3.4-17.4c8.1-8.2 15.2-18.2 21.7-29c11.7-19.6 18.7-40.6 21.3-63.1c0 0-.1-.1-.1-.2C19.6 327.1 0 286.6 0 240z"
            fill="currentColor"></path>
        </svg>
        <span class="func-header-text">コメント欄</span>
      </div>
      <p class="func-desc">読者からの感想や応援メッセージを受け取れます。ファンとの交流を深められます。</p>
    </div>
    <div class="func">
      <div class="func-header">
        <svg class="func-header-icon" viewBox="0 0 512 512" xmlns="http://www.w3.org/2000/svg" data-icon="gift"
          data-prefix="fal" aria-hidden="true">
          <path
            d="M365.1 32c-17.6 0-33.9 9-43.2 23.9l-45 72.1H288h80c26.5 0 48-21.5 48-48s-21.5-48-48-48h-2.9zm-130 96l-45-72.1C180.8 41 164.5 32 146.9 32H144c-26.5 0-48 21.5-48 48s21.5 48 48 48h80 11.1zM256 101l38.8-62c15.2-24.2 41.7-39 70.3-39H368c44.2 0 80 35.8 80 80c0 18-6 34.6-16 48h32c26.5 0 48 21.5 48 48v64c0 20.9-13.4 38.7-32 45.3V288 448c0 35.3-28.7 64-64 64H96c-35.3 0-64-28.7-64-64V288v-2.7C13.4 278.7 0 260.9 0 240V176c0-26.5 21.5-48 48-48H80C70 114.6 64 98 64 80C64 35.8 99.8 0 144 0h2.9c28.6 0 55.2 14.7 70.3 39L256 101zM144 160H48c-8.8 0-16 7.2-16 16v64c0 8.8 7.2 16 16 16H64 240V160H224 144zm128 0v96H448h16c8.8 0 16-7.2 16-16V176c0-8.8-7.2-16-16-16H368 288 272zM240 288H64V448c0 17.7 14.3 32 32 32H240V288zm32 192H416c17.7 0 32-14.3 32-32V288H272V480z"
            fill="currentColor"></path>
        </svg>
        <span class="func-header-text">投げ銭システム</span>
      </div>
      <p class="func-desc"> 読者から直接支援を受けられます。ファンの熱意が作家活動を後押しします。</p>
    </div>
  </CardHalfFrame>
</template>

<style lang="sass" scoped>
.func
  &-header
    display: flex
    align-items: center
  &-header-icon
    color: #B88E23
    height: 45px
    width: 45px
  &-header-text
    font-size: 1.4rem
    margin-left: 20px

  & + .func
    margin-top: 30px
</style>
