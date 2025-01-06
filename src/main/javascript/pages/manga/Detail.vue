<script setup lang="ts">
import { getRecentTime } from '@/commons/date/date'
import { NAMES } from '@/router/name'

const mangas = {
  id: 1,
  title: '回復術士のやり直し',
  img: 'https://dummyimage.com/320x400/000/fff',
  desc: 'I’ve become a rogue lord in another world! This is the story of a game I was playing! The story of the protagonist, who has become a rogue lord who dies after two days, successfully uses the game’s history and system to escape his impending doom and become the strongest.',
  updateTime: '2024-12-19 09:26:54',
  views: 3085481,
  chpaters: [
    { no: 1, updateTime: '2018-03-03 05:54:54' },
    { no: 2, updateTime: '2024-12-19 09:26:54' },
    { no: 3, updateTime: '2024-12-19 09:26:54' },
    { no: 4, updateTime: '2024-12-19 09:26:54' },
    { no: 5, updateTime: '2024-12-19 09:26:54' },
    { no: 6, updateTime: '2024-12-19 09:26:54' },
    { no: 7, updateTime: '2018-03-03 05:54:54' },
    { no: 8, updateTime: '2024-12-19 09:26:54' },
    { no: 9, updateTime: '2024-12-19 09:26:54' },
    { no: 10, updateTime: '2024-12-19 09:26:54' },
    { no: 11, updateTime: '2024-12-19 09:26:54' },
    { no: 12, updateTime: '2024-12-19 09:26:54' }
  ]
}

const getChapter = (chapter: number) => `${chapter}章`
</script>

<template>
  <main>
    <section class="detail-main">
      <div>
        <img :src="mangas.img" />
      </div>
      <div class="detail-main-desc-wrapper">
        <h2 class="detail-main-title">{{ mangas.title }}</h2>
        <p class="detail-main-desc">{{ mangas.desc }}</p>
        <div class="detail-main-recent-time-wrapper">
          <span class="detail-main-recent-time-label">最終更新：</span>
          <span class="detail-main-recent-time">{{ getRecentTime(mangas.updateTime) }}</span>
        </div>
        <div class="detail-main-views-wrapper">
          <span class="detail-main-views-label">閲覧数：</span>
          <span class="detail-main-views">{{ mangas.views }}</span>
        </div>
      </div>
    </section>
    <section class="detail-chapter">
      <h2 class="detail-chapter-title">章</h2>
      <ul class="detail-chapter-list">
        <li v-for="chapter in mangas.chpaters" class="detail-chapter-item">
          <router-link :to="{ name: NAMES.Manga.Views, params: { mangaId: mangas.id, chapterId: chapter.no } }"
            class="detail-chapter-link">{{ getChapter(chapter.no) }}</router-link>
          <span>{{ getRecentTime(chapter.updateTime) }}</span>
        </li>
      </ul>
    </section>
  </main>
</template>

<style lang="sass" scoped>
main
  color: black
  display: flex
  font-size: 1rem
  padding: 2rem
  width: 100vw

.detail-main
  background-color: #FFFDFA
  display: flex
  padding: 1rem
  margin-right: 30px
  &-desc-wrapper
    margin-left: 20px
  &-title
    font-size: 1.56rem
    margin-bottom: 10px
  &-desc
    margin-bottom: 5px
  &-recent-time-wrapper
    display: flex
  &-recent-time-label
    margin-right: 20px
    min-width: 100px
  &-views-wrapper
    display: flex
  &-views-label
    margin-right: 20px
    min-width: 100px


.detail-chapter
  background-color: #FFFDFA
  flex-shrink: 0
  padding: 1rem
  width: 20vw
  &-title
    font-size: 1.56rem
    margin-bottom: 10px
  &-list
    padding-left: 0
  &-item
    list-style-type: none
    display: flex
    justify-content: space-between
    padding-top: .25rem
    padding-bottom: .25rem
    & + .detail-chapter-item
      border-top: 1px solid #ddd3ba
  &-link
    background-color: transparent
    border: none
</style>
