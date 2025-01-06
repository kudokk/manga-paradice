<script setup lang="ts">
import ImageMangaka9 from '@/assets/image/sample_mangaka9.jpg'
import HeroImage from '@/commons/HeroImage.vue'
import CardInnerFrame from '@/commons/frame/CardInnerFrame.vue'
</script>
<template>
  <HeroImage :src="ImageMangaka9" back-color="white">
    <CardInnerFrame header="漫画家さんを全力サポート">
      <ul class="support-list">
        <li class="support-item">
          <article class="support-card">
            <h3 class="support-header">分析ツール</h3>
            <p class="support-desc">読者層や人気ページなど、詳細な分析データを提供。作品改善に役立ちます。</p>
          </article>
        </li>
        <li class="support-item">
          <article class="support-card">
            <h3 class="support-header">コミュニティフォーラム</h3>
            <p class="support-desc">他の漫画家との交流や情報交換ができる場を用意。切磋琢磨して成長しましょう。</p>
          </article>
        </li>
        <li class="support-item">
          <article class="support-card">
            <h3 class="support-header">編集者からのアドバイス</h3>
            <p class="support-desc">プロの編集者が定期的にフィードバックを提供。作品のクオリティ向上をサポートします。</p>
          </article>
        </li>
        <li class="support-item">
          <article class="support-card">
            <h3 class="support-header">宣伝サポート</h3>
            <p class="support-desc">SNSや他メディアでの宣伝をお手伝い。より多くのファンを獲得できます。</p>
          </article>
        </li>
      </ul>
    </CardInnerFrame>
  </HeroImage>
</template>

<style lang="sass" scoped>
.support-list
  display: flex
  flex-wrap: wrap
  padding-left: 0
  gap: 1em

.support-item
  background-color: #f7edd4ff
  border: 1px solid #ddd3baff
  border-radius: 10px
  padding: 1.125em
  list-style-type: none
  width: calc(50% - 1em)
</style>
