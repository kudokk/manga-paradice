<script setup lang="ts">
import { useRouter } from 'vue-router'
import { NAMES } from '@/router/name'
import { getRecentTime } from '@/commons/date/date'
import StickyLabel from '@/commons/label/StickyLabel.vue'
import IconFire from '@/components/icons/IconFire.vue'
import IconNew from '@/components/icons/IconNew.vue'

const hotMangas = [
  { id: 1, img: 'https://dummyimage.com/320x400/000/fff', title: '永世乙女の戦い方aaaaaaaaaaaaaaaaaaaa', lastChapter: 1, updateTime: '2018-03-03 05:54:54' },
  { id: 2, img: 'https://dummyimage.com/320x800/000/fff', title: '永世乙女の戦い方', lastChapter: 2, updateTime: '2024-12-19 09:26:54' },
  { id: 3, img: 'https://dummyimage.com/320x300/000/fff', title: '永世乙女の戦い方', lastChapter: 106, updateTime: '2024-12-18 09:26:54' },
  { id: 4, img: 'https://dummyimage.com/320x400/000/fff', title: '永世乙女の戦い方', lastChapter: 106, updateTime: '2024-12-01 05:54:54' },
  { id: 5, img: 'https://dummyimage.com/320x400/000/fff', title: '永世乙女の戦い方', lastChapter: 106, updateTime: '2024-10-01 05:54:54' },
  { id: 6, img: 'https://dummyimage.com/320x400/000/fff', title: '永世乙女の戦い方', lastChapter: 106, updateTime: '2018-03-03 05:54:54' },
  { id: 7, img: 'https://dummyimage.com/320x400/000/fff', title: '永世乙女の戦い方', lastChapter: 106, updateTime: '2018-03-03 05:54:54' },
]
const recentMangas = [
  { id: 8, img: 'https://dummyimage.com/320x400/000/fff', title: '永世乙女の戦い方aaaaaaaaaaaaaaaaaaaa', lastChapter: 1, updateTime: '2018-03-03 05:54:54', desc: 'I’ve become a rogue lord in another world! This is the story of a game I was playing! The story of the protagonist, who has become a rogue lord who dies after two days, successfully uses the game’s history and system to escape his impending doom and become the strongest.' },
  { id: 9, img: 'https://dummyimage.com/320x800/000/fff', title: '永世乙女の戦い方', lastChapter: 2, updateTime: '2018-03-03 05:54:54', desc: '' },
  { id: 10, img: 'https://dummyimage.com/320x300/000/fff', title: '永世乙女の戦い方', lastChapter: 106, updateTime: '2018-03-03 05:54:54', desc: '' },
  { id: 11, img: 'https://dummyimage.com/320x400/000/fff', title: '永世乙女の戦い方', lastChapter: 106, updateTime: '2018-03-03 05:54:54', desc: '' },
  { id: 12, img: 'https://dummyimage.com/320x400/000/fff', title: '永世乙女の戦い方', lastChapter: 106, updateTime: '2018-03-03 05:54:54', desc: '' },
  { id: 13, img: 'https://dummyimage.com/320x400/000/fff', title: '永世乙女の戦い方aaaaaaaaaaaaaaaaaaaa', lastChapter: 1, updateTime: '2018-03-03 05:54:54', desc: '' },
  { id: 14, img: 'https://dummyimage.com/320x800/000/fff', title: '永世乙女の戦い方', lastChapter: 2, updateTime: '2018-03-03 05:54:54', desc: '' },
  { id: 15, img: 'https://dummyimage.com/320x300/000/fff', title: '永世乙女の戦い方', lastChapter: 106, updateTime: '2018-03-03 05:54:54', desc: '' },
  { id: 16, img: 'https://dummyimage.com/320x400/000/fff', title: '永世乙女の戦い方', lastChapter: 106, updateTime: '2018-03-03 05:54:54', desc: '' },
  { id: 17, img: 'https://dummyimage.com/320x400/000/fff', title: '永世乙女の戦い方', lastChapter: 106, updateTime: '2018-03-03 05:54:54', desc: '' },
  { id: 18, img: 'https://dummyimage.com/320x400/000/fff', title: '永世乙女の戦い方aaaaaaaaaaaaaaaaaaaa', lastChapter: 1, updateTime: '2018-03-03 05:54:54', desc: '' },
  { id: 19, img: 'https://dummyimage.com/320x800/000/fff', title: '永世乙女の戦い方', lastChapter: 2, updateTime: '2018-03-03 05:54:54', desc: '' },
  { id: 20, img: 'https://dummyimage.com/320x300/000/fff', title: '永世乙女の戦い方', lastChapter: 106, updateTime: '2018-03-03 05:54:54', desc: '' },
  { id: 21, img: 'https://dummyimage.com/320x400/000/fff', title: '永世乙女の戦い方', lastChapter: 106, updateTime: '2018-03-03 05:54:54', desc: '' },
  { id: 22, img: 'https://dummyimage.com/320x400/000/fff', title: '永世乙女の戦い方', lastChapter: 106, updateTime: '2018-03-03 05:54:54', desc: '' },
  { id: 23, img: 'https://dummyimage.com/320x400/000/fff', title: '永世乙女の戦い方aaaaaaaaaaaaaaaaaaaa', lastChapter: 1, updateTime: '2018-03-03 05:54:54', desc: '' },
  { id: 24, img: 'https://dummyimage.com/320x800/000/fff', title: '永世乙女の戦い方', lastChapter: 2, updateTime: '2018-03-03 05:54:54', desc: '' },
  { id: 25, img: 'https://dummyimage.com/320x300/000/fff', title: '永世乙女の戦い方', lastChapter: 106, updateTime: '2018-03-03 05:54:54', desc: '' },
  { id: 26, img: 'https://dummyimage.com/320x400/000/fff', title: '永世乙女の戦い方', lastChapter: 106, updateTime: '2018-03-03 05:54:54', desc: '' },
  { id: 27, img: 'https://dummyimage.com/320x400/000/fff', title: '永世乙女の戦い方', lastChapter: 106, updateTime: '2018-03-03 05:54:54', desc: '' },
  { id: 28, img: 'https://dummyimage.com/320x400/000/fff', title: '永世乙女の戦い方aaaaaaaaaaaaaaaaaaaa', lastChapter: 1, updateTime: '2018-03-03 05:54:54', desc: '' },
  { id: 29, img: 'https://dummyimage.com/320x800/000/fff', title: '永世乙女の戦い方', lastChapter: 2, updateTime: '2018-03-03 05:54:54', desc: '' },
  { id: 30, img: 'https://dummyimage.com/320x300/000/fff', title: '永世乙女の戦い方', lastChapter: 106, updateTime: '2018-03-03 05:54:54', desc: '' },
  { id: 31, img: 'https://dummyimage.com/320x400/000/fff', title: '永世乙女の戦い方', lastChapter: 106, updateTime: '2018-03-03 05:54:54', desc: '' },
  { id: 32, img: 'https://dummyimage.com/320x400/000/fff', title: '永世乙女の戦い方', lastChapter: 106, updateTime: '2018-03-03 05:54:54', desc: '' },
]

