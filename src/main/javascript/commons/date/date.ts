/**
 * 1秒・1分・1日・1時間・1月・1週・1年のミリ秒数
 */
const secondTime = 1000
const minuteTime = secondTime * 60
const hourTime = minuteTime * 60
const dayTime = hourTime * 24
const weekTime = dayTime * 7
const monthTime = dayTime * 30 // TODO: その月の日数を計算
const yearTime = dayTime * 365

/**
 * 指定ミリ秒を年表記で取得する
 * @param differenceTime ミリ秒
 * @returns 指定ミリ秒を1年のミリ秒で割って、整数で切り捨てた値
 */
const getYears = (differenceTime: number) => Math.floor(differenceTime / yearTime) + '年前'
/**
 * 指定ミリ秒を月表記で取得する
 * @param differenceTime ミリ秒
 * @returns 指定ミリ秒を1月のミリ秒で割って、整数で切り捨てた値
 */
const getMonths = (differenceTime: number) => Math.floor(differenceTime / monthTime) + 'ヵ月前'
/**
 * 指定ミリ秒を週表記で取得する
 * @param differenceTime ミリ秒
 * @returns 指定ミリ秒を1週のミリ秒で割って、整数で切り捨てた値
 */
const getWeeks = (differenceTime: number) => Math.floor(differenceTime / weekTime) + '週間前'
/**
 * 指定ミリ秒を日表記で取得する
 * @param differenceTime ミリ秒
 * @returns 指定ミリ秒を1日のミリ秒で割って、整数で切り捨てた値
 */
const getDays = (differenceTime: number) => Math.floor(differenceTime / dayTime) + '日前'
/**
 * 指定ミリ秒を時間表記で取得する
 * @param differenceTime ミリ秒
 * @returns 指定ミリ秒を1時間のミリ秒で割って、整数で切り捨てた値
 */
const getHours = (differenceTime: number) => Math.floor(differenceTime / hourTime) + '時間前'
/**
 * 指定ミリ秒を分表記で取得する
 * @param differenceTime ミリ秒
 * @returns 指定ミリ秒を1分のミリ秒で割って、整数で切り捨てた値
 */
const getMinuts = (differenceTime: number) => Math.floor(differenceTime / minuteTime) + '分前'
/**
 * 指定ミリ秒を秒表記で取得する
 * @param differenceTime ミリ秒
 * @returns 指定ミリ秒を1秒のミリ秒で割って、整数で切り捨てた値
 */
const getSeconds = (differenceTime: number) => Math.floor(differenceTime / secondTime) + '秒前'

/**
 * 指定ミリ秒が1年以上であるか判定する
 * @param differenceTime ミリ秒
 * @returns 指定ミリ秒を1年のミリ秒で割って、整数で切り捨てた値が1以上であればtrue, そうでなければfalse
 */
const has1Year = (differenceTime: number) => differenceTime / yearTime >= 1
/**
 * 指定ミリ秒が1月以上であるか判定する
 * @param differenceTime ミリ秒
 * @returns 指定ミリ秒を1月のミリ秒で割って、整数で切り捨てた値が1以上であればtrue, そうでなければfalse
 */
const has1Month = (differenceTime: number) => differenceTime / monthTime >= 1
/**
 * 指定ミリ秒が1週以上であるか判定する
 * @param differenceTime ミリ秒
 * @returns 指定ミリ秒を1週のミリ秒で割って、整数で切り捨てた値が1以上であればtrue, そうでなければfalse
 */
const has1Week = (differenceTime: number) => differenceTime / weekTime >= 1
/**
 * 指定ミリ秒が1日以上であるか判定する
 * @param differenceTime ミリ秒
 * @returns 指定ミリ秒を1日のミリ秒で割って、整数で切り捨てた値が1以上であればtrue, そうでなければfalse
 */
const has1Day = (differenceTime: number) => differenceTime / dayTime >= 1
/**
 * 指定ミリ秒が1時間以上であるか判定する
 * @param differenceTime ミリ秒
 * @returns 指定ミリ秒を1時間のミリ秒で割って、整数で切り捨てた値が1以上であればtrue, そうでなければfalse
 */
const has1Hour = (differenceTime: number) => differenceTime / hourTime >= 1
/**
 * 指定ミリ秒が1分以上であるか判定する
 * @param differenceTime ミリ秒
 * @returns 指定ミリ秒を1分のミリ秒で割って、整数で切り捨てた値が1以上であればtrue, そうでなければfalse
 */
const has1Minute = (differenceTime: number) => differenceTime / minuteTime >= 1
/**
 * 指定ミリ秒が1秒以上であるか判定する
 * @param differenceTime ミリ秒
 * @returns 指定ミリ秒を1秒のミリ秒で割って、整数で切り捨てた値が1以上であればtrue, そうでなければfalse
 */
const has1Second = (differenceTime: number) => differenceTime / secondTime >= 1

/**
 * 現在日付と指定日付における差のミリ秒を取得
 * @param date 指定日付
 * @returns 現在日付と指定日付における差のミリ秒
 */
const getDifferenceTime = (date: Date) => new Date().getTime() - date.getTime()


/**
 * 指定時間を年・月・週・日・時間・分・秒の表記で返却する
 * @param time 指定時間
 * @returns 
 */
export const getRecentTime = (time: string) => {
  const differenceTime = getDifferenceTime(new Date(time))
  if (has1Year(differenceTime)) return getYears(differenceTime)
  if (has1Month(differenceTime)) return getMonths(differenceTime)
  if (has1Week(differenceTime)) return getWeeks(differenceTime)
  if (has1Day(differenceTime)) return getDays(differenceTime)
  if (has1Hour(differenceTime)) return getHours(differenceTime)
  if (has1Minute(differenceTime)) return getMinuts(differenceTime)
  if (has1Second(differenceTime)) return getSeconds(differenceTime)
  return '1秒前'
} 