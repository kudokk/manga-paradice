<script setup lang="ts">
import AboutEnd from '@/components/about/AboutEnd.vue'
import AboutIntroduceFunc from '@/components/about/AboutIntroduceFunc.vue'
import AboutIntroduceGenre from '@/components/about/AboutIntroduceGenre.vue'
import AboutProfit from '@/components/about/AboutProfit.vue'
import AboutStepFlow from '@/components/about/AboutStepFlow.vue'
import AboutSuccessStory from '@/components/about/AboutSuccessStory.vue'
import AboutSupport from '@/components/about/AboutSupport.vue'
import AboutTop from '@/components/about/AboutTop.vue'
</script>

<template>
  <div class="main-body">
    <div style="white-space:pre-wrap;width:100%" data-node-view-content="">
      <div data-node-view-content-inner="document" style="white-space:inherit">
        <AboutTop />
        <AboutStepFlow />
        <AboutProfit />
        <AboutSupport />
        <AboutIntroduceFunc />
        <AboutIntroduceGenre />
        <AboutSuccessStory />
        <AboutEnd />
      </div>
    </div>
  </div>
</template>

<style lang="sass" scoped>
html
  min-height: var(--\31 00vh)
  min-height: -webkit-fill-available
  height: var(--\31 00vh)
  height: -webkit-fill-available

body
  position: relative
  --\31 00vh: 100vh
  --editor-width: 100vw

[data-node-view-content-inner="paragraph"]:empty::after
  content: " "

.main-body
  font-size: 1.125rem
  width: 100vw
</style>
