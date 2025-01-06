<script setup lang="ts">
import ImageMangaka7 from '@/assets/image/sample_mangaka7.jpg'
import HeroImage from '@/commons/HeroImage.vue'
import CardInnerFrame from '@/commons/frame/CardInnerFrame.vue'
</script>
<template>
  <div class="react-renderer node-card block block-card">
    <div data-node-view-wrapper="">
      <div class="css-zfp3b4">
        <div class="css-hblaxo">
          <div class="css-1rwvfra">
            <div class="card-content is-full-width is-borderless">
              <div data-node-view-content-inner="card">
                <HeroImage :src="ImageMangaka7" back-color="white">
                  <CardInnerFrame header="成功事例：夢を叶えた漫画家たち">
                    <div class="react-renderer node-cardLayoutItem">
                      <div data-node-view-content-inner="cardLayoutItem">
                        <div
                          class="react-renderer node-smartLayout animatable-on-load-content-parent block block-smartLayout last-block animate-has-animated">
                          <div class=""
                            style="white-space:normal;position:relative;display:grid;grid-template-columns:100%"
                            data-node-view-wrapper="">
                            <div class="css-m9cipp">
                              <div class="css-fq9rba">
                                <div class="themed-line css-he26gr"></div>
                              </div>
                              <div data-node-view-content-outer="smartLayout">
                                <div data-node-view-content-inner="smartLayout">
                                  <div
                                    class="react-renderer node-smartLayoutCell animatable-on-load-content-child animate-has-animated">
                                    <div data-node-view-wrapper="">
                                      <div class=" css-1doss8g">
                                        <div class="css-1hgoaaj">
                                          <span class="story-num">1</span>
                                          <div class="story-num-border story-num-border-top"></div>
                                          <article class="story">
                                            <h3 class="story-header story-header-left">佐藤美咲さん</h3>
                                            <p class="story-desc">
                                              「週末だけの趣味だった漫画が、今では生活の柱になりました。月間100万プレビューを達成し、専業漫画家として生活できています。」
                                            </p>
                                          </article>
                                        </div>
                                      </div>
                                    </div>
                                  </div>
                                  <div
                                    class="react-renderer node-smartLayoutCell animatable-on-load-content-child animate-has-animated">
                                    <div data-node-view-wrapper="">
                                      <div class=" css-1doss8g">
                                        <div class="css-1qlf6z5">
                                          <span class="story-num story-num-left">2</span>
                                          <div class="story-num-border story-num-border-left"></div>
                                          <article class="story">
                                            <h3 class="story-header">田中健太さん</h3>
                                            <p class="story-desc">
                                              「最初は不安でしたが、視聴者コメントのおかげで作品が大幅に改善。今では出版社からオファーをいただくまでになりました。」
                                            </p>
                                          </article>
                                        </div>
                                      </div>
                                    </div>
                                  </div>
                                  <div
                                    class="react-renderer node-smartLayoutCell animatable-on-load-content-child animate-has-animated">
                                    <div data-node-view-wrapper="">
                                      <div class=" css-1doss8g">
                                        <div class="css-1hgoaaj">
                                          <span class="story-num">3</span>
                                          <div class="story-num-border"></div>
                                          <article class="story">
                                            <h3 class="story-header story-header-left">山本愛子さん</h3>
                                            <p class="story-desc">「他の漫画家さんとの交流で刺激を受け、新しいジャンルに挑戦。予想外の人気を博し、アニメ化が決定しました。」
                                            </p>
                                          </article>
                                        </div>
                                      </div>
                                    </div>
                                  </div>
                                </div>
                              </div>
                              <div class="css-1on9ed1"></div>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </CardInnerFrame>
                </HeroImage>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="sass" scoped>
.story
  color: #454240
  padding-bottom: .25em
  &-header
    font-size: 1.4rem
    margin-bottom: 0.6em
    &-left
      text-align: right
  &-desc
    font-size: 1.1rem

.story-num
  background-color: #f7edd4ff
  border-radius: .35em
  border: 1px solid #ddd3baff
  font-size: 1.68rem
  font-family: "Libre Baskerville"
  display: flex
  align-items: center
  justify-content: center
  height: 2.5rem
  width: 2.5rem
  position: absolute
  right: 0
  margin-top: 2.5rem
  margin-bottom: 1.125em
  transform: translateX(50%)
  z-index: 2
  &-left
    right: auto
    left: 0
    transform: translateX(-50%)
  &-border
    background-color: #ddd3baff
    position: absolute
    z-index: 1
    width: 3.5em
    height: 2px
    right: 0
    margin-top: 3.75rem
    &-left
      right: auto
      left: 0
</style>
