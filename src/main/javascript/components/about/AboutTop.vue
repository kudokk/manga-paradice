<script setup lang="ts">
import ImageMangaka1 from '@/assets/image/sample_mangaka1.jpg'
import RoundButton from '@/commons/button/RoundButton.vue'
import CardInnerFrame from '@/commons/frame/CardInnerFrame.vue'
import HeroImage from '@/commons/HeroImage.vue'
</script>
<template>
  <div class="about-top">
    <div class="about-top-inner">
      <HeroImage :src="ImageMangaka1" back-color="black">
        <CardInnerFrame header="漫画家になろう - あなたの才能を収益化" class="about-top-inner-frame">
          <p class="about-top-desc">
            漫画家になろうは、あなたの漫画作品を世界中のファンに届け、その人気に応じて収益を得られるプラットフォームです。<br>才能あふれる皆さま、ここで夢を叶えましょう！
          </p>
          <div class="button-wrapper">
            <RoundButton class="button-left" text="今すぐ登録" />
            <RoundButton text="詳細を見る" />
          </div>
        </CardInnerFrame>
      </HeroImage>
    </div>
  </div>
</template>

<style lang="sass" scoped>
.about-top
  display: flex
  justify-content: center

  &-inner-frame :deep(.card-inner-frame-header)
    color: white

.about-top-desc
  color: white
  font-size: 1em
  margin-top: 1.125em
  margin-bottom: 1.125em

.button-wrapper
  display: flex

.button-left
  margin-right: 10px
</style>
