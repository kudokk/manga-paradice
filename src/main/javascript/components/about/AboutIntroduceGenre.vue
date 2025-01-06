<script setup lang="ts">
import CardInnerFrame from '@/commons/frame/CardInnerFrame.vue'
</script>
<template>
  <div class="genre">
    <CardInnerFrame header="多彩なジャンルに対応" class="genre-frame-inner">
      <div class="genre_list">
        <img src="@/assets/image/manga_genre_battle_teen.png" alt="青春抗争" class="genre_item" height="250" width="250" />
        <img src="@/assets/image/manga_genre_love.png" alt="恋愛" class="genre_item" height="250" width="250" />
        <img src="@/assets/image/manga_genre_sf.png" alt="SF" class="genre_item" height="250" width="250" />
        <img src="@/assets/image/manga_genre_mangaka.png" alt="夢を追う青春" class="genre_item" height="250" width="250" />
        <img src="@/assets/image/manga_genre_battle.png" alt="戦国" class="genre_item" height="250" width="250" />
        <img src="@/assets/image/manga_genre_feature.png" alt="近未来SF" class="genre_item" height="250" width="250" />
      </div>
      <p class="genre_desc">
        アクション、ロマンス、コメディ、ホラー、SF...どんなジャンルの作品も大歓迎です。あなたの個性を存分に発揮してください。<br>読者は多様な作品を求めています。ジャンルの壁を超えた新しい表現にも挑戦してみましょう。
      </p>
    </CardInnerFrame>
  </div>
</template>

<style lang="sass" scoped>
.genre
  background-color: #B88E23

  &-frame-inner :deep(.card-inner-frame-header)
    color: black

.genre_list
  display: flex
  flex-wrap: wrap

.genre_item
  border-radius: 10px
  margin-right: 10px
  margin-top: 10px

.genre_desc
  color: black
  margin-top: 20px
</style>