const getBackgroundImgStyle = (src: string) => `background-image: url(${src})`
const getChapter = (chapter: number) => `${chapter}章`

const router = useRouter()
</script>

<template>
  <main>
    <div class="home-main">
      <div class="mangas-hot">
        <h2 class="mangas-hot-header">
          <IconFire class="mangas-hot-header-icon" />HOT
        </h2>
        <ul class="mangas-hot-list">
          <li v-for="hot in hotMangas" class="mangas-hot-item">
            <article class="hot-manga" @click="router.push({ name: NAMES.Manga.Detail, params: { mangaId: hot.id } })">
              <StickyLabel :text="getRecentTime(hot.updateTime)" />
              <div class="hot-manga-thum" :style="getBackgroundImgStyle(hot.img)"></div>
              <h3 class="hot-manga-title">{{ hot.title }}</h3>
            </article>
          </li>
        </ul>
      </div>
      <div class="mangas-recent">
        <h2 class="mangas-recent-header">
          <IconNew class="mangas-recent-header-icon" />NEW
        </h2>
        <ul class="mangas-recent-list">
          <li v-for="recent in recentMangas" class="mangas-recent-item">
            <article class="recent-manga">
              <div class="recent-manga-thum" :style="getBackgroundImgStyle(recent.img)"></div>
              <div class="recent-manga-desc-wrapper">
                <h3 class="recent-manga-title">{{ recent.title }}</h3>
                <p class="recent-manga-desc">{{ recent.desc }}</p>
                <div class="recent-manga-last">
                  <span class="recent-manga-chapter">{{ getChapter(recent.lastChapter) }}</span>
                  <span class="recent-manga-time">{{ getRecentTime(recent.updateTime) }}</span>
                </div>
              </div>
            </article>
          </li>
        </ul>
      </div>
    </div>
  </main>
</template>

<style lang="sass" scoped>
.home-main
  color: black
  display: flex
  flex-direction: column
  align-items: center
  width: 100vw

.mangas-hot, .mangas-recent
  background-color: #FFFDFA
  margin-top: 20px
  width: 90vw

.mangas-hot-header, .mangas-recent-header
  background-color: #B88E23
  color: black
  font-size: 1.56rem

.mangas-hot
  &-header
    font-family: "Libre Baskerville"
    display: flex
    align-items: center
  &-header-icon
    margin-top: -5px
    width: 1.8rem
  &-list
    display: flex
    overflow: auto
    padding: 10px 5px
  &-item
    list-style-type: none
    & + .mangas-hot-item
      margin-left: 5px

.mangas-recent
  &-header
    font-family: "Libre Baskerville"
    display: flex
    align-items: center
  &-header-icon
    margin-top: -5px
    width: 1.8rem
  &-list
    display: flex
    flex-wrap: wrap
    gap: 5px
    margin-top: 5px
    padding-left: 0
  &-item
    list-style-type: none
    width: calc(25% - 5px)

.hot-manga
  position: relative
  max-width: 180px
  &:hover
    background-color: rgba(0,0,0,.1)
    cursor: pointer
  &-label
    background-color: #f7edd4ff
  &-thum
    background-size: cover
    background-repeat: no-repeat
    background-position: 50%
    height: 250px
    width: 180px
  &-title
    margin-top: 3px
    font-size: 1rem
    line-height: 1rem
    max-height: 1.75rem
    overflow: hidden

.recent-manga
  display: flex
  &:hover
    background-color: rgba(0,0,0,.1)
    cursor: pointer
  &-label
    background-color: #f7edd4ff
  &-thum
    background-size: cover
    background-repeat: no-repeat
    background-position: 50%
    flex-shrink: 0
    height: 160px
    width: 100px
  &-desc-wrapper
    margin-left: 5px
    word-break: break-all
    width: 100%
  &-title
    font-size: 1rem
    line-height: 1rem
    max-height: 1.75rem
    overflow: hidden
  &-desc
    margin-top: 3px
    font-size: .875rem
    line-height: .875rem
    height: calc( .875rem * 1.75 )
    overflow: hidden
  &-last
    margin-top: 3px
    display: flex
    justify-content: space-between
</style>