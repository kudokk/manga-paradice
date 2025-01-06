<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import CardInnerFrame from '@/commons/frame/CardInnerFrame.vue'
import Step from '@/commons/label/Step.vue'

const getStepWidth = () => window.innerWidth / 4

const stepWidth = ref<number>(getStepWidth())

onMounted(() => {
  window.addEventListener('resize', () => stepWidth.value = getStepWidth())
})

const StepDescWidth = computed(() => stepWidth.value + 'px')

</script>
<template>
  <CardInnerFrame header="簡単3ステップで作品を公開" class="genre-frame-inner">
    <div class="step-list">
      <Step text="1" :width="stepWidth" />
      <Step text="2" :width="stepWidth" />
      <Step text="3" :width="stepWidth" />
    </div>
    <div class="step-desc-list">
      <div class="step-desc-item">
        <h3 class="step-desc-header">アカウント作成</h3>
        <p class="step-desc-text">無料で簡単に登録できます。メールアドレスと基本情報を入力するだけです。</p>
      </div>
      <div class="step-desc-item">
        <h3 class="step-desc-header">作品をアップロード</h3>
        <p class="step-desc-text">完成した漫画をPDFまたは画像ファイルでアップロードします。シリーズものも歓迎です。</p>
      </div>
      <div class="step-desc-item">
        <h3 class="step-desc-header">公開して収益化</h3>
        <p class="step-desc-text">公開ボタンを押すだけで、世界中のファンがあなたの作品を楽しめます。プレビュー数に応じて収益が発生します。</p>
      </div>
    </div>
  </CardInnerFrame>
</template>

<style lang="sass" scoped>
.step-list
  display: flex
  justify-content: space-between
  width: calc(100% - 72px)

.step-desc
  &-list
    display: flex
  &-item
    margin-top: 20px
    width: v-bind(StepDescWidth)
  &-header
    font-size: 1.4rem
</style>
