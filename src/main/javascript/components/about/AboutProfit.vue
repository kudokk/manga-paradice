<script setup lang="ts">
import ImageManga11 from '@/assets/image/sample_manga11.jpg'
import CardHalfFrame from '@/commons/frame/CardHalfFrame.vue'
</script>
<template>
  <CardHalfFrame header="プレビュー数で収益を獲得" :src="ImageManga11" alt="漫画集">
    <div class="profit">
      <div class="profit-place">1円</div>
      <div class="profit-per">1プレビューごと</div>
      <p class="profit-desc">読者が作品をプレビューするたびに、1円の収益が発生します。人気作品ほど高収入に！</p>
    </div>
    <div class="profit">
      <div class="profit-place">100円</div>
      <div class="profit-per">100プレビューごと</div>
      <p class="profit-desc">プレビュー数が増えるほど、収益も増加。100回のプレビューで100円の収入です。</p>
    </div>
    <div class="profit">
      <div class="profit-place">10000円</div>
      <div class="profit-per">10000プレビューごと</div>
      <p class="profit-desc">人気作品なら月に10000円以上の収入も夢ではありません。才能を活かして稼ぎましょう。</p>
    </div>
  </CardHalfFrame>
</template>

<style lang="sass" scoped>
.profit
  &-place
    text-align: center
    font-size: 3.75rem
    font-family: "Libre Baskerville"

  &-per
    text-align: center
    font-size: 1.4rem
    font-family: "Libre Baskerville"

  &-desc
    font-size: 1rem

  & + .profit
    margin-top: 20px
</style>