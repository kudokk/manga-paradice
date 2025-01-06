<script setup lang="ts">
import { ref, nextTick } from 'vue'
import ChangeOrderButton from '@/commons/button/ChangeOrderButton.vue'
import RoundButton from '@/commons/button/RoundButton.vue'
import { throwError } from '@/commons/exception/exception'
import FormFrame from '@/commons/frame/FormFrame.vue'
import { previewImage } from '@/commons/image/previewImage'
import { Direction } from '@/types/direction'

/**
 * 投稿漫画の画像群
 */
interface MangaChaptar {
  /**
   * 投稿漫画の画像群
   * @property key 章
   * @property value ファイル配列
   */
  [key: number]: File[]
}

const chapter = ref(0)
/**
 * 章数をインクリメント
 */
const incrementChapter = () => chapter.value++
const mangas = ref<MangaChaptar>({
  [chapter.value]: []
})

/**
 * アップロードされた画像を投稿漫画画像群に格納
 * @param e 画像インプットイベント
 * @param index 章数
 */
const setMangasValue = (e: Event, index: number) => {
  const files = (e.target as HTMLInputElement).files
  if (files == null) {
    throwError('漫画投稿： 不正な画像投稿')
    return
  }
  mangas.value = {
    ...mangas.value,
    [index]: Array.from(files)
  }
}

/**
 * 章数に応じてアップロード画像をプレビュー表示
 * @param index 章数
 */
const previewImages = (index: number) => {
  mangas.value[index].forEach((manga, i) => {
    const img = document.getElementById(getImageId(index, i))
    if (img == null) {
      throwError('漫画投稿： アップロード済み画像の読み込み失敗')
      return
    }
    previewImage(img as HTMLImageElement, manga)
  })
}

/**
 * 章数に応じたアップロード画像配列を取得
 * @param chapterIndex 章数
 */
const getMangas = (chapterIndex: number) => (mangas.value[chapterIndex])

/**
 * 章数 + 1
 * @param chapterIndex 章数
 */
const getChpater = (chapterIndex: number) => `${Number(chapterIndex) + 1}話`

/**
 * 章数とアップロード画像のインデックスを使用した文字列
 * @param index 章数
 * @param i アップロード画像のインデックス
 */
const getImageId = (index: number, i: number) => index.toString() + i.toString()

const isFirstIndex = (i: number) => Number(i) === 0


const isLastIndex = (index: number, i: number) => mangas.value[index].length === Number(i) + 1

/**
 * 画像インプット時の処理
 * @param e 画像インプットイベント
 */
const onUpload = async (e: Event, index: number) => {
  // アップロードされた画像を投稿漫画画像群に格納
  setMangasValue(e, index)
  // 章数をインクリメント
  if (Number(index) + 1 === Object.keys(mangas.value).length) {
    incrementChapter()
    mangas.value = {
      ...mangas.value,
      [chapter.value]: []
    }
  }
  await nextTick()
  previewImages(index)
}

const upOrder = (index: number, i: number) => {
  const targetIndex = Number(i) - 1
  const files = mangas.value[index]
  const file = files[i]
  const targetFile = files[targetIndex]
  files[targetIndex] = file
  files[i] = targetFile
}

const downOrder = (index: number, i: number) => {
  const targetIndex = Number(i) + 1
  const files = mangas.value[index]
  const file = files[i]
  const targetFile = files[targetIndex]
  files[targetIndex] = file
  files[i] = targetFile
}
</script>

<template>
  <main>
    <div class="manga-create">
      <h2 class="manga-create-title">漫画投稿</h2>
      <FormFrame>
        <div class="form-item">
          <h3 class="form-item-label">漫画タイトル</h3>
          <div class="form-item-content">
            <input type="text" />
          </div>
        </div>
        <div class="form-item">
          <h3 class="form-item-label">サムネイル</h3>
          <div class="form-item-content">
            <input type="file" accept="image/png, image/jpeg" />
          </div>
        </div>
        <div class="form-item">
          <h3 class="form-item-label">漫画詳細</h3>
          <div class="form-item-content">
            <textarea></textarea>
          </div>
        </div>
        <hr />
        <div class="form-item">
          <h3 class="form-item-label">漫画原稿</h3>
          <div class="form-item-content">
            <template v-for="(manga, index) in mangas" :key="manga">
              <span class="manga-preview-chapter">{{ getChpater(index) }}</span>
              <input type="file" @change="(e: Event) => onUpload(e, index)" accept="image/png, image/jpeg" multiple />
              <ul class="manga-preview-list">
                <li v-for="(file, i) in getMangas(index)" :key="file.name" class="manga-preview-item">
                  <img :id="getImageId(index, i)" class="manga-preview-image" />
                  <div class="manga-preview-order">
                    <ChangeOrderButton v-if="!isFirstIndex(i)" :direction="Direction.UP" @click="upOrder(index, i)" />
                    <ChangeOrderButton v-if="!isLastIndex(index, i)" :direction="Direction.DOWN"
                      @click="downOrder(index, i)" />
                  </div>
                </li>
              </ul>
            </template>
          </div>
        </div>
        <hr />
        <div class="form-item">
          <h3 class="form-item-label">著者コメント</h3>
          <div class="form-item-content">
            <textarea></textarea>
          </div>
        </div>
        <div class="manga-create-button">
          <RoundButton text="投稿する" />
        </div>
      </FormFrame>
    </div>
  </main>
</template>

<style lang="sass" scoped>
main
  color: black
  display: flex
  flex-direction: column
  align-items: center
  padding: 2rem
  width: 100vw

.manga-create
  width: 90vw
  &-title
    font-size: 1.4rem
    margin-bottom: 1rem
  &-button
    text-align: center

.form-item
  display: flex
  margin-top: .5rem
  &-label
    color: #5C4E3D
    min-width: max(15vw, 150px)
  &-content
    margin-top: 5px

.manga-preview
  &-chapter
    margin-right: 1rem
  &-list
    padding-left: 2.5rem
    margin-top: 5px
    margin-bottom: 5px
  &-item
    display: flex
    list-style-type: none
    margin-top: 3px
    width: 70vw
  &-image
    width: 70%
  &-order
    display: flex
    flex-direction: column
    justify-content: space-evenly
    margin-left: 5px
</style>