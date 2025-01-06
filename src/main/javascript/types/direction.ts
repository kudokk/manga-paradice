export const Direction = {
  UP: 'up',
  DOWN: 'down'
} as const

export type DirectionType = (typeof Direction)[keyof typeof Direction]