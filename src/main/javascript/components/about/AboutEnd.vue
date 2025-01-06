<script setup lang="ts">
import CardInnerFrame from '@/commons/frame/CardInnerFrame.vue'
</script>
<template>
  <CardInnerFrame header="今すぐ始めよう！">
    <ul class="about-end-list">
      <li class="about-end-item">
        <article class="about-end-item-wrapper">
          <h3 class="about-end-header">無料登録</h3>
          <p class="about-end-desc">登録料・月額費用は一切かかりません。</p>
        </article>
      </li>
      <li class="about-end-item">
        <article class="about-end-item-wrapper">
          <h3 class="about-end-header">即日公開</h3>
          <p class="about-end-desc">アップロードしたその日から作品を公開できます</p>
        </article>
      </li>
      <li class="about-end-item">
        <article class="about-end-item-wrapper">
          <h3 class="about-end-header">安心サポート</h3>
          <p class="about-end-desc">24時間体制のカスタマーサポートがあなたをバックアップ。</p>
        </article>
      </li>
      <li class="about-end-item">
        <article class="about-end-item-wrapper">
          <h3 class="about-end-header">夢への第一歩</h3>
          <p class="about-end-desc">あなたの才能を眠らせたままにしないで。今こそチャンスです！</p>
        </article>
      </li>
    </ul>
  </CardInnerFrame>
</template>

<style lang="sass" scoped>
.about-end
  &-list
    display: flex
    flex-wrap: wrap
    padding-left: 0
  &-item
    list-style-type: none
    position: relative
    width: 50%
    &::before
      position: absolute
      background-color: #f7edd4ff
      border-radius: .35em
      border: 1px solid #ddd3baff
      content: ''
      top: 0
      bottom: 0
      margin-top: auto
      margin-bottom: auto
      height: 1.875rem
      width: 1.875rem
  &-item-wrapper
    padding-left: 3rem
  &-header
    font-size: 1.4rem
    margin-top: 1.125rem
  &-desc
    font-size: 1.125rem
</style>
